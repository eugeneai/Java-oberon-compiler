package org.isu.oberon;
import org.bytedeco.javacpp.LLVM.*;
import static org.bytedeco.javacpp.LLVM.*;

public class IntegerType extends NumberType {
    IntegerType(){
        super("INTEGER");
    }

    public ArithValue infixOp(Context s, ArithValue arg1, int op, ArithValue arg2)
    {
        LLVMValueRef res;
        switch (op) {
            case org.isu.oberon.OberonParser.PLUS:
                res = LLVMBuildAdd(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.MINUS:
                res = LLVMBuildSub(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.MUL:
                res =  LLVMBuildMul(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.DIV:
                res = LLVMBuildSDiv(s.builder, arg1.ref, arg2.ref, ""); // FIXME: Signed op.
                break;
            default:
                System.out.println("Wrong integer operation!");
                System.exit(1);
                return null;
        }
        return new ArithValue(this, res); // Should not get here
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String val) {
        return LLVMConstIntOfString(LLVMInt64Type(),
                val, (byte) 10);
    }
}
