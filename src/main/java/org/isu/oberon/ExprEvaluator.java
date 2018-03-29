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
                return LLVMBuildFAdd(s.builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MINUS:
                return LLVMBuildFSub(s.builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MUL:
                return LLVMBuildFMul(s.builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.DIV:
                return LLVMBuildFDiv(s.builder, arg1, arg2, "");

            default:
                System.out.println("Wrong Operation!");
                System.exit(1);
        }
        return null; // Should not get here
    }
}
