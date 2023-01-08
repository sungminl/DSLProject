package ast;

import java.io.PrintWriter;
import java.util.Objects;

public class MutableVariable extends TerminalExpression {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableVariable that = (MutableVariable) o;
        return Objects.equals(mutableVariable, that.mutableVariable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mutableVariable);
    }

    private final String mutableVariable;

    public MutableVariable(String mutableVariable) {
        this.mutableVariable = mutableVariable;
    }

    public String getMutableVariable() {
        return mutableVariable;
    }

    @Override
    public <T, U> U accept(LatexConverterVisitor<T, U> v, T param) {
        return v.visit(this, param);
    }
}
