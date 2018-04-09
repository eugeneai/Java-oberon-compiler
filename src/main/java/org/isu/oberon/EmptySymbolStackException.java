package org.isu.oberon;

import java.util.EmptyStackException;

public class EmptySymbolStackException extends EmptyStackException {
    public String message;
    public EmptySymbolStackException(String message) {
        super();
        this.message = message;
    }
}
