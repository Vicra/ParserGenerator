package Automaton;

import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.NonterminalType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.JavaCodePart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;

import java.util.ArrayList;

/**
 * Created by vicra on 2/17/2017.
 */
public class Automata {
    public ArrayList<StatementNode> Tree;
    private static final String EPSILON = "ε";


    public Automata(ArrayList<StatementNode> tree) {
        Tree = tree;
    }

    public ArrayList<String> getPrimero(ProductionStatementNode production) throws SemanticException {
        RightHandSideNode ultimaProduccion = production.RightHandSideList.get(production.RightHandSideList.size()-1);
        ArrayList<String> result = new ArrayList<>();
        for(int i = production.RightHandSideList.size()-1 ; i >= 0 ; i --){
            RightHandSideNode produccion = production.RightHandSideList.get(i);

            if(produccion.ProductionParts.size() == 0){
                result.add(EPSILON);
                continue;
            }

            if( produccion.ProductionParts.get(0) instanceof SymbolPart){
                SymbolPart symbol = (SymbolPart)produccion.ProductionParts.get(0);
                BaseType type = SymbolTable.getInstance().GetType(symbol.Symbol.Lexeme);
                if(type instanceof TerminalType){
                //todo: add id to list
                    continue;
                }else{
                    if(result.contains(EPSILON) && symbol.Symbol.Lexeme.equals(production.LeftHandSide.Lexeme)){
                        for(int n = 1; n < produccion.ProductionParts.size(); n++){
                            if(produccion.ProductionParts.get(n) instanceof JavaCodePart)
                                continue;
                            symbol = (SymbolPart)produccion.ProductionParts.get(n);
                            ArrayList<String> primero = getPrimero(getProductionStatement(symbol.Symbol.Lexeme));
                            if(!(primero.size()==1 && primero.contains(EPSILON)))
                                break;
                        }
                         //todo: change 1 to n

                    }
                    ArrayList<String> primero = getPrimero(getProductionStatement(symbol.Symbol.Lexeme));
                    //todo: add all first to list
                    continue;
                }
            }else{

            }

        }
    }

    private ProductionStatementNode getProductionStatement(String id) {
        for (StatementNode node :
                Tree) {
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
