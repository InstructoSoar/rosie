grammar Regress;
@header {
package edu.umich.rosie.tools.config;
}
corpus   : block+ COMMENT* ;
block    : COMMENT* sentence FAILED? expected? ;
sentence : sentenceWord (sentenceWord | ',' )* ('.' | TERMINATOR)? ;
sentenceWord : QUOTE | WORD;
expected : (COMMENT* rhs)+ ;
rhs      : '(' '@'? SYMBOL (attr value COMMENT?)+  ')' ;
attr     : '^' (NUMBER | (WORD ('.' WORD)*) ) ;
value    : (variable | '@'? SYMBOL | WORD | NUMBER) ;
variable : '<' WORD '>' ;
QUOTE    : '"' ~["]* '"';
SYMBOL   : [a-zA-Z][0-9]+ ;
WORD     : [a-zA-Z!][a-zA-Z0-9\-'_]* ;
NUMBER   : '-'? ('.' DIGIT+ | DIGIT+ ('.' DIGIT*)? ) ;
fragment DIGIT : [0-9] ;
PAREN    : [()] ;
TERMINATOR : [?!;];
FAILED   : '#   FAILED!' ;
COMMENT  : '#' ~[\r\n]* ;
BRACKETS : '[' .*? ']' -> skip ;
WS       : [ \t\r\n]+ -> skip ;
