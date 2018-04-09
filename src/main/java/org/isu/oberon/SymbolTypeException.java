package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;

public class SymbolTypeException extends FailedPredicateException {
    SymbolTypeException(org.isu.oberon.ExprParser parser, String m1, String m2) {
        super(parser, m1, m2);
    }
}
