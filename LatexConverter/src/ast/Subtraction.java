package ast;

import java.io.PrintWriter;

public class Subtraction extends RestEquation {
    private final Equation equation;

    public Subtraction(Equation equation) {
        this.equation = equation;
    }

    public Equation getEquation() {
        return equation;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }

    @Override
    public Equation.EquationType setType() {
        return Equation.EquationType.SUBTRACT;
    }
}
