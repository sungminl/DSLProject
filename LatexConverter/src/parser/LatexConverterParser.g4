parser grammar LatexConverterParser;
options { tokenVocab=LatexConverterLexer; }

// Grammar
program: callable* EOF;
callable: function_definition | non_definable_callable;
non_definable_callable: function_call | text_entry | comment | mv_assignment | graph | loop | table | matrix;

quoted_text: QUOTE TEXT? CLOSE_QUOTE;
//Comment
comment: COMMENT_START COMMENT_TEXT? COMMENT_END;

//Functions
function_definition: FUNCTION_DEFN_START FUNCTION_NAME OPEN_BRACKET parameters? CLOSED_BRACKET OPEN_CURLY non_definable_callable* CLOSED_CURLY;
parameters: MUTABLE_VARIABLE (COMMA MUTABLE_VARIABLE)*;
function_call: FUNCTION_CALL_START FUNCTION_NAME (COMMA parameters)? CLOSED_BRACKET;

equation: terminal_expression | terminal_expression rest_eqn;
rest_eqn: add | sub | div | mult | pow;
terminal_expression: OPEN_BRACKET equation CLOSED_BRACKET | NUM | MUTABLE_VARIABLE | SYMBOL | log | sin | cos | tan | sqrt | SUB equation;

//Arrays
num_array: OPEN_SQUARE (equation (COMMA equation)*)? CLOSED_SQUARE;

text_array: OPEN_SQUARE (quoted_text (COMMA quoted_text)*)? CLOSED_SQUARE;

//Text Entry
text_entry: TEXT_ENTRY_START text_style_settings? OPEN_CURLY quoted_text CLOSED_CURLY;
text_style_settings: OPEN_BRACKET text_style (COMMA text_style)* CLOSED_BRACKET;
text_style: BOLD | HEADING | ITALICS| UNDERLINE;

//Mutable Variables
mv_assignment: MUTABLE_VARIABLE EQUAL (equation | quoted_text | text_array | num_array | MUTABLE_VARIABLE);

//Graphs & DFAs
graph: GRAPH_START nodes COMMA connections CLOSED_CURLY;
nodes: NODES_START node (NODE_NAME_SEPERATOR node)* CLOSE_CURLY_NODE;
node: NODE_NAME (OPEN_BRACKET_NODE START_SYMBOL? ACCEPT_SYMBOL? CLOSE_BRACKET_NODE)?;
connections: CONNECTIONS_START connection (NODE_CONNECTION_SEPERATOR connection)* CLOSE_CURLY_CONNECTION;
connection: NODE_NAME_CONNECTION connection_type (OPEN_BRACKET_NODE_CONNECTION STRINGLABEL CLOSE_BRACKET_NODE_CONNECTION)? NODE_NAME_CONNECTION;
connection_type: LEFT_ARROW | BIDIRECTIONAL | RIGHT_ARROW | UNDIRECTED;



//Loops
loop: LOOP_START MUTABLE_VARIABLE LOOP_FROM equation LOOP_TO equation OPEN_CURLY non_definable_callable* CLOSED_CURLY;

//tables
table: TABLE_START row_col top_header? left_header? OPEN_CURLY row+ CLOSED_CURLY;
row_col: row_count COMMA col_count | col_count COMMA row_count;

row_count: ROW_COUNT NUM;
col_count: COL_COUNT NUM;

top_header: TOP_HEADER  quoted_text (COMMA quoted_text)* CLOSED_BRACKET;
left_header: LEFT_HEADER  quoted_text (COMMA quoted_text)* CLOSED_BRACKET;
row: row_content (COMMA row_content)* PIPE;
row_content: quoted_text | MUTABLE_VARIABLE;

//matrices
matrix: MATRIX_START row_col (OPEN_BRACKET parameters? CLOSED_BRACKET)? OPEN_CURLY matrix_content CLOSED_CURLY;
matrix_content: mat_row+ | equation;
mat_row: equation (COMMA equation)* PIPE;

add: ADD equation;
sub: SUB equation;
div: DIV equation;
mult: MULT equation;
pow: POW equation;
log: LOG_START equation CLOSED_BRACKET;
sin: SIN_START equation CLOSED_BRACKET;
cos: COS_START equation CLOSED_BRACKET;
tan: TAN_START equation CLOSED_BRACKET;
sqrt: SQRT_START equation CLOSED_BRACKET;