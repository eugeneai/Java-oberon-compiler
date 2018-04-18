package org.isu.oberon;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;

import java.io.IOException;

import static org.bytedeco.javacpp.LLVM.*;

public class Main {
    public static void main(String[] args ) {
        // Show where we are.
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        Context.initializeTypeTable();
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

                org.isu.oberon.OberonLexer lexer = new org.isu.oberon.OberonLexer(input);
                org.isu.oberon.OberonParser parser = new org.isu.oberon.OberonParser(new CommonTokenStream(lexer));
                // parser.addParseListener(new MyListener());

                // Start parsing
                Context s = parser.module(parser).s;
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

                LLVMGenericValueRef exec_args = LLVMCreateGenericValueOfInt(LLVMInt64Type(), 10, 0);
                LLVMGenericValueRef exec_res = LLVMRunFunction(engine, s.func, 0, exec_args);
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
