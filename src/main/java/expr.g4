grammar expr;

expression returns [int value]
    :   mult { int m1 = $mult.value; }
        pm   { int tt=$pm.value; }
        mult { $value = interp(m1, tt, $mult.value); }
    ;

pm returns [int value]
    :   PLUS  { $value = $PLUS.type; }
    |   MINUS { $value = $MINUS.type; }
    ;

mult returns [int value]
    :   term { int t1 = $term.value; }
        md   { int tt=$md.value; }
        term { $value = iterp(t1, tt, $term.value); }
    ;

md returns [int value]
    :   MUL { $value = $MUL.type; }
    |   DIV { $value = $DIV.type; }
    ;

term returns [int value]
    :   NUMBER               { $value = $NUMBER.int; }
    |   LPAR expression RPAR { $value = $expression.value; }
    ;

/* Lexical rules */

PLUS : '+' ;
MINUS: '-' ;
DIV  : '/' ;
MUL  : '*' ;
LPAR : '(' ;
RPAR : ')' ;

NUMBER  : '-'?[0-9]+ ;

WS : [ \r\t\u000C\n]+ -> skip ;
