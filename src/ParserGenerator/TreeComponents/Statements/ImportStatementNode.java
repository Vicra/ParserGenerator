package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.StatementNode;

import java.util.ArrayList;

public class ImportStatementNode extends StatementNode {
    public ArrayList<String> ImportIdentifiers;

    public ImportStatementNode(ArrayList<String> identifiers){
        ImportIdentifiers = identifiers;
    }
}
