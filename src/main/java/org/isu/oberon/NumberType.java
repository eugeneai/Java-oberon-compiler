package org.isu.oberon;

public abstract class NumberType extends TypeSymbol {
    public NumberType(String name) {
        super(name);
    }
    public abstract ArithValue infixOp(Context s,
                                     ArithValue arg1,
                                     int op,
                                     ArithValue arg2);
}
