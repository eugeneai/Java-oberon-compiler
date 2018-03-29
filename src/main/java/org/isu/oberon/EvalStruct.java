package org.isu.oberon;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;

public class EvalStruct {

    public LLVMModuleRef mod;
    public LLVMValueRef expr;
    public LLVMBuilderRef builder;

    EvalStruct(LLVMModuleRef mod, LLVMValueRef expr, LLVMBuilderRef builder) {
        this.mod=mod;
        this.expr=expr;
        this.builder=builder;
    }

    public LLVMValueRef interp(LLVMValueRef arg1, int op, LLVMValueRef arg2)
    {
        switch (op) {
            case org.isu.oberon.ExprParser.PLUS:
                return LLVMBuildFAdd(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MINUS:
                return LLVMBuildFSub(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MUL:
                return LLVMBuildFMul(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.DIV:
                return LLVMBuildFDiv(builder, arg1, arg2, "");

            default:
                System.out.println("Wrong Operation!");
                System.exit(1);
        }
        return null; // Should not get here
    }
}
