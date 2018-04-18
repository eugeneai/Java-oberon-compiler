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
            LLVMModuleRef mod = LLVMModuleCreateWithName($mid.text);

            LLVMTypeRef fac_arg = null;

            LLVMValueRef main = LLVMAddFunction(mod, $mid.text+"@module", LLVMFunctionType(LLVMInt64Type(), fac_arg, 0, 0));
            LLVMSetFunctionCallConv(main, LLVMCCallConv);


            LLVMBuilderRef builder = LLVMCreateBuilder();

            $s = new Context($parser, mod, main, builder);

            $s.addModule($mid.text);
        }
        declarationSequence [$s]
        (
         BEGIN
         statementSequence [$s]
         ) ?
        END eid=IDENT
        {
            if ($mid.text != $eid.text) {
                throw new FailedPredicateException($s.parser,
                    "head-tail-module-name-mismatch",
                    "Tail and head module names must be equal");
            }
        }
        DOT
    ;

declarationSequence [Context s]:
    ( VAR (variableDeclaration [$s] SEMI ) + ) ?
;

variableDeclaration [Context s] locals [Vector<String> vars]:
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
            for (String var: $vars) {
                $s.addVariable(var, $ty.text);
            };
        }
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
       LLVMBasicBlockRef end = LLVMAppendBasicBlock($s.func, "end");
       LLVMPositionBuilderAtEnd($s.builder, end);

       LLVMBuildRet($s.builder, $e.value.ref);
   }
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

term [Context s] returns [ArithValue value]
    :   NUMBER {
             // LLVMBasicBlockRef number = LLVMAppendBasicBlock($s.expr, "number");
             // LLVMPositionBuilderAtEnd($s.builder, number);

             LLVMValueRef ref = LLVMConstInt(LLVMInt64Type(), $NUMBER.int, 0);
             $value = new ArithValue((IntegerType) $s.getType("INTEGER"), ref);
        }
    |   id=IDENT
        {
             $value = $s.getRef($id.text);
        }
    |   LPAR expression[$s] RPAR { $value = $expression.value; }
    ;

/* Lexical rules */

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

BEGIN : 'BEGIN' ;
END   : 'END'   ;
MODULE: 'MODULE';
VAR   : 'VAR'   ;
RETURN: 'RETURN';

NUMBER  : '-'?[0-9]+ ;
IDENT   : [_a-zA-Z][_a-zA-Z0-9]* ;

WS : [ \r\t\u000C\n]+ -> skip ;
