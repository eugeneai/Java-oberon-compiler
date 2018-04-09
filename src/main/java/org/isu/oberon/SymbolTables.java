package org.isu.oberon;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class SymbolTables {
    private HashMap<String,Symbol> current; // Cache
    private Stack<HashMap<String,Symbol>> stack;
    private HashMap<String,Symbol> typeTable;

    SymbolTables(HashMap<String,Symbol> typeTable) {
        stack = new Stack<HashMap<String, Symbol>>();
        this.typeTable = typeTable;
        stack.push(typeTable);
        setCurrent();
    }

    private void setCurrent() {
        this.current = stack.peek();
    }

    public HashMap<String, Symbol> pop() throws EmptySymbolStackException {
        if (stack.peek() == typeTable) {
            throw new EmptySymbolStackException("Cannot throw out the type table");
        }
        HashMap<String,Symbol> prev = stack.pop();
        setCurrent();
        return prev;
    }

    public HashMap<String,Symbol> push(HashMap<String, Symbol> table) {
        HashMap<String,Symbol> answer = stack.push(table);
        setCurrent();
        return answer;
    }

    public HashMap<String,Symbol> push() {
        return  push(new HashMap<String, Symbol>());
    }

    public Symbol get(String name){
        if (current.containsKey(name)) {
            return current.get(name);
        };
        return typeTable.get(name);
    }

    public HashMap<String,Symbol> getCurrent(){
        return current;
    }
}
