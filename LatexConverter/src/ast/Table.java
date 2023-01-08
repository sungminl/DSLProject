package ast;

import java.io.PrintWriter;
import java.util.List;

public class Table extends NonDefinableCallable {
    private final int rows;
    private final int cols;
    private final TopHeader rowHeader;

    private final LeftHeader colHeader;
    private final List<Row> rowList;

    public Table(int rows, int cols, List<Row> rowList) {
        this.rows = rows;
        this.cols = cols;
        this.rowHeader = null;
        this.colHeader = null;
        this.rowList = rowList;
    }

    public Table(int rows, int cols, LeftHeader colHeader, List<Row> rowList) {
        this.rows = rows;
        this.cols = cols;
        this.rowHeader = null;
        this.colHeader = colHeader;
        this.rowList = rowList;
    }

    public Table(int rows, int cols, TopHeader rowHeader, List<Row> rowList) {
        this.rows = rows;
        this.cols = cols;
        this.rowHeader = rowHeader;
        this.colHeader = null;
        this.rowList = rowList;
    }

    public Table(int rows, int cols, TopHeader rowHeader, LeftHeader colHeader, List<Row> rowList) {
        this.rows = rows;
        this.cols = cols;
        this.rowHeader = rowHeader;
        this.colHeader = colHeader;
        this.rowList = rowList;
    }

    public TopHeader getTopHeader() {
        return rowHeader;
    }

    public LeftHeader getLeftHeader() {
        return colHeader;
    }

    public List<Row> getRowList() {
        return rowList;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
