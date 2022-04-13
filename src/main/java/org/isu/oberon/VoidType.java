package org.isu.oberon;

import org.bytedeco.llvm.LLVM.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bytedeco.llvm.global.LLVM.LLVMInt64Type;

public class VoidType extends TypeSymbol {
    private static final Logger logger = LoggerFactory.getLogger(VoidType.class);

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
        logger.warn("Usage of void type reference");
        return null;
    }

    @Override
    protected LLVMTypeRef genRef() {
        return LLVMInt64Type();
    }
}
