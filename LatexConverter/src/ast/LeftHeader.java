package ast;

import libs.Node;

import java.io.PrintWriter;
import java.util.List;

public class LeftHeader extends Node {
    private final List<QuotedText> quotedTextList;

    public LeftHeader(List<QuotedText> quotedTextList) {
        this.quotedTextList = quotedTextList;
    }

    public List<QuotedText> getQuotedTextList() {
        return quotedTextList;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
