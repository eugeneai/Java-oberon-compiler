package org.isu.oberon;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;

import static org.bytedeco.llvm.global.LLVM.*;

public class BooleanType extends NumberType {
    BooleanType(){
        super("BOOLEAN");
    }

    public Value infixOp(Context s, Value arg1, int op, Value arg2)
    {
        LLVMValueRef res;
        switch (op) {
            case org.isu.oberon.OberonParser.EQOP:
                res = LLVMBuildICmp(s.builder, LLVM.LLVMIntEQ , arg1.ref, arg2.ref, "test_equ");
                break;
            default:
                System.out.println("Wrong BOOLEAN equation operation!");
                System.exit(1);
                return null;
        }
        return new Value(this, res); // Should not get here
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String val) {
        if (val.equals("TRUE")) {
            return LLVMConstInt(genRef(),1, 0);
        } else if (val.equals("FALSE")) {
            return LLVMConstInt(genRef(), 0, 0);
        } else {
            System.out.println("Wrong BOOLEAN contatnt");
            System.exit(1);
            return null;
        }
    }

    @Override
    protected LLVMTypeRef genRef() {
        return LLVMInt1Type();
    }
}
