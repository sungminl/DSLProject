package ast;

import java.io.PrintWriter;
import java.util.List;

public class FunctionDefinition extends Callable {
    private final String functionName;
    private final Parameters parameters;
    private final List<NonDefinableCallable> nonDefinableCallableList;

    public FunctionDefinition(String functionName, List<NonDefinableCallable> nonDefinableCallableList) {
        this.functionName = functionName;
        this.parameters = null;
        this.nonDefinableCallableList = nonDefinableCallableList;
    }

    public FunctionDefinition(String functionName, Parameters parameters, List<NonDefinableCallable> nonDefinableCallableList) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.nonDefinableCallableList = nonDefinableCallableList;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public List<NonDefinableCallable> getNonDefinableCallableList() {
        return nonDefinableCallableList;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
