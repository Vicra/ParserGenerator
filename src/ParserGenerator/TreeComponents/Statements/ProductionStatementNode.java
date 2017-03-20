package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.LexerComponents.TokenTypes;
import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.NonterminalType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.SemanticComponents.TypesTable;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.JavaCodePart;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductionStatementNode extends StatementNode{
    @SerializedName("Left Side")
    public Token LeftHandSide;
    @SerializedName("Right Side RightHandSide")
    public ArrayList<RightHandSideNode> RightHandSideList;

    public ProductionStatementNode(Token leftHandSide, ArrayList<RightHandSideNode> rightHandSideList){
        LeftHandSide = leftHandSide;
        RightHandSideList = rightHandSideList;
    }

    public ProductionStatementNode(String symbol, ArrayList<RightHandSideNode> rightHandSideList){
        LeftHandSide = new Token(-1,-1,symbol, TokenTypes.IDENTIFIER);
        RightHandSideList = rightHandSideList;
    }

    @Override
    public void ValidateSemantic() throws SemanticException {
        BaseType type = SymbolTable.getInstance().GetType(LeftHandSide.Lexeme);
        if (type instanceof TerminalType){
            throw new SemanticException("Solo puede hacer produccion de symbolo no terminal");
        }
        for (RightHandSideNode node : RightHandSideList){
            for (ProductionPart part : node.ProductionParts){
                if (part instanceof SymbolPart){
                    SymbolTable.getInstance().GetType(((SymbolPart) part).LeftLabel.Lexeme);
                }
            }
        }
    }
}
