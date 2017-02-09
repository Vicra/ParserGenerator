package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;

import java.util.ArrayList;

public class RightHandSideNode {
    ArrayList<ProductionPart> ProductionParts;

    public RightHandSideNode(ArrayList<ProductionPart> productionParts){
        ProductionParts = productionParts;
    }
}
