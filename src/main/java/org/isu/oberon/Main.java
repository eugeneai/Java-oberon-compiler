package org.isu.oberon;

import org.antlr.v4.runtime.ANTLRInputStream;

import java.io.FileInputStream;

public class Main {
    public static void main(String[] argv ) {
        try {
            ANTLRInputStream input = new ANTLRInputStream(
                    new FileInputStream(args[0]));

            GYOOLexer lexer = new GYOOLexer(input);
            GYOOParser parser = new GYOOParser(new CommonTokenStream(lexer));
            parser.addParseListener(new MyListener());

            // Start parsing
            parser.program();
        } catch (IOException e) {
            e.printStackTrace();
        }    }
}
