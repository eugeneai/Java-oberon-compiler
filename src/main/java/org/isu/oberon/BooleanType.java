package org.isu.oberon;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bytedeco.llvm.global.LLVM.*;

public class BooleanType extends NumberType {
    private static final Logger logger = LoggerFactory.getLogger(BooleanType.class);

    BooleanType(){
        super("BOOLEAN");
    }

    public Value infixOp(Context s, Value arg1, int op, Value arg2)
    {
        LLVMValueRef res;
        switch (op) {
            case org.isu.oberon.OberonParser.EQOP:
                res = LLVMBuildICmp(s.builder, LLVMIntEQ , arg1.ref, arg2.ref, "test_equ");
                break;
            case org.isu.oberon.OberonParser.LEOP:
                res = LLVMBuildICmp(s.builder, LLVMIntSLE , arg1.ref, arg2.ref, "test_sle");
                break;
            case org.isu.oberon.OberonParser.GEOP:
                res = LLVMBuildICmp(s.builder, LLVMIntSGE , arg1.ref, arg2.ref, "test_uge");
                break;
            case org.isu.oberon.OberonParser.NQOP:
                res = LLVMBuildICmp(s.builder, LLVMIntNE , arg1.ref, arg2.ref, "test_neq");
                break;
            case org.isu.oberon.OberonParser.LTOP:
                res = LLVMBuildICmp(s.builder, LLVMIntSLT , arg1.ref, arg2.ref, "test_neq");
                break;
            case org.isu.oberon.OberonParser.GTOP:
                res = LLVMBuildICmp(s.builder, LLVMIntSGT , arg1.ref, arg2.ref, "test_neq");
                break;
            default:
                logger.error("Wrong BOOLEAN equation operation");
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
            logger.error("Wrong BOOLEAN constant");
            System.exit(1);
            return null;
        }
    }

    @Override
    protected LLVMTypeRef genRef() {
        return LLVMInt1Type();
    }
}
