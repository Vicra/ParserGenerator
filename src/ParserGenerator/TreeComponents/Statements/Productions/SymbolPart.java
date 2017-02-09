package ParserGenerator.TreeComponents.Statements.Productions;

import ParserGenerator.LexerComponents.Token;

public class SymbolPart extends ProductionPart{
    public Token Symbol;
    public Token OptionalLabel = null;

    public SymbolPart(Token symbol, Token optionalLabel){
        Symbol = symbol;
        OptionalLabel = optionalLabel;
    }

    public SymbolPart(Token symbol){
        Symbol = symbol;
    }
}
