package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.StatementNode;

import java.util.ArrayList;

public class PackageStatementNode extends StatementNode{
    public ArrayList<Token> PackageIdentifiers;

    public PackageStatementNode(ArrayList<Token> identifiers){
        PackageIdentifiers = identifiers;
    }
}
