package test;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;
import parser.LatexConverterLexer;
import parser.LatexConverterParser;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class LexerParserTest {
    private void runTest(String fileName, int numFailedErrors) throws IOException {
        LatexConverterLexer lexer = new LatexConverterLexer(CharStreams.fromFileName("test/" + fileName));
        lexer.getAllTokens();
        lexer.reset();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        System.out.println("Done tokenizing");

        LatexConverterParser parser = new LatexConverterParser(tokens);
        LatexConverterParser.ProgramContext program = parser.program();
        assertEquals(numFailedErrors, parser.getNumberOfSyntaxErrors());
        System.out.println("Done parsing");
    }

    @Test
    public void basicClassesTest() throws IOException {
        runTest("basicClasses.txt", 0);
    }

    @Test
    public void equationsAndMatrices() throws IOException {
        runTest("numbersAndMatricesTest.txt", 0);
    }

    @Test
    public void functionTest() throws IOException {
        runTest("functionTest.txt", 0);
    }

    @Test
    public void loopTest() throws IOException {
        runTest("loopTest.txt", 0);
    }

    @Test
    public void failDeclarationTest() throws IOException {
        runTest("functionAndLoopDeclarationFailTest.txt", 5);
    }

    @Test
    public void failMixedTypes() throws IOException {
        runTest("mixedTypesFailTest.txt", 6);
    }
}
