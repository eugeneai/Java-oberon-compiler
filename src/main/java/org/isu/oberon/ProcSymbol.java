package org.isu.oberon;

import org.bytedeco.javacpp.LLVM;

public class ProcSymbol extends Symbol {
    public final LLVM.LLVMValueRef proc;
    ProcSymbol(String name, LLVM.LLVMValueRef proc) {  // FIXME: Add parameters
        super(name);
        this.proc=proc;
    }
}
