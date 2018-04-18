package org.isu.oberon;

import java.util.HashMap;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;


public class Context {

    public final org.isu.oberon.OberonParser parser;
    final LLVMModuleRef mod;
    public final LLVMValueRef main; // FIXME: rename main as function.
    public final LLVMBuilderRef builder;
    SymbolTables tables = null; // FIXME: It must be only the current hashtable.

    Context(org.isu.oberon.OberonParser parser, LLVMModuleRef mod, LLVMValueRef main, LLVMBuilderRef builder) {
        this.parser=parser;
        this.mod=mod;
        this.main=main;
        this.builder=builder;

        initializeTypeTable();
    }

    public void setExpr(String name, LLVMValueRef expr) throws FailedPredicateException {
        IdExists(name);
        VarSymbol var = (VarSymbol) getCurrent().get(name);
        var.ref = expr;
    }

    public ArithValue getRef(String name) {
        VarSymbol var = (VarSymbol) getCurrent().get(name);
        ArithValue val = new ArithValue((NumberType) var.type, var.ref);
        return val;
    }

    public boolean IdDoesNotExist(String name) throws FailedPredicateException {
        if (getCurrent().containsKey(name)) {
            throw new FailedPredicateException(parser,
                    "symbol-exists-already",
                    String.format("The '%s' identifier is already defined", name));
        }
        return true;
    }

    public boolean IdExists(String name) throws FailedPredicateException {
        if (! getCurrent().containsKey(name)) {
            throw new FailedPredicateException(parser,
                    "symbol-does-not-exist",
                    String.format("The '%s' identifier was not defined", name));
        }
        return true;
    }

    public VarSymbol addVariable(String name, String typeName) throws FailedPredicateException {
        TypeSymbol t = (TypeSymbol) getType(typeName);
        return (VarSymbol) addSymbol(new VarSymbol(name, t));
    }

    public TypeSymbol getType(String name) throws SymbolTypeException{
        Symbol sym = tables.get(name);
        if (sym == null) {
            throw new SymbolTypeException(parser,
                    "type-not-found",
                    String.format("Symbol '%s' not found (expected to be a type).",
                            name));
        }

        if(sym.isType()) {
            return (TypeSymbol) sym;
        } else {
            throw new SymbolTypeException(parser,
                    "type-not-found",
                    String.format("Symbol '%s' is not a type symbol",
                       name));
        }
    }

    public void addModule(String name){
        addSymbol(new ModuleSymbol(name));
    }

    private Symbol addSymbol(Symbol sym){
        System.out.println(String.format("Added symbol: '%s':'%s'",
                sym.name, sym.getClass().getName()));
        return getCurrent().put(sym.name, sym);
    }

    public HashMap<String,Symbol> createSymbolTable(){
        return tables.push();
    }

    public HashMap<String,Symbol> removeSymbolTable() {
        return tables.pop();
    }

    private void initializeTypeTable() {
        HashMap<String,Symbol> types = new HashMap<>();
        tables = new SymbolTables(types);

        addSymbol(new IntegerType());
        // FIXME: add other basic types: REAL, FLOAT, CARDINAL, STRING, CHAR
    }

    private HashMap<String,Symbol> getCurrent() {
        return tables.getCurrent();
    }

    public NumberType infixTypeCast(ArithValue op1, ArithValue op2) {
        NumberType t1 = op1.type;
        NumberType t2 = op2.type;
        return t1; // FIXME: implement implicit casting.
    }
}
