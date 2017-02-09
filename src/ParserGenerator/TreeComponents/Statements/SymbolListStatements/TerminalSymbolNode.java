package ParserGenerator.TreeComponents.Statements.SymbolListStatements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.Statements.SymbolListStatementNode;

import java.util.ArrayList;

public class TerminalSymbolNode extends SymbolListStatementNode{
    public ArrayList<Token> MultiPart = null;

    public TerminalSymbolNode(ArrayList<Token> multiPart, ArrayList<Token> declaresTerm){
        MultiPart = multiPart;
        DeclaresTerm = declaresTerm;
    }

    public TerminalSymbolNode(ArrayList<Token> declaresTerm){
        DeclaresTerm = declaresTerm;
    }
}
