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

    public static boolean TokenStartsAsImportStatement(Token currentToken) {
        if(currentToken.Type == TokenTypes.RESERVED_IMPORT){
            return true;
        }
        return false;
    }

    public static boolean TokenStartsAsCodeParts(Token currentToken) {
        if (currentToken.Type == TokenTypes.RESERVED_ACTION ||
            currentToken.Type == TokenTypes.RESERVED_INIT ||
            currentToken.Type == TokenTypes.RESERVED_SCAN ||
            currentToken.Type == TokenTypes.RESERVED_PARSER){
            return true;
        }
        return false;
    }

    public static boolean TokenStartsAsSymbolList(Token currentToken) {
        if(currentToken.Type == TokenTypes.RESERVED_TERMINAL ||
            currentToken.Type == TokenTypes.RESERVED_NON ||
            currentToken.Type == TokenTypes.RESERVED_NONTERMINAL){
            return true;
        }
        return false;
    }

    public static boolean TokenStartsAsProductionList(Token currentToken) {
        if (currentToken.Type == TokenTypes.IDENTIFIER){
            return true;
        }
        return false;
    }

    public static boolean TokenIsPipe(Token currentToken) {
        if (currentToken.Type == TokenTypes.SYM_PIPE){
            return true;
        }
        return false;
    }
}
