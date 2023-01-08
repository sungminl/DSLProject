package test;

import ast.*;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.junit.jupiter.api.Test;
import parser.LatexConverterLexer;
import parser.LatexConverterParser;
import parser.ParseTreeToAST;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ASTConversionTest {
    private Program convertAST(String input) {
        LatexConverterLexer lexer = null;
        try{
            lexer = new LatexConverterLexer(CharStreams.fromFileName("test/" + input));
        } catch (IOException e) {
            System.out.println("Test file doesnt work");
            fail();
        }

        TokenStream tokens = new CommonTokenStream(lexer);
        System.out.println("Done tokenizing");

        LatexConverterParser parser = new LatexConverterParser(tokens);
        ParseTreeToAST visitor = new ParseTreeToAST();
        LatexConverterParser.ProgramContext p = parser.program();
        System.out.println("Done parsing");
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        Program parsedProgram = visitor.visitProgram(p);
        System.out.println("Done Conversion");
        return parsedProgram;
    }

    @Test
    public void TEST_BASIC_CLASSES()
    {
        Program program = convertAST("basicClasses.txt");
        assertEquals(10, program.getCallableList().size());
        TextEntry text = (TextEntry) program.getCallableList().get(0);
        assertEquals("abcdefg this is my text entry", text.getQuotedText().getText());
        assertEquals(4, text.getTextStyleSettings().getTextStyleList().size());
        assertEquals(TextStyleSettings.TextSetting.BOLD,text.getTextStyleSettings().getTextStyleList().get(0));
        assertEquals(TextStyleSettings.TextSetting.HEADING,text.getTextStyleSettings().getTextStyleList().get(1));
        assertEquals(TextStyleSettings.TextSetting.UNDERLINE,text.getTextStyleSettings().getTextStyleList().get(2));
        assertEquals(TextStyleSettings.TextSetting.ITALICS,text.getTextStyleSettings().getTextStyleList().get(3));

        MVAssignment mvAssignment = (MVAssignment) program.getCallableList().get(1);
        assertEquals(new MutableVariable("&a"), mvAssignment.getFirstMutableVariable());
        assertEquals(3.4, ((Num)mvAssignment.getEquation().getTerminalExpression()).getNum());

        mvAssignment = (MVAssignment) program.getCallableList().get(2);
        assertEquals(new MutableVariable("&x"), mvAssignment.getFirstMutableVariable());
        assertEquals("a", ((Symbol)mvAssignment.getEquation().getTerminalExpression()).getSymbol());
        assertEquals(42.0, ((Num)((Add)mvAssignment.getEquation().getRestEquation()).getEquation().getTerminalExpression()).getNum());

        mvAssignment = (MVAssignment) program.getCallableList().get(3);
        assertEquals(new MutableVariable("&b"), mvAssignment.getFirstMutableVariable());
        assertEquals("abc", mvAssignment.getQuotedText().getText());

        mvAssignment = (MVAssignment) program.getCallableList().get(4);
        assertEquals(new MutableVariable("&c"), mvAssignment.getFirstMutableVariable());
        NumArray array = (NumArray) mvAssignment.getNumArray();
        assertEquals(1, ((Num) array.getEquations().get(0).getTerminalExpression()).getNum());
        assertEquals(2, ((Num) array.getEquations().get(1).getTerminalExpression()).getNum());
        assertEquals(3, ((Num) array.getEquations().get(2).getTerminalExpression()).getNum());

        mvAssignment = (MVAssignment) program.getCallableList().get(5);
        assertEquals(new MutableVariable("&d"), mvAssignment.getFirstMutableVariable());
        TextArray textarray = (TextArray) mvAssignment.getTextArray();
        assertEquals("a", textarray.getTexts().get(0).getText());
        assertEquals("b", textarray.getTexts().get(1).getText());
        assertEquals("c", textarray.getTexts().get(2).getText());

        Graph graph = (Graph) program.getCallableList().get(6);
        assertEquals(2 ,graph.getGraphNodeList().size());
        assertEquals("Node1", graph.getGraphNodeList().get(0).getName());
        assertTrue(graph.getGraphNodeList().get(0).isStart());
        assertEquals("Node2", graph.getGraphNodeList().get(1).getName());
        assertTrue(graph.getGraphNodeList().get(1).isAccept());
        assertEquals(1, graph.getConnections().size());
        assertEquals(Connection.ConnectionType.RIGHT_ARROW, graph.getConnections().get(0).getConnectionType());
        assertEquals("text", graph.getConnections().get(0).getLabel());
        assertEquals("Node1", graph.getConnections().get(0).getNameFrom());
        assertEquals("Node2", graph.getConnections().get(0).getNameTo());

        Table table = (Table) program.getCallableList().get(7);
        assertEquals(3, table.getRows());
        assertEquals(3, table.getCols());
        assertEquals("T1", table.getTopHeader().getQuotedTextList().get(0).getText());
        assertEquals("T2", table.getTopHeader().getQuotedTextList().get(1).getText());
        assertEquals("T3", table.getTopHeader().getQuotedTextList().get(2).getText());
        assertEquals("L1", table.getLeftHeader().getQuotedTextList().get(0).getText());
        assertEquals("L2", table.getLeftHeader().getQuotedTextList().get(1).getText());
        assertEquals("L3", table.getLeftHeader().getQuotedTextList().get(2).getText());
        assertEquals("a", table.getRowList().get(0).getRowContentList().get(0).getQuotedText().getText());
        assertEquals("&b", table.getRowList().get(0).getRowContentList().get(1).getMutableVariable().getMutableVariable());
        assertEquals("c", table.getRowList().get(0).getRowContentList().get(2).getQuotedText().getText());
        assertEquals("a", table.getRowList().get(1).getRowContentList().get(0).getQuotedText().getText());
        assertEquals("b", table.getRowList().get(1).getRowContentList().get(1).getQuotedText().getText());
        assertEquals("c", table.getRowList().get(1).getRowContentList().get(2).getQuotedText().getText());
        assertEquals("a", table.getRowList().get(1).getRowContentList().get(0).getQuotedText().getText());
        assertEquals("b", table.getRowList().get(1).getRowContentList().get(1).getQuotedText().getText());
        assertEquals("c", table.getRowList().get(1).getRowContentList().get(2).getQuotedText().getText());

        Matrix matrix = (Matrix) program.getCallableList().get(8);
        assertEquals(3, matrix.getRows());
        assertEquals(3, matrix.getCols());
        MatRow matRow = matrix.getMatrixContent().getMatRowList().get(0);
        assertEquals(3, matRow.getEquationList().size());
        assertEquals(2, ((Num)matRow.getEquationList().get(0).getTerminalExpression()).getNum());
        assertEquals(2.54, ((Num)matRow.getEquationList().get(1).getTerminalExpression()).getNum());
        assertEquals(5, ((Num)((Equation)matRow.getEquationList().get(2).getTerminalExpression()).getTerminalExpression()).getNum());
        assertTrue(matRow.getEquationList().get(2).getNegate());

        matRow = matrix.getMatrixContent().getMatRowList().get(1);
        assertEquals(3, matRow.getEquationList().size());
        assertEquals("&a", ((MutableVariable)matRow.getEquationList().get(0).getTerminalExpression()).getMutableVariable());
        assertEquals("&a", ((MutableVariable)matRow.getEquationList().get(1).getTerminalExpression()).getMutableVariable());
        assertEquals(1, ((Num)((Add)matRow.getEquationList().get(1).getRestEquation()).getEquation().getTerminalExpression()).getNum());
        assertEquals(5, ((Num)matRow.getEquationList().get(2).getTerminalExpression()).getNum());

        matRow = matrix.getMatrixContent().getMatRowList().get(2);
        assertEquals(3, matRow.getEquationList().size());
        assertEquals(5, ((Num)matRow.getEquationList().get(0).getTerminalExpression()).getNum());
        Equation eq = ((Equation )((Power) matRow.getEquationList().get(0).getRestEquation()).getEquation().getTerminalExpression());
        assertEquals(42, ((Num)eq.getTerminalExpression()).getNum());
        assertEquals("x", ((Symbol)((Add)eq.getRestEquation()).getEquation().getTerminalExpression()).getSymbol());
        assertEquals(5, ((Num)((Log)matRow.getEquationList().get(1).getTerminalExpression()).getEquation().getTerminalExpression()).getNum());
        assertEquals("a", ((Symbol)matRow.getEquationList().get(2).getTerminalExpression()).getSymbol());
        assertEquals(2, ((Num)((Multiplication)matRow.getEquationList().get(2).getRestEquation()).getEquation().getTerminalExpression()).getNum());
    }

    @Test
    public void TEST_FUNCTIONS()
    {
        Program program = convertAST("functionTest.txt");
        FunctionDefinition funcDef = (FunctionDefinition) program.getCallableList().get(2);
        assertEquals("print_Text", funcDef.getFunctionName());
        assertEquals("&a",funcDef.getParameters().getMutableVariableList().get(0).getMutableVariable());
        assertEquals("&b",funcDef.getParameters().getMutableVariableList().get(1).getMutableVariable());

        assertEquals(3, funcDef.getNonDefinableCallableList().size());

        MVAssignment mvAssignment = (MVAssignment) funcDef.getNonDefinableCallableList().get(0);
        assertEquals("&c", mvAssignment.getFirstMutableVariable().getMutableVariable());
        assertEquals(1, ((Num) mvAssignment.getNumArray().getEquations().get(0).getTerminalExpression()).getNum());
        assertEquals(2, ((Num) mvAssignment.getNumArray().getEquations().get(1).getTerminalExpression()).getNum());
        assertEquals(3, ((Num) mvAssignment.getNumArray().getEquations().get(2).getTerminalExpression()).getNum());

        TextEntry entry = (TextEntry) funcDef.getNonDefinableCallableList().get(1);
        assertEquals(TextStyleSettings.TextSetting.BOLD, entry.getTextStyleSettings().getTextStyleList().get(0));
        assertEquals(TextStyleSettings.TextSetting.UNDERLINE, entry.getTextStyleSettings().getTextStyleList().get(1));
        assertEquals("print this", entry.getQuotedText().getText());

        Matrix matrix = (Matrix) funcDef.getNonDefinableCallableList().get(2);
        assertEquals(3, matrix.getCols());
        assertEquals(3, matrix.getRows());
        assertEquals(1, matrix.getMatrixContent().getMatRowList().get(0).getEquationList().size());
        assertEquals("&c", ((MutableVariable)matrix.getMatrixContent().getMatRowList().get(0).getEquationList().get(0).getTerminalExpression()).getMutableVariable());

        assertEquals(3, matrix.getMatrixContent().getMatRowList().get(1).getEquationList().size());
        assertEquals("&a",((MutableVariable)matrix.getMatrixContent().getMatRowList().get(1).getEquationList().get(0).getTerminalExpression()).getMutableVariable());
        assertEquals(1, ((Num) matrix.getMatrixContent().getMatRowList().get(1).getEquationList().get(1).getTerminalExpression()).getNum());
        assertEquals("&b",((MutableVariable)matrix.getMatrixContent().getMatRowList().get(1).getEquationList().get(2).getTerminalExpression()).getMutableVariable());

        assertEquals(1, matrix.getMatrixContent().getMatRowList().get(2).getEquationList().size());
        assertEquals("&c", ((MutableVariable)matrix.getMatrixContent().getMatRowList().get(2).getEquationList().get(0).getTerminalExpression()).getMutableVariable());
        assertEquals(3, ((Num)((Multiplication)matrix.getMatrixContent().getMatRowList().get(2).getEquationList().get(0).getRestEquation()).getEquation().getTerminalExpression()).getNum());

        FunctionCall call = (FunctionCall) program.getCallableList().get(3);
        assertEquals("print_Text", call.getFunctionName());
        assertEquals(2, call.getParameters().getMutableVariableList().size());
        assertEquals("&a", call.getParameters().getMutableVariableList().get(0).getMutableVariable());
        assertEquals("&b", call.getParameters().getMutableVariableList().get(1).getMutableVariable());
    }

    @Test
    public void TEST_LOOPS()
    {
        Program program = convertAST("loopTest.txt");
        Loop loop = (Loop) program.getCallableList().get(1);
        assertEquals("&a", loop.getMutableVariable().getMutableVariable());
        assertEquals(1, ((Num)loop.getExpressionFrom().getTerminalExpression()).getNum());
        assertEquals(1, ((Num)((Subtraction) loop.getExpressionFrom().getRestEquation()).getEquation().getTerminalExpression()).getNum());
        assertEquals(5, ((Num)loop.getExpressionTo().getTerminalExpression()).getNum());

        assertEquals(1, loop.getNonDefinableCallableList().size());
        Matrix m = (Matrix) loop.getNonDefinableCallableList().get(0);
        assertEquals(2, m.getRows());
        assertEquals(4, m.getCols());
        assertEquals(2, ((Num)m.getMatrixContent().getMatRowList().get(0).getEquationList().get(0).getTerminalExpression()).getNum());
        assertEquals("&a", ((MutableVariable)((Multiplication)m.getMatrixContent().getMatRowList().get(0).getEquationList().get(0).getRestEquation()).getEquation().getTerminalExpression()).getMutableVariable());
        assertEquals("&a", ((MutableVariable) m.getMatrixContent().getMatRowList().get(1).getEquationList().get(0).getTerminalExpression()).getMutableVariable());
        assertEquals(2, ((Num)((Division)m.getMatrixContent().getMatRowList().get(1).getEquationList().get(0).getRestEquation()).getEquation().getTerminalExpression()).getNum());
    }
}
