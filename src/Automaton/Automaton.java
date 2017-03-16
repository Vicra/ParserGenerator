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
import com.google.gson.GsonBuilder;
import javafx.util.Pair;

import java.io.PrintWriter;
import java.util.*;

import static java.util.Arrays.asList;

public class Automaton {
    public ArrayList<StatementNode> Statements;
    public ArrayList<Pair<String, ArrayList<ArrayList<String>>>> MinimizedGrammar;
    private static final String EPSILON = "ε";
    private static final String EOF = "$";
    private DetalleProduccion Aumentada;
    public ArrayList<State> States = new ArrayList<>();
    private static Map<String, List<String>> allLookUps = new HashMap<>();
    private int automatonSize = 1;


    public Automaton(ArrayList<StatementNode> statements) {
        Statements = statements;
        MinimizedGrammar = minimizeGrammar(Statements);
        Aumentada = getMinimizedAumentada();
    }

    public State generateFirstState() throws Exception {
        State initialState = new State("I0");
        initialState.isInitialState = true;
        ComponenteInicial componenteInicial = new ComponenteInicial(Aumentada);

        DetalleProduccion produccionAumentada = componenteInicial.Producciones.get(0);
        String simboloEnPuntero = produccionAumentada.rhs.get(produccionAumentada.puntero);
        if (isNonTerminalType(simboloEnPuntero)) {
            Pair<String, ArrayList<ArrayList<String>>> productionStatementNode = getMinimizedProductionStatement(simboloEnPuntero);
            for (ArrayList<String> rhProduction : productionStatementNode.getValue()) {
                DetalleProduccion rootProduccion = new DetalleProduccion();
                rootProduccion.LeftSideKey = productionStatementNode.getKey();
                rootProduccion.rhs = productionStatementNode.getValue().get(0);
                rootProduccion.rhs = rhProduction;
                ArrayList<String> conjunto = new ArrayList<>(asList("$"));
                rootProduccion.conjunto.addAll(conjunto);
                componenteInicial.Producciones.add(rootProduccion);
            }
            ArrayList<DetalleProduccion> clausura = GetClosure(componenteInicial.Producciones.get(0));
            for (DetalleProduccion clausuraItem : clausura) {
                if (!componenteInicial.Producciones.contains(clausuraItem)) {
                    componenteInicial.Producciones.add(clausuraItem);
                }
            }
        }
        initialState.componente = componenteInicial;
        return initialState;
    }

    public ArrayList<State> generateStates() throws Exception {
        State firstState = generateFirstState();
        States.add(firstState);
        int currentPosition = 0;
        while (currentPosition < automatonSize) {
            createNextState(currentPosition);
            currentPosition++;
        }
        String primeros = new GsonBuilder().setPrettyPrinting().create().toJson(States);
        PrintWriter writer = new PrintWriter("automata.txt", "UTF-8");
        writer.println(primeros);
        writer.close();

        reorderDetalleProduccion();

        return reduceStates(this.States);
    }

