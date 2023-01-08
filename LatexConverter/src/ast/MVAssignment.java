package ast;

import java.io.PrintWriter;

public class MVAssignment extends NonDefinableCallable {
    private final MutableVariable  firstMutableVariable;
    private final Equation equation;
    private final QuotedText quotedText;
    private final TextArray textArray;
    private final NumArray numArray;
    private final MutableVariable  secondMutableVariable;

    public MVAssignment (MutableVariable firstMutableVariable, Equation equation) {
        this.firstMutableVariable = firstMutableVariable;
        this.equation = equation;
        this.quotedText = null;
        this.textArray = null;
        this.numArray = null;
        this.secondMutableVariable = null;
    }
    public MVAssignment (MutableVariable firstMutableVariable, QuotedText quotedText) {
        this.firstMutableVariable = firstMutableVariable;
        this.equation = null;
        this.quotedText = quotedText;
        this.textArray = null;
        this.numArray = null;
        this.secondMutableVariable = null;
    }
    public MVAssignment (MutableVariable firstMutableVariable, TextArray textArray) {
        this.firstMutableVariable = firstMutableVariable;
        this.equation = null;
        this.quotedText = null;
        this.textArray = textArray;
        this.numArray = null;
        this.secondMutableVariable = null;
    }
    public MVAssignment (MutableVariable firstMutableVariable, NumArray numArray) {
        this.firstMutableVariable = firstMutableVariable;
        this.equation = null;
        this.quotedText = null;
        this.textArray = null;
        this.numArray = numArray;
        this.secondMutableVariable = null;
    }
    public MVAssignment (MutableVariable firstMutableVariable, MutableVariable secondMutableVariable) {
        this.firstMutableVariable = firstMutableVariable;
        this.equation = null;
        this.quotedText = null;
        this.textArray = null;
        this.numArray = null;
        this.secondMutableVariable = secondMutableVariable;
    }

    public MutableVariable getFirstMutableVariable() {
        return firstMutableVariable;
    }

    public Equation getEquation() {
        return equation;
    }

    public QuotedText getQuotedText() {
        return quotedText;
    }

    public TextArray getTextArray() {
        return textArray;
    }

    public NumArray getNumArray() {
        return numArray;
    }

    public MutableVariable getSecondMutableVariable() {
        return secondMutableVariable;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
