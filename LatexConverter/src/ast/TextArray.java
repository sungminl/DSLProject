package ast;

import libs.Node;

import java.util.List;

public class TextArray extends Node {
    private final List<QuotedText> texts;

    public TextArray(List<QuotedText> texts) {
        this.texts = texts;
    }

    public List<QuotedText> getTexts() {
        return texts;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
