package Automata;

import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.NonterminalType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;

import java.util.*;

public class Automata {

    private static final String EOF = "$";
    private static final String EPSILON = "ɛ";
    private static final String PrimeSymbol = "S'";
    private static Integer namingValueNumber = 1;
    private static Integer statesCount = 1;
    private static ArrayList<State> States = new ArrayList<>();
    private static Map<String, ArrayList<String>> allLookUps = new HashMap<>();
    public ArrayList<GrammarDetail> minimizedGrammar;

    public ArrayList<State> generateAllStates(ArrayList<ProductionStatementNode> productionStatements) throws Exception {
        minimizedGrammar = minimizeGrammar(productionStatements);
        ArrayList<ProductionStatementNode> productionStatementNodes = new ArrayList<>();
        productionStatementNodes.addAll(productionStatements);
        Map<String, ArrayList<RightHandSideNode>> rightHandSideProductionMap = GrammarUtilies.getProductionMap(productionStatementNodes);
        Map<String, ArrayList<ArrayList<String>>> productionMap = ProductionsUtilities.getProductionMap(productionStatements);
        State state = createFirstState(productionStatements, rightHandSideProductionMap);
        States.add(state);
        Integer stateIndex = 0;
        while (stateIndex < statesCount) {
            getNextNodes(States.get(stateIndex), rightHandSideProductionMap, productionMap);
            stateIndex++;
        }
        return getReducedAutomata(States);
    }

    private ArrayList<GrammarDetail> minimizeGrammar(ArrayList<ProductionStatementNode> productionNodeList) {
        ArrayList<GrammarDetail> returnTable = new ArrayList<>();
        for (StatementNode statementNode : productionNodeList) {
            if (statementNode instanceof ProductionStatementNode) {
                String leftName = ((ProductionStatementNode) statementNode).LeftHandSide.Lexeme;
                for (RightHandSideNode rightHandSideNode : ((ProductionStatementNode) statementNode).RightHandSideList) {
                    ArrayList<String> parts = new ArrayList<>();
                    for (ProductionPart productionPart : rightHandSideNode.ProductionParts) {
                        if (productionPart instanceof SymbolPart) {
                            parts.add(((SymbolPart) productionPart).LeftLabel.Lexeme);
                        }
                    }
                    returnTable.add(new GrammarDetail(leftName, parts));
                }

            }
        }
        return returnTable;
    }

    private static ArrayList<State> getReducedAutomata(ArrayList<State> states) {

        ArrayList<String> alreadySearchedStates = new ArrayList<>();
        ArrayList<State> reducedAutomata = new ArrayList<>();
        Integer namingComplementation = 0;
        for (Integer i = 0; i < states.size(); i++) {
            for (Integer j = 0; j < states.size(); j++) {
                if (!states.get(i).Name.equals(states.get(j).Name) && !alreadySearchedStates.contains(states.get(i).Name)) {
                    if (checkIfStatesAreEqual(states.get(i), states.get(j))) {
                        for (Integer k = 0; k < states.get(i).Productions.size(); k++) {
                            states.get(i).Productions.get(k).LookAhead.addAll(states.get(j).Productions.get(k).LookAhead);
                            Set<String> lookAheadToAdd = new HashSet<>();
                            lookAheadToAdd.addAll(states.get(i).Productions.get(k).LookAhead);
                            states.get(i).Productions.get(k).LookAhead.clear();
                            states.get(i).Productions.get(k).LookAhead.addAll(lookAheadToAdd);
                        }
                        alreadySearchedStates.add(states.get(j).Name);
                        for (State state : states) {
                            for (Object terminalTransition : state.TerminalTransitions.entrySet()) {
                                Map.Entry pair = (Map.Entry) terminalTransition;
                                if (((State) pair.getValue()).Name.equals(states.get(j).Name))
                                    pair.setValue(states.get(i));
                            }

                            for (Object nonTerminalTransition : state.NonTerminalTransitions.entrySet()) {
                                Map.Entry pair = (Map.Entry) nonTerminalTransition;

                                if (((State) pair.getValue()).Name.equals(states.get(j).Name))
                                    pair.setValue(states.get(i));
                            }
                        }
                    }
                }
            }
            if (!alreadySearchedStates.contains(states.get(i).Name)) {
                states.get(i).Name = "I" + namingComplementation;
                namingComplementation++;
                reducedAutomata.add(states.get(i));
            }
        }
        return reducedAutomata;
    }

