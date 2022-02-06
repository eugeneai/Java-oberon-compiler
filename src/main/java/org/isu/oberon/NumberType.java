package org.isu.oberon;

import org.bytedeco.llvm.LLVM;


public abstract class NumberType extends TypeSymbol {
    public NumberType(String name) {
        super(name);
    }
    public abstract Value infixOp(Context s,
                                  Value arg1,
                                  int op,
                                  Value arg2);

    @Override
    protected LLVM.LLVMValueRef genConstant(Context c) {
        return genConstant(c,"0");
    }
}
