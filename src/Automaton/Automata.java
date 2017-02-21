package Automaton;

import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.NonterminalType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;

import java.util.ArrayList;

public class Automata {
    public ArrayList<StatementNode> Statements;
    private static final String EPSILON = "ε";


    public Automata(ArrayList<StatementNode> statements) {
        Statements = statements;
    }

    public ArrayList<String> getFirst(String symbol) throws SemanticException {
        ArrayList<String> FirstList = new ArrayList<>();
        if (symbol.equals(EPSILON)){
            FirstList.add(EPSILON);
            return FirstList;
        }
        String leftSide = symbol;
        BaseType type = SymbolTable.getInstance().GetType(leftSide);
        if (type instanceof TerminalType){
            FirstList.add(leftSide);
            return FirstList;
        }

        ProductionStatementNode productionStatementNode = getProductionStatement(symbol);
        ArrayList<RightHandSideNode> orderedRightHandSideList = null;
        if (productionStatementNode != null) {
            orderedRightHandSideList = OrderStatements(leftSide, productionStatementNode.RightHandSideList);
        }
        if (orderedRightHandSideList != null) {
            for (RightHandSideNode rightHandSideNode : orderedRightHandSideList){
                if (rightHandSideNode.ProductionParts.size() == 0){
                    FirstList.add(EPSILON);
                }
                for(ProductionPart productionPart : rightHandSideNode.ProductionParts){
                    ArrayList<String> resultY;
                    if (productionPart instanceof SymbolPart){
                        SymbolPart currentPart = (SymbolPart)productionPart;
                        if (currentPart.LeftLabel.Lexeme.equals(leftSide)){
                            if (!FirstList.contains(EPSILON)){
                                break;
                            }
                        }
                        else{
                            BaseType currentType = SymbolTable.getInstance().GetType(currentPart.LeftLabel.Lexeme);
                            if (currentType instanceof NonterminalType){
                                resultY = getFirst(currentPart.LeftLabel.Lexeme);
                                FirstList.addAll(resultY);
                                if (!resultY.contains(EPSILON)){
                                    break;
                                }
                            }
                            else{
                                FirstList.add(currentPart.LeftLabel.Lexeme);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return FirstList;
    }

    public ArrayList<String> getNext(String symbol) throws SemanticException {
        ArrayList<String> NextList = new ArrayList<>();
        ArrayList<RightHandSideNode> rightProductionsThatContainSymbol = getRightProductionsThatContainSymbol(symbol);
        for (RightHandSideNode rightHandSideNode : rightProductionsThatContainSymbol){
            String nextSymbol = getNextSymbol(rightHandSideNode, symbol);
            ArrayList<String> firstList_nextSymbol = getFirst(nextSymbol);

            for (String firsts : firstList_nextSymbol){
                if (!firsts.equals(EPSILON)){
                    NextList.add(firsts);
                }
            }
            if (firstList_nextSymbol.contains(EPSILON)){
                String leftSide = getLeftSideProduction(rightHandSideNode);
                ArrayList<String> leftSide_nextSymbol = getNext(leftSide);
                for (String leftSideFirsts : leftSide_nextSymbol){
                    NextList.add(leftSideFirsts);
                }
            }
        }
        return NextList;
    }

    private String getLeftSideProduction(RightHandSideNode rightHandSideNode) {
        for (StatementNode statementNode : Statements){
            if (statementNode instanceof ProductionStatementNode){
                for (RightHandSideNode node : ((ProductionStatementNode) statementNode).RightHandSideList){
                    if (node.equals(rightHandSideNode))
                        return ((ProductionStatementNode) statementNode).LeftHandSide.Lexeme;
                }
            }
        }
        return null;
    }

    private String getNextSymbol(RightHandSideNode rightHandSideNode, String currentSymbol) {
        for (int i=0; i< rightHandSideNode.ProductionParts.size(); i++){
            ProductionPart productionPart = rightHandSideNode.ProductionParts.get(i);
            if (productionPart instanceof SymbolPart){
                SymbolPart currentPart = (SymbolPart)productionPart;
                if (currentPart.LeftLabel.Lexeme.equals(currentSymbol)){
                    if (i+1 >= rightHandSideNode.ProductionParts.size()){
                        return EPSILON;
                    }
                    else{
                        ProductionPart nextPart = rightHandSideNode.ProductionParts.get(i+1);
                        if (nextPart instanceof SymbolPart){
                            return ((SymbolPart) nextPart).LeftLabel.Lexeme;
                        }
                    }
                }
            }
        }
        return null;
    }

    private ArrayList<RightHandSideNode> getRightProductionsThatContainSymbol(String symbol) {
        ArrayList<RightHandSideNode> returnArray = new ArrayList<>();
        for (StatementNode statementNode : Statements){
            if (statementNode instanceof ProductionStatementNode){
                ProductionStatementNode currentStatement = (ProductionStatementNode)statementNode;
                for (RightHandSideNode rightHandSideNode : currentStatement.RightHandSideList){
                    boolean contains = false;
                    for (ProductionPart productionPart : rightHandSideNode.ProductionParts){
                        if (productionPart instanceof SymbolPart){
                            SymbolPart currentPart = (SymbolPart)productionPart;
                            if (currentPart.LeftLabel.Lexeme.equals(symbol)){
                                contains = true;
                            }
                        }
                    }
                    if (contains){
                        returnArray.add(rightHandSideNode);
                    }
                }
            }
        }
        return returnArray;
    }

    private ArrayList<RightHandSideNode> OrderStatements(String leftSide, ArrayList<RightHandSideNode> rightHandSideList) {
        ArrayList<RightHandSideNode> returnOrderedList = new ArrayList<>();
        for (RightHandSideNode rightNode : rightHandSideList){
            if (rightNode.ProductionParts.size() == 0){
                returnOrderedList.add(rightNode);
            }
        }
        for (RightHandSideNode rightNode : rightHandSideList){
            if(!returnOrderedList.contains(rightNode)){
                boolean contains = false;
                for (ProductionPart part : rightNode.ProductionParts){
                    if (part instanceof SymbolPart){
                        SymbolPart currentPart = (SymbolPart) part;
                        if (currentPart.LeftLabel.Lexeme.equals(leftSide)){
                            contains = true;
                        }
                    }
                }
                if (!contains){
                    returnOrderedList.add(rightNode);
                }
            }
        }
        for (RightHandSideNode rightNode : rightHandSideList){
            if(!returnOrderedList.contains(rightNode)){
                returnOrderedList.add(rightNode);
            }
        }
        return returnOrderedList;
    }

    private ProductionStatementNode getProductionStatement(String id) {
        for (StatementNode node : Statements) {
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
