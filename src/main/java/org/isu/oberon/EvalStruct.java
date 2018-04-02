package org.isu.oberon;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;


public class EvalStruct {

    org.isu.oberon.ExprParser parser;
    public LLVMModuleRef mod;
    public LLVMValueRef main;
    public LLVMBuilderRef builder;

    EvalStruct(org.isu.oberon.ExprParser parser, LLVMModuleRef mod, LLVMValueRef main, LLVMBuilderRef builder) {
        this.parser=parser;
        this.mod=mod;
        this.main=main;
        this.builder=builder;
    }

    public LLVMValueRef interp(LLVMValueRef arg1, int op, LLVMValueRef arg2)
    {
        switch (op) {
            case org.isu.oberon.ExprParser.PLUS:
                return LLVMBuildAdd(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MINUS:
                return LLVMBuildSub(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MUL:
                return LLVMBuildMul(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.DIV:
                return LLVMBuildSDiv(builder, arg1, arg2, "");

            default:
                System.out.println("Wrong Operation!");
                System.exit(1);
        }
        return null; // Should not get here
    }

}
