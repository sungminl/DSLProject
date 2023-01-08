package parser;

import ast.*;
import libs.Node;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeToAST extends LatexConverterParserBaseVisitor<Node>{
    @Override
    public Program visitProgram(LatexConverterParser.ProgramContext ctx) {
        List<Callable> callableList = new ArrayList<>();
        for(LatexConverterParser.CallableContext c :ctx.callable()){
            callableList.add((Callable) c.accept(this));
        }
        return new Program(callableList);
    }

    @Override
    public Callable visitCallable(LatexConverterParser.CallableContext ctx) {
        return (Callable) ctx.getChild(0).accept(this);
    }

    @Override
    public NonDefinableCallable visitNon_definable_callable(LatexConverterParser.Non_definable_callableContext ctx) {
        return (NonDefinableCallable) ctx.getChild(0).accept(this);//split this between func def and other callables and accept and return the val
    }

    @Override
    public QuotedText visitQuoted_text(LatexConverterParser.Quoted_textContext ctx) {
        return new QuotedText(ctx.TEXT() == null? "" : ctx.TEXT().toString());
    }

    @Override
    public Comment visitComment(LatexConverterParser.CommentContext ctx) {
        return new Comment(ctx.COMMENT_TEXT().getText());
    }

    @Override
    public FunctionDefinition visitFunction_definition(LatexConverterParser.Function_definitionContext ctx) {
        Parameters param = ctx.parameters() == null? null : (Parameters) ctx.parameters().accept(this);
        List<NonDefinableCallable> df = new ArrayList<>();
        for (LatexConverterParser.Non_definable_callableContext callable : ctx.non_definable_callable()){
            df.add((NonDefinableCallable) callable.accept(this));
        }

        return new FunctionDefinition(ctx.FUNCTION_NAME().getText(), param, df);
    }

    @Override
    public Parameters visitParameters(LatexConverterParser.ParametersContext ctx) {
        List<MutableVariable> mutableVars = new ArrayList<>();
        for(int i =0; i < ctx.MUTABLE_VARIABLE().size(); i++){
            mutableVars.add(new MutableVariable(ctx.MUTABLE_VARIABLE(i).toString()));
        }
        return new Parameters(mutableVars);
    }

    @Override
    public FunctionCall visitFunction_call(LatexConverterParser.Function_callContext ctx) {
        Parameters params = ctx.parameters() == null? null: (Parameters)ctx.parameters().accept(this);
        return new FunctionCall(ctx.FUNCTION_NAME().toString(),params);
    }

    @Override
    public Equation visitEquation(LatexConverterParser.EquationContext ctx) {
        boolean negate = ctx.terminal_expression().SUB() != null;

        if(ctx.rest_eqn() != null){
            return new Equation((TerminalExpression) ctx.terminal_expression().accept(this),
                    (RestEquation) ctx.rest_eqn().accept(this),
                    negate);
        }else {
            return new Equation((TerminalExpression) ctx.terminal_expression().accept(this),negate);
        }
    }


    @Override
    public RestEquation visitRest_eqn(LatexConverterParser.Rest_eqnContext ctx) {
        return (RestEquation) ctx.getChild(0).accept(this);
    }

    @Override
    public TerminalExpression visitTerminal_expression(LatexConverterParser.Terminal_expressionContext ctx) {
        if (ctx.NUM() != null){
            return new Num(Double.parseDouble(ctx.NUM().toString())); //token types
        }
        else if (ctx.equation() != null){
            return (Equation) ctx.equation().accept(this); //this one has more than one token
        }
        else if (ctx.MUTABLE_VARIABLE() != null){
            return new MutableVariable(ctx.MUTABLE_VARIABLE().toString());//token types
        }
        else if (ctx.SYMBOL() != null){
            return new Symbol(ctx.SYMBOL().toString());//token types
        }
        else {
            return (TerminalExpression) ctx.getChild(0).accept(this); //other cases with just one thing in the terminal exp
        }
    }

    @Override
    public NumArray visitNum_array(LatexConverterParser.Num_arrayContext ctx) {
        List<Equation> equations = new ArrayList<>();
        if(ctx.equation() != null){
            for (LatexConverterParser.EquationContext eq :ctx.equation()){
                equations.add((Equation) eq.accept(this));
            }
        }
        return new NumArray(equations);
    }


    @Override
    public TextArray visitText_array(LatexConverterParser.Text_arrayContext ctx) {
        List<QuotedText> texts = new ArrayList<>();
        if (ctx.quoted_text() != null){
            for (LatexConverterParser.Quoted_textContext eq :ctx.quoted_text()){
                texts.add((QuotedText) eq.accept(this));
            }
        }
        return new TextArray(texts);
    }


    @Override
    public TextEntry visitText_entry(LatexConverterParser.Text_entryContext ctx) {
        QuotedText text = (QuotedText) ctx.quoted_text().accept(this);
        if(ctx.text_style_settings() != null){
            TextStyleSettings settings = (TextStyleSettings) ctx.text_style_settings().accept(this);
            return new TextEntry(settings, text);
        }else {
            return new TextEntry(text);
        }
    }

    @Override
    public TextStyleSettings visitText_style_settings(LatexConverterParser.Text_style_settingsContext ctx) {
        List<TextStyleSettings.TextSetting> settings = new ArrayList<>();
        for (LatexConverterParser.Text_styleContext style : ctx.text_style()) {
            switch (style.getText()){
                case "Bold":
                    settings.add(TextStyleSettings.TextSetting.BOLD);
                    break;
                case "Heading":
                    settings.add(TextStyleSettings.TextSetting.HEADING);
                    break;
                case "Italics":
                    settings.add(TextStyleSettings.TextSetting.ITALICS);
                    break;
                case "Underline":
                    settings.add(TextStyleSettings.TextSetting.UNDERLINE);
                    break;
                default:
                    break;
            }
        }
        return new TextStyleSettings(settings);
    }


    @Override
    public MVAssignment visitMv_assignment(LatexConverterParser.Mv_assignmentContext ctx) {
        MutableVariable fromVar = new MutableVariable(ctx.MUTABLE_VARIABLE(0).getText());
        if(ctx.MUTABLE_VARIABLE().size() > 1){
            return new MVAssignment(fromVar, new MutableVariable(ctx.MUTABLE_VARIABLE(1).getText()));
        }
        else if (ctx.equation() != null){
            return new MVAssignment(fromVar, (Equation) ctx.equation().accept(this));
        }
        else if (ctx.quoted_text() != null){
            return new MVAssignment(fromVar, (QuotedText) ctx.quoted_text().accept(this));
        }
        else if (ctx.text_array() != null){
            return new MVAssignment(fromVar, (TextArray) ctx.text_array().accept(this));
        }
        else if (ctx.num_array() != null){
            return new MVAssignment(fromVar, (NumArray) ctx.num_array().accept(this));
        }
        return null;
    }

    @Override
    public Graph visitGraph(LatexConverterParser.GraphContext ctx) {
        List<GraphNode> nodes = new ArrayList<>();
        for(LatexConverterParser.NodeContext node: ctx.nodes().node()){
            nodes.add((GraphNode) node.accept(this));
        }
        List<Connection> connections = new ArrayList<>();
        for(LatexConverterParser.ConnectionContext connection: ctx.connections().connection()){
            connections.add((Connection) connection.accept(this));
        }
        return new Graph(nodes, connections);
    }

    @Override
    public GraphNode visitNode(LatexConverterParser.NodeContext ctx) {
        boolean accept = ctx.ACCEPT_SYMBOL() != null;
        boolean start = ctx.START_SYMBOL() != null;
        return new GraphNode(ctx.NODE_NAME().getText(), accept, start);
    }

    @Override
    public Connection visitConnection(LatexConverterParser.ConnectionContext ctx) {
        String startNode = ctx.NODE_NAME_CONNECTION(0).getText();
        String endNode = ctx.NODE_NAME_CONNECTION(1).getText();
        String label = ctx.STRINGLABEL() == null? null : ctx.STRINGLABEL().getText();

        if(ctx.connection_type().getText().equals("<-")){
            return new Connection(startNode, Connection.ConnectionType.LEFT_ARROW, endNode, label);
        }
        else if(ctx.connection_type().getText().equals("->")){
            return new Connection(startNode, Connection.ConnectionType.RIGHT_ARROW, endNode, label);
        }
        else if(ctx.connection_type().getText().equals("<->")){
            return new Connection(startNode, Connection.ConnectionType.BIDIRECTIONAL, endNode, label);
        }
        else {
            return new Connection(startNode, Connection.ConnectionType.UNDIRECTED, endNode, label);
        }
    }


    @Override
    public Loop visitLoop(LatexConverterParser.LoopContext ctx) {
        MutableVariable mv = new MutableVariable(ctx.MUTABLE_VARIABLE().getText());
        Equation from = (Equation) ctx.equation(0).accept(this);
        Equation to = (Equation) ctx.equation(1).accept(this);
        List<NonDefinableCallable> nonDefinableCallableList = new ArrayList<>();

        for (LatexConverterParser.Non_definable_callableContext n : ctx.non_definable_callable()){
            nonDefinableCallableList.add((NonDefinableCallable) n.accept(this));
        }
        return new Loop(mv, from, to, nonDefinableCallableList);
    }

    @Override
    public Table visitTable(LatexConverterParser.TableContext ctx) {
        int rowCount = -1;
        int colCount = -1;
        try {
            rowCount = Integer.parseInt(ctx.row_col().row_count().NUM().getText());
            colCount = Integer.parseInt(ctx.row_col().col_count().NUM().getText());
        }catch (NumberFormatException e)
        {
            System.out.println("Had non Integer value for row and column count");
        }


        TopHeader topHeader = ctx.top_header() == null? null : (TopHeader) ctx.top_header().accept(this);
        LeftHeader leftHeader = ctx.left_header() == null? null : (LeftHeader) ctx.left_header().accept(this);
        List<Row> rowList = new ArrayList<>();
        for(LatexConverterParser.RowContext r :ctx.row()){
            Row row = (Row) r.accept(this);
            row.setNumberOfCols(colCount);
            rowList.add(row);
        }


        return new Table(rowCount, colCount, topHeader, leftHeader, rowList);
    }

    @Override
    public TopHeader visitTop_header(LatexConverterParser.Top_headerContext ctx) {
        List<QuotedText> quotedText = new ArrayList<>();
        for (LatexConverterParser.Quoted_textContext q :ctx.quoted_text()){
            quotedText.add((QuotedText) q.accept(this));
        }
        return new TopHeader(quotedText);
    }

    @Override
    public LeftHeader visitLeft_header(LatexConverterParser.Left_headerContext ctx) {
        List<QuotedText> quotedText = new ArrayList<>();
        for (LatexConverterParser.Quoted_textContext q :ctx.quoted_text()){
            quotedText.add((QuotedText) q.accept(this));
        }
        return new LeftHeader(quotedText);
    }

    @Override
    public Row visitRow(LatexConverterParser.RowContext ctx) {
        List<RowContent> content = new ArrayList<>();
        for(LatexConverterParser.Row_contentContext row : ctx.row_content()){
            content.add((RowContent) row.accept(this));
        }
        return new Row(content);
    }

    @Override
    public RowContent visitRow_content(LatexConverterParser.Row_contentContext ctx) {
        if(ctx.MUTABLE_VARIABLE() != null){
            return new RowContent(new MutableVariable(ctx.MUTABLE_VARIABLE().getText()));
        }
        else {
            return new RowContent((QuotedText) ctx.quoted_text().accept(this));
        }
    }

    @Override
    public Matrix visitMatrix(LatexConverterParser.MatrixContext ctx) {
        int rowCount = -1;
        int colCount = -1;
        try {
            rowCount = Integer.parseInt(ctx.row_col().row_count().NUM().getText());
            colCount = Integer.parseInt(ctx.row_col().col_count().NUM().getText());
        }catch (NumberFormatException e)
        {
            System.out.println("Had non Integer value for row and column count");
        }
        Parameters params = ctx.parameters() == null? null : (Parameters) ctx.parameters().accept(this);
        MatrixContent content = (MatrixContent) ctx.matrix_content().accept(this);
        content.setNumberOfCols(colCount);
        content.setNumberOfRows(rowCount);
        return new Matrix(rowCount,colCount,params,content);
    }

    @Override
    public MatRow visitMat_row(LatexConverterParser.Mat_rowContext ctx) {
        List<Equation> equations = new ArrayList<>();
        for(LatexConverterParser.EquationContext e:ctx.equation()){
            equations.add((Equation) e.accept(this));
        }
        return new MatRow(equations);
    }

    @Override
    public MatrixContent visitMatrix_content(LatexConverterParser.Matrix_contentContext ctx) {
        if(ctx.equation() != null){
            return new MatrixContent((Equation) ctx.equation().accept(this));
        }else {
            List<MatRow> rows = new ArrayList<>();
            for(LatexConverterParser.Mat_rowContext m : ctx.mat_row()){
                rows.add((MatRow) m.accept(this));
            }
            return new MatrixContent(rows);
        }
    }

    @Override
    public Add visitAdd(LatexConverterParser.AddContext ctx) {
        return new Add((Equation) ctx.equation().accept(this));
    }

    @Override
    public Subtraction visitSub(LatexConverterParser.SubContext ctx) {
        return new Subtraction((Equation) ctx.equation().accept(this));
    }

    @Override
    public Division visitDiv(LatexConverterParser.DivContext ctx) {
        return new Division((Equation) ctx.equation().accept(this));
    }

    @Override
    public Multiplication visitMult(LatexConverterParser.MultContext ctx) {
        return new Multiplication((Equation) ctx.equation().accept(this));
    }

    @Override
    public Power visitPow(LatexConverterParser.PowContext ctx) {
        return new Power((Equation) ctx.equation().accept(this));
    }

    @Override
    public Log visitLog(LatexConverterParser.LogContext ctx) {
        return new Log((Equation) ctx.equation().accept(this));
    }

    @Override
    public Sin visitSin(LatexConverterParser.SinContext ctx) {
        return new Sin((Equation) ctx.equation().accept(this));
    }

    @Override
    public Cos visitCos(LatexConverterParser.CosContext ctx) {
        return new Cos((Equation) ctx.equation().accept(this));
    }

    @Override
    public Tan visitTan(LatexConverterParser.TanContext ctx) {
        return new Tan((Equation) ctx.equation().accept(this));
    }

    @Override
    public Sqrt visitSqrt(LatexConverterParser.SqrtContext ctx) {
        return new Sqrt((Equation) ctx.equation().accept(this));
    }
}
