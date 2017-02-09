package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.TreeComponents.StatementNode;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductionStatementNode extends StatementNode{
    @SerializedName("Left Side")
    public Token LeftHandSide;
    @SerializedName("Right Side Productions")
    public ArrayList<RightHandSideNode> RightHandSideList;

    public ProductionStatementNode(Token leftHandSide, ArrayList<RightHandSideNode> rightHandSideList){
        LeftHandSide = leftHandSide;
        RightHandSideList = rightHandSideList;
    }
}
