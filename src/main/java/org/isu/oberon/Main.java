package org.isu.oberon;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args ) {
        // Show where we are.
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        try {

            ANTLRInputStream input = new ANTLRInputStream(
                    new FileInputStream(args[0]));

            ExprLexer lexer = new ExprLexer(input);
            ExprParser parser = new ExprParser(new CommonTokenStream(lexer));
            // parser.addParseListener(new MyListener());


            // Start parsing
            parser.expression();

        } catch (IOException e) {
            e.printStackTrace();
        }
        CompileLLVM.Test();
    }
}
