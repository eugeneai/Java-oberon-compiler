package org.isu.oberon;

import org.bytedeco.llvm.global.LLVM;

public abstract class TypeSymbol extends Symbol {
    public TypeSymbol parent;

    TypeSymbol(String name) {
        super(name);
        this.parent = null;
    }

    TypeSymbol(String name, TypeSymbol parent) {
        super(name);
        this.parent = parent;
    }

    @Override
    public boolean isType() {
        return true;
    }

    public LLVM.LLVMValueRef genDefaultValueRef(VarSymbol var, Context c) {
        return genConstant(c);
    }

    protected abstract LLVM.LLVMValueRef genConstant(Context c, String value);

    protected abstract LLVM.LLVMValueRef genConstant(Context c);

    protected abstract LLVM.LLVMTypeRef genRef();

}
