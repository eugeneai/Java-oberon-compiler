package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;


public class Symbol {
    public String name;
    public boolean isType() {
        return false;
    };

    Symbol(String name) {
        this.name = name;
    }
}
