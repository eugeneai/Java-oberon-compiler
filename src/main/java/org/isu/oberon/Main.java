package org.isu.oberon;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;

import java.io.FileInputStream;
import java.io.IOException;

import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.LLVM.*;

public class Main {
    public static void main(String[] args ) {
        // Show where we are.
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        BytePointer error = new BytePointer((Pointer)null); // Used to retrieve messages from functions
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeDisassembler();
        LLVMInitializeNativeTarget();

        for(String fileName: args) {
            try {
                System.out.println(String.format("Processing file: '%s': ", fileName));
                CharStream input = CharStreams.fromFileName(fileName);

                org.isu.oberon.ExprLexer lexer = new org.isu.oberon.ExprLexer(input);
                org.isu.oberon.ExprParser parser = new org.isu.oberon.ExprParser(new CommonTokenStream(lexer));
                // parser.addParseListener(new MyListener());

                // Start parsing
                EvalStruct s = parser.program().s;
                //LLVMVerifyModule(mod, LLVMAbortProcessAction, error);
                //LLVMDisposeMessage(error); // Handler == LLVMAbortProcessAction -> No need to check errors

                System.out.println("\n-------------------- dump ------------");
                LLVMDumpModule(s.mod);
                System.out.println("\n-------------------- exec ------------");

                LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
                if(LLVMCreateJITCompilerForModule(engine, s.mod, 2, error) != 0) {
                    System.err.println(error.getString());
                    LLVMDisposeMessage(error);
                    System.exit(-1);
                }

                LLVMPassManagerRef pass = LLVMCreatePassManager();
                LLVMAddConstantPropagationPass(pass);
                LLVMAddInstructionCombiningPass(pass);
                LLVMAddPromoteMemoryToRegisterPass(pass);
                // LLVMAddDemoteMemoryToRegisterPass(pass); // Demotes every possible value to memory
                LLVMAddGVNPass(pass);
                LLVMAddCFGSimplificationPass(pass);
                LLVMRunPassManager(pass, s.mod);
                LLVMDumpModule(s.mod);

                LLVMGenericValueRef exec_args = LLVMCreateGenericValueOfInt(LLVMInt64Type(), 10, 0);
                LLVMGenericValueRef exec_res = LLVMRunFunction(engine, s.expr, 0, exec_args);
                System.out.println(" ----> Result: " + LLVMGenericValueToInt(exec_res, 0));

                LLVMDisposePassManager(pass);
                LLVMDisposeExecutionEngine(engine);
                LLVMDisposeBuilder(s.builder);


                System.out.println("\n--------------------------------------");
                System.out.println("Translation: [SUCCCESS]");
            } catch (IOException e) {
                System.out.println("[FAILURE]");
                e.printStackTrace();
            }
        }
        System.out.flush();
    }
}
