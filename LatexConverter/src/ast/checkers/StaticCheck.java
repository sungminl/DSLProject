package ast.checkers;

import ast.*;
import ast.evaluators.VariableData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticCheck implements LatexConverterVisitor<StringBuilder, List<String>> {
    private int numberOfErrors = 1;

    private final Map<MutableVariable, VariableData> variableTable = new HashMap<>();
    private final Map<String, FunctionDefinition> functionTable = new HashMap<>();

    @Override
    public List<String> visit(Add a, StringBuilder stringBuilder) {
        return a.getEquation().accept(this, stringBuilder);
    }

    @Override
    public List<String> visit(LeftHeader c, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Comment c, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Connection c, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Cos c, StringBuilder stringBuilder) {
        List<String> equations = c.getEquation().accept(this, stringBuilder);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\cos( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Division d, StringBuilder stringBuilder) {
        return d.getEquation().accept(this, stringBuilder);
    }

    @Override
    public List<String> visit(Equation e, StringBuilder stringBuilder) {
        List<String> terminalEquations = e.getTerminalExpression().accept(this, stringBuilder);
        List<String> restEquations = null;
        if (e.getRestEquation() != null) {
            restEquations = e.getRestEquation().accept(this, stringBuilder);
            e.setEquationType(e.getRestEquation().setType());
        }
        List<String> values = new ArrayList<>();
        if (terminalEquations != null) {
            for (String equation : terminalEquations) {
                try {
                    Double terminalEquationValue = Double.valueOf(equation);
                    if (e.getNegate()) {
                        terminalEquationValue = -terminalEquationValue;
                    }
                    if (restEquations != null && restEquations.size() > 0) {
                        for (String restEquation : restEquations) {
                            try {
                                Double restEquationValue = Double.valueOf(restEquation);
                                switch (e.getType()) {
                                    case ADD -> values.add(String.valueOf(terminalEquationValue + restEquationValue));
                                    case SUBTRACT -> values.add(String.valueOf(terminalEquationValue - restEquationValue));
                                    case POWER -> values.add(String.valueOf(Math.pow(terminalEquationValue, restEquationValue)));
                                    case MULTIPLY -> values.add(String.valueOf(terminalEquationValue * restEquationValue));
                                    case DIVISION -> {
                                        if (restEquationValue == 0) {
                                            throw new NumberFormatException();
                                        }
                                        values.add(String.valueOf(terminalEquationValue / restEquationValue));
                                    }
                                    case NONE -> values.add(String.valueOf(terminalEquationValue));
                                }
                            } catch (NumberFormatException ex) {
                                String restEquationEval = restEquation;
                                switch (e.getType()) {
                                    case ADD -> restEquationEval = " + ( " + restEquationEval + " )";
                                    case SUBTRACT -> restEquationEval = " - ( " + restEquationEval + " )";
                                    case POWER -> restEquationEval = " ^ { " + restEquationEval + " }";
                                    case MULTIPLY -> restEquationEval = " \\cdot ( " + restEquationEval + " )";
                                    case DIVISION -> restEquationEval = " / ( " + restEquationEval + " )";
                                    case NONE -> restEquationEval = "";
                                }
                                String fullEquation;
                                if (e.getNegate()) {
                                    fullEquation = "- " + equation + restEquationEval;
                                } else {
                                    fullEquation = equation + restEquationEval;
                                }
                                values.add(fullEquation);
                            }
                        }
                    } else {
                        values.add(String.valueOf(terminalEquationValue));
                    }
                } catch (NumberFormatException ex) {
                    String terminalEquationString = equation;
                    if (e.getNegate()) {
                        terminalEquationString = " - " + terminalEquationString;
                    }
                    if (restEquations != null && restEquations.size() > 0) {
                        for (String restEquation : restEquations) {
                            String restEquationEval = restEquation;
                            switch (e.getType()) {
                                case ADD -> restEquationEval = " + ( " + restEquationEval + " )";
                                case SUBTRACT -> restEquationEval = " - ( " + restEquationEval + " )";
                                case POWER -> restEquationEval = " ^ { " + restEquationEval + " }";
                                case MULTIPLY -> restEquationEval = " \\cdot ( " + restEquationEval + " )";
                                case DIVISION -> restEquationEval = " / ( " + restEquationEval + " )";
                                case NONE -> restEquationEval = "";
                            }
                            String fullEquation = terminalEquationString + restEquationEval;
                            values.add(fullEquation);
                        }
                    } else {
                        values.add(terminalEquationString);
                    }
                }
            }
        }
        return values;
    }

    @Override
    public List<String> visit(FunctionCall f, StringBuilder stringBuilder) {
        if (!functionTable.containsKey(f.getFunctionName())) {
            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Attempt to call function: ").append(f.getFunctionName()).append(" when it is not defined.\n");
            numberOfErrors++;
        } else {
            FunctionDefinition dfn = functionTable.get(f.getFunctionName());
            if (dfn.getParameters() != null) {
                if(f.getParameters() == null || dfn.getParameters().getMutableVariableList().size() != f.getParameters().getMutableVariableList().size()){
                    stringBuilder.append("(").append(numberOfErrors).append(") ").append("Invalid number of parameters when calling function: ").append(f.getFunctionName()).append(".\n");
                    numberOfErrors++;
                }
                else
                {
                    for (int i = 0; i < dfn.getParameters().getMutableVariableList().size(); i++) { //initializing params
                        VariableData data = variableTable.get(f.getParameters().getMutableVariableList().get(i));
                        variableTable.put(dfn.getParameters().getMutableVariableList().get(i), data);
                    }
                }
            }
            for (NonDefinableCallable nonDefinableCallable : dfn.getNonDefinableCallableList()) {
                nonDefinableCallable.accept(this, stringBuilder);
            }
        }
        return null;
    }

    @Override
    public List<String> visit(FunctionDefinition f, StringBuilder stringBuilder) {
        if (functionTable.containsKey(f.getFunctionName())) {
            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Attempt to redefine function: ").append(f.getFunctionName()).append(" when it was previously defined.\n");
            numberOfErrors++;
        } else {
            functionTable.put(f.getFunctionName(), f);
        }
        return null;
    }

    @Override
    public List<String> visit(Graph g, StringBuilder stringBuilder) {
        List<GraphNode> nodes = g.getGraphNodeList();
        List<Connection> connections = g.getConnections();

        List<String> namesOfNodes = new ArrayList<>();

        for (GraphNode graphNode : nodes) {
            namesOfNodes.add(graphNode.getName());
        }

        for (Connection c : connections) {
            String from = c.getNameFrom();
            String to = c.getNameTo();
            if (!namesOfNodes.contains(from)) {
                stringBuilder.append("(").append(numberOfErrors).append(") ").append("Tries to connect from node ").append(from).append(" to node ").append(to).append(", but node ").append(from).append(" not defined in graph.\n");
                numberOfErrors++;
            }
            if (!namesOfNodes.contains(to)) {
                stringBuilder.append("(").append(numberOfErrors).append(") ").append("Tries to connect from node ").append(from).append(" to node ").append(to).append(", but node ").append(to).append(" not defined in graph.\n");
                numberOfErrors++;
            }
        }
        return null;
    }

    @Override
    public List<String> visit(GraphNode g, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Log l, StringBuilder stringBuilder) {
        List<String> equations = l.getEquation().accept(this, stringBuilder);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\log( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Loop l, StringBuilder stringBuilder) {
        List<String> startString = l.getExpressionFrom().accept(this, stringBuilder);
        List<String> endString = l.getExpressionTo().accept(this, stringBuilder);

        variableTable.put(l.getMutableVariable(), new VariableData(new Equation(new Num(0.0), false)));
        for (NonDefinableCallable c : l.getNonDefinableCallableList()) {
            c.accept(this, stringBuilder);
        }
        return null;
    }

    @Override
    public List<String> visit(Matrix m, StringBuilder stringBuilder) {
        List<MutableVariable> parameters = new ArrayList<>();
        if (m.getParameters() != null) {
            parameters = m.getParameters().getMutableVariableList();
            for (MutableVariable mv : parameters) {
                if (!variableTable.containsKey(mv)) {
                    stringBuilder.append("(").append(numberOfErrors).append(") ").append("Variable: ").append(mv.getMutableVariable()).append(" used as parameter when not declared previously.\n");
                    numberOfErrors++;
                }
            }
        }
        if (m.getMatrixContent().getEquation() != null) {
            TerminalExpression terminalExpression = m.getMatrixContent().getEquation().getTerminalExpression();
            if (terminalExpression instanceof MutableVariable) {
                if (!parameters.contains(((MutableVariable) terminalExpression))) {
                    stringBuilder.append("(").append(numberOfErrors).append(") ").append("Attempt to use variable: ").append(((MutableVariable) terminalExpression).getMutableVariable()).append(" that is not declared in the parameters.\n");
                    numberOfErrors++;
                }
            }
        } else {
            for (MatRow matRow : m.getMatrixContent().getMatRowList()) {
                for (Equation equation : matRow.getEquationList()) {
                    if (equation.getTerminalExpression() instanceof MutableVariable) {
                        if (!parameters.contains((MutableVariable) equation.getTerminalExpression())) {
                            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Attempt to use variable: ").append(((MutableVariable) equation.getTerminalExpression()).getMutableVariable()).append(" that is not declared in the parameters.\n");
                            numberOfErrors++;
                        }
                    }
                }
            }
        }


        m.getMatrixContent().accept(this, stringBuilder);
        return null;
    }

    @Override
    public List<String> visit(MatrixContent m, StringBuilder stringBuilder) {
        if (m.getMatRowList() != null) {
            List<MatRow> rows = m.getMatRowList();
            if (rows.size() == m.getRows()) {
                for (MatRow row : rows) {
                    row.accept(this, stringBuilder);
                }
            } else if (rows.size() == 1) {
                for (int i = 0; i < m.getRows(); i++) {
                    rows.get(0).accept(this, stringBuilder);
                }
            }
        }
        return null;
    }

    @Override
    public List<String> visit(MatRow m, StringBuilder stringBuilder) {
        List<Equation> equations = m.getEquationList();
        List<String> innerEquations = new ArrayList<>();
        for (Equation equation : equations) {
            innerEquations.addAll(equation.accept(this, stringBuilder));
        }
        return null;
    }

    @Override
    public List<String> visit(Multiplication m, StringBuilder stringBuilder) {
        return m.getEquation().accept(this, stringBuilder);
    }

    @Override
    public List<String> visit(MutableVariable m, StringBuilder stringBuilder) {
        if (!variableTable.containsKey(m)) {
            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Attempt to use variable: ").append(m.getMutableVariable()).append(" that is not declared.\n");
            numberOfErrors++;
        }
        return null;
    }

    @Override
    public List<String> visit(MVAssignment m, StringBuilder stringBuilder) {
        if (m.getEquation() != null) {
            variableTable.put(m.getFirstMutableVariable(), new VariableData(m.getEquation()));
        } else if (m.getQuotedText() != null) {
            variableTable.put(m.getFirstMutableVariable(), new VariableData(m.getQuotedText().getText()));
        } else if (m.getNumArray() != null) {
            variableTable.put(m.getFirstMutableVariable(), new VariableData(null, m.getNumArray().getEquations()));
        } else {
            List<String> strArray = new ArrayList<>();
            for (QuotedText q : m.getTextArray().getTexts()) {
                strArray.add(q.getText());
            }
            variableTable.put(m.getFirstMutableVariable(), new VariableData(strArray, null));
        }
        return null;
    }

    @Override
    public List<String> visit(Num n, StringBuilder stringBuilder) {
        List<String> value = new ArrayList<>();
        value.add(String.valueOf(n.getNum()));
        return value;
    }

    @Override
    public List<String> visit(NumArray n, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Parameters p, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Power p, StringBuilder stringBuilder) {
        return p.getEquation().accept(this, stringBuilder);
    }

    @Override
    public List<String> visit(Program p, StringBuilder stringBuilder) {
        for (Callable c : p.getCallableList()) {
            c.accept(this, stringBuilder);
        }
        return null;
    }

    @Override
    public List<String> visit(QuotedText q, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(Row r, StringBuilder stringBuilder) {
        List<RowContent> rowContent = r.getRowContentList();
        if (rowContent.size() == 1) {
            rowContent.get(0).accept(this, stringBuilder);
        } else {
            for (int i = 0; i < rowContent.size() - 1; i++) {
                rowContent.get(i).accept(this, stringBuilder);
            }
        }
        return null;
    }

    @Override
    public List<String> visit(RowContent r, StringBuilder stringBuilder) {
        if(r.getQuotedText() != null) {
            r.getQuotedText().accept(this, stringBuilder);
        }
        else {
            r.getMutableVariable().accept(this, stringBuilder);
        }
        return null;
    }

    @Override
    public List<String> visit(TopHeader r, StringBuilder stringBuilder) {
        List<QuotedText> header = r.getQuotedTextList();
        if (header.size() > 0) {
            for (int i = 0; i < header.size() - 1; i++) {
                header.get(i).accept(this, stringBuilder);
            }
            header.get(header.size() - 1).accept(this, stringBuilder);
        }
        return null;
    }

    @Override
    public List<String> visit(Sin s, StringBuilder stringBuilder) {
        List<String> equations = s.getEquation().accept(this, stringBuilder);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\sin( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Sqrt s, StringBuilder stringBuilder) {
        List<String> equations = s.getEquation().accept(this, stringBuilder);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\sqrt{ " + equation + " }";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Subtraction s, StringBuilder stringBuilder) {
        return s.getEquation().accept(this, stringBuilder);
    }

    @Override
    public List<String> visit(Symbol s, StringBuilder stringBuilder) {
        List<String> equations = new ArrayList<>();
        equations.add(s.getSymbol());
        return equations;
    }

    @Override
    public List<String> visit(Table table, StringBuilder stringBuilder) {
        int rows = table.getRows();
        int cols = table.getCols();
        if(rows <= 0){
            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Invalid number of Rows in Table.\n");
            numberOfErrors++;
        }
        if(cols <= 0){
            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Invalid number of Cols in Table.\n");
            numberOfErrors++;
        }

        TopHeader topHeader = table.getTopHeader();
        LeftHeader leftHeader = table.getLeftHeader();
        if (topHeader != null) {
            List<QuotedText> topHeaderList = topHeader.getQuotedTextList();
            if (topHeaderList.size() != cols) {
                stringBuilder.append("(").append(numberOfErrors).append(") ").append("Number of top header entries do not match the declared number of table columns.\n");
                numberOfErrors++;
            }
        }
        if (leftHeader != null) {
            List<QuotedText> leftHeaderList = leftHeader.getQuotedTextList();
            if (leftHeaderList.size() != rows) {
                stringBuilder.append("(").append(numberOfErrors).append(") ").append("Number of left header entries do not match the declared number of table rows.\n");
                numberOfErrors++;
            }
        }
        if (table.getRowList().size() != rows) {
            stringBuilder.append("(").append(numberOfErrors).append(") ").append("Number of row content entries do not match the declared number of table rows.\n");
            numberOfErrors++;
        }
        for (Row row : table.getRowList()) {
            if (row.getRowContentList().size() != cols) {
                stringBuilder.append("(").append(numberOfErrors).append(") ").append("Number of column content entries do not match the declared number of table columns.\n");
                numberOfErrors++;
            }
        }
        List<Row> rowList = table.getRowList();
        for (int r = 0; r < table.getRowList().size(); r++) {
            rowList.get(r).accept(this, stringBuilder);
        }
        return null;
    }

    @Override
    public List<String> visit(Tan tan, StringBuilder stringBuilder) {
        List<String> equations = tan.getEquation().accept(this, stringBuilder);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\tan( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(TextArray ta, StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public List<String> visit(TextEntry te, StringBuilder stringBuilder) {
        TextStyleSettings style = te.getTextStyleSettings();
        QuotedText text = te.getQuotedText();

        if (style != null) {
            text.accept(this, stringBuilder);
        } else {
            text.accept(this, stringBuilder);
        }
        return null;
    }

    @Override
    public List<String> visit(TextStyleSettings tss, StringBuilder stringBuilder) {
        return null;
    }
}