package ParserGenerator.TreeComponents.Statements.SymbolListStatements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.TreeComponents.Statements.SymbolListStatementNode;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TerminalDeclarationNode extends SymbolListStatementNode{
    @SerializedName("Java Data Type")
    public ArrayList<Token> MultiPart = null;

    public TerminalDeclarationNode(ArrayList<Token> multiPart, ArrayList<Token> declaresTerm){
        MultiPart = multiPart;
        DeclaresTerm = declaresTerm;
    }

    public TerminalDeclarationNode(ArrayList<Token> declaresTerm){
        DeclaresTerm = declaresTerm;
    }

    @Override
    public void ValidateSemantic() throws SemanticException {
        for (Token token : DeclaresTerm) {
            SymbolTable.getInstance().DeclareVariable(token.Lexeme,"terminal");
        }
    }
}
