package org.isu.oberon;

import org.bytedeco.javacpp.LLVM.*;

import static org.bytedeco.javacpp.LLVM.*;

public class FloatType extends NumberType {
    public FloatType() {
        super("FLOAT");
    }

    @Override
    public ArithValue infixOp(EvalStruct s, ArithValue arg1, int op, ArithValue arg2) {
        LLVMValueRef res;
        switch (op) {
            case org.isu.oberon.ExprParser.PLUS:
                res = LLVMBuildFAdd(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.ExprParser.MINUS:
                res = LLVMBuildFSub(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.ExprParser.MUL:
                res =  LLVMBuildFMul(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.ExprParser.DIV:
                res = LLVMBuildFDiv(s.builder, arg1.ref, arg2.ref, ""); // FIXME: Signed op.
                break;
            default:
                System.out.println("Wrong floating-point operation!");
                System.exit(1);
                return null;
        }
        return new ArithValue(this, res); // Should not get here
    }
}