    private ArrayList<State> reduceStates(ArrayList<State> states) {
        List<String> stringArrayList = new ArrayList<>();
        ArrayList<State> reducedAutomata = new ArrayList<>();
        int stateNumericName = 0;
        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < states.size(); j++) {
                if (!states.get(i).name.equals(states.get(j).name) && !stringArrayList.contains(states.get(i).name)) {
                    if (CompareStates(states.get(i), states.get(j))) {
                        for (int k = 0; k < states.get(i).componente.Producciones.size(); k++) {
                            states.get(i).componente.Producciones.get(k).conjunto.addAll(states.get(j).componente.Producciones.get(k).conjunto);
                            Set<String> lookAheadActual = new HashSet<>();
                            lookAheadActual.addAll(states.get(i).componente.Producciones.get(k).conjunto);
                            states.get(i).componente.Producciones.get(k).conjunto.clear();
                            states.get(i).componente.Producciones.get(k).conjunto.addAll(lookAheadActual);
                        }
                        stringArrayList.add(states.get(j).name);
                        for (State state : states) {
                            for (Transition transition : state.transitions) {
                                if (transition.destiny.equals(states.get(j).name))
                                    transition.destiny = states.get(i).name;
                            }
                        }
                    }
                }
            }
            if (!stringArrayList.contains(states.get(i).name)) {
                states.get(i).name = "I" + stateNumericName;
                stateNumericName++;
                reducedAutomata.add(states.get(i));
            }
        }
        return reducedAutomata;
    }

    private boolean CompareStates(State state, State state1) {
        if (state.componente.Producciones.size() != state1.componente.Producciones.size())
            return false;
        for (int i = 0; i < state.componente.Producciones.size(); i++) {
            if (!state.componente.Producciones.get(i).LeftSideKey.equals(state1.componente.Producciones.get(i).LeftSideKey))
                return false;
            if (state.componente.Producciones.get(i).rhs.size() != state1.componente.Producciones.get(i).rhs.size())
                return false;
            ArrayList<String> rightHandSideA = new ArrayList<>(state.componente.Producciones.get(i).rhs);
            ArrayList<String> rightHandSideB = new ArrayList<>(state1.componente.Producciones.get(i).rhs);
            if (!rightHandSideA.equals(rightHandSideB))
                return false;
            if (state.componente.Producciones.get(i).puntero != state1.componente.Producciones.get(i).puntero)
                return false;
        }
        return true;
    }

    public String printAutomata(ArrayList<State> states) {
        String cadena = "/*AUTOMATA*/\n";
        for (State state : states) {
            cadena += state.name + "\n";
            if (state.transitions.size() > 0) {
                cadena += "Transiciones" + "\n";
                for (Transition transition : state.transitions) {
                    cadena += "origin:" + transition.origin + " destiny:" + transition.destiny + " symbol:" + transition.symbol + "\n";
                }
            }
            cadena += "\n";
            cadena += "Producciones" + "\n";
            for (DetalleProduccion produccion : state.componente.Producciones) {
                cadena += produccion.LeftSideKey;
                cadena += "->";
                for (int i = 0; i < produccion.rhs.size(); i++) {
                    if (i == produccion.puntero) {
                        cadena += "•";
                    }
                    cadena += produccion.rhs.get(i) + " ";
                }
                if (produccion.rhs.size() == produccion.puntero) {
                    cadena += "•";
                }
                cadena += ",{";
                for (int i = 0; i < produccion.conjunto.size(); i++) {
                    cadena += produccion.conjunto.get(i);
                    if (i < produccion.conjunto.size() - 1) {
                        cadena += "/";
                    }
                }
                cadena += "}";
                cadena += "\n";
            }
            cadena += "\n";
        }
        return cadena;
    }

    public void reorderDetalleProduccion() {
        for (State state : States) {
            ArrayList<DetalleProduccion> newDetalleProduccion = new ArrayList<>();
            for (Pair<String, ArrayList<ArrayList<String>>> iter : this.MinimizedGrammar) {
                for (DetalleProduccion produccion : state.componente.Producciones) {
                    if (produccion.LeftSideKey.equals(iter.getKey()) || produccion.LeftSideKey.equals("S'")) {
                        if (!newDetalleProduccion.contains(produccion)) {
                            newDetalleProduccion.add(produccion);
                        }
                    }
                }
            }
            state.componente.Producciones = newDetalleProduccion;
        }
        System.out.println("");
    }

    private void createNextState(int position) throws Exception {
        ArrayList<String> symbols = new ArrayList<>();
        State currentState = States.get(position);
        for (DetalleProduccion detalleProduccion : currentState.componente.Producciones) {
            if (detalleProduccion.puntero < detalleProduccion.rhs.size()) {
                if (!symbols.contains(detalleProduccion.rhs.get(detalleProduccion.puntero))) {
                    String currentSymbol = detalleProduccion.rhs.get(detalleProduccion.puntero);
                    State stateToCheck = goTo(currentSymbol, currentState.componente.Producciones);
                    if (!stateExists(stateToCheck)) {
                        stateToCheck.name = getNextStateName();
                        States.add(stateToCheck);
                        automatonSize = States.size();
                    }
                    String symbolo = detalleProduccion.rhs.get(detalleProduccion.puntero);
                    String name = !Objects.equals(stateToCheck.name, null) ? stateToCheck.name : getStateName(stateToCheck);
                    currentState.transitions.add(new Transition(symbolo, currentState.name, name));
                    symbols.add(detalleProduccion.rhs.get(detalleProduccion.puntero));
                    allLookUps.clear();
                }
            }
        }
    }

    private String getStateName(State stateToCheck) {
        for (State state : States) {
            if (statesAreEqual(state, stateToCheck))
                return state.name;
        }
        return "";
    }

    private boolean stateExists(State newState) {
        for (State state : States) {
            if (StatesAreTheSame(state, newState))
                return true;
        }
        return false;
    }

    private static boolean StatesAreTheSame(State state, State stateB) {
        if (state.componente.Producciones.size() != stateB.componente.Producciones.size())
            return false;

        for (int i = 0; i < state.componente.Producciones.size(); i++) {
            if (!state.componente.Producciones.get(i).LeftSideKey.equals(stateB.componente.Producciones.get(i).LeftSideKey))
                return false;
            if (state.componente.Producciones.get(i).rhs.size() != stateB.componente.Producciones.get(i).rhs.size())
                return false;
            ArrayList<String> rightHandSideA = new ArrayList<>(state.componente.Producciones.get(i).rhs);
            ArrayList<String> rightHandSideB = new ArrayList<>(stateB.componente.Producciones.get(i).rhs);
            if (!rightHandSideA.equals(rightHandSideB))
                return false;
            ArrayList<String> lookAheadA = new ArrayList<>(state.componente.Producciones.get(i).conjunto);
            ArrayList<String> lookAheadB = new ArrayList<>(stateB.componente.Producciones.get(i).conjunto);
            if (!lookAheadA.equals(lookAheadB))
                return false;

            if (state.componente.Producciones.get(i).puntero != stateB.componente.Producciones.get(i).puntero)
                return false;
        }
        return true;
    }

    private String getNextStateName() {
        return "I" + automatonSize;
    }

    private boolean statesAreEqual(State state, State newState) {
        if (state.componente.Producciones.size() != newState.componente.Producciones.size()) {
            return false;
        } else {
            for (int i = 0; i < state.componente.Producciones.size(); i++) {
                String stateLeftKey = state.componente.Producciones.get(i).LeftSideKey;
                String newStateLeftKey = newState.componente.Producciones.get(i).LeftSideKey;
                if (!stateLeftKey.equals(newStateLeftKey)) {
                    return false;
                } else if (state.componente.Producciones.get(i).rhs.size() != newState.componente.Producciones.get(i).rhs.size()) {
                    return false;
                } else {
                    ArrayList<String> arrayExistingState = new ArrayList<>(state.componente.Producciones.get(i).rhs);
                    ArrayList<String> arrayToCheckingState = new ArrayList<>(newState.componente.Producciones.get(i).rhs);
                    if (!arrayExistingState.equals(arrayToCheckingState))
                        return false;
                    if (state.componente.Producciones.get(i).puntero != newState.componente.Producciones.get(i).puntero)
                        return false;
                    arrayExistingState = new ArrayList<>(state.componente.Producciones.get(i).conjunto);
                    arrayToCheckingState = new ArrayList<>(newState.componente.Producciones.get(i).conjunto);
                    if (!arrayExistingState.equals(arrayToCheckingState))
                        return false;
                }
            }
        }
        return true;
    }

    private State goTo(String currentSymbol, ArrayList<DetalleProduccion> producciones) throws Exception {
        State newState = new State();
        List<String> doneSymbols = new ArrayList<>();
        for (DetalleProduccion detalleProduccion : producciones) {
            if (detalleProduccion.puntero < detalleProduccion.rhs.size()) {
                if (detalleProduccion.rhs.get(detalleProduccion.puntero).equals(currentSymbol)) {
                    DetalleProduccion newDetalleProduccion = new DetalleProduccion();
                    newDetalleProduccion.LeftSideKey = detalleProduccion.LeftSideKey;
                    newDetalleProduccion.rhs.addAll(detalleProduccion.rhs);
                    newDetalleProduccion.puntero = detalleProduccion.puntero + 1;
                    newDetalleProduccion.conjunto.addAll(detalleProduccion.conjunto);
                    newState.componente.Producciones.add(newDetalleProduccion);
                    if (newDetalleProduccion.puntero < newDetalleProduccion.rhs.size()) {
                        if (!doneSymbols.contains(newDetalleProduccion.rhs.get(newDetalleProduccion.puntero))) {
                            String symbolPuntero = newDetalleProduccion.rhs.get(newDetalleProduccion.puntero);
                            if (isNonTerminalType(symbolPuntero)) {
                                newState.componente.Producciones.addAll(GetClosure(newDetalleProduccion));
                                doneSymbols.add(newDetalleProduccion.rhs.get(newDetalleProduccion.puntero));
                            }
                        }
                    }
                }
            }
        }
        return newState;
    }

    private ArrayList<DetalleProduccion> GetClosure(DetalleProduccion nodeLine) throws Exception {
        ArrayList<DetalleProduccion> closure = new ArrayList<>();
        String postDot = nodeLine.rhs.get(nodeLine.puntero);
        ArrayList<String> F = GetLookAhead(nodeLine);
        ArrayList<ArrayList<String>> productions = new ArrayList<>();
        for (Pair<String, ArrayList<ArrayList<String>>> pair : MinimizedGrammar) {
            if (pair.getKey().equals(postDot)) {
                productions = pair.getValue();
            }
        }
        for (List<String> production : productions) {
            if (!postDot.equals("S_prime")) {
                DetalleProduccion toAdd = new DetalleProduccion(postDot);
                toAdd.rhs.addAll(production);
                toAdd.conjunto.addAll(F);
                if (toAdd.rhs.get(toAdd.puntero).equals("ɛ")) {
                    toAdd.puntero++;
                }
                closure.add(toAdd);
                if (toAdd.puntero < toAdd.rhs.size()) {
                    if (toAdd.rhs.get(toAdd.puntero).equals(postDot) && toAdd.puntero + 1 < toAdd.rhs.size()) {
                        int i = 1;
                        while (true) {
                            ArrayList<String> first = getFirst(toAdd.rhs.get(toAdd.puntero + i));
                            toAdd.conjunto.addAll(first);
                            if (!first.contains("ɛ")) {
                                break;
                            }
                            if (toAdd.puntero + i + 1 == toAdd.rhs.size()) {
                                toAdd.conjunto.addAll(nodeLine.conjunto);
                                Set<String> hs = new HashSet<>();
                                hs.addAll(toAdd.conjunto);
                                toAdd.conjunto.clear();
                                toAdd.conjunto.addAll(hs);
                                break;
                            }
                            i++;
                        }

                        if (allLookUps.containsKey(toAdd.LeftSideKey)) {
                            allLookUps.get(toAdd.LeftSideKey).addAll(toAdd.conjunto);
                            Set<String> hs = new HashSet<>();
                            hs.addAll(allLookUps.get(toAdd.LeftSideKey));
                            allLookUps.get(toAdd.LeftSideKey).clear();
                            allLookUps.get(toAdd.LeftSideKey).addAll(hs);
                        } else {
                            allLookUps.put(toAdd.LeftSideKey, toAdd.conjunto);
                        }
                    }
                    if (allLookUps.containsKey(toAdd.LeftSideKey)) {
                        toAdd.conjunto.addAll(allLookUps.get(toAdd.LeftSideKey));
                        Set<String> hs = new HashSet<>();
                        hs.addAll(toAdd.conjunto);
                        toAdd.conjunto.clear();
                        toAdd.conjunto.addAll(hs);
                    }
                    if (SymbolTable.getInstance().GetType(toAdd.rhs.get(toAdd.puntero)) instanceof NonterminalType
                            && !toAdd.rhs.get(toAdd.puntero).equals(postDot)) {
                        closure.addAll(GetClosure(toAdd));
                    }
                }
            }
        }
        return closure;
    }

    private ArrayList<String> GetLookAhead(DetalleProduccion nodeLine) throws Exception {
        ArrayList<String> conjuntoLookAhead = new ArrayList<>();
        int puntero = nodeLine.puntero;
        if (puntero + 1 == nodeLine.rhs.size()) {
            conjuntoLookAhead.addAll(nodeLine.conjunto);
        } else if (nodeLine.rhs.get(puntero + 1).equals(EPSILON)) {
            conjuntoLookAhead.addAll(nodeLine.conjunto);
        } else {
            int index = 1;
            while (true) {
                ArrayList<String> first = getFirst(nodeLine.rhs.get(puntero + index));
                conjuntoLookAhead.addAll(first);
                if (!first.contains("ɛ")) {
                    break;
                }
                if (puntero + index + 1 == nodeLine.rhs.size()) {
                    conjuntoLookAhead.addAll(nodeLine.conjunto);
                    break;
                }
                index++;
            }
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(conjuntoLookAhead);
        conjuntoLookAhead.clear();
        conjuntoLookAhead.addAll(hs);
        return conjuntoLookAhead;
    }

    private ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizeGrammar(ArrayList<StatementNode> statements) {
        ArrayList<Pair<String, ArrayList<ArrayList<String>>>> returnTable = new ArrayList<>();
        for (StatementNode statementNode : statements) {
            if (statementNode instanceof ProductionStatementNode) {
                String leftName = ((ProductionStatementNode) statementNode).LeftHandSide.Lexeme;
                ArrayList<ArrayList<String>> produccionesDerecha = new ArrayList<>();
                for (RightHandSideNode rightHandSideNode : ((ProductionStatementNode) statementNode).RightHandSideList) {
                    ArrayList<String> parts = new ArrayList<>();
                    for (ProductionPart productionPart : rightHandSideNode.ProductionParts) {
                        if (productionPart instanceof SymbolPart) {
                            parts.add(((SymbolPart) productionPart).LeftLabel.Lexeme);
                        }
                    }
                    produccionesDerecha.add(parts);
                }
                returnTable.add(new Pair<>(leftName, produccionesDerecha));
            }
        }
        return returnTable;
    }

    public ArrayList<String> getFirst(String symbol) throws SemanticException {
        ArrayList<String> FirstList = new ArrayList<>();
        if (symbol.equals(EPSILON)) {
            FirstList.add(EPSILON);
            return FirstList;
        }
        String leftSide = symbol;
        BaseType type = SymbolTable.getInstance().GetType(leftSide);
        if (type instanceof TerminalType) {
            FirstList.add(leftSide);
            return FirstList;
        }
        ProductionStatementNode productionStatementNode = getProductionStatement(symbol);
        ArrayList<RightHandSideNode> orderedRightHandSideList = null;
        if (productionStatementNode != null) {
            orderedRightHandSideList = OrderStatements(leftSide, productionStatementNode.RightHandSideList);
        }
        if (orderedRightHandSideList != null) {
            for (RightHandSideNode rightHandSideNode : orderedRightHandSideList) {
                if (rightHandSideNode.ProductionParts.size() == 0) {
                    FirstList.add(EPSILON);
                }
                for (ProductionPart productionPart : rightHandSideNode.ProductionParts) {
                    ArrayList<String> resultY;
                    if (productionPart instanceof SymbolPart) {
                        SymbolPart currentPart = (SymbolPart) productionPart;
                        if (currentPart.LeftLabel.Lexeme.equals(leftSide)) {
                            if (!FirstList.contains(EPSILON)) {
                                break;
                            }
                        } else {
                            BaseType currentType = SymbolTable.getInstance().GetType(currentPart.LeftLabel.Lexeme);
                            if (currentType instanceof NonterminalType) {
                                resultY = getFirst(currentPart.LeftLabel.Lexeme);
                                FirstList.addAll(resultY);
                                if (!resultY.contains(EPSILON)) {
                                    break;
                                }
                            } else {
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
        if (symbol.equals(Aumentada.rhs.get(0)))
            NextList.add(EOF);
        ArrayList<RightHandSideNode> rightProductionsThatContainSymbol = getRightProductionsThatContainSymbol(symbol);
        for (RightHandSideNode rhs : rightProductionsThatContainSymbol) {
            if (rhs.ProductionParts.size() == 0)
                continue;
            ArrayList<String> betas = getNextSymbols(rhs, symbol);
            for (String beta : betas) {
                String leftSide = getLeftSideProduction(rhs);
                if (beta.equals(EPSILON)) {
                    if (!leftSide.equals(symbol)) {
                        ArrayList<String> nextA = getNext(leftSide);
                        nextA.removeIf(it -> it.equals(EPSILON));
                        for (String next : nextA) {
                            if (!NextList.contains(next))
                                NextList.add(next);
                        }
                    }
                } else {
                    if (isTerminalType(beta)) {
                        if (!NextList.contains(beta))
                            NextList.add(beta);
                    } else {
                        ArrayList<String> firstBetaElements = getFirst(beta);
                        if (firstBetaElements.contains(EPSILON)) {
                            if (!symbol.equals(leftSide)) {
                                ArrayList<String> nextA = getNext(leftSide);
                                for (String next : nextA) {
                                    if (!firstBetaElements.contains(next))
                                        firstBetaElements.add(next);
                                }
                                firstBetaElements.removeIf(it -> it.equals(EPSILON));
                                for (String firstBetaElement : firstBetaElements) {
                                    if (!NextList.contains(firstBetaElement))
                                        NextList.add(firstBetaElement);
                                }
                            }
                        } else {
                            for (String firstBetaElement : firstBetaElements) {
                                if (!NextList.contains(firstBetaElement))
                                    NextList.add(firstBetaElement);
                            }
                        }
                    }
                }
            }
        }
        return NextList;
    }

    private ArrayList<String> getNextSymbols(RightHandSideNode rhs, String symbol) {
        ArrayList<String> returnSymbols = new ArrayList<>();
        for (int i = 0; i < rhs.ProductionParts.size(); i++) {
            ProductionPart productionPart = rhs.ProductionParts.get(i);
            if (productionPart instanceof SymbolPart) {
                SymbolPart currentPart = (SymbolPart) productionPart;
                if (currentPart.LeftLabel.Lexeme.equals(symbol)) {
                    if (i + 1 >= rhs.ProductionParts.size()) {
                        returnSymbols.add(EPSILON);
                    } else {
                        ProductionPart nextPart = rhs.ProductionParts.get(i + 1);
                        if (nextPart instanceof SymbolPart) {
                            returnSymbols.add(((SymbolPart) nextPart).LeftLabel.Lexeme);
                        }
                    }
                }
            }
        }
        return returnSymbols;
    }

    private String getLeftSideProduction(RightHandSideNode rhs) {
        for (StatementNode statementNode : Statements) {
            if (statementNode instanceof ProductionStatementNode) {
                for (RightHandSideNode node : ((ProductionStatementNode) statementNode).RightHandSideList) {
                    if (node.equals(rhs))
                        return ((ProductionStatementNode) statementNode).LeftHandSide.Lexeme;
                }
            }
        }
        return null;
    }

    private boolean isTerminalType(String beta) throws SemanticException {
        BaseType type = SymbolTable.getInstance().GetType(beta);
        return type instanceof TerminalType;
    }

    private boolean isNonTerminalType(String beta) throws SemanticException {
        BaseType type = SymbolTable.getInstance().GetType(beta);
        return type instanceof NonterminalType;
    }

    private ArrayList<RightHandSideNode> getRightProductionsThatContainSymbol(String symbol) {
        ArrayList<RightHandSideNode> returnArray = new ArrayList<>();
        for (StatementNode statementNode : Statements) {
            if (statementNode instanceof ProductionStatementNode) {
                ProductionStatementNode currentStatement = (ProductionStatementNode) statementNode;
                for (RightHandSideNode rightHandSideNode : currentStatement.RightHandSideList) {
                    boolean contains = false;
                    for (ProductionPart productionPart : rightHandSideNode.ProductionParts) {
                        if (productionPart instanceof SymbolPart) {
                            SymbolPart currentPart = (SymbolPart) productionPart;
                            if (currentPart.LeftLabel.Lexeme.equals(symbol)) {
                                contains = true;
                            }
                        }
                    }
                    if (contains) {
                        returnArray.add(rightHandSideNode);
                    }
                }
            }
        }
        return returnArray;
    }

    private ArrayList<RightHandSideNode> OrderStatements(String leftSide, ArrayList<RightHandSideNode> rightHandSideList) {
        ArrayList<RightHandSideNode> returnOrderedList = new ArrayList<>();
        for (RightHandSideNode rightNode : rightHandSideList) {
            if (rightNode.ProductionParts.size() == 0) {
                returnOrderedList.add(rightNode);
            }
        }
        for (RightHandSideNode rightNode : rightHandSideList) {
            if (!returnOrderedList.contains(rightNode)) {
                boolean contains = false;
                for (ProductionPart part : rightNode.ProductionParts) {
                    if (part instanceof SymbolPart) {
                        SymbolPart currentPart = (SymbolPart) part;
                        if (currentPart.LeftLabel.Lexeme.equals(leftSide)) {
                            contains = true;
                        }
                    }
                }
                if (!contains) {
                    returnOrderedList.add(rightNode);
                }
            }
        }
        for (RightHandSideNode rightNode : rightHandSideList) {
            if (!returnOrderedList.contains(rightNode)) {
                returnOrderedList.add(rightNode);
            }
        }
        return returnOrderedList;
    }

    private ProductionStatementNode getProductionStatement(String id) {
        for (StatementNode node : Statements) {
            if (node instanceof ProductionStatementNode) {
                ProductionStatementNode productionStatement = (ProductionStatementNode) node;
                if (productionStatement.LeftHandSide.Lexeme.equals(id)) {
                    return productionStatement;
                }
            }
        }
        return null;
    }

    private Pair<String, ArrayList<ArrayList<String>>> getMinimizedProductionStatement(String id) {
        for (int i = 0; i < MinimizedGrammar.size(); i++) {
            if (MinimizedGrammar.get(i).getKey().equals(id))
                return MinimizedGrammar.get(i);
        }
        return null;
    }

    public DetalleProduccion getMinimizedAumentada() {
        String symbol = MinimizedGrammar.get(0).getKey();
        DetalleProduccion detalleAumentada = new DetalleProduccion();
        detalleAumentada.conjunto = new ArrayList<>(Arrays.asList("$"));
        detalleAumentada.rhs.add(symbol);
        detalleAumentada.LeftSideKey = "S'";
        return detalleAumentada;
    }
}
