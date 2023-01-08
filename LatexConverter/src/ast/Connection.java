package ast;

import libs.Node;

import java.io.PrintWriter;

public class Connection extends Node {
    private final String nameFrom;
    private final ConnectionType connectionType;
    private final String label;
    private final String nameTo;

    public enum ConnectionType {
        LEFT_ARROW, BIDIRECTIONAL, RIGHT_ARROW, UNDIRECTED
    }

    public Connection(String nameFrom, ConnectionType connectionType, String nameTo) {
        this.nameFrom = nameFrom;
        this.connectionType = connectionType;
        this.label = null;
        this.nameTo = nameTo;
    }

    public Connection(String nameFrom, ConnectionType connectionType, String nameTo, String label) {
        this.nameFrom = nameFrom;
        this.connectionType = connectionType;
        this.nameTo = nameTo;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getNameTo() {
        return nameTo;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
