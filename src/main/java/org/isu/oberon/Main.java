package org.isu.oberon;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.bytedeco.llvm.global.LLVM.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args ) {
        logger.info("Working Directory is {}", System.getProperty("user.dir"));
        Context.initializeTypeTable();

        BytePointer error = new BytePointer((Pointer)null); // Used to retrieve messages from functions

        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeDisassembler();
        LLVMInitializeNativeTarget();

        for(String fileName: args) {
            try {
                logger.info("Processing file: {}", fileName);
                CharStream input = CharStreams.fromFileName(fileName);

                org.isu.oberon.OberonLexer lexer = new org.isu.oberon.OberonLexer(input);
                org.isu.oberon.OberonParser parser = new org.isu.oberon.OberonParser(new CommonTokenStream(lexer));
                // parser.addParseListener(new MyListener());

                // Start parsing
                Context s = parser.module(parser).s;
                //LLVMVerifyModule(mod, LLVMAbortProcessAction, error);
                //LLVMDisposeMessage(error); // Handler == LLVMAbortProcessAction -> No need to check errors

                logger.info("------------ dump ------------");
                LLVMDumpModule(s.getModule().mod);
                logger.info("------------------------------");
                logger.info("------------ exec ------------");

                /*
                LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
                if (LLVMCreateJITCompilerForModule(engine, s.getModule().mod, 2, error) != 0) {
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
                LLVMRunPassManager(pass, s.getModule().mod);

                // ProcSymbol proc_to_run = s.getProc("@MAIN@");
                ProcSymbol proc_to_run = s.proc;
                LLVMValueRef proc_to_run_ref = proc_to_run.proc;

                LLVMGenericValueRef exec_args = LLVMCreateGenericValueOfInt(LLVMInt64Type(), 10, 0);
                LLVMGenericValueRef exec_res = LLVMRunFunction(engine, proc_to_run_ref, 0, exec_args);
                System.out.println(" ----> Result: " + LLVMGenericValueToInt(exec_res, 0));

                LLVMDisposePassManager(pass);
                LLVMDisposeExecutionEngine(engine);
*/
                LLVMDisposeBuilder(s.builder);

                logger.info("------------------------------");
                logger.info("Translation: [SUCCESS]");
            } catch (IOException e) {
                logger.error("Translation: [FAILURE]");
                e.printStackTrace();
            }
        }
    }
}
