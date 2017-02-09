package ParserGenerator.TreeComponents.Statements.SymbolListStatements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.Statements.SymbolListStatementNode;

import java.util.ArrayList;

public class NonTerminalSymbolNode extends SymbolListStatementNode {
    public ArrayList<Token> MultiPart = null;

    public NonTerminalSymbolNode(ArrayList<Token> multiPart, ArrayList<Token> declaresTerm){
        MultiPart = multiPart;
        DeclaresTerm = declaresTerm;
    }

    public NonTerminalSymbolNode(ArrayList<Token> declaresTerm){
        DeclaresTerm = declaresTerm;
    }
}
