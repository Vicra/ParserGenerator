package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.StatementNode;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public abstract class SymbolListStatementNode extends StatementNode{
    @SerializedName("Declarations")
    public ArrayList<Token> DeclaresTerm;
}
