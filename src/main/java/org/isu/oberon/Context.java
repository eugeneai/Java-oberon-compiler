package org.isu.oberon;

import java.util.HashMap;
import java.util.Vector;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.llvm.LLVM.*;

import static org.isu.oberon.OberonParser.DIV;


public class Context {

    public Context parent = null;
    public ProcSymbol proc;
    public final LLVMBuilderRef builder;
    public final HashMap<String,Symbol> symbols = new HashMap<>();

    public static HashMap<String, Symbol> types = null;
    public /* static */ final org.isu.oberon.OberonParser parser;


    public Context(org.isu.oberon.OberonParser parser,
            ProcSymbol proc,
            LLVMBuilderRef builder,
            Context parent) {
        this.parser=parser;
        this.proc=proc;
        this.builder=builder;
        this.parent=parent;
    }

    public Context(org.isu.oberon.OberonParser parser,
                   ModuleSymbol mod,
                   LLVMBuilderRef builder) {
        this.parser=parser;
        this.proc=mod;
        this.builder=builder;
        this.addModule(mod);
    }


    public Context(Context parent) {  // Copy Constructor;
        this.parent = parent;
        this.parser = parent.parser;
        this.proc = parent.proc;
        this.builder = parent.builder;
    }

    public ModuleSymbol getModule() {
        if (proc.isModule()) return (ModuleSymbol) proc;
        return parent.getModule();
    }

    public LLVMBasicBlockRef getBody() {
        return proc.body;
    }

    public void setExpr(String name, LLVMValueRef expr) throws FailedPredicateException {
        IdExists(name);
        VarSymbol var = (VarSymbol) getCurrent().get(name);
        var.setExpr(expr);
    }

    public Value getRef(String name) {
        VarSymbol var = (VarSymbol) getCurrent().get(name);
        Value val = new Value((NumberType) var.type, var.ref);
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
            String msg = String.format("Identifier '%s' is not defined", name);
            System.err.println(msg);
            throw new FailedPredicateException(parser,
                    "symbol-does-not-exist",
                    msg);
        }
        return true;
    }

    public VarSymbol addVariable(String name, String typeName, int index) throws FailedPredicateException {
        TypeSymbol t = getType(typeName);
        return (VarSymbol) addSymbol(new VarSymbol(name, t));
    }

    public VarSymbol addVariable (String name, String typeName) throws FailedPredicateException {
        return addVariable(name, typeName, VarSymbol.NOINDEX);
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

    public ModuleSymbol addModule(String name){
        return (ModuleSymbol) addSymbol(new ModuleSymbol(name));
    }
    public ModuleSymbol addModule(ModuleSymbol mod){
        return (ModuleSymbol) addSymbol(mod);
    }

    public ProcSymbol addProc(String name, Vector<VarSymbol> args, TypeSymbol type){ // FIXE: Add parameters
        return (ProcSymbol) addSymbol(new ProcSymbol(name, args, type));
    }

    public ProcSymbol addProc(String name){ // FIXE: Add parameters
        return (ProcSymbol) addSymbol(new ProcSymbol(name));
    }

    public ProcSymbol getProc(String name) {
        //FIXME: Test type of "name" to be a PROCEDURE before.
        return (ProcSymbol) get(name);
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
        addSymbol(types, new VoidType());
        // FIXME: add other basic types: REAL, FLOAT, CARDINAL, STRING, CHAR
    }

    private HashMap<String,Symbol> getCurrent() {
        return symbols;
    }

    public NumberType infixTypeCast(Value op1, int op, Value op2, Context c) {
        NumberType t1 = (NumberType) op1.type;
        NumberType t2 = (NumberType) op2.type;
        FloatType fl = (FloatType) c.getType("FLOAT");
        if (op == DIV) return fl; // FIXME: only for Int and Float and derivatives.
        if (t1 == t2) return t1;
        if (t1 == fl) return fl;
        if (t2 == fl) return fl;
        assert false : "Should not reach here";
        return fl;
    }

    public Symbol get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        };
        if (parent!=null) {
            return parent.get(name);
        }
        Symbol sym = types.get(name);

        if (sym == null) {
            System.err.println("ERROR: Symbol '"+name+"' not found");
            System.exit(2);
        }

        return sym;
    }
}
