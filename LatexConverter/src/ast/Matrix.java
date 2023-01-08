package ast;

import java.io.PrintWriter;

public class Matrix extends NonDefinableCallable {
    private final int rows;
    private final int cols;
    private final Parameters parameters;
    private final MatrixContent matrixContent;

    public Matrix(int rows, int cols, MatrixContent matrixContent) {
        this.rows = rows;
        this.cols = cols;
        this.parameters = null;
        this.matrixContent = matrixContent;
    }

    public Matrix(int rows, int cols, Parameters parameters, MatrixContent matrixContent) {
        this.rows = rows;
        this.cols = cols;
        this.parameters = parameters;
        this.matrixContent = matrixContent;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public MatrixContent getMatrixContent() {
        return matrixContent;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
