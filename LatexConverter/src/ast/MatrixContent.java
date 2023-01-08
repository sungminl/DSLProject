package ast;

import libs.Node;

import java.io.PrintWriter;
import java.util.List;

public class MatrixContent extends Node {
    private final List<MatRow> matRowList;
    private final Equation equation;
    private int rows;
    private int cols;

    public MatrixContent(List<MatRow> matRowList) {
        this.matRowList = matRowList;
        this.equation = null;
    }

    public MatrixContent(Equation equation) {
        this.matRowList = null;
        this.equation = equation;
    }

    public List<MatRow> getMatRowList() {
        return matRowList;
    }

    public Equation getEquation() {
        return equation;
    }

    public void setNumberOfCols(int entries){
        if(matRowList != null){
            for(MatRow m: matRowList){
                m.setNumberOfCols(entries);
            }
        }
        cols = entries;
    }

    public void setNumberOfRows(int entries) {
        rows = entries;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
