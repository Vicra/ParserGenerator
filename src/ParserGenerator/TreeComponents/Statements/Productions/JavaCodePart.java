package ParserGenerator.TreeComponents.Statements.Productions;

import ParserGenerator.LexerComponents.Token;
import com.google.gson.annotations.SerializedName;

public class JavaCodePart extends ProductionPart{
    @SerializedName("JavaCode")
    public Token JavaCodeToken;

    public JavaCodePart(Token javaCodeToken){
        JavaCodeToken = javaCodeToken;
    }
}
