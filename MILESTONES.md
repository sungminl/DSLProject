# Table of Contents
- [Milestone 1](#milestone-1)
- [Milestone 2](#milestone-2)
- [Milestone 3](#milestone-3)
- [Milestone 4](#milestone-4)
- [Milestone 5](#milestone-5)



# Milestone 1

## Idea
- Description: Latex graph generator with support for tables, matrices, diagrams, DFAs, etc.
- Main Language Feature Ideas:
  - Creating a table in a document easily
  - Nested tables
  - Matrices inside of tables
  - Creating matrices
  - User defined arrays (any type) (maybe refine it down to numbers/equations?)
  - For loops and mutable variables to create multiple tables/matrices
  - User defined functions for populating things
  - Not using latex macros since we dont require the person to know the language
  - Graphs: directed graphs, trees
  - DFA: DFAs & NFA
- Target users:
  - People wanting to create formal pdf documents with little Latex experience
  - People wanting to create tables and other visual representations quickly which can be a pain in latex
  - People writing scientific papers who require vigorous diagrams and/or proofs
- Examples:
  - Creating a long table with sequential values

Input:
```
Array x = [1, 2, 3]

CREATE TABLE (x) 3, 3
{
  x
  x + 3
  x + 6
}
```

Output:
```
\begin{center}
\begin{tabular}{ |c|c|c| }
 \hline
 1 & 2 & 3 \\
 4 & 5 & 6 \\
 7 & 8 & 9 \\
 \hline
\end{tabular}
\end{center}
```
  - Creating a Vandermonde matrix of a specific size

Input:
```
Array x = [1, 2, 3]

CREATE MATRIX (x) 3, 3
{
  x
  x^2
  x^3
}
```

Output:
```
\begin{bmatrix}
1 & 2 & 3\\
1 & 4 & 9 \\
1 & 8 & 27 \\
\end{bmatrix}
```

## TA Feedback
- Covered enough features could work
- Fix up and elaborate
- Idea seems interesting enough
- 1 comment: write some pseudo code
- 1 thing worried about: concerned that macros already exist and the functions might just be one to one

## Follow-Up Tasks/Features To Design
- create a simple test grammar for our Features
- finish designing what can be used where (rules)



# Milestone 2
```
Draft Grammar:

//Numerical values or equations
NUM ::= [0-9]+(.[0-9]+)?;

ADD ::= ‘+’ EQUATION;
SUB ::= ‘-’ EQUATION;
DIV ::=  ‘/’ EQUATION;
MULT ::=  ‘*’ EQUATION;
POW ::=  ‘^’ EQUATION;
LOG ::= ‘log(‘ EQUATION ‘)’;
SIN ::= ‘sin(‘EQUATION ‘)’;
COS ::= ‘cos(‘EQUATION’)’;
TAN ::= ‘tan(‘EQUATION’)’;
SQRT ::= ‘sqrt(EQUATION’)’;‘

SYMBOL ::= [a-zA-Z];
EQUATION ::= ‘(‘ EXPRESSION ‘)’ | EXPRESSION ;
EXPRESSION::= (NUM | MUTABLE_VARIABLE | SYMBOL) RESTEQN? | ‘-’EXPRESSION | LOG | SQRT | SIN | COS | TAN;
RESTEQN ::= ADD | SUB | DIV | MULT | POW ;

//Text
TEXT ::= ~[|“‘[‘’]’’{‘‘}’’&’’,’]+;
QUOTED_TEXT ::= “ TEXT ”‘;

TEXT_ENTRY ::= ’textEntry’ (‘(‘ TEXT_STYLE (‘,’ TEXT_STYLE )* ‘)’)?  ‘{‘ QUOTED_TEXT ‘}’
TEXT_STYLE :: ‘Bold’ | ‘Heading’ | ‘Italics’| ‘Underline’;

//Arrays
NUMARRAY ::= ‘[‘ NUM_ELEMENT?  ‘]’;
NUM_ELEMENT ::= NUMVALUE (‘,’ NUMVALUE )?;
NUMVALUE ::= NUMARRAY  | EQUATION ;

STRINGARRAY ::= ‘[‘ STRING_ELEMENT? ‘]’;
STRING_ELEMENT ::= STRINGVALUE (‘,’ STRINGVALUE )?;
STRINGVALUE ::= STRINGARRAY  | QUOTED_TEXT;

//mutable variables
MUTABLE_VARIABLE ::= ‘&’[a-zA-Z_]+;

MV_ASSIGNMENT ::= MUTABLE_VARIABLE ‘=’ (EQUATION | QUOTED_TEXT | STRINGARRAY | NUMARRAY | MUTABLE_VARIABLE);

//Graphs
GRAPH::= ‘GRAPH’’{‘ ‘Nodes:’ ’{‘ NODE_NAME (‘,’ NODE_NAME )* ‘}’ , ‘Connections:’ ‘{‘CONNECTION (‘,’ CONNECTION )* ‘}’ ‘}’;
CONNECTION ::= NODE_NAME CON_TYPE (’(‘STRINGLABEL*‘)’ )? NODE_NAME ;
CON_TYPE::= [‘<-’,’--’,’->’,’<->’];

//DFA
DFA ::= ‘DFA’’{‘ ‘Nodes:’ ’{‘ DFA_NODE (‘,’ DFA_NODE )* ‘}’ , ‘Connections:’ ‘{‘CONNECTION (‘,’ CONNECTION )* ‘}’ ‘}’;

NODE_NAME ::= [a-zA-Z0-9_]+;
DFA_NODE ::= NODENAME (‘(‘ ‘S’? ‘A’? ‘)’)?; //s for start and A for accept
STRINGLABEL ::= [a-zA-Z0-9_, ];

//Rows and Columns
ROWCOL ::= NUM ‘,’ NUM
ROW ::= (QUOTED_TEXT  (‘,’ QUOTED_TEXT )*)? ‘|’
ROW_HEADER ::= ‘ROWHEADER:’ ‘(‘(TEXT (‘,’ TEXT)*)’)’
COL_HEADER ::= ‘COLHEADER:’ ‘(‘(TEXT (‘,’ TEXT)*)’)’

MATROW ::= ( EQUATION(‘,’ EQUATION)*)? ‘|’

//Tables
TABLE ::= ‘TABLE’ ROWCOL ROW_HEADER? COL_HEADER? ‘{‘ ROW+ ‘}’

//Matrices
MATRIX ::= ‘MATRIX’ ROWCOL (‘(‘ PARAMETERS ‘)’)? ‘{‘ MATROW+ ‘}’

//Loops
LOOP ::= ‘LOOP ‘ MUTABLE_VARIABLE ‘ FROM ’ EXPRESSION ‘ TO ’ EXPRESSION ‘{‘ CALLABLES* ‘}’

//Functions
FUNCTION_DEFINITION ::= ‘FUNCTION ‘ FUNCTION_NAME ‘(‘ PARAMETERS? ‘)’ ‘{‘ CALLABLES*‘}’;
PARAMETERS ::=  MUTABLE_VARIABLE (‘,’ MUTABLE_VARIABLE )*;
FUNCTION_NAME ::= [a-zA-Z_]+;
FUNCTION_CALL ::= 'FUNCTION_CALL'‘(‘ FUNCTION_NAME’,’  ‘(‘ PARAMETERS ?  ‘)’‘)’;

//Whole program
PROGRAM ::= DEFNSPACE? CALLABLES* EOF;
DEFNSPACE ::= ‘FNDefinitions:’ ‘{‘ FUNCTION_DEFINITION* ‘}’;

CALLABLES :: = MV_ASSIGNMENT  | LOOP  | FUNCTION_CALL | DFA | TEXT_ENTRY | GRAPH | MATRIX | TABLE | COMMENT;

COMMENT ::= ‘/*’ COMMENT_TEXT ? ‘*/’;
COMMENT_TEXT ::= ~[*]+’

```

## Division of Responsibilities
- Tokenization & Parsing & parse tree: Luis
- AST & Static checks: Sherman & Sean
- Evaluation step: Emerson & Jack
- Video: Sherman

## Road Map
- User study one: Sept 28th
- AST: Oct 2nd (Hopefully)
- Parser & Lexer & parse tree: October 2nd (try before)
- 2nd user study: Oct 7th - 11th
- Static checks done by: Oct 10th;
- Finalized working prototype: Oct 12
- Video done by: Oct 15th

## Summary
We have almost completed the first draft of our Grammar as shown above, and will begin implementing it in code in this coming week. We have already decided a preliminary division of responsibilities and a basic timeline for when some of the larger parts of the project should be completed. We have also started to ask people to partake in our user study for next week and started thinking about what content those will contain. Overall we are on track so far and the team is working smoothly.

## TA Feedback
- Consider pontential other uses like formatting a 40 page resume
- Look into using modes when using ANTLR to simplify our grammar
- Plan user studies soon
- Remember to document changes based on feedback from user study
- Not necessary to record session during user study, just have someone take notes during the session



# Milestone 3

## Feedback Summary (from user study)
- Getting rid of duplicate logic particularly DFAs and Graph distinction
- Consistency with strings
- Being able to declare functions in not just the function declaration area (possibly removing)
- Matrix inputs for whole row or whole matrix not intuative
- Row and Column distinction for column and table sizes not clear
- Unclear if loops are exclusive or inclusive
- String concatenation

## Things to Change (based on user study feedback)
- Adding quotes around all strings -> simplifies parser and lexer
    - Allows more characters in strings now
- Merging DFA and Graphs into one feature
- Having explicit Row and Columns to wrap size to tell which one is which
- Being able to declare functions everywhere except other functions or loops
- String concatenatoin (low priority if ever done)
- Being able to call funcitons directly instead of Function_Call (dont think this one will get done)
   - instead will simplify to Call and remove the brackets for params

## Weekly Summary
- First draft of lexer and parser complete
  - Will be updated with changes from first study
  - Will have tests added to make sure its working and creating correct parse tree
- Progress on AST design (aim to complete by monday)
- Did User study on inital design of the system
  - Got 3 users to participate at different levels of experience with Latex
  - Got great feedback and things to change
- On track according to our schedule

https://docs.google.com/document/d/1xt2xmFkfpsPc1359WECQ3yvPbCgE4mivtevX90XDIRM/edit?usp=sharing (template for study)

https://docs.google.com/document/d/1uIx_hwSLeNGmucekxpUWGV-0_NRlB9o8X3qGHvU439w/edit?usp=sharing (user study 1 notes)

## TA Feedback
- focus on implementation now
  - AST needs to get done
- put user study templates and notes in the milestone doc



# Milestone 4

## Status of Implementation
- Lexer and parser are done
- AST node classes are done
- Parse tree to AST visitor is in progress
- AST visitor interface is in progress
- Evaluator is in progress

## Plans for Final User Study
- Use same format as previous user study (given the expected result, recreate it using our DSL) but actually use the DSL
- Plan to take place right after evaluator is finished (~ mid next week)

## Planned Timeline for Remaining Days
- 2nd user stud / Oct 11th -12th
- First draft for the evaluator / Oct 10th
- Tests and Static checks done / Oct 10th;
- Finalized working prototype / Oct 14
- Video done / Oct 16th

## TA Feedback
- Get the implementation working before the User Study
- Showing the pdf output would be nice instead of just the txt



# Milestone 5

## Status of Implementation
- Lexer and parser are done
- AST node classes are done
- Parse tree to AST visitor is done
- AST visitor interface is done
- Evaluator is mostly done - finishing some dynamic checks/runtime errors, unit tests
- Static checks in progress
- Video in progress

## Status and Results of Final User Study
- Finished final user study with 2 participants.
- Provided them with example code from our DSL and output.
- Gave expected output and instructed them to use our actual DSL to achieve the same output.
- Main feedback summarized below in “Changes to Language Design”

## Changes to Language Design
- Decided to keep parameters for matrices and will enforce that only variables passed in as parameters can be used
- Changed capitalization of the key words within graph definition (NODES, CONNECTIONS) for consistency with other parts of language
- Decided to implement matrices so that a single row can fill the whole matrix if wanted
- Changed the naming of row and col headers as it was confusing for people
- Changed loops to be exclusive as that makes more sense based on feedback

## Planned Timeline for Remaining Days
- Test evaluator + remaining dynamic checks (Jack)
- Test parse tree to AST (Luis)
- Finish static checks (Sean)
  - Check to see table contents and headers are right size
  - Check to see that matrices are right size (hard to do as you need to check the array size)
  - Check to see that nodes in graph connections are defined in graph nodes
  - Make sure that mutable variables used in matrices have values and been passed in through params
- Video (Sherman)
  - Video script by Wed night
  - First draft done by Saturday
  - Final version done by Sunday night

## TA Feedback
- Documentation for features
- Documentation for install and setup
- Make sure video is done
- Good job team
