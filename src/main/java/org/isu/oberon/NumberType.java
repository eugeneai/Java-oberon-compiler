package org.isu.oberon;

import org.bytedeco.javacpp.LLVM;

public abstract class NumberType extends TypeSymbol {
    public NumberType(String name) {
        super(name);
    }
    public abstract ArithValue infixOp(Context s,
                                     ArithValue arg1,
                                     int op,
                                     ArithValue arg2);

    @Override
    protected LLVM.LLVMValueRef genConstant(Context c) {
        return genConstant(c,"0");
    }
}
