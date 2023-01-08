package libs;

import ast.LatexConverterVisitor;

import java.io.*;

public abstract class Node {
    abstract public <T, U> U accept(LatexConverterVisitor<T, U> v, T param);
}
