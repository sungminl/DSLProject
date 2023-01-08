package ast;

import libs.Node;


public class Equation extends TerminalExpression {
    private final TerminalExpression terminalExpression;
    private final RestEquation restEquation;
    private final boolean negate;

    public EquationType getType() {
        return type;
    }

    private EquationType type = EquationType.NONE;

    public enum EquationType {
        ADD, NONE, SUBTRACT, POWER, MULTIPLY, DIVISION
    }

    public Equation(TerminalExpression terminalExpression, boolean negate) {
        this.terminalExpression = terminalExpression;
        this.restEquation = null;
        this.negate = negate;
    }

    public Equation(TerminalExpression terminalExpression, RestEquation restEquation, boolean negate) {
        this.terminalExpression = terminalExpression;
        this.restEquation = restEquation;
        this.negate = negate;
    }

    public void setEquationType(EquationType type) {
        this.type = type;
    }

    public TerminalExpression getTerminalExpression() {
        return terminalExpression;
    }

    public RestEquation getRestEquation() {
        return restEquation;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }

    public boolean getNegate() {
        return negate;
    }
}

