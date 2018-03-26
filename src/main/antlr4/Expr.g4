grammar Expr;

@header {
package org.isu.oberon;

// import java.util.HashMap;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.LLVM.*;
}

program returns [LLVMModuleRef mod]
    :   expression {
            //System.out.println(String.format("Expr = %d",
            //    $expression.value));
            $mod = LLVMModuleCreateWithName("expr_module");

            LLVMTypeRef fac_arg = null;

            LLVMValueRef expr = LLVMAddFunction($mod, "expr", LLVMFunctionType(LLVMInt64Type(), fac_arg, 0, 0));
            LLVMSetFunctionCallConv(expr, LLVMCCallConv);
        }
    ;

expression returns [int value]
    :
        m=mult { $value = $m.value; }
    (
        op=pm
        e=expression { $value = ExprEvaluator.interp($value, $op.value, $e.value); }
    )*
    ;

pm returns [int value]
    :   PLUS  { $value = $PLUS.type; }
    |   MINUS { $value = $MINUS.type; }
    ;

mult returns [int value]
    :
        t=term { $value = $t.value; }
    (
        op=md
        m=mult { $value = ExprEvaluator.interp($value, $op.value, $m.value); }
    )*
    ;

md returns [int value]
    :   MUL { $value = $MUL.type; }
    |   DIV { $value = $DIV.type; }
    ;

term returns [int value]
    :   NUMBER               { $value = $NUMBER.int; }
    |   LPAR expression RPAR { $value = $expression.value; }
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
