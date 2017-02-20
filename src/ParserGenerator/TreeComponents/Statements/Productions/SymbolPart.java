package ParserGenerator.TreeComponents.Statements.Productions;

import ParserGenerator.LexerComponents.Token;
import com.google.gson.annotations.SerializedName;

public class SymbolPart extends ProductionPart{
    @SerializedName("Symbol")
    public Token LeftLabel;
    @SerializedName("Optional Label")
    public Token RightOptionalLabel = null;

    public SymbolPart(Token leftLabel, Token rightOptionalLabel){
        LeftLabel = leftLabel;
        RightOptionalLabel = rightOptionalLabel;
    }

    public SymbolPart(Token leftLabel){
        LeftLabel = leftLabel;
    }
}
