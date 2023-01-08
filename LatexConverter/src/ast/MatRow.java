package ast;

import libs.Node;

import java.util.List;

public class MatRow extends Node {
    private final List<Equation> equationList;
    private int numberOfCols;

    public MatRow(List<Equation> equationList) {
        this.equationList = equationList;
    }

    public List<Equation> getEquationList() {
        return equationList;
    }

    public int getNumberOfCols(){return numberOfCols;}

    public void setNumberOfCols(int numberOfCols){this.numberOfCols = numberOfCols;}
    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
