package ast;

import java.io.PrintWriter;

public class Num extends TerminalExpression {
    private final Double num;

    public Num(Double num) {
        this.num = num;
    }

    public Double getNum() {
        return num;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
