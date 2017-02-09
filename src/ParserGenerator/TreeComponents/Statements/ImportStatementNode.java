package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.StatementNode;

import java.util.ArrayList;

public class ImportStatementNode extends StatementNode {
    public ArrayList<Token> ImportIdentifiers;

    public ImportStatementNode(ArrayList<Token> identifiers){
        ImportIdentifiers = identifiers;
    }
}
