package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.StatementNode;

import java.util.ArrayList;

public class PackageStatementNode extends StatementNode{
    public ArrayList<String> PackageIdentifiers;

    public PackageStatementNode(ArrayList<String> identifiers){
        PackageIdentifiers = identifiers;
    }
}
