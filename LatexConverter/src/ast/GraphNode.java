package ast;

import libs.Node;

public class GraphNode extends Node {
    private final String name;
    private final boolean accept;
    private final boolean start;

    public GraphNode(String name, boolean accept, boolean start) {
        this.name = name;
        this.accept = accept;
        this.start = start;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean isStart() {
        return start;
    }
}
