package ParserGenerator.LexerComponents;

public enum TokenTypes {
    IDENTIFIER,
    END_OF_FILE,
    JAVA_CODE,

    /*Reserved Words*/
    RESERVED_TERMINAL,
    RESERVED_NONTERMINAL,
    RESERVED_IMPORT,
    RESERVED_ACTION,
    RESERVED_PARSER,
    RESERVED_INIT,
    RESERVED_SCAN,
    RESERVED_PRECEDENCE,
    RESERVED_START,
    RESERVED_WITH,
    RESERVED_LEFT,
    RESERVED_RIGHT,
    RESERVED_NONASSOC,
    RESERVED_PACKAGE,

    /*Simple character symbol*/
    SYM_PIPE,
    SYM_SEMICOLON,
    SYM_COLON,
    SYM_DOT,
    SYM_ASTERISK,
    SYM_COMMA,

    /*Triple character symbol*/
    SYM_PRODUCTION,


}
