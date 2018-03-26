package org.isu.oberon;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args ) {
        // Show where we are.
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        for(String fileName: args) {
            try {
                System.out.println(String.format("Processing file: '%s': ", fileName));
                CharStream input = CharStreams.fromFileName(fileName);

                ExprLexer lexer = new ExprLexer(input);
                ExprParser parser = new ExprParser(new CommonTokenStream(lexer));
                // parser.addParseListener(new MyListener());

                // Start parsing
                parser.program();
                System.out.println("Translation: [SUCCCESS]");
            } catch (IOException e) {
                System.out.println("[FAILURE]");
                e.printStackTrace();
            }
        }
        System.out.flush();
        //CompileLLVM.Test();
    }
}
