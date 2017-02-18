package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RightHandSideNode {
    @SerializedName("Production Parts")
    public ArrayList<ProductionPart> ProductionParts;

    public RightHandSideNode(ArrayList<ProductionPart> productionParts){
        ProductionParts = productionParts;
    }
}
