package org.isu.oberon;

import org.bytedeco.javacpp.LLVM;

import static org.bytedeco.javacpp.LLVM.LLVMModuleCreateWithName;

public class ModuleSymbol extends ProcSymbol{
    public final LLVM.LLVMModuleRef mod;
    ModuleSymbol(String name) {
        super(name);
        mod = LLVMModuleCreateWithName(name);
    }

    @Override
    public boolean isModule() {
        return true;
    }


    @Override
    public String tangle(String name) {
        return "@MAIN@";
    }
}