    private static void getNextNodes(State state, Map<String, ArrayList<RightHandSideNode>> rightHandSideProductionMap, Map<String, ArrayList<ArrayList<String>>> productionMap) throws Exception {
        ArrayList<String> alreadyCheckedSymbols = new ArrayList<>();
        for (ProductionDetail productionDetail : state.Productions) {
            if (productionDetail.Pointer < productionDetail.RightHandSideProductions.size()) {
                if (!alreadyCheckedSymbols.contains(productionDetail.RightHandSideProductions.get(productionDetail.Pointer))) {
                    State stateToCheck = goTo(productionDetail.RightHandSideProductions.get(productionDetail.Pointer), state.Productions, rightHandSideProductionMap, productionMap);
                    stateToCheck = getNewState(stateToCheck);
                    if (SymbolTable.getInstance().GetType(productionDetail.RightHandSideProductions.get(productionDetail.Pointer)) instanceof TerminalType) {
                        String symbol = productionDetail.RightHandSideProductions.get(productionDetail.Pointer);
                        state.TerminalTransitions.put(symbol, stateToCheck);
                    } else {
                        String symbol = productionDetail.RightHandSideProductions.get(productionDetail.Pointer);
                        state.NonTerminalTransitions.put(symbol, stateToCheck);
                    }
                    alreadyCheckedSymbols.add(productionDetail.RightHandSideProductions.get(productionDetail.Pointer));
                    allLookUps.clear();
                }
            }
        }
    }

    private static State getNewState(State stateToCheck) {

        for (State state : States) {
            if (checkIfStatesAreSame(state, stateToCheck))
                return state;
        }
        stateToCheck.Name = "I" + namingValueNumber;
        namingValueNumber++;
        States.add(stateToCheck);
        statesCount = States.size();
        return stateToCheck;
    }

    private static boolean checkIfStatesAreEqual(State stateA, State stateB) {
        if (stateA.Productions.size() != stateB.Productions.size())
            return false;

        for (Integer i = 0; i < stateA.Productions.size(); i++) {
            if (!stateA.Productions.get(i).LeftHandSideKey.equals(stateB.Productions.get(i).LeftHandSideKey))
                return false;

            if (stateA.Productions.get(i).RightHandSideProductions.size() != stateB.Productions.get(i).RightHandSideProductions.size())
                return false;

            ArrayList<String> rightHandSideProductionsA = new ArrayList<>(stateA.Productions.get(i).RightHandSideProductions);
            ArrayList<String> rightHandSideProductionsB = new ArrayList<>(stateB.Productions.get(i).RightHandSideProductions);

            if (!rightHandSideProductionsA.equals(rightHandSideProductionsB))
                return false;

            if (!Objects.equals(stateA.Productions.get(i).Pointer, stateB.Productions.get(i).Pointer))
                return false;

        }

        return true;
    }

    private static boolean checkIfStatesAreSame(State stateA, State stateB) {
        if (stateA.Productions.size() != stateB.Productions.size())
            return false;

        for (Integer i = 0; i < stateA.Productions.size(); i++) {
            if (!stateA.Productions.get(i).LeftHandSideKey.equals(stateB.Productions.get(i).LeftHandSideKey))
                return false;
            if (stateA.Productions.get(i).RightHandSideProductions.size() != stateB.Productions.get(i).RightHandSideProductions.size())
                return false;

            ArrayList<String> rightHandSideProductionsA = new ArrayList<>(stateA.Productions.get(i).RightHandSideProductions);
            ArrayList<String> rightHandSideProductionsB = new ArrayList<>(stateB.Productions.get(i).RightHandSideProductions);
            if (!rightHandSideProductionsA.equals(rightHandSideProductionsB))
                return false;

            ArrayList<String> loohAkeadA = new ArrayList<>(stateA.Productions.get(i).LookAhead);
            ArrayList<String> loohAkeadB = new ArrayList<>(stateB.Productions.get(i).LookAhead);
            if (!loohAkeadA.equals(loohAkeadB))
                return false;

            if (!Objects.equals(stateA.Productions.get(i).Pointer, stateB.Productions.get(i).Pointer))
                return false;

        }

        return true;
    }

