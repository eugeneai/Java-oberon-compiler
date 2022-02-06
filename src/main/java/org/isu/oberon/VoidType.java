package org.isu.oberon;

import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.LLVMInt64Type;

public class VoidType extends TypeSymbol {
    public VoidType() {
        super("@VOID@");
    }

    @Override
    protected LLVMValueRef genConstant(Context c) {
        return genConstant(c,"");
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String value) {
        // FIXME: issue an exception on using void type value.
        System.err.println("WARNING: usage of void type reference");
        return null;
    }

    @Override
    protected LLVMTypeRef genRef() {
        return LLVMInt64Type();
    }
}
