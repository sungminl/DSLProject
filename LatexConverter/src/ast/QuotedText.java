package ast;

import libs.Node;

public class QuotedText extends Node {
    private final String text;

    public QuotedText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