    private static State goTo(String s, ArrayList<ProductionDetail> productionDetailArrayList, Map<String, ArrayList<RightHandSideNode>> rhsGrammarTable, Map<String, ArrayList<ArrayList<String>>> grammarTable) throws Exception {
        State state = new State();
        ArrayList<String> doneSymbols = new ArrayList<>();
        for (ProductionDetail productionDetail : productionDetailArrayList) {
            if (productionDetail.Pointer < productionDetail.RightHandSideProductions.size()) {
                if (productionDetail.RightHandSideProductions.get(productionDetail.Pointer).equals(s)) {
                    ProductionDetail currentProductionDetail = new ProductionDetail(productionDetail.LeftHandSideKey);
                    currentProductionDetail.RightHandSideProductions.addAll(productionDetail.RightHandSideProductions);
                    currentProductionDetail.Pointer = productionDetail.Pointer + 1;
                    currentProductionDetail.LookAhead.addAll(productionDetail.LookAhead);
                    state.Productions.add(currentProductionDetail);
                    if (currentProductionDetail.Pointer < currentProductionDetail.RightHandSideProductions.size()) {
                        if (!doneSymbols.contains(currentProductionDetail.RightHandSideProductions.get(currentProductionDetail.Pointer))) {
                            if (SymbolTable.getInstance().GetType(currentProductionDetail.RightHandSideProductions.get(currentProductionDetail.Pointer)) instanceof NonterminalType) {
                                ArrayList<ProductionDetail> closure = getClosure(currentProductionDetail, grammarTable, rhsGrammarTable);
                                state.Productions.addAll(closure);
                                doneSymbols.add(currentProductionDetail.RightHandSideProductions.get(currentProductionDetail.Pointer));
                            }
                        }
                    }
                }
            }
        }
        return state;
    }

    private static State createFirstState(ArrayList<ProductionStatementNode> productionNodeList, Map<String, ArrayList<RightHandSideNode>> firstTable) throws Exception {
        Map<String, ArrayList<ArrayList<String>>> grammarTable = ProductionsUtilities.getProductionMap(productionNodeList);
        State state = new State();
        state.Name = "I0";
        ProductionDetail productionDetail = new ProductionDetail(PrimeSymbol);
        productionDetail.LookAhead.add(EOF);
        productionDetail.RightHandSideProductions.addAll(grammarTable.get(PrimeSymbol).get(0));
        state.Productions.add(productionDetail);

        if (SymbolTable.getInstance().GetType(productionDetail.RightHandSideProductions.get(productionDetail.Pointer)) instanceof NonterminalType) {
            ArrayList<ProductionDetail> closure = getClosure(productionDetail, grammarTable, firstTable);
            state.Productions.addAll(closure);
        }

        return state;
    }

