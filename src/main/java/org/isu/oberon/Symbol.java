package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;

public class Symbol {
    public String name;
    public boolean isType() {
        return false;
    };

    Symbol(String name) {
        this.name = name;
    }
}