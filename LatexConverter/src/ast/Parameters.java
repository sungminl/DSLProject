package ast;

import libs.Node;

import java.io.PrintWriter;
import java.util.List;

public class Parameters extends Node {
    private final List<MutableVariable> mutableVariableList;

    public Parameters(List<MutableVariable> mutableVariableList) {
        this.mutableVariableList = mutableVariableList;
    }

    public List<MutableVariable> getMutableVariableList() {
        return mutableVariableList;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
