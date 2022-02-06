package org.isu.oberon;

import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class FloatType extends NumberType {
    public FloatType() {
        super("FLOAT");
    }

    @Override
    public Value infixOp(Context s, Value arg1, int op, Value arg2) {
        LLVMValueRef res;
        switch (op) {
            case org.isu.oberon.OberonParser.PLUS:
                res = LLVMBuildFAdd(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.MINUS:
                res = LLVMBuildFSub(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.MUL:
                res =  LLVMBuildFMul(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.DIV:
                res = LLVMBuildFDiv(s.builder, arg1.ref, arg2.ref, ""); // FIXME: Signed op.
                break;
            default:
                System.out.println("Wrong floating-point operation!");
                System.exit(1);
                return null;
        }
        return new Value(this, res); // Should not get here
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String val) {
        return LLVMConstRealOfString(LLVMFloatType(), val);
    }

    @Override
    protected LLVMTypeRef genRef() {
        return LLVMFloatType();
    }
}
