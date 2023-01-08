package ast;

import libs.Node;

import java.io.PrintWriter;
import java.util.List;

public class Program extends Node {
    private final List<Callable> callableList;

    public Program(List<Callable> callableList) {
        this.callableList = callableList;
    }

    public List<Callable> getCallableList() {
        return callableList;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
