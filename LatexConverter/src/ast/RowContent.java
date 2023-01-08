package ast;

import libs.Node;

import java.io.PrintWriter;

public class RowContent extends Node {
    private final QuotedText quotedText;
    private final MutableVariable mutableVariable;

    public RowContent(QuotedText quotedText) {
        this.quotedText = quotedText;
        this.mutableVariable = null;
    }

    public RowContent(MutableVariable mutableVariable) {
        this.quotedText = null;
        this.mutableVariable = mutableVariable;
    }

    public QuotedText getQuotedText() {
        return quotedText;
    }

    public MutableVariable getMutableVariable() {
        return mutableVariable;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
