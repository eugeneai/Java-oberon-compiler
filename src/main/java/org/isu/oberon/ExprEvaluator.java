package org.isu.oberon;

public class ExprEvaluator {
    public static int interp(int arg1, int op, int arg2)
    {
        switch (op) {
            case org.isu.oberon.ExprParser.PLUS:
                return arg1 + arg2;

            case org.isu.oberon.ExprParser.MINUS:
                return arg1 - arg2;

            case org.isu.oberon.ExprParser.MUL:
                return arg1 * arg2;

            case org.isu.oberon.ExprParser.DIV:
                return arg1 / arg2;

            default:
                System.out.println("Wrong Operation!");
                return 0;
        }
    }
}
