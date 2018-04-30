package org.isu.oberon;

import org.bytedeco.javacpp.LLVM.*;

import static org.bytedeco.javacpp.LLVM.*;

public class FloatType extends NumberType {
    public FloatType() {
        super("FLOAT");
    }

    @Override
    public ArithValue infixOp(Context s, ArithValue arg1, int op, ArithValue arg2) {
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
        return new ArithValue(this, res); // Should not get here
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String val) {
        return LLVMConstRealOfString(LLVMFloatType(), val);
    }
}
