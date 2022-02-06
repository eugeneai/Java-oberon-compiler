package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;


public class VarSymbol extends Symbol {
    protected static int NOINDEX = -1;
    public LLVMValueRef ref = null;
    public TypeSymbol type = null;

    public VarSymbol(String name, TypeSymbol type) {
        super(name);
        this.type = type;
    }

    public void setExpr(LLVMValueRef ref) {
        this.ref = ref;
    }

}
