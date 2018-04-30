package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;


public class VarSymbol extends Symbol {
    protected static int NOINDEX = -1;
    public LLVMValueRef ref = null;
    public TypeSymbol type = null;
    public int index;

    public VarSymbol(String name, TypeSymbol type) {
        super(name);
        this.type = type;
        this.index = NOINDEX;
    }

    public VarSymbol(String name, TypeSymbol type, int index) {
        super(name);
        this.type = type;
        this.index = index;
    }
}
