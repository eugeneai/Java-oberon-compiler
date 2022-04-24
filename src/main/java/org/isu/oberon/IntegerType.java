package org.isu.oberon;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class IntegerType extends NumberType {
    IntegerType(){
        super("INTEGER");
    }

    public Value infixOp(Context s, Value arg1, int op, Value arg2)
    {
        LLVMValueRef res;
        System.out.println(arg1.ref);
        System.out.println(arg2.ref);

        switch (op) {
            case org.isu.oberon.OberonParser.PLUS:
                res = LLVMBuildAdd(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.MINUS:
                res = LLVMBuildSub(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.MUL:
                res = LLVMBuildMul(s.builder, arg1.ref, arg2.ref, "");
                break;
            case org.isu.oberon.OberonParser.DIV:
                res = LLVMBuildSDiv(s.builder, arg1.ref, arg2.ref, ""); // FIXME: Signed op.
                break;
            default:
                System.out.println("Wrong integer operation!");
                System.exit(1);
                return null;
        }
        return new Value(this, res); // Should not get here
    }

    @Override
    protected LLVMValueRef genConstant(Context c, String val) {
        return LLVMConstIntOfString(genRef(),
                val, (byte) 10);
    }

    @Override
    protected LLVMTypeRef genRef() {
        return LLVMInt64Type();
    }
}
