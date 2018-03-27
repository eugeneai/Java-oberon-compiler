grammar Expr;

@header {
package org.isu.oberon;

// import java.util.HashMap;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.LLVM.*;
}

program returns [EvalStruct s]
    :   {
            LLVMModuleRef mod = LLVMModuleCreateWithName("expr_module");

            LLVMTypeRef fac_arg = null;

            LLVMValueRef expr = LLVMAddFunction(mod, "expr", LLVMFunctionType(LLVMInt64Type(), fac_arg, 0, 0));
            LLVMSetFunctionCallConv(expr, LLVMCCallConv);


            LLVMBuilderRef builder = LLVMCreateBuilder();

            $s = new EvalStruct(mod, expr, builder);
        }
        e=expression[$s] {
            //System.out.println(String.format("Expr = %d",
            //    $expression.value));

            // FIXME: Might be useful for Exceptions and Exits.
            LLVMBasicBlockRef end = LLVMAppendBasicBlock(expr, "end");
            LLVMPositionBuilderAtEnd($s.builder, end);

            // Generating return value
            LLVMValueRef res = LLVMConstInt(LLVMInt64Type(), 42, 0);
            LLVMBuildRet(builder, res);

        }
    ;

expression [EvalStruct s] returns [int value]
    :
        m=mult[$s] { $value = $m.value; }
    (
        op=pm
        e=expression[$s] { $value = ExprEvaluator.interp($value, $op.value, $e.value); }
    )*
    ;

pm returns [int value]
    :   PLUS  { $value = $PLUS.type; }
    |   MINUS { $value = $MINUS.type; }
    ;

mult [EvalStruct s] returns [int value]
    :
        t=term[$s] { $value = $t.value; }
    (
        op=md
        m=mult[$s] { $value = ExprEvaluator.interp($value, $op.value, $m.value); }
    )*
    ;

md returns [int value]
    :   MUL { $value = $MUL.type; }
    |   DIV { $value = $DIV.type; }
    ;

term [EvalStruct s] returns [int value]
    :   NUMBER                   { $value = $NUMBER.int; }
    |   LPAR expression[$s] RPAR { $value = $expression.value; }
    ;

/* Lexical rules */

PLUS : '+' ;
MINUS: '-' ;
DIV  : '/' ;
MUL  : '*' ;
LPAR : '(' ;
RPAR : ')' ;

NUMBER  : '-'?[0-9]+ ;

WS : [ \r\t\u000C\n]+ -> skip ;