    private static ArrayList<ProductionDetail> getClosure(ProductionDetail productionDetail, Map<String, ArrayList<ArrayList<String>>> productionMap, Map<String, ArrayList<RightHandSideNode>> rhsGrammarTable) throws Exception {
        ArrayList<ProductionDetail> closure = new ArrayList<>();
        String postDot = productionDetail.RightHandSideProductions.get(productionDetail.Pointer);
        ArrayList<String> lookAhead = getLookAhead(productionDetail, rhsGrammarTable);
        ArrayList<ArrayList<String>> productions = productionMap.get(postDot);
        for (ArrayList<String> production : productions) {
            if (!postDot.equals(PrimeSymbol)) {
                ProductionDetail toAdd = new ProductionDetail(postDot);
                toAdd.RightHandSideProductions.addAll(production);
                toAdd.LookAhead.addAll(lookAhead);
                if (toAdd.RightHandSideProductions.get(toAdd.Pointer).equals(EPSILON)) {
                    toAdd.Pointer++;
                }
                closure.add(toAdd);
                if (toAdd.Pointer < toAdd.RightHandSideProductions.size()) {
                    if (toAdd.RightHandSideProductions.get(toAdd.Pointer).equals(postDot) && toAdd.Pointer + 1 < toAdd.RightHandSideProductions.size()) {
                        Integer i = 1;
                        while (true) {
                            ArrayList<String> first = GrammarUtilies.getFirst(toAdd.RightHandSideProductions.get(toAdd.Pointer + i), rhsGrammarTable);
                            toAdd.LookAhead.addAll(first);
                            if (!first.contains(EPSILON)) {
                                break;
                            }
                            if (toAdd.Pointer + i + 1 == toAdd.RightHandSideProductions.size()) {
                                toAdd.LookAhead.addAll(productionDetail.LookAhead);
                                Set<String> toClearLookAhead = new HashSet<>();
                                toClearLookAhead.addAll(toAdd.LookAhead);
                                toAdd.LookAhead.clear();
                                toAdd.LookAhead.addAll(toClearLookAhead);
                                break;
                            }
                            i++;
                        }

                        if (allLookUps.containsKey(toAdd.LeftHandSideKey)) {
                            allLookUps.get(toAdd.LeftHandSideKey).addAll(toAdd.LookAhead);
                            Set<String> toClearLookAhead = new HashSet<>();
                            toClearLookAhead.addAll(allLookUps.get(toAdd.LeftHandSideKey));
                            allLookUps.get(toAdd.LeftHandSideKey).clear();
                            allLookUps.get(toAdd.LeftHandSideKey).addAll(toClearLookAhead);
                        } else {
                            allLookUps.put(toAdd.LeftHandSideKey, toAdd.LookAhead);
                        }
                    }
                    if (allLookUps.containsKey(toAdd.LeftHandSideKey)) {
                        toAdd.LookAhead.addAll(allLookUps.get(toAdd.LeftHandSideKey));
                        Set<String> toClearLookAhead = new HashSet<>();
                        toClearLookAhead.addAll(toAdd.LookAhead);
                        toAdd.LookAhead.clear();
                        toAdd.LookAhead.addAll(toClearLookAhead);
                    }
                    if (SymbolTable.getInstance().GetType(toAdd.RightHandSideProductions.get(toAdd.Pointer)) instanceof NonterminalType
                            && !toAdd.RightHandSideProductions.get(toAdd.Pointer).equals(postDot)) {
                        ArrayList<ProductionDetail> myClosure = getClosure(toAdd, productionMap, rhsGrammarTable) ;
                        closure.addAll(myClosure);
                    }
                }
            }
        }
        return closure;
    }

    private static ArrayList<String> getLookAhead(ProductionDetail productionDetail, Map<String, ArrayList<RightHandSideNode>> rightHandSideProductionMap) throws Exception {
        ArrayList<String> lookAhead = new ArrayList<>();
        Integer pointer = productionDetail.Pointer;
        if (pointer + 1 == productionDetail.RightHandSideProductions.size()) {
            lookAhead.addAll(productionDetail.LookAhead);
        } else if (productionDetail.RightHandSideProductions.get(pointer + 1).equals(EPSILON)) {
            lookAhead.addAll(productionDetail.LookAhead);
        } else {
            Integer i = 1;
            while (true) {
                ArrayList<String> first = GrammarUtilies.getFirst(productionDetail.RightHandSideProductions.get(pointer + i), rightHandSideProductionMap);
                lookAhead.addAll(first);
                if (!first.contains(EPSILON)) {
                    break;
                }
                if (pointer + i + 1 == productionDetail.RightHandSideProductions.size()) {
                    lookAhead.addAll(productionDetail.LookAhead);
                    break;
                }
                i++;
            }
        }
        Set<String> toClearLookAhead = new HashSet<>();
        toClearLookAhead.addAll(lookAhead);
        lookAhead.clear();
        lookAhead.addAll(toClearLookAhead);
        return lookAhead;
    }
}
