lexer grammar LatexConverterLexer;
NUM: [0-9]+('.'[0-9]+)?;

OPEN_BRACKET: '(';
CLOSED_BRACKET: ')';
OPEN_SQUARE: '[';
CLOSED_SQUARE: ']';
OPEN_CURLY: '{';
CLOSED_CURLY: '}';
COMMA: ',';
QUOTE: '"' ->mode(TEXT_MODE);

EQUAL: '=';
ADD: '+';
SUB: '-';
DIV:  '/';
MULT: '*';
POW: '^';
LOG_START: 'log(';
SIN_START: 'sin(';
COS_START: 'cos(';
TAN_START: 'tan(';
SQRT_START: 'sqrt(';
SYMBOL: [a-zA-Z];


MUTABLE_VARIABLE: '&'[a-zA-Z_]+;

//Comments
COMMENT_START: '/*' ->mode(COMMENT_MODE);
COMMENT_END: '*/';

//Loops
LOOP_START: 'LOOP';
LOOP_FROM: 'FROM';
LOOP_TO: 'TO';

//Functions
FUNCTION_DEFN_START: 'FUNCTION' -> mode(FN_DEFN_NAME_MODE);
FUNCTION_CALL_START: 'CALL' WS* '(' -> mode(FN_DEFN_NAME_MODE);
FUNCTION_DEFN_SECTION_START: 'FN_DEFINITIONS:' WS* '{';

//Graphs & DFAs
GRAPH_START: 'GRAPH' WS* '{';
NODES_START: 'NODES:' WS* '{' ->mode(NODE_NAME_MODE);
CONNECTIONS_START: 'CONNECTIONS:' WS* '{' ->mode(NODE_CONNECTION_MODE);
DFA_START: 'DFA' WS* '{';


//Tables & Matrices
MATRIX_START: 'MATRIX';
TABLE_START: 'TABLE';
ROW_COUNT: 'ROW:';
COL_COUNT: 'COL:';
TOP_HEADER: 'TOPHEADER:' WS* '(';
LEFT_HEADER: 'LEFTHEADER:' WS* '(';
PIPE: '|';


//Text stuff
TEXT_ENTRY_START: 'TEXT';
BOLD: 'Bold';
HEADING: 'Heading';
ITALICS: 'Italics';
UNDERLINE: 'Underline';

// White space is ignored during tokenization
WS : [\r\n ]+ -> channel(HIDDEN);

mode TEXT_MODE;
TEXT: ~["\r\n]+;
CLOSE_QUOTE: '"' ->mode(DEFAULT_MODE);


mode NODE_NAME_MODE;
NODE_NAME: [a-zA-Z0-9]+;
OPEN_BRACKET_NODE: '(' -> mode(START_END_MODE);
NODE_NAME_SEPERATOR: ',';
CLOSE_CURLY_NODE: '}' -> mode(DEFAULT_MODE);
WS_NODE : [\r\n ]+ -> channel(HIDDEN);

mode START_END_MODE;
START_SYMBOL: 'S';
ACCEPT_SYMBOL: 'A';
CLOSE_BRACKET_NODE: ')' -> mode(NODE_NAME_MODE);

mode NODE_CONNECTION_MODE;
NODE_NAME_CONNECTION: [a-zA-Z0-9_]+;
LEFT_ARROW: '<-';
BIDIRECTIONAL: '<->';
RIGHT_ARROW: '->';
UNDIRECTED: '--';
OPEN_BRACKET_NODE_CONNECTION: '(' -> mode(NODE_STRING_LABEL);
CLOSE_BRACKET_NODE_CONNECTION: ')';
NODE_CONNECTION_SEPERATOR: ',';
WS_CONNECTION : [\r\n ]+ -> channel(HIDDEN);
CLOSE_CURLY_CONNECTION: '}' -> mode(DEFAULT_MODE);

mode NODE_STRING_LABEL;
STRINGLABEL: [a-zA-Z0-9_, ]+ -> mode(NODE_CONNECTION_MODE);

mode FN_DEFN_NAME_MODE;
FUNCTION_NAME :  [a-zA-Z_]+ ->mode(DEFAULT_MODE);
WS_FN : [\r\n ]+ -> channel(HIDDEN);


mode COMMENT_MODE;
COMMENT_TEXT: ~[*]+ ->mode(DEFAULT_MODE);
