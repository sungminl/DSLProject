package ast.evaluators;

import ast.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Evaluator implements LatexConverterVisitor<PrintWriter, List<String>> {
    private final Map<MutableVariable, VariableData> variableTable = new HashMap<>();
    private final StringBuilder errors;

    public Evaluator(StringBuilder sb) {
        this.errors = sb;
    }

    public Map<MutableVariable, VariableData> getVariableTable() {
        return variableTable;
    }

    public Map<String, FunctionDefinition> getFunctionTable() {
        return functionTable;
    }

    private final Map<String, FunctionDefinition> functionTable = new HashMap<>();

    @Override
    public List<String> visit(Add a, PrintWriter printWriter) {
        return a.getEquation().accept(this, printWriter);
    }

    @Override
    public List<String> visit(LeftHeader c, PrintWriter printWriter) {
        return null;
    }

    @Override
    public List<String> visit(Comment c, PrintWriter printWriter) {
        return null;
    }

    @Override
    public List<String> visit(Connection c, PrintWriter printWriter) {
        Connection.ConnectionType connectionType = c.getConnectionType();

        printWriter.print("\\path[");
        switch (connectionType) {
            case LEFT_ARROW -> printWriter.print("<-");
            case BIDIRECTIONAL -> printWriter.print("<->");
            case RIGHT_ARROW -> printWriter.print("->");
            case UNDIRECTED -> printWriter.print("-");
            default -> {
            }
        }
        printWriter.print("] ");
        return null;
    }

    @Override
    public List<String> visit(Cos c, PrintWriter printWriter) {
        List<String> equations = c.getEquation().accept(this, printWriter);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\cos( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Division d, PrintWriter printWriter) {
        return d.getEquation().accept(this, printWriter);
    }

    @Override
    public List<String> visit(Equation e, PrintWriter printWriter) {
        List<String> terminalEquations = e.getTerminalExpression().accept(this, printWriter);
        if (terminalEquations.contains("@")) {
            errors.append("Strings are not allowed in matrices.");
        }
        List<String> restEquations = null;
        if (e.getRestEquation() != null) {
            restEquations = e.getRestEquation().accept(this, printWriter);
            e.setEquationType(e.getRestEquation().setType());
        }
            List<String> values = new ArrayList<>();
            for (String equation : terminalEquations) {
                try {
                    Double terminalEquationValue = Double.valueOf(equation);
                    if (e.getNegate()) {
                        terminalEquationValue = - terminalEquationValue;
                    }
                    if (restEquations != null && restEquations.size() > 0) {
                        for (String restEquation: restEquations) {
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
                                    fullEquation = "- " +  equation + restEquationEval;
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
                        for (String restEquation: restEquations) {
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
        return values;
    }

    @Override
    public List<String> visit(FunctionCall f, PrintWriter printWriter) {
        if (!functionTable.containsKey(f.getFunctionName())) {
            //ERROR
        } else {
            FunctionDefinition dfn = functionTable.get(f.getFunctionName());
            if (dfn.getParameters() != null) {
                for (int i = 0; i < dfn.getParameters().getMutableVariableList().size(); i++) { //initializing params
                    VariableData data = variableTable.get(f.getParameters().getMutableVariableList().get(i));
                    variableTable.put(dfn.getParameters().getMutableVariableList().get(i), data);
                }
            }
            for (NonDefinableCallable nonDefinableCallable : dfn.getNonDefinableCallableList()) {
                nonDefinableCallable.accept(this, printWriter);
            }
        }
        return null;
    }

    @Override
    public List<String> visit(FunctionDefinition f, PrintWriter printWriter) {
        functionTable.put(f.getFunctionName(), f);
        return null;
    }

    @Override
    public List<String> visit(Graph g, PrintWriter printWriter) {
        List<GraphNode> nodes = g.getGraphNodeList();
        List<Connection> connections = g.getConnections();

        printWriter.println("\\begin{tikzpicture}[node distance={2cm}, thick, state/.style = {draw, circle}]");

        for (int i = 0; i < nodes.size(); i++) {
            GraphNode n = nodes.get(i);
            String name = n.getName();
            n.accept(this, printWriter);
            printWriter.print("(" + name + ") ");
            if (i > 0) {
                printWriter.print("[right of=" + nodes.get(i - 1).getName() + "] ");
            }
            printWriter.println("{" + name + "};");
        }

        boolean isIncreasingBend = true;
        int edgeBend = 30;
        for (Connection c : connections) {
            String from = c.getNameFrom();
            String to = c.getNameTo();
            String label = c.getLabel();

            c.accept(this, printWriter);
            printWriter.print("(" + from + ") edge[bend right = " + edgeBend + "] ");
            if (isIncreasingBend) {
                if (edgeBend >= 45) {
                    isIncreasingBend = false;
                    edgeBend = 40;
                } else {
                    edgeBend += 5;
                }
            } else {
                if (edgeBend <= 30) {
                    isIncreasingBend = true;
                    edgeBend = 35;
                } else {
                    edgeBend -= 5;
                }
            }
            if (label != null) {
                printWriter.print("node[below = 0.1cm] {" + label.replace("_", "\\_") + "} ");
            }
            printWriter.println("(" + to + ");");
        }

        printWriter.println("\\end{tikzpicture}");
        printWriter.println();
        return null;
    }

    @Override
    public List<String> visit(GraphNode g, PrintWriter printWriter) {
        boolean accept = g.isAccept();
        boolean start = g.isStart();

        printWriter.print("\\node[state");
        if (start) {
            printWriter.print(",initial left");
        }
        if (accept) {
            printWriter.print(",accepting");
        }
        printWriter.print("] ");
        return null;
    }

    @Override
    public List<String> visit(Log l, PrintWriter printWriter) {
        List<String> equations = l.getEquation().accept(this, printWriter);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\log( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Loop l, PrintWriter printWriter) {

        List<String> startString = l.getExpressionFrom().accept(this, printWriter);
        List<String> endString = l.getExpressionTo().accept(this, printWriter);

        int start = 0;
        int end = 0;

        if (startString != null && startString.size() == 1) {
            try {
                start = (int) Math.floor(Double.parseDouble(startString.get(0)));
                if (endString != null && endString.size() == 1) {
                    try {
                        end = (int) Math.floor(Double.parseDouble(endString.get(0)));
                    } catch (NumberFormatException ex) {
                        errors.append("Invalid end bound equation for loop.");
                    }
                } else {
                    errors.append("Invalid end bound equation for loop.");
                }
            } catch (NumberFormatException ex) {
                errors.append("Invalid start bound equation for loop.");
            }
        } else {
            errors.append("Invalid start bound equation for loop.");
        }

        for (int i = start; i < end; i++) {
            TerminalExpression index = new Num((double) i);
            variableTable.put(l.getMutableVariable(), new VariableData(new Equation(index, false)));
            for (NonDefinableCallable c : l.getNonDefinableCallableList()) {
                c.accept(this, printWriter);
            }
        }
        return null;
    }

    @Override
    public List<String> visit(Matrix m, PrintWriter printWriter) {
        printWriter.println("\\[\\begin{bmatrix}");
        m.getMatrixContent().accept(this, printWriter);
        printWriter.println("\\end{bmatrix}\\]");
        return null;
    }

    @Override
    public List<String> visit(MatrixContent m, PrintWriter printWriter) {
        if (m.getMatRowList() != null) {
            List<MatRow> rows = m.getMatRowList();
            if (rows.size() == m.getRows()) {
                if (!errors.isEmpty()) {
                    return null;
                }
                for (MatRow row : rows) {
                    row.accept(this, printWriter);
                    printWriter.println("\\\\");
                }
            } else if (rows.size() == 1) {
                for (int i = 0; i < m.getRows(); i++) {
                    if (!errors.isEmpty()) {
                        break;
                    }
                    rows.get(0).accept(this, printWriter);
                    printWriter.println("\\\\");
                }
            } else {
                errors.append("Number of rows in matrix content does not match declared number of rows.");
            }
        } else if (m.getEquation() != null) {
            List<String> values = m.getEquation().accept(this, printWriter);
            if (values.size() == m.getCols()) {
                for (int i = 0; i < m.getRows(); i++) {
                    for (int j = 0; j < values.size(); j++) {
                        String value = values.get(j);
                        printWriter.print(value);
                        if (j != m.getCols() - 1) {
                            printWriter.print(" & ");
                        }
                    }
                    printWriter.println("\\\\");
                }
            } else if (values.size() == 1) {
                String value = values.get(0);
                for (int i = 0; i < m.getRows(); i++) {
                    for (int j = 0; j < m.getCols(); j++) {
                        printWriter.print(value);
                        if (j != m.getCols() - 1) {
                            printWriter.print(" & ");
                        }
                    }
                    printWriter.println("\\\\");
                }
            } else {
                errors.append("Number of equations in the row does not match declared number of columns.");
            }
        }
        return null;
    }

    @Override
    public List<String> visit(MatRow m, PrintWriter printWriter) {
        if (!errors.isEmpty()) {
            return null;
        } else {
            List<Equation> equations = m.getEquationList();
            List<String> innerEquations = new ArrayList<>();
            for (int i = 0; i < equations.size(); i++) {
                Equation equation = equations.get(i);
                innerEquations.addAll(equation.accept(this, printWriter));
            }
            if (innerEquations.size() == m.getNumberOfCols()) {
                for (int i = 0; i < innerEquations.size(); i++) {
                    printWriter.print(innerEquations.get(i));
                    if (i != innerEquations.size() - 1) {
                        printWriter.print(" & ");
                    }
                }
            } else if (innerEquations.size() == 1) {
                for (int i = 0; i < m.getNumberOfCols(); i++) {
                    printWriter.print(innerEquations.get(0));
                    if (i != m.getNumberOfCols() - 1) {
                        printWriter.print(" & ");
                    }
                }
            } else {
                errors.append("Number of equations in the row content does not match declared number of columns.");
            }
            return null;
        }
    }

    @Override
    public List<String> visit(Multiplication m, PrintWriter printWriter) {
        return m.getEquation().accept(this, printWriter);
    }

    @Override
    public List<String> visit(MutableVariable m, PrintWriter printWriter) {
        if (variableTable.get(m) != null) {
            VariableData variable = variableTable.get(m);
            List<String> values = new ArrayList<>();
            switch (variable.getVariableType()) {
                case STRING -> {
                    printWriter.print(variable.getText());
                    values.add("@");
                    return values;
                }
                case ARRAY_EQ -> {
                    List<Equation> equations = variable.getEquationArray();
                    for (Equation equation : equations) {
                        List<String> equationStrings = equation.accept(this, printWriter);
                        values.addAll(equationStrings);
                    }
                    return values;
                }
                case EQUATION -> {
                    values.addAll(variable.getEquation().accept(this, printWriter));
                    return values;
                }
                case ARRAY_STR -> {
                    List<String> strings = variable.getTextArray();
                    for (String string : strings) {
                        printWriter.print(string);
                    }
                    values.add("@");
                    return values;
                }
            }
        }
        return null;
    }

    @Override
    public List<String> visit(MVAssignment m, PrintWriter printWriter) {
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
    public List<String> visit(Num n, PrintWriter printWriter) {
        List<String> value = new ArrayList<>();
        value.add(String.valueOf(n.getNum()));
        return value;
    }

    @Override
    public List<String> visit(NumArray n, PrintWriter printWriter) {
        return null;
    }

    @Override
    public List<String> visit(Parameters p, PrintWriter printWriter) {
        return null;
    }

    @Override
    public List<String> visit(Power p, PrintWriter printWriter) {
        return p.getEquation().accept(this, printWriter);
    }

    @Override
    public List<String> visit(Program p, PrintWriter printWriter) {
        printWriter.println("\\documentclass[]{article}");
        printWriter.println("\\usepackage{amsmath}");
        printWriter.println("\\usepackage{tikz}");
        printWriter.println("\\usetikzlibrary{graphs,automata}");
        printWriter.println("\\title{Document}");
        printWriter.println("\\begin{document}");
        for (Callable c : p.getCallableList()) {
            c.accept(this, printWriter);
            if (!errors.isEmpty()) {
                break;
            }
        }
        printWriter.println("\\end{document}");
        return null;
    }

    @Override
    public List<String> visit(QuotedText q, PrintWriter printWriter) {
        printWriter.print(q.getText().replace("&", "\\&")
                .replace("%", "\\%")
                .replace("$", "\\$")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("_", "\\_"));
        return null;
    }

    @Override
    public List<String> visit(Row r, PrintWriter printWriter) {
        List<RowContent> rowContent = r.getRowContentList();
        for (int i = 0; i < rowContent.size() - 1; i++) {
            rowContent.get(i).accept(this, printWriter);
            printWriter.print("&");
        }
        rowContent.get(rowContent.size() - 1).accept(this, printWriter);
        printWriter.println("\\\\");
        return null;
    }

    @Override
    public List<String> visit(RowContent r, PrintWriter printWriter) {
        if(r.getQuotedText() != null) {
            r.getQuotedText().accept(this, printWriter);
        }
        else {
            r.getMutableVariable().accept(this, printWriter);
        }
        return null;
    }

    @Override
    public List<String> visit(TopHeader r, PrintWriter printWriter) {
        List<QuotedText> header = r.getQuotedTextList();
        if (header.size() > 0) {
            for (int i = 0; i < header.size() - 1; i++) {
                printWriter.print("\\textbf{");
                header.get(i).accept(this, printWriter);
                printWriter.print("}&");
            }
            printWriter.print("\\textbf{");
            header.get(header.size() - 1).accept(this, printWriter);
            printWriter.println("}\\\\");
        }
        return null;
    }

    @Override
    public List<String> visit(Sin s, PrintWriter printWriter) {
        List<String> equations = s.getEquation().accept(this, printWriter);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\sin( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Sqrt s, PrintWriter printWriter) {
        List<String> equations = s.getEquation().accept(this, printWriter);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\sqrt{ " + equation + " }";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(Subtraction s, PrintWriter printWriter) {
        return s.getEquation().accept(this, printWriter);
    }

    @Override
    public List<String> visit(Symbol s, PrintWriter printWriter) {
        List<String> equations = new ArrayList<>();
        equations.add(s.getSymbol());
        return equations;
    }

    @Override
    public List<String> visit(Table table, PrintWriter printWriter) {
        int rows = table.getRows();
        int cols = table.getCols();
        TopHeader rowHeader = table.getTopHeader();
        LeftHeader colHeader = table.getLeftHeader();
        List<QuotedText> colHeaderList = new ArrayList<>();
        if (colHeader != null) {
            colHeaderList = colHeader.getQuotedTextList();
        }
        List<Row> rowList = table.getRowList();

        // Being table and column format
        printWriter.println("\\begin{center}");
        printWriter.print("\\begin{tabular}{");
        if (colHeader != null && colHeaderList.size() > 0) {
            printWriter.print("|l");
        }
        for (int c = 0; c < cols; c++) {
            printWriter.print("|c");
        }
        printWriter.println("|}");
        printWriter.println("\\hline");

        // Row header
        if (rowHeader != null) {
            if (colHeader != null && colHeaderList.size() > 0) {
                printWriter.print("&");
            }
            rowHeader.accept(this, printWriter);
            printWriter.println("\\hline");
        }


        if (colHeader != null && colHeaderList.size() > 0) {
            for (int r = 0; r < rows; r++) {
                printWriter.print("\\textbf{");
                colHeaderList.get(r).accept(this, printWriter);
                printWriter.print("}&");
                rowList.get(r).accept(this, printWriter);
                printWriter.println("\\hline");
            }
        } else {
            for (int r = 0; r < rows; r++) {
                rowList.get(r).accept(this, printWriter);
                printWriter.println("\\hline");
            }
        }

        // End table
        printWriter.println("\\end{tabular}");
        printWriter.println("\\end{center}");
        printWriter.println();
        return null;
    }

    @Override
    public List<String> visit(Tan tan, PrintWriter printWriter) {
        List<String> equations = tan.getEquation().accept(this, printWriter);
        List<String> values = new ArrayList<>();
        for (String equation : equations) {
            String equationString = " \\tan( " + equation + " )";
            values.add(equationString);
        }
        return values;
    }

    @Override
    public List<String> visit(TextArray ta, PrintWriter printWriter) {
        return null;
    }

    @Override
    public List<String> visit(TextEntry te, PrintWriter printWriter) {
        TextStyleSettings style = te.getTextStyleSettings();
        QuotedText text = te.getQuotedText();

        if (style != null) {
            int numStyles = Integer.parseInt(style.accept(this, printWriter).get(0));
            text.accept(this, printWriter);
            for (int i = 0; i < numStyles; i++) {
                printWriter.print("}");
            }
        } else {
            text.accept(this, printWriter);
        }
        printWriter.println();
        printWriter.println();
        return null;
    }

    @Override
    public List<String> visit(TextStyleSettings tss, PrintWriter printWriter) {
        List<TextStyleSettings.TextSetting> styles = tss.getTextStyleList();
        boolean isHeading = false;
        boolean isBold = false;
        boolean isItalics = false;
        boolean isUnderline = false;
        int numFeatures = 0;

        // Check for styles
        for (TextStyleSettings.TextSetting style : styles) {
            switch (style) {
                case HEADING -> isHeading = true;
                case BOLD -> isBold = true;
                case ITALICS -> isItalics = true;
                case UNDERLINE -> isUnderline = true;
                default -> {
                }
            }
        }

        // Convert styles in correct order
        if (isHeading) {
            printWriter.print("\\section*{");
            numFeatures++;
        }
        if (isBold) {
            printWriter.print("\\textbf{");
            numFeatures++;
        }
        if (isItalics) {
            printWriter.print("\\textit{");
            numFeatures++;
        }
        if (isUnderline) {
            printWriter.print("\\underline{");
            numFeatures++;
        }
        List<String> numFeaturesList = new ArrayList<>();
        numFeaturesList.add(Integer.toString(numFeatures));
        return numFeaturesList;
    }
}
