package ast;

import libs.Node;

import java.io.PrintWriter;
import java.util.List;

public class TextStyleSettings extends Node {
    public enum TextSetting {
        BOLD, HEADING, ITALICS, UNDERLINE
    }

    private final List<TextSetting> textSettings;

    public TextStyleSettings(List<TextSetting> textSettings) {
        this.textSettings = textSettings;
    }

    public List<TextSetting> getTextStyleList() {
        return textSettings;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
