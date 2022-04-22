grammar Oberon;

@header {
package org.isu.oberon;

// import java.util.HashMap;
import java.util.Vector;
import static org.bytedeco.llvm.global.LLVM.*;
import org.bytedeco.llvm.LLVM.*;
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
    BEGIN
     (
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
      procedureBody [$h.fc, $h.name] // FIXME:fc
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

procedureBody [Context c, String name]:
   block [$c]
   eid=IDENT
      {
         $name.equals($eid.text)
      }?
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
   | ifOp [$s]
   | whileOp [$s]
   | repeatOp [$s]
   | forOp [$s]
   |
;

logicalOp returns [int value]:
    EQOP { $value = $EQOP.type; }
    |
    LEOP { $value = $LEOP.type; }
    |
    GEOP { $value = $GEOP.type; }
    |
    NQOP { $value = $NQOP.type; }
    |
    LTOP { $value = $LTOP.type; }
    |
    GTOP { $value = $GTOP.type; }
    |
    ;

logicalExpression [Context s] returns [Value value] locals [LLVMValueRef ref, BooleanType type]:
    TRUE {
           $type = (BooleanType) $s.getType("BOOLEAN");
           $value = new Value($type, $type.genConstant($s, "TRUE"));
         }
    |
    FALSE {
           $type = (BooleanType) $s.getType("BOOLEAN");
           $value = new Value($type, $type.genConstant($s, "FALSE"));
          }
    |
    e1=expression[$s] op=logicalOp e2=expression[$s]
         {
            $type = (BooleanType) $s.getType("BOOLEAN");
            $value = $type.infixOp($s, $e1.value, $op.value, $e2.value);
         }
    ;

expression [Context s] returns [Value value]
    :
        m=mult[$s] { $value = $m.value; }
    (
        op=pm
        e=expression[$s]
           {
                NumberType type = $s.infixTypeCast($value, $op.value, $e.value, $s); // FIXME: returns typecast;
                $value = type.infixOp($s, $value, $op.value, $e.value);
           }
    )*
    ;

pm returns [int value]
    :   PLUS  { $value = $PLUS.type; }
    |   MINUS { $value = $MINUS.type; }
    ;

mult [Context s] returns [Value value]
    :
        t=term[$s] { $value = $t.value; }
    (
        op=md
        m=mult[$s]
           {
                NumberType type = $s.infixTypeCast($value, $op.value, $m.value, $s); // FIXME: returns typecast;
                $value = type.infixOp($s, $value, $op.value, $m.value);
           }
    )*
    ;

md returns [int value]
    :   MUL { $value = $MUL.type; }
    |   DIV { $value = $DIV.type; }
    ;

term [Context s] returns [Value value] locals [LLVMValueRef ref, NumberType type, Vector <Value> exprs, ProcSymbol proc]
    :
    n=NUMBER {
             if ($n.text.contains(".")) {
                 $type = (FloatType) $s.getType("FLOAT");
             } else {
                 $type = (IntegerType) $s.getType("INTEGER");
             };
             $ref = $type.genConstant($s, $n.text);
             System.out.println(String.format("CONSTANT: (%s) = %s", $type.name, $n.text));
             $value = new Value($type, $ref);
        }
    |   id=IDENT
        {
             // term-proc
             $proc = null;
        }
        (
            LPAR
                {
                    $proc = (ProcSymbol) $s.get($id.text);
                    $exprs = new Vector<Value>();
                }
                (
                    fav=expression [$s]
                    {
                        $exprs.add($fav.value);
                    }
                    (
                        COMMA
                        nav=expression [$s]
                        {
                            $exprs.add($nav.value);
                        }
                    )*
                )?
            RPAR
        )?
        {
            // Process parameters
            if ($proc != null) {
               $value = $proc.genCall($exprs, $s);
            } else {
               $value = $s.getRef($id.text);
            }
        }
    |   LPAR expression[$s] RPAR { $value = $expression.value; }
    ;


assignment [Context s] returns [Value value]:
   id=IDENT ASSIGN e=expression [$s]
   {
        LLVMSetValueName($e.value.ref, $id.text);
        $s.setExpr($id.text, $e.value.ref);
        $value=$e.value;
   }
   ;

ifOp [Context s] locals [LLVMBasicBlockRef elsif_block, boolean else_happened ]:
   IF e=logicalExpression[$s]
     {
       $else_happened=false;
       LLVMBasicBlockRef then_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_if_then");
       $elsif_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_if_elsif");
       LLVMBasicBlockRef join_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_if_join");
       // TODO: CHECK CMP or TRUTH value
       LLVMBuildCondBr($s.builder,$e.value.ref,then_block,$elsif_block);
     }
   THEN
     {
       LLVMPositionBuilderAtEnd($s.builder, then_block);
     }
   statementSequence[$s]
     {
       LLVMBuildBr($s.builder, join_block);
     }
   ( ELSIF
    {
       LLVMPositionBuilderAtEnd($s.builder, $elsif_block);
       LLVMBasicBlockRef elsif_then_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_if_elsif_then");
       $elsif_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_if_elsif_next");
    }
    ee=logicalExpression[$s]
     {
       LLVMBuildCondBr($s.builder,$e.value.ref,elsif_then_block,$elsif_block);
     }
     THEN
     {
       LLVMPositionBuilderAtEnd($s.builder, elsif_then_block);
     }
     statementSequence[$s]
     {
       LLVMBuildBr($s.builder, join_block);
       LLVMPositionBuilderAtEnd($s.builder, $elsif_block); // Empty one
       LLVMBuildBr($s.builder, join_block);
     }
   )*
   (
     ELSE
     {
       $else_happened=true;
       LLVMPositionBuilderAtEnd($s.builder, $elsif_block);
     }
     statementSequence[$s]
     {
       LLVMBuildBr($s.builder, join_block);
     }
   )?
   END
   {
     // Phi
     if ($else_happened==false) {
       LLVMPositionBuilderAtEnd($s.builder, $elsif_block);
       LLVMBuildBr($s.builder, join_block);
     }
     LLVMPositionBuilderAtEnd($s.builder, join_block);
     LLVMBasicBlockRef end_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_if_end");
     LLVMBuildBr($s.builder, end_block);
     LLVMPositionBuilderAtEnd($s.builder, end_block);
   }
   ;

repeatOp [Context s]:
    REPEAT
    {
        LLVMBasicBlockRef repeat_block = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_repeat_block");
        LLVMBasicBlockRef repeat_end = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_repeat_end");
        LLVMPositionBuilderAtEnd($s.builder, repeat_block);
    }
    statementSequence[$s]
    UNTIL e = logicalExpression[$s]
    {
        LLVMBuildCondBr($s.builder,$e.value.ref,repeat_end, repeat_block);
        LLVMPositionBuilderAtEnd($s.builder, repeat_end);
    }
    ;

whileOp [Context s]:
    {
        LLVMBasicBlockRef head_experssion = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_while_head");
        LLVMBasicBlockRef do_while = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_do_while");
        LLVMBasicBlockRef exit_while = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_exit_while");
    }
    WHILE
    {
        LLVMPositionBuilderAtEnd($s.builder, head_experssion);
    }
    e=logicalExpression[$s]
    {
        LLVMBuildCondBr($s.builder,$e.value.ref,do_while,exit_while);
    }
    DO
    {
        LLVMPositionBuilderAtEnd($s.builder, do_while);
    }
    statementSequence[$s]
    {
        LLVMBuildBr($s.builder, head_experssion);
    }
    END
    {
        LLVMPositionBuilderAtEnd($s.builder, exit_while);
    }
    ;

returnOp [Context s]:
   RETURN e=expression [$s]
   {
       // FIXME: Block "end" migt be useful for Exceptions and Exits.

       // System.out.println("RETURN: from "+$s.proc.name);

       assert $s.proc.proc != null : "ERROR: Null pointer!";

       // LLVMBasicBlockRef end = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_end");
       // LLVMPositionBuilderAtEnd($s.builder, end);

       LLVMBuildRet($s.builder, $e.value.ref);

       // System.out.println("RETURN END: "+$s.proc.name);
   }
   ;

forOp [Context s] locals [ Value inc, LLVMValueRef from, Value to, NumberType type  ]:


        FOR IDENT ASSIGN e=expression [$s] TO ee=expression [$s]
            {
                $type = (IntegerType) $s.getType("INTEGER");
                $inc = new Value($type, $type.genConstant($s, "1"));

                LLVMBasicBlockRef do_for = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_do_for");
                LLVMBasicBlockRef exit_for = LLVMAppendBasicBlock($s.proc.proc, $s.proc.name+"_exit_for");

                $from=$e.value.ref;
                $to=$ee.value;
            }

        DO
            {
                $from = LLVMBuildAdd($s.builder, $from, $inc.ref, "");
                LLVMBuildCondBr($s.builder,
                                LLVMBuildICmp($s.builder, 0, $from, $to.ref, ""),
                                do_for,
                                exit_for);
                LLVMPositionBuilderAtEnd($s.builder, do_for);

                System.out.println("check");
            }
            statementSequence[$s]
            {
            LLVMBuildBr($s.builder, do_for);
            }
        END
            {
                LLVMPositionBuilderAtEnd($s.builder, exit_for);
            }
        ;


/* Lexical rules */

BEGIN : 'BEGIN' ;
END   : 'END'   ;
MODULE: 'MODULE';
VAR   : 'VAR'   ;
RETURN: 'RETURN';
IF    : 'IF'    ;
THEN  : 'THEN'  ;
ELSE  : 'ELSE'  ;
ELSIF : 'ELSIF' ;
TRUE  : 'TRUE'  ;
FALSE : 'FALSE' ;
WHILE : 'WHILE' ;
DO    : 'DO'    ;
REPEAT: 'REPEAT';
UNTIL : 'UNTIL' ;
FOR   : 'FOR'   ;
TO    : 'TO'    ;
BY    : 'BY'    ;

PROCEDURE: 'PROCEDURE';

fragment NUM : [0-9]+ ;

// NUMBER   : (NUM | NUM?[.]NUM([Ee]NUM)?);
NUMBER   : (NUM);

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

LEOP : '<=' ;
GEOP : '>=' ;
LTOP : '<' ;
GTOP : '>' ;
EQOP : '=' ;
NQOP : '#' ;

ASSIGN: ':=';


WS : [ \r\t\u000C\n]+ -> skip ;

IDENT   : [_a-zA-Z][_a-zA-Z0-9]* ;

