package org.isu.oberon;

import org.bytedeco.javacpp.LLVM.*;

import static org.bytedeco.javacpp.LLVM.*;

public class ExprEvaluator {
    public static LLVMValueRef interp(EvalStruct s, LLVMValueRef arg1, int op, LLVMValueRef arg2)
    {
        /*
        System.out.println(
                String.format("--> Op: %d '%d' %d",
                        arg1, op, arg2)
        );
        */
        switch (op) {
            case org.isu.oberon.ExprParser.PLUS:
                return LLVMBuildAdd(s.builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MINUS:
                return LLVMBuildSub(s.builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MUL:
                return LLVMBuildMul(s.builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.DIV:
                return LLVMBuildSDiv(s.builder, arg1, arg2, "");

            default:
                System.out.println("Wrong Operation!");
                System.exit(1);
        }
        return null; // Should not get here
    }
}
