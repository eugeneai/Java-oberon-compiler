package org.isu.oberon;

import org.bytedeco.javacpp.LLVM;

public class ArithValue extends Value {

    public LLVM.LLVMValueRef ref = null;

    public ArithValue(NumberType type) {
        super(type);
    }

    public ArithValue(NumberType type, LLVM.LLVMValueRef ref) {
        super(type);
        setRef(ref);
    }

    public void setRef(LLVM.LLVMValueRef ref) {
        this.ref = ref;
    }
}
