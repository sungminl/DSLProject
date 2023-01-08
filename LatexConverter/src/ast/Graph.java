package ast;

import java.io.PrintWriter;
import java.util.List;

public class Graph extends NonDefinableCallable {
    private final List<GraphNode> graphNodeList;
    private final List<Connection> connections;

    public Graph(List<GraphNode> graphNodeList, List<Connection> connections) {
        this.graphNodeList = graphNodeList;
        this.connections = connections;
    }

    public List<GraphNode> getGraphNodeList() {
        return graphNodeList;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
