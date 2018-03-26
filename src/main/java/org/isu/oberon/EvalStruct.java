package org.isu.oberon;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.LLVM.*;

public class EvalStruct {
    public LLVMModuleRef mod;
    public LLVMValueRef expr;
    EvalStruct(LLVMModuleRef mod, LLVMValueRef expr) {
        this.mod=mod;
        this.expr=expr;
    }
}
