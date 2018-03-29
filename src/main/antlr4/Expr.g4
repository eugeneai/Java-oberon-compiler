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

            LLVMValueRef expr = LLVMAddFunction(mod, "expr", LLVMFunctionType(LLVMDoubleType(), fac_arg, 0, 0));
            LLVMSetFunctionCallConv(expr, LLVMCCallConv);


            LLVMBuilderRef builder = LLVMCreateBuilder();

            $s = new EvalStruct(mod, expr, builder);
        }
        e=expression[$s] {

            // FIXME: Block "end" migt be useful for Exceptions and Exits.
            LLVMBasicBlockRef end = LLVMAppendBasicBlock(expr, "end");
            LLVMPositionBuilderAtEnd($s.builder, end);

            LLVMBuildRet(builder, $e.value);

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
    :   i=INTNUMBER {
             // $value = LLVMConstIntOfString(LLVMInt64Type(), $i.text, (byte) 10);
             $value = LLVMConstRealOfString(LLVMDoubleType(), $i.text);
        }
    |   f=floatnumber {
             $value = LLVMConstRealOfString(LLVMDoubleType(), $f.text);
        }

    |   LPAR expression[$s] RPAR { $value = $expression.value; }
    ;

floatnumber returns [String value] locals [String number]
    :   i=INTNUMBER
        {    $number = $i.text;  }
        DOT
        (
         j=INTNUMBER
         {   $number += "."+$j.text; }
        )?
        (
         e=ENUMBER
         {   $number += $e.text; }
        )?
        {    $value = $number;  }
    ;
/* Lexical rules */

PLUS : '+' ;
MINUS: '-' ;
DIV  : '/' ;
MUL  : '*' ;
LPAR : '(' ;
RPAR : ')' ;
DOT  : '.' ;
EXP  : [eE];

INTNUMBER    : '-'?[0-9]+;
ENUMBER      : [eE]'-'?[0-9]+;

WS : [ \r\t\u000C\n]+ -> skip ;
