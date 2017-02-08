package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.StatementNode;

import java.util.ArrayList;

/**
 * Created by vicra on 2/8/2017.
 */
public class ImportStatementNode extends StatementNode {
    public ArrayList<String> ImportIdentifiers;

    public ImportStatementNode(ArrayList<String> identifiers){
        ImportIdentifiers = identifiers;
    }
}
