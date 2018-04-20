package org.isu.oberon;

import java.util.HashMap;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;


public class Context {

    public /* static */ final org.isu.oberon.OberonParser parser;
    final LLVMModuleRef mod;
    public final LLVMValueRef func;
    public final LLVMBuilderRef builder;
    public final HashMap<String,Symbol> symbols = new HashMap<>();
    public static HashMap<String, Symbol> types = null;
    public Context parent = null;

    public Context(org.isu.oberon.OberonParser parser,
            LLVMModuleRef mod,
            LLVMValueRef func,
            LLVMBuilderRef builder,
            Context parent) {
        this.parser=parser;
        this.mod=mod;
        this.func=func;
        this.builder=builder;
        this.parent=parent;
    }

    public Context(org.isu.oberon.OberonParser parser,
            LLVMModuleRef mod,
            LLVMValueRef func,
            LLVMBuilderRef builder) {
        this.parser=parser;
        this.mod=mod;
        this.func=func;
        this.builder=builder;
    }

    public Context(Context parent) {
        this.parent = parent;
        this.parser = parent.parser;
        this.mod = parent.mod;
        this.func = parent.func;
        this.builder = parent.builder;
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
            String msg = String.format("The '%s' identifier is not defined", name);
            System.err.println(msg);
            throw new FailedPredicateException(parser,
                    "symbol-does-not-exist",
                    msg);
        }
        return true;
    }

    public VarSymbol addVariable(String name, String typeName) throws FailedPredicateException {
        TypeSymbol t = (TypeSymbol) getType(typeName);
        return (VarSymbol) addSymbol(new VarSymbol(name, t));
    }

    public TypeSymbol getType(String name) throws SymbolTypeException{
        Symbol sym = get(name);
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
        return Context.addSymbol(getCurrent(), sym);
    }

    private static Symbol addSymbol(HashMap<String,Symbol> table, Symbol sym) {
        System.out.println(String.format("Added symbol: '%s':'%s'",
                sym.name, sym.getClass().getName()));
        table.put(sym.name, sym);
        return sym;
    }

    public Context newContext(){
        return new Context(this);
    }

    public static void initializeTypeTable() {
        if (types == null) {
            types = new HashMap<>();
        };
        addSymbol(types, new IntegerType());
        addSymbol(types, new FloatType());
        // FIXME: add other basic types: REAL, FLOAT, CARDINAL, STRING, CHAR
    }

    private HashMap<String,Symbol> getCurrent() {
        return symbols;
    }

    public NumberType infixTypeCast(ArithValue op1, ArithValue op2) {
        NumberType t1 = op1.type;
        NumberType t2 = op2.type;
        return t1; // FIXME: implement implicit casting.
    }

    public Symbol get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        };
        if (parent!=null) {
            return parent.get(name);
        }
        return types.get(name);
    }
}
