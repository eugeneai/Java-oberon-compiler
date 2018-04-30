package org.isu.oberon;

import org.bytedeco.javacpp.LLVM;

import java.util.Vector;

import static org.bytedeco.javacpp.LLVM.*;

public class ProcSymbol extends Symbol {
    public LLVM.LLVMValueRef proc = null;
    public Vector<VarSymbol> args = null;
    public LLVM.LLVMBasicBlockRef body = null;

    public ProcSymbol(String name, Vector<VarSymbol> args, LLVM.LLVMValueRef proc) {  // FIXME: Add parameters
        super(name);
        this.args = args;
        this.proc=proc;
    }

    public ProcSymbol(String name, Vector<VarSymbol> args) {
        super(name);
        this.args = args;
    }

    public ProcSymbol(String name) {
        super(name);
    }

    public Vector<VarSymbol> createArgs() {
        this.args = new Vector<>();
        return this.args;
    }

    private void appendBodyBlock() {
        body = LLVMAppendBasicBlock(proc, "body");
    }

    public boolean isModule() {
        return false;
    }

    public LLVM.LLVMValueRef createProc(ModuleSymbol mod) {
        LLVM.LLVMTypeRef fac_arg = null; // FIXME: no arguments

        LLVM.LLVMValueRef proc = LLVMAddFunction(mod.mod,
                tangle(name),
                LLVMFunctionType(LLVMInt64Type(),
                        fac_arg,
                        0,
                        0));
        LLVMSetFunctionCallConv(proc, LLVMCCallConv);
        appendBodyBlock();
        return proc;
    }

    public String tangle(String name) {
        return name;
    }
}
