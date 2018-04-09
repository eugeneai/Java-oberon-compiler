package org.isu.oberon;

import java.util.HashMap;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;


public class EvalStruct {

    public final org.isu.oberon.ExprParser parser;
    final LLVMModuleRef mod;
    public final LLVMValueRef main;
    public final LLVMBuilderRef builder;
    SymbolTables tables = null;

    EvalStruct(org.isu.oberon.ExprParser parser, LLVMModuleRef mod, LLVMValueRef main, LLVMBuilderRef builder) {
        this.parser=parser;
        this.mod=mod;
        this.main=main;
        this.builder=builder;

        initializeTypeTable();
    }

    public LLVMValueRef interp(LLVMValueRef arg1, int op, LLVMValueRef arg2)
    {
        switch (op) {
            case org.isu.oberon.ExprParser.PLUS:
                return LLVMBuildAdd(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MINUS:
                return LLVMBuildSub(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.MUL:
                return LLVMBuildMul(builder, arg1, arg2, "");

            case org.isu.oberon.ExprParser.DIV:
                return LLVMBuildSDiv(builder, arg1, arg2, "");

            default:
                System.out.println("Wrong Operation!");
                System.exit(1);
        }
        return null; // Should not get here
    }

    public void setExpr(String name, LLVMValueRef expr) throws FailedPredicateException {
        IdExists(name);
        VarSymbol var = (VarSymbol) getCurrent().get(name);
        var.ref = expr;
    }

    public LLVMValueRef getRef(String name) {
        return ((VarSymbol) getCurrent().get(name)).ref;
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
        if (sym.isType()) {
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
}
