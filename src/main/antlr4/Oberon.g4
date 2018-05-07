grammar Oberon;

@header {
package org.isu.oberon;

// import java.util.HashMap;
import java.util.Vector;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.LLVM.*;
}

/*  MODULE ident ";" [ImportList] DeclarationSequence
      [BEGIN StatementSequence] END ident "." .


      */

module [OberonParser parser] returns [Context s]
    :
        MODULE mid=IDENT SEMI
        /* importlist */
        {

            LLVMBuilderRef builder = LLVMCreateBuilder();

            $s = new Context($parser, new ModuleSymbol($mid.text), builder);

            ModuleSymbol mod = (ModuleSymbol) $s.proc;
            mod.setType($s.getType("@VOID@"));
            mod.createProc($s);
            LLVMPositionBuilderAtEnd($s.builder, $s.proc.body);

        }
        declarationSequence [$s]
        block [$s]
        eid=IDENT
        { $mid.text.equals($eid.text) }?
        DOT
        EOF
    ;

block [Context s]:
    (
     BEGIN
     statementSequence [$s]
    ) ?
    END
    ;

declarationSequence [Context s]:
      ( VAR (variableListDeclaration [$s, -1] SEMI ) + )?
      ( procedureDeclaration [$s] SEMI ) *
    ;

variableListDeclaration [Context s, int index] returns [int nextIndex] locals [Vector<String> vars]:
   id=IDENT
        {
            $vars = new Vector<String>();
            $vars.add($id.text);
            $s.IdDoesNotExist($id.text);
        }
   (
   COMMA
   id=IDENT
        {
            if ($vars.contains($id.text)) {
                throw new FailedPredicateException($s.parser,
                              "repeated-identifier",
                              String.format("Identifier '%s' already listed", $id.text));

            };

            $vars.add($id.text);
            $s.IdDoesNotExist($id.text);
        }
   )*
   COLON
   ty=IDENT
        {
            TypeSymbol t = $s.getType($ty.text);
            for (String var: $vars) {
                VarSymbol var_s = $s.addVariable(var, $ty.text);
                if ($index >= 0) {
                    $s.proc.args.add(var_s);
                    $index++;
                } else {

                }
            };
            $nextIndex=$index;
        }
   ;

procedureDeclaration [Context c]
   :
      h=procedureHeading [$c]
      SEMI
      procedureBody [$h.fc] // FIXME:fc
      eid=IDENT
      {
         $h.name.equals($eid.text)
      }?
   ;

procedureHeading [Context c] returns [String name, Context fc] locals [TypeSymbol retType]:
   PROCEDURE
   apid=IDENT
     {
        LLVMBuilderRef builder = LLVMCreateBuilder();
        // LLVMBuilderRef builder = $c.builder;

        $fc = new Context($c.parser, $c.addProc($apid.text), builder, $c);

        $name = $apid.text;

        $retType=$c.getType("@VOID@");
     }
   (
       LPAR
       (
              ni=variableListDeclaration [$fc, 0]
              (
                 SEMI ni=variableListDeclaration [$fc, $ni.nextIndex]
              )*
       )?
       RPAR   // FIXME: Add Variables
   )?
   {
   }
   (
       COLON rt=IDENT
       {
           $retType=$c.getType($rt.text);
       }
   )?
   {
       $fc.proc.setType($retType);
       // header-createproc
       $fc.proc.createProc($fc);
       LLVMPositionBuilderAtEnd($fc.builder, $fc.proc.body);
   }
   ;

procedureBody [Context c]:
   block [$c]
   ;

statementSequence  [Context s]:
   statement [$s]
   (
      SEMI
      statement [$s]
   ) *
;

statement [Context s]:
     assignment [$s]
   | returnOp [$s]
   |
;

expression [Context s] returns [ArithValue value]
    :
        m=mult[$s] { $value = $m.value; }
    (
        op=pm
        e=expression[$s]
           {
                NumberType type = $s.infixTypeCast($value, $e.value); // FIXME: returns typecast;
                $value = type.infixOp($s, $value, $op.value, $e.value);
           }
    )*
    ;

pm returns [int value]
    :   PLUS  { $value = $PLUS.type; }
    |   MINUS { $value = $MINUS.type; }
    ;

mult [Context s] returns [ArithValue value]
    :
        t=term[$s] { $value = $t.value; }
    (
        op=md
        m=mult[$s]
           {
                NumberType type = $s.infixTypeCast($value, $m.value); // FIXME: returns typecast;
                $value = type.infixOp($s, $value, $op.value, $m.value);
           }
    )*
    ;

md returns [int value]
    :   MUL { $value = $MUL.type; }
    |   DIV { $value = $DIV.type; }
    ;

term [Context s] returns [ArithValue value] locals [LLVMValueRef ref, NumberType type, Vector <ArthValue> exprs, ProcSymbol proc]
    :
    n=NUMBER {
             if ($n.text.contains(".")) {
                 $type = (FloatType) $s.getType("FLOAT");
             } else {
                 $type = (IntegerType) $s.getType("INTEGER");
             };
             $ref = $type.genConstant($s, $n.text);
             System.out.println(String.format("CONSTANT: (%s) = %s", $type.name, $n.text));
             $value = new ArithValue($type, $ref);
        }
    |   id=IDENT
        {
             // term-proc
             $value = $s.getRef($id.text);
        }
        (
            LPAR
                {
                    $proc = $s.get($id.text);
                    $exprs = new Vector<ArithValue>;
                }
                (
                    fav=expression [$s]
                    {
                        $exprs.push_back($fav.value);
                    }
                    (
                        COMMA
                        nav=expression [$s]
                        {
                            $exprs.push_back($fav.value);
                        }
                    )*
                )?
            RPAR
            {$exprs.size() == $proc.args.size()}?
            // FIXME: Check types here or individually for each variable;
            {
                // Process parameters

                $value = $proc.genCall($exprs);
            }
        )?
    |   LPAR expression[$s] RPAR { $value = $expression.value; }
    ;


assignment [Context s]:
   id=IDENT ASSIGN e=expression [$s]
   {
        LLVMSetValueName($e.value.ref, $id.text);
        $s.setExpr($id.text, $e.value.ref);
   }
   ;

returnOp [Context s]:
   RETURN e=expression [$s]
   {
       // FIXME: Block "end" migt be useful for Exceptions and Exits.

       System.out.println("RETURN: from "+$s.proc.name);

       assert $s.proc.proc != null : "ERROR: Null pointer!";

       LLVMBasicBlockRef end = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_end");
       LLVMPositionBuilderAtEnd($s.builder, end);
       LLVMBuildRet($s.builder, $e.value.ref);

       System.out.println("RETURN END: "+$s.proc.name);
   }
   ;



/* Lexical rules */

BEGIN : 'BEGIN' ;
END   : 'END'   ;
MODULE: 'MODULE';
VAR   : 'VAR'   ;
RETURN: 'RETURN';

PROCEDURE: 'PROCEDURE';

fragment NUM : [0-9]+ ;

NUMBER   : '-'?(NUM | NUM?[.]NUM([Ee]NUM)?);

PLUS : '+' ;
MINUS: '-' ;
DIV  : '/' ;
MUL  : '*' ;
LPAR : '(' ;
RPAR : ')' ;
DOT  : '.' ;
SEMI : ';' ;
COLON: ':' ;
COMMA: ',' ;

ASSIGN: ':=';


WS : [ \r\t\u000C\n]+ -> skip ;

IDENT   : [_a-zA-Z][_a-zA-Z0-9]* ;

