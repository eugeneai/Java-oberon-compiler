package org.isu.oberon;
import org.bytedeco.javacpp.*;
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
}
