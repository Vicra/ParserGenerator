package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.StatementNode;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImportStatementNode extends StatementNode {
    @SerializedName("Import Path")
    public ArrayList<Token> ImportIdentifiers;

    public ImportStatementNode(ArrayList<Token> identifiers){
        ImportIdentifiers = identifiers;
    }

    @Override
    public void ValidateSemantic() {

    }
}
