package Automata;

import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionsUtilities {
    private static final String PrimeSymbol = "S'";

    public static Map<String, ArrayList<ArrayList<String>>> getProductionMap(ArrayList<ProductionStatementNode> productionNodeList) {
        Map<String, ArrayList<ArrayList<String>>> productionMap = new HashMap<>();
        String firstState = productionNodeList.get(0).LeftHandSide.Lexeme;
        ArrayList<String> aumentada = new ArrayList<>();
        aumentada.add(firstState);
        ArrayList<ArrayList<String>> toAdd = new ArrayList<>();
        toAdd.add(aumentada);
        productionMap.put(PrimeSymbol, toAdd);
        for (ProductionStatementNode productionStatementNode : productionNodeList) {
            ArrayList<ArrayList<String>> productions = new ArrayList<>();
            for (RightHandSideNode rightHandSideNode : productionStatementNode.RightHandSideList) {
                ArrayList<String> productionDetailList = new ArrayList<>();
                for (ProductionPart productionPart : rightHandSideNode.ProductionParts) {
                    if (productionPart instanceof SymbolPart) {
                        if (((SymbolPart) productionPart).LeftLabel.Lexeme != null) {
                            productionDetailList.add(((SymbolPart) productionPart).LeftLabel.Lexeme);
                        }
                    }
                }
                productions.add(productionDetailList);
            }
            productionMap.put(productionStatementNode.LeftHandSide.Lexeme, productions);
        }
        return productionMap;
    }
}
