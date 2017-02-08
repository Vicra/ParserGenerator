package ParserGenerator.SyntacticComponents;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.LexerComponents.TokenTypes;

public class TokenValidations {
    public static boolean TokenStartsAsStatement(Token currentToken) {

        if (currentToken.Type == TokenTypes.RESERVED_IMPORT
            || currentToken.Type == TokenTypes.RESERVED_PACKAGE
            || currentToken.Type == TokenTypes.RESERVED_ACTION
            || currentToken.Type == TokenTypes.RESERVED_INIT
            || currentToken.Type == TokenTypes.RESERVED_SCAN
            || currentToken.Type == TokenTypes.RESERVED_PARSER
            || currentToken.Type == TokenTypes.RESERVED_NONTERMINAL
            || currentToken.Type == TokenTypes.RESERVED_TERMINAL
            || currentToken.Type == TokenTypes.RESERVED_PRECEDENCE
            || currentToken.Type == TokenTypes.IDENTIFIER
            ){
            return true;
        }
        return false;
    }
}
