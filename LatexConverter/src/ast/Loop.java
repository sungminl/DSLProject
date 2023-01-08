package ast;

import java.io.PrintWriter;
import java.util.List;

public class Loop extends NonDefinableCallable {
    private final MutableVariable mutableVariable;
    private final Equation expressionFrom;
    private final Equation expressionTo;
    private final List<NonDefinableCallable> nonDefinableCallableList;

    public Loop(MutableVariable mutableVariable, Equation expressionFrom, Equation expressionTo, List<NonDefinableCallable> nonDefinableCallableList) {
        this.mutableVariable = mutableVariable;
        this.expressionFrom = expressionFrom;
        this.expressionTo = expressionTo;
        this.nonDefinableCallableList = nonDefinableCallableList;
    }

    public MutableVariable getMutableVariable() {
        return mutableVariable;
    }

    public Equation getExpressionFrom() {
        return expressionFrom;
    }

    public Equation getExpressionTo() {
        return expressionTo;
    }

    public List<NonDefinableCallable> getNonDefinableCallableList() {
        return nonDefinableCallableList;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
