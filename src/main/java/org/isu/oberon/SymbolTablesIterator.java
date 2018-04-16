package org.isu.oberon;

import java.util.HashMap;
import java.util.Iterator;

public class SymbolTablesIterator implements Iterator<HashMap<String, Symbol>> {
    int pos;
    SymbolTables tables;
    public SymbolTablesIterator(SymbolTables tables) {
        this.tables=tables;
        pos = tables.getStack().size();
    }

    @Override
    public HashMap<String, Symbol> next() {
        pos--;
        return tables.getStack().get(pos);
    }

    @Override
    public boolean hasNext() {
        return pos>0;
    }

    @Override
    public void remove() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
}
