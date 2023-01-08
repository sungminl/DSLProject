package ast;

import java.io.PrintWriter;

public class TextEntry extends NonDefinableCallable {
    private final TextStyleSettings textStyleSettings;
    private final QuotedText quotedText;

    public TextEntry(QuotedText quotedText) {
        this.textStyleSettings = null;
        this.quotedText = quotedText;
    }

    public TextEntry(TextStyleSettings textStyleSettings, QuotedText quotedText) {
        this.textStyleSettings = textStyleSettings;
        this.quotedText = quotedText;
    }

    public TextStyleSettings getTextStyleSettings() {
        return textStyleSettings;
    }

    public QuotedText getQuotedText() {
        return quotedText;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
