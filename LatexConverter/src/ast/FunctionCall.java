package ast;

import java.io.PrintWriter;

public class FunctionCall extends NonDefinableCallable {
    private final String functionName;
    private final Parameters parameters;

    public FunctionCall(String functionName) {
        this.functionName = functionName;
        this.parameters = null;
    }

    public FunctionCall(String functionName, Parameters parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Parameters getParameters() {
        return parameters;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
