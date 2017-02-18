package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.StatementNode;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PackageStatementNode extends StatementNode{
    @SerializedName("Package Path")
    public ArrayList<Token> PackageIdentifiers;

    public PackageStatementNode(ArrayList<Token> identifiers){
        PackageIdentifiers = identifiers;
    }

    @Override
    public void ValidateSemantic() {

    }
}
