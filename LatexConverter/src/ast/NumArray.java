package ast;

import libs.Node;

import java.util.List;

public class NumArray extends Node {
    private final List<Equation> equations;

    public NumArray(List<Equation> equations) {
        this.equations = equations;
    }

    public List<Equation> getEquations() {
        return equations;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
