package org.isu.oberon;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class Value {

    public TypeSymbol type = null;
    public LLVMValueRef ref = null;

    public Value(TypeSymbol type, LLVMValueRef ref) {
        this.type=type;
        setRef(ref);
    }

    public void setRef(LLVMValueRef ref) {
        this.ref = ref;
    }
}
