package ast;

import libs.Node;

import java.io.PrintWriter;
import java.util.List;

public class Row extends Node {
    private final List<RowContent> rowContentList;
    private int numberOfCols;

    public Row(List<RowContent> rowContentList) {
        this.rowContentList = rowContentList;
    }

    public List<RowContent> getRowContentList() {
        return rowContentList;
    }


    public int getNumberOfCols(){return numberOfCols;}

    public void setNumberOfCols(int numberOfCols){this.numberOfCols = numberOfCols;}
    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
