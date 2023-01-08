package ast;

import java.io.PrintWriter;

public class Sin extends TerminalExpression {
    private final Equation equation;

    public Sin(Equation equation) {
        this.equation = equation;
    }

    public Equation getEquation() {
        return equation;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
