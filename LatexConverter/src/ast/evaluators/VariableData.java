package ast.evaluators;

import ast.*;

import java.util.List;

public class VariableData {
    private final Equation equation;
    private final List<Equation> equationArray;
    private final String text;
    private final List<String> textArray;
    private final VariableType variableType;

    public enum VariableType {
        EQUATION, STRING, ARRAY_EQ, ARRAY_STR
    }




    public VariableData(List<String> textArray, List<Equation> equationArray) {
        if(equationArray != null){
            this.equation = null;
            this.equationArray = equationArray;
            this.text = null;
            this.textArray = null;
            this.variableType = VariableType.ARRAY_EQ;
        }else {
            this.equation = null;
            this.equationArray = null;
            this.text = null;
            this.textArray = textArray;
            this.variableType = VariableType.ARRAY_STR;
        }
    }

    public VariableData(Equation equation) {
        this.equation = equation;
        this.equationArray = null;
        this.text = null;
        this.textArray = null;
        this.variableType = VariableType.EQUATION;
    }


    public VariableData(String text) {
        this.equation = null;
        this.equationArray = null;
        this.text = text;
        this.textArray = null;
        this.variableType = VariableType.STRING;
    }

    public Equation getEquation() {
        return equation;
    }

    public List<Equation> getEquationArray() {
        return equationArray;
    }


    public String getText() {
        return text;
    }

    public List<String> getTextArray() {
        return textArray;
    }

    public VariableType getVariableType() {
        return variableType;
    }
}
