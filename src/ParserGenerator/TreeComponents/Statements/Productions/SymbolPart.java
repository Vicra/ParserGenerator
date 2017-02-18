package ParserGenerator.TreeComponents.Statements.Productions;

import ParserGenerator.LexerComponents.Token;
import com.google.gson.annotations.SerializedName;

public class SymbolPart extends ProductionPart{
    @SerializedName("Symbol")
    public Token Symbol;
    @SerializedName("Optional Label")
    public Token OptionalLabel = null;

    public SymbolPart(Token symbol, Token optionalLabel){
        Symbol = symbol;
        OptionalLabel = optionalLabel;
    }

    public SymbolPart(Token symbol){
        Symbol = symbol;
    }
}
