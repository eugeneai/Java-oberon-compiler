package org.isu.oberon;

import org.antlr.v4.runtime.FailedPredicateException;
import org.bytedeco.javacpp.LLVM.*;

import java.util.HashMap;

import static org.bytedeco.javacpp.LLVM.*;


public class EvalStruct {

    public final org.isu.oberon.ExprParser parser;
    final LLVMModuleRef mod;
    public final LLVMValueRef main;
    public final LLVMBuilderRef builder;
    private final HashMap<String, LLVMValueRef> symTable;

    EvalStruct(org.isu.oberon.ExprParser parser, LLVMModuleRef mod, LLVMValueRef main, LLVMBuilderRef builder) {
        this.symTable = new HashMap<>();
        this.parser=parser;
        this.mod=mod;
        this.main=main;
        this.builder=builder;
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

    public void addExpr(String name, LLVMValueRef expr) throws FailedPredicateException {
        if (symTable.containsKey(name)) {
            throw new FailedPredicateException(parser,
                    "symbol-exists-already",
                    String.format("The '%s' identifier is already defined", name));
        }
        symTable.put(name,expr);
    }

    public LLVMValueRef getRef(String name) {
        return symTable.get(name);
    }

}
