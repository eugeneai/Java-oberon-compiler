package org.isu.oberon;

import org.bytedeco.llvm.LLVM.LLVMModuleRef;

import static org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithName;

public class ModuleSymbol extends ProcSymbol {
    public final LLVMModuleRef mod;
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
