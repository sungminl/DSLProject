package ui;

import ast.Program;
import ast.checkers.StaticCheck;
import ast.evaluators.Evaluator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import parser.LatexConverterLexer;
import parser.LatexConverterParser;
import parser.ParseTreeToAST;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static boolean runDsl(String inputFile, String outputFile) throws IOException {
        LatexConverterLexer lexer = new LatexConverterLexer(CharStreams.fromFileName(inputFile + ".txt"));
        for (Token token : lexer.getAllTokens()) {
            System.out.println(token);
        }
        lexer.reset();
        TokenStream tokens = new CommonTokenStream(lexer);
        System.out.println("Done tokenizing.");

        LatexConverterParser parser = new LatexConverterParser(tokens);
        ParseTreeToAST visitor = new ParseTreeToAST();
        LatexConverterParser.ProgramContext program = parser.program();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.out.println("Syntax error occurred. Refer to above outputs for more details.");
            return false;
        }
        Program parsedProgram = visitor.visitProgram(program);
        System.out.println("Done parsing.");

        StringBuilder staticCheckErrors = new StringBuilder();
        StaticCheck sc = new StaticCheck();
        parsedProgram.accept(sc, staticCheckErrors);
        if (!staticCheckErrors.isEmpty()) {
            System.out.println("Static Check Error(s)");
            System.out.println(staticCheckErrors);
            System.out.println("End of Static Check Error(s)");
            return false;
        }

        StringBuilder sb = new StringBuilder();
        Evaluator e = new Evaluator(sb);
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile + ".tex"));
        parsedProgram.accept(e, writer);
        writer.close();
        System.out.println("Done evaluating.");
        if (sb.isEmpty()) {
            System.out.println("Evaluation completed successfully.");
        } else {
            System.out.println("Runtime error occurred:\n" + sb);
            PrintWriter errorWriter = new PrintWriter(new FileWriter(outputFile + ".tex"));
            errorWriter.println("The program could not successfully complete.");
            errorWriter.println("A runtime error was encountered:");
            errorWriter.println(sb);
            errorWriter.close();
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        boolean runStatus = runDsl("input", "output");
        if (!runStatus) {
            return;
        }

        try {
            String path = System.getProperty("user.dir");
            ProcessBuilder pb = new ProcessBuilder("pdflatex", path + "/output.tex").inheritIO().directory(new File(path));
            Process process = pb.start();
            process.waitFor();
        } catch (InterruptedException ex) {
            System.out.println("Error converting to PDF.");
        } catch (IOException ex) {
            System.out.println("Missing Latex compiler installation. Skipping PDF conversion.");
        }
    }
}
