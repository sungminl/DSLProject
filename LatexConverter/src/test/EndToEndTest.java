package test;

import org.junit.jupiter.api.Test;
import ui.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class EndToEndTest {
    private boolean runDsl(String fileName) throws IOException {
        return Main.runDsl("test/" + fileName, "test/output/" + fileName);
    }

    private boolean checkFileEquality(String fileName) throws IOException {
        Path expected = Path.of("test/expected/" + fileName + ".tex");
        Path actual = Path.of("test/output/" + fileName + ".tex");
        System.out.println("Files diff at: " + Files.mismatch(expected, actual));
        return Files.mismatch(expected, actual) == -1;
    }

    // VALID PROGRAMS
    @Test
    public void basicClassesTest() throws IOException {
        assertTrue(runDsl("basicClasses"));
        assertTrue(checkFileEquality("basicClasses"));
    }

    @Test
    public void numbersAndMatrices() throws IOException {
        assertTrue(runDsl("numbersAndMatricesTest"));
        assertTrue(checkFileEquality("numbersAndMatricesTest"));
    }

    @Test
    public void functionTest() throws IOException {
        assertTrue(runDsl("functionTest"));
        assertTrue(checkFileEquality("functionTest"));
    }

    @Test
    public void loopTest() throws IOException {
        assertTrue(runDsl("loopTest"));
        assertTrue(checkFileEquality("loopTest"));
    }

    // SYNTAX ERRORS
    @Test
    public void tableIncorrectSyntax() throws IOException {
        assertFalse(runDsl("tableIncorrectSyntax"));
    }

    // DYNAMIC CHECK ERRORS
    @Test
    public void matrixIncorrectNumRows() throws IOException {
        assertFalse(runDsl("matrixIncorrectNumRows"));
        assertTrue(checkFileEquality("matrixIncorrectNumRows"));
    }

    @Test
    public void matrixIncorrectNumCols() throws IOException {
        assertFalse(runDsl("matrixIncorrectNumCols"));
        assertTrue(checkFileEquality("matrixIncorrectNumCols"));
    }

    @Test
    public void loopInvalidEndBound() throws IOException {
        assertFalse(runDsl("loopInvalidEndBound"));
        assertTrue(checkFileEquality("loopInvalidEndBound"));
    }

    // STATIC CHECK ERRORS
    @Test
    public void matrixVariableNotInParameters() throws IOException {
        assertFalse(runDsl("matrixVariableNotInParameters"));
    }

    @Test
    public void functionNotDefined() throws IOException {
        assertFalse(runDsl("functionNotDefined"));
    }

    @Test
    public void graphConnectionNodeNotDefined() throws IOException {
        assertFalse(runDsl("graphConnectionNodeNotDefined"));
    }

    @Test
    public void tableTooManyHeaderItems() throws IOException {
        assertFalse(runDsl("tableTooManyHeaderItems"));
    }
}
