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

            ModuleSymbol mod = $s.addModule($mid.text);
            mod.createProc();

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
      ( VAR (variablesDeclaration [$s, -1] SEMI ) + )?
      ( procedureDeclaration [$s] SEMI ) *
    ;

variablesDeclaration [Context s, int index] returns [int nextIndex] locals [Vector<String> vars]:
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
            int i = $index;
            for (String var: $vars) {
                if ($index!=-1) {
                    $s.addVariable(var, $ty.text, i);
                } else {
                    $s.addVariable(var, $ty.text);
                }
                i++;
            };
            $nextIndex=i;
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

procedureHeading [Context c] returns [String name, Context fc]:
   PROCEDURE
   apid=IDENT
     {
        LLVMTypeRef fac_arg = null;

        /*
        LLVMValueRef proc = LLVMAddFunction($c.mod, $apid.text,
               LLVMFunctionType(LLVMInt64Type(), fac_arg, 0, 0));
        LLVMSetFunctionCallConv(proc, LLVMCCallConv);
        */

        LLVMBuilderRef builder = LLVMCreateBuilder();

        $fc = new Context($c.parser, new ProcSymbol($apid.text), builder, $c);

        $name = $apid.text;

        $c.addProc($apid.text);
     }
   LPAR
   (   (
          ni=variablesDeclaration [$fc, 0]
          (
             SEMI ni=variablesDeclaration [$fc, $ni.nextIndex]
          )*
       ) ?
   )?
   RPAR   // FIXME: Add Variables
   {
       $fc.proc.createProc($fc.getModule());
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

term [Context s] returns [ArithValue value] locals [LLVMValueRef ref, NumberType type]
    :
    n=NUMBER {
             if ($n.text.contains(".")) {
                 $type = (FloatType) $s.getType("FLOAT");
                 $ref = LLVMConstRealOfString(LLVMFloatType(),
                                              $n.text);
             } else {
                 $type = (IntegerType) $s.getType("INTEGER");
                 $ref = LLVMConstIntOfString(LLVMInt64Type(),
                                         $n.text,
                                         (byte) 10);
             };
             $value = new ArithValue($type, $ref);
        }
    |   id=IDENT
        {
             $value = $s.getRef($id.text);
        }
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
       LLVMBasicBlockRef end = LLVMAppendBasicBlock($s.proc.proc, "end");
       LLVMPositionBuilderAtEnd($s.builder, end);

       LLVMBuildRet($s.builder, $e.value.ref);
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

