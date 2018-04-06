grammar Expr;

@header {
package org.isu.oberon;

// import java.util.HashMap;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.LLVM.*;
}

/*  MODULE ident ";" [ImportList] DeclarationSequence
      [BEGIN StatementSequence] END ident "." .


      */

module [ExprParser parser] returns [EvalStruct s]
    :
        MODULE mid=IDENT SEMI
        /* importlist */
        {
            LLVMModuleRef mod = LLVMModuleCreateWithName($mid.text);

            LLVMTypeRef fac_arg = null;

            LLVMValueRef main = LLVMAddFunction(mod, "@MODULEBLOCK@", LLVMFunctionType(LLVMInt64Type(), fac_arg, 0, 0));
            LLVMSetFunctionCallConv(main, LLVMCCallConv);


            LLVMBuilderRef builder = LLVMCreateBuilder();

            $s = new EvalStruct($parser, mod, main, builder);
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

declarationSequence [EvalStruct s]:
    ( VAR (variableDeclaration [$s] SEMI ) + ) ?
;

variableDeclaration [EvalStruct s]:
   IDENT (COMMA IDENT)* COLON IDENT
   ;

statementSequence  [EvalStruct s]:
   statement [$s]
   (
      SEMI
      statement [$s]
   ) *
;

statement [EvalStruct s]:
     assignment [$s]
   | returnOp [$s]
;

assignment [EvalStruct s]:
   id=IDENT ASSIGN e=expression [$s]
   {
        LLVMSetValueName($e.value, $id.text);
        $s.addExpr($id.text, $e.value);
   }
   ;

returnOp [EvalStruct s] returns [LLVMValueRef value]:
   RETURN e=expression [$s]
   {
       // FIXME: Block "end" migt be useful for Exceptions and Exits.
       LLVMBasicBlockRef end = LLVMAppendBasicBlock($s.main, "end");
       LLVMPositionBuilderAtEnd($s.builder, end);

       LLVMBuildRet($s.builder, $e.value);

       $value = $e.value;
   }
   ;

expression [EvalStruct s] returns [LLVMValueRef value]
    :
        m=mult[$s] { $value = $m.value; }
    (
        op=pm
        e=expression[$s] { $value = $s.interp($value, $op.value, $e.value); }
    )*
    ;

pm returns [int value]
    :   PLUS  { $value = $PLUS.type; }
    |   MINUS { $value = $MINUS.type; }
    ;

mult [EvalStruct s] returns [LLVMValueRef value]
    :
        t=term[$s] { $value = $t.value; }
    (
        op=md
        m=mult[$s] { $value = $s.interp($value, $op.value, $m.value); }
    )*
    ;

md returns [int value]
    :   MUL { $value = $MUL.type; }
    |   DIV { $value = $DIV.type; }
    ;

term [EvalStruct s] returns [LLVMValueRef value]
    :   NUMBER {
             // LLVMBasicBlockRef number = LLVMAppendBasicBlock($s.expr, "number");
             // LLVMPositionBuilderAtEnd($s.builder, number);
             $value = LLVMConstInt(LLVMInt64Type(), $NUMBER.int, 0);
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
