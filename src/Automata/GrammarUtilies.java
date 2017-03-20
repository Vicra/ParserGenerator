package Automata;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.NonterminalType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.JavaCodePart;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatementNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.NonTerminalDeclarationNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.TerminalDeclarationNode;

import java.util.*;

public class GrammarUtilies {
    private static String EPSILON = "ɛ";

    public static Map<String, ArrayList<RightHandSideNode>> getProductionMap(List<ProductionStatementNode> productionNodeList) {
        Map<String, ArrayList<RightHandSideNode>> returnProductionMap = new HashMap<>();
        for (ProductionStatementNode productionStatementNode : productionNodeList) {
            returnProductionMap.put(productionStatementNode.LeftHandSide.Lexeme, productionStatementNode.RightHandSideList);
        }
        return returnProductionMap;
    }

    public static Map<String, String> getTypeMap(ArrayList<SymbolListStatementNode> symbolList) {
        Map<String, String> returnTypeMap = new HashMap<>();
        for (SymbolListStatementNode symbolListStatementNode : symbolList) {
            for (Token symbolToken : symbolListStatementNode.DeclaresTerm) {
                if (symbolListStatementNode instanceof TerminalDeclarationNode) {
                    if (((TerminalDeclarationNode) symbolListStatementNode).MultiPart != null) {
                        returnTypeMap.put(symbolToken.Lexeme, (((TerminalDeclarationNode) symbolListStatementNode).MultiPart.get(0).Lexeme));
                    } else {
                        returnTypeMap.put(symbolToken.Lexeme, "Object");
                    }
                } else if (symbolListStatementNode instanceof NonTerminalDeclarationNode) {
                    if (((NonTerminalDeclarationNode) symbolListStatementNode).MultiPart != null) {
                        returnTypeMap.put(symbolToken.Lexeme, (((NonTerminalDeclarationNode) symbolListStatementNode).MultiPart.get(0).Lexeme));
                    } else {
                        returnTypeMap.put(symbolToken.Lexeme, "Object");
                    }
                }
            }
        }
        return returnTypeMap;
    }

    private static ArrayList<RightHandSideNode> orderProductions(String name, ArrayList<RightHandSideNode> rightHandSideNodeList) {
        Integer i = 0;
        Integer j = 0;

        while (i < rightHandSideNodeList.size()) {
            while (j < rightHandSideNodeList.get(i).ProductionParts.size()) {
                if (rightHandSideNodeList.get(i).ProductionParts.get(j) instanceof SymbolPart) {
                    if (((SymbolPart) rightHandSideNodeList.get(i).ProductionParts.get(j)).LeftLabel != null) {
                        if (((SymbolPart) rightHandSideNodeList.get(i).ProductionParts.get(j)).LeftLabel.Lexeme.equals(name)) {
                            if (j + 1 < rightHandSideNodeList.get(i).ProductionParts.size()) {
                                RightHandSideNode rightHandSideNode = rightHandSideNodeList.get(i);
                                rightHandSideNodeList.remove(i);
                                rightHandSideNodeList.add(rightHandSideNode);
                            } else {
                                j++;
                            }
                        } else {
                            j++;
                        }
                    } else {
                        j++;
                    }
                }
            }
            i++;
        }
        return rightHandSideNodeList;
    }

    public static ArrayList<String> getFirst(String keyToSearch, Map<String, ArrayList<RightHandSideNode>> productionMap) throws Exception {
        ArrayList<String> FirstList = new ArrayList<>();
        if (SymbolTable.getInstance().GetType(keyToSearch) instanceof TerminalType) {
            FirstList.add(keyToSearch);
            return FirstList;
        }
        ArrayList<RightHandSideNode> rightHandSideNodeArrayList = productionMap.get(keyToSearch);
        rightHandSideNodeArrayList = orderProductions(keyToSearch, rightHandSideNodeArrayList);
        for (RightHandSideNode rightHandSideNode : rightHandSideNodeArrayList) {
            for (ProductionPart productionPart : rightHandSideNode.ProductionParts) {
                ArrayList<String> resultY;
                if (productionPart instanceof SymbolPart) {
                    if (((SymbolPart) productionPart).LeftLabel != null)
                        if (((SymbolPart) productionPart).LeftLabel.Lexeme.equals(keyToSearch)) {
                            if (!FirstList.contains(EPSILON))
                                break;
                        } else {
                            resultY = getFirst(((SymbolPart) productionPart).LeftLabel.Lexeme, productionMap);
                            FirstList.addAll(resultY);
                            if (!resultY.contains(EPSILON))
                                break;
                        }
                }
            }
        }
        return FirstList;
    }

    public static ArrayList<GrammarDetail> getGrammarDetails(ArrayList<ProductionStatementNode> productionNodeList, Map<String, String> typeTable) {
        ArrayList<GrammarDetail> nonSimplifiedGrammarTable = new ArrayList<>();
        for (ProductionStatementNode productionNode : productionNodeList) {
            for (RightHandSideNode rightHandSideNode : productionNode.RightHandSideList) {
                ArrayList<String> toAdd = new ArrayList<>();
                ArrayList<Label> labelList = new ArrayList<>();
                ArrayList<String> javaCodeList = new ArrayList<>();
                for (Integer i = 0; i < rightHandSideNode.ProductionParts.size(); i++) {
                    if (rightHandSideNode.ProductionParts.get(i) instanceof SymbolPart) {
                        if (((SymbolPart) rightHandSideNode.ProductionParts.get(i)).LeftLabel != null) {
                            toAdd.add(((SymbolPart) rightHandSideNode.ProductionParts.get(i)).LeftLabel.Lexeme);
                            if (((SymbolPart) rightHandSideNode.ProductionParts.get(i)).LeftLabel.Lexeme != null) {
                                labelList.add(new Label(((SymbolPart) rightHandSideNode.ProductionParts.get(i)).LeftLabel.Lexeme,
                                        typeTable.get(((SymbolPart) rightHandSideNode.ProductionParts.get(i)).LeftLabel.Lexeme), i));
                            }
                        }
                    } else {
                        javaCodeList.add(((JavaCodePart) rightHandSideNode.ProductionParts.get(i)).JavaCodeToken.Lexeme);
                    }
                }
                nonSimplifiedGrammarTable.add(new GrammarDetail(productionNode.LeftHandSide.Lexeme, toAdd, labelList, javaCodeList));
            }
        }
        return nonSimplifiedGrammarTable;
    }


    public static String getReductionName(ProductionDetail productionDetail, ArrayList<GrammarDetail> grammar) {
        for (Integer i = 0; i < grammar.size(); i++) {
            if (productionDetail.RightHandSideProductions.equals(grammar.get(i).RightHandSide)) {
                return "r" + (i + 1);
            }
        }
        return null;
    }
}
