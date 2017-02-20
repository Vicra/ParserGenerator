package Automaton;

import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.JavaCodePart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Automata {
    public ArrayList<StatementNode> Statements;
    private static final String EPSILON = "ε";


    public Automata(ArrayList<StatementNode> statements) {
        Statements = statements;
    }

    public ArrayList<String> getPrimero(ProductionStatementNode production) throws SemanticException {
        ArrayList<String> result = new ArrayList<>();

        for(int i = production.RightHandSideList.size()-1 ; i >= 0 ; i --){
            RightHandSideNode rightSideProduction = production.RightHandSideList.get(i);

            if(rightSideProduction.ProductionParts.size() == 0){
                result.add(EPSILON);
            }

            if(rightSideProduction.ProductionParts.get(0) instanceof SymbolPart){
                SymbolPart symbol = (SymbolPart)rightSideProduction.ProductionParts.get(0);
                BaseType type = SymbolTable.getInstance().GetType(symbol.LeftLabel.Lexeme);
                if(type instanceof TerminalType){
                    result.add(symbol.LeftLabel.Lexeme);
                    continue;
                }else{
                    if(result.contains(EPSILON) && symbol.LeftLabel.Lexeme.equals(production.LeftHandSide.Lexeme)){
                        for(int n = 1; n < rightSideProduction.ProductionParts.size(); n++){
                            if(rightSideProduction.ProductionParts.get(n) instanceof JavaCodePart)
                                continue;
                            symbol = (SymbolPart)rightSideProduction.ProductionParts.get(n);
                            ArrayList<String> primero = getPrimero(getProductionStatement(symbol.LeftLabel.Lexeme));
                            if(!(primero.size()==1 && primero.contains(EPSILON))){
                                result.addAll(primero);
                                return result;
                            }
                        }
                    }
                    else {
                        ArrayList<String> primero = getPrimero(getProductionStatement(symbol.LeftLabel.Lexeme));
                        result.addAll(primero);
                        continue;
                    }
                }
            }
        }
        return result;
    }

    private ProductionStatementNode getProductionStatement(String id) {
        for (StatementNode node :
                Statements) {
            if (node instanceof ProductionStatementNode){
                ProductionStatementNode productionStatement = (ProductionStatementNode) node;
                if(productionStatement.LeftHandSide.Lexeme.equals(id)){
                    return productionStatement;
                }
            }
        }
        return null;
    }
}
