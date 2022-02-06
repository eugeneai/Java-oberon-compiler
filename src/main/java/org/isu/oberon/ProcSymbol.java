package org.isu.oberon;


import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.util.Vector;

import static org.bytedeco.llvm.global.LLVM.*;

public class ProcSymbol extends TypeSymbol {
    public LLVMValueRef proc = null;
    public Vector<VarSymbol> args = null;
    public LLVMBasicBlockRef body = null;
    public TypeSymbol type = null;

    public ProcSymbol(String name,
                      Vector<VarSymbol> args,
                      TypeSymbol type,
                      LLVMValueRef proc) {  // FIXME: Add parameters
        super(name);
        this.args = args;
        this.type = type;
        this.proc=proc;
    }

    public ProcSymbol(String name, Vector<VarSymbol> args, TypeSymbol type) {
        super(name);
        this.args = args;
        this.type = type;
    }

    public ProcSymbol(String name) {
        super(name);
        this.args = new Vector<>();
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    public Vector<VarSymbol> createArgs() {
        this.args = new Vector<>();
        return this.args;
    }

    private void appendBodyBlock() {
        body = LLVMAppendBasicBlock(proc, "body");
    }

    public boolean isModule() {
        return false;
    }

    public LLVMValueRef createProc(Context c) {

        this.proc = LLVMAddFunction(c.getModule().mod,
                tangle(name), genRef());

        assert this.proc!=null : "PROC is null!";

        LLVMSetFunctionCallConv(this.proc, LLVMCCallConv);

        int i=0;
        for (VarSymbol arg: args) {
            arg.setExpr(LLVMGetParam(this.proc, i));
            i++;
        }

        appendBodyBlock();

        return this.proc;
    }

    public String tangle(String name) {
        return name;
    }

    @Override
    protected LLVMValueRef genConstant(Context c) {
        return null;
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String value) {
        return null;
    }

    @Override
    protected LLVMTypeRef genRef() {

        int s = args.size();
        LLVMTypeRef [] fac_arg = new LLVMTypeRef[s];

        int i=0;
        for (VarSymbol arg: args) {
            fac_arg[i]=arg.type.genRef();
            i++;
        }

        LLVMTypeRef func_ref=null;

        if (s>0) {
            func_ref = LLVMFunctionType(type.genRef(),
                    fac_arg[0],
                    s,
                    0);
        } else {
            LLVMTypeRef [] null_arg = {LLVMInt64Type()};
            // FIXME: Is there a better way to define zero-length argument list;
            func_ref = LLVMFunctionType(type.genRef(),
                    null_arg[0],
                    s,
                    0);
        }

        return func_ref;
    }

    public Value genCall(Vector<Value> exprs, Context c) {
        int as = args.size();
        int es = exprs.size();
        assert as==es : String.format("ERROR: Procedure '%s' accepts %d parameters, but %d given", name, as, es);

        LLVMValueRef[] call_fac_args = new LLVMValueRef[as];


        int i;
        for (i=0; i < as; i++) {
            call_fac_args[i] = exprs.get(i).ref;
        }

        LLVMValueRef call_proc = LLVMBuildCall(c.builder, proc, new PointerPointer(call_fac_args), as, "");
        return new Value(type, call_proc);
    }
}
