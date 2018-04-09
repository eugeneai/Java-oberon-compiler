package org.isu.oberon;

public class TypeSymbol extends Symbol {
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
}
