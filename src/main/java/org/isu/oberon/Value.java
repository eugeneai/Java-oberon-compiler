package org.isu.oberon;

import org.bytedeco.llvm.LLVM;

public class Value {

    public TypeSymbol type = null;
    public LLVM.LLVMValueRef ref = null;

    public Value(TypeSymbol type, LLVM.LLVMValueRef ref) {
        this.type=type;
        setRef(ref);
    }

    public void setRef(LLVM.LLVMValueRef ref) {
        this.ref = ref;
    }
}
