package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;


public class VarSymbol extends Symbol {
    public LLVMValueRef ref = null;
    TypeSymbol type = null;

    VarSymbol(String name, TypeSymbol type) {
        super(name);
        this.type = type;
    }
}
