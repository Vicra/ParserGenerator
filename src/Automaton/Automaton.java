package Automaton;

import ParserGenerator.LexerComponents.Token;
import ParserGenerator.LexerComponents.TokenTypes;
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
import javafx.util.Pair;
import jdk.nashorn.internal.ir.Symbol;

import java.lang.reflect.Array;
import java.util.*;

import static java.util.Arrays.asList;

public class Automaton {
    public ArrayList<StatementNode> Statements;
    public ArrayList<Pair<String, ArrayList<ArrayList<String>>>> MinimizedGrammar;
    private static final String EPSILON = "ε";
    private static final String EOF = "$";
    public Hashtable<String, ArrayList<String>> FirstsTable;
    public Hashtable<String, ArrayList<String>> SecondsTable;
    private DetalleProduccion Aumentada;
    public ArrayList<State> States = new ArrayList<>();
    int automatonSize = 1;


    public Automaton(ArrayList<StatementNode> statements){
        Statements = statements;
        MinimizedGrammar = minimizeGrammar(Statements);
        Aumentada = getMinimizedAumentada();
        //Statements.add(0, Aumentada);
    }

    public State GenerateFirstState() throws SemanticException {
        State initialState = new State("I0");
        ComponenteInicial componenteInicial = new ComponenteInicial(Aumentada);

        DetalleProduccion produccionAumentada = componenteInicial.Producciones.get(0);
        String simboloEnPuntero = produccionAumentada.rhs.get(produccionAumentada.puntero);
        if (isNonTerminalType(simboloEnPuntero)){
            Pair<String, ArrayList<ArrayList<String>>> productionStatementNode = getMinimizedProductionStatement(simboloEnPuntero);
            DetalleProduccion rootProduccion = new DetalleProduccion();
            rootProduccion.LeftSideKey = productionStatementNode.getKey();
            rootProduccion.rhs = productionStatementNode.getValue().get(0);
            ArrayList<String> conjunto = new ArrayList<>(asList("$"));
            rootProduccion.conjunto.addAll(conjunto);
            componenteInicial.Producciones.add(rootProduccion);
            componenteInicial.Producciones.addAll(getClosure(componenteInicial.Producciones.get(0)));
        }
        initialState.componente = componenteInicial;
        return initialState;
    }

    public void GenerateStates() throws SemanticException {
        State firstState = GenerateFirstState();
        States.add(firstState);
        int i = 0;
        while(i < automatonSize)
        {
            CreateNextNode(i);
            i++;
        }

    }

    private void CreateNextNode(int position) throws SemanticException {
        ArrayList<String> doneSymbols = new ArrayList<>();
        State currentState = States.get(position);
        for(DetalleProduccion nodeLine : currentState.componente.Producciones)
        {

        }
    }

    private ArrayList<DetalleProduccion> getClosure(DetalleProduccion produccionPadre) throws SemanticException {
        ArrayList<DetalleProduccion> returnList = new ArrayList<>();
        for (Pair<String, ArrayList<ArrayList<String>>> production : MinimizedGrammar){
            if (production.getKey().equals(produccionPadre.rhs.get(produccionPadre.puntero))){
                for(ArrayList<String> rightSide : production.getValue()){
                    String primerSimbolo = rightSide.get(0);
                    if (isNonTerminalType(primerSimbolo)){
                        if (!Objects.equals(production.getKey(), primerSimbolo)){
                            DetalleProduccion detalle = new DetalleProduccion();
                            detalle.LeftSideKey = production.getKey();
                            detalle.rhs = rightSide;
                            detalle.conjunto = getConjunto(produccionPadre);
                            returnList.addAll(getClosure(detalle));
                        }
                    }
                    else{
                        DetalleProduccion detalle = new DetalleProduccion();
                        detalle.LeftSideKey = production.getKey();
                        detalle.rhs = rightSide;
                        detalle.conjunto = getConjunto(produccionPadre);
                        returnList.add(detalle);
                    }
                }
            }
        }
        return returnList;
    }

    private ArrayList<String> getConjunto(DetalleProduccion conjuntoPadre) throws SemanticException {
        ArrayList<String> conjunto = new ArrayList<>();
        int puntero = conjuntoPadre.puntero;
        if(puntero + 1 == conjuntoPadre.rhs.size())
        {
            conjunto.addAll(conjuntoPadre.conjunto);
        }
        else if(conjuntoPadre.rhs.get(puntero + 1).equals(EPSILON))
        {
            conjunto.addAll(conjuntoPadre.conjunto);
        }
        else
        {
            int i = 1;
            while(true)
            {
                ArrayList<String> first = getFirst(conjuntoPadre.rhs.get(puntero + i));
                conjunto.addAll(first);
                if(!first.contains("ɛ"))
                {
                    break;
                }
                if(puntero + i + 1 == conjuntoPadre.rhs.size())
                {
                    conjunto.addAll(conjuntoPadre.conjunto);
                    break;
                }
                i++;
            }
        }
        Set<String> nuevoConjunto = new HashSet<>();
        nuevoConjunto.addAll(conjunto);
        conjunto.clear();
        conjunto.addAll(nuevoConjunto);
        return conjunto;

    }

    private String getValueFromPuntero(ArrayList<String> rhs, int puntero) {
        if (puntero > rhs.size() || puntero < 0)
            return EPSILON;
        return rhs.get(puntero);
    }

    private ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizeGrammar(ArrayList<StatementNode> statements) {
        ArrayList<Pair<String, ArrayList<ArrayList<String>>>> returnTable = new ArrayList<>();
        for (StatementNode statementNode : statements){
            if(statementNode instanceof ProductionStatementNode){
                String leftName = ((ProductionStatementNode) statementNode).LeftHandSide.Lexeme;
                ArrayList<ArrayList<String>> produccionesDerecha = new ArrayList<>();
                for (RightHandSideNode rightHandSideNode : ((ProductionStatementNode) statementNode).RightHandSideList){
                    ArrayList<String> parts = new ArrayList<>();
                    for (ProductionPart productionPart : rightHandSideNode.ProductionParts){
                        if (productionPart instanceof SymbolPart){
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
        if (symbol.equals(Aumentada.rhs.get(0)))
            NextList.add(EOF);
        ArrayList<RightHandSideNode> rightProductionsThatContainSymbol = getRightProductionsThatContainSymbol(symbol);
        for (RightHandSideNode rhs : rightProductionsThatContainSymbol){
            if (rhs.ProductionParts.size() == 0)
                continue;
            ArrayList<String> betas = getNextSymbols(rhs, symbol);
            for (String beta : betas){
                String leftSide = getLeftSideProduction(rhs);
                if (beta.equals(EPSILON)){
                    if (!leftSide.equals(symbol)){
                        ArrayList<String> nextA = getNext(leftSide);
                        nextA.removeIf(it -> it.equals(EPSILON));
                        for (String next : nextA){
                            if (!NextList.contains(next))
                                NextList.add(next);
                        }
                    }
                }
                else{
                    if (isTerminalType(beta)){
                        if (!NextList.contains(beta))
                            NextList.add(beta);
                    }
                    else{
                        ArrayList<String> firstBetaElements = getFirst(beta);
                        if(firstBetaElements.contains(EPSILON)){
                            if (!symbol.equals(leftSide)){
                                ArrayList<String> nextA = getNext(leftSide);
                                for (String next : nextA){
                                    if (!firstBetaElements.contains(next))
                                        firstBetaElements.add(next);
                                }
                                firstBetaElements.removeIf( it -> it.equals(EPSILON));
                                for (String firstBetaElement : firstBetaElements){
                                    if (!NextList.contains(firstBetaElement))
                                        NextList.add(firstBetaElement);
                                }
                            }
                        }else{
                            for (String firstBetaElement : firstBetaElements){
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
        for (int i=0; i< rhs.ProductionParts.size(); i++){
            ProductionPart productionPart = rhs.ProductionParts.get(i);
            if (productionPart instanceof SymbolPart){
                SymbolPart currentPart = (SymbolPart)productionPart;
                if (currentPart.LeftLabel.Lexeme.equals(symbol)){
                    if (i+1 >= rhs.ProductionParts.size()){
                        returnSymbols.add(EPSILON);
                    }
                    else{
                        ProductionPart nextPart = rhs.ProductionParts.get(i+1);
                        if (nextPart instanceof SymbolPart){
                            returnSymbols.add(((SymbolPart)nextPart).LeftLabel.Lexeme);
                        }
                    }
                }
            }
        }
        return returnSymbols;
    }

    private String getLeftSideProduction(RightHandSideNode rhs) {
        for (StatementNode statementNode : Statements){
            if (statementNode instanceof ProductionStatementNode){
                for (RightHandSideNode node : ((ProductionStatementNode) statementNode).RightHandSideList){
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

    private Pair<String,ArrayList<ArrayList<String>>> getMinimizedProductionStatement(String id) {
        for(int i=0; i<MinimizedGrammar.size(); i++){
            if (MinimizedGrammar.get(i).getKey().equals(id))
                return MinimizedGrammar.get(i);
        }
        return null;
    }

    public ProductionStatementNode getAumentada() {
        String symbol = MinimizedGrammar.get(0).getKey();
        SymbolPart part = new SymbolPart(new Token(0,0,symbol, TokenTypes.IDENTIFIER));
        ArrayList<ProductionPart> parts = new ArrayList<>();
        parts.add(part);
        RightHandSideNode rightHandSideNode = new RightHandSideNode(parts);
        ArrayList<RightHandSideNode> rightHandSideNodeArrayList = new ArrayList<>();
        rightHandSideNodeArrayList.add(rightHandSideNode);
        return new ProductionStatementNode("S'",rightHandSideNodeArrayList);
    }

    public DetalleProduccion getMinimizedAumentada() {
        String symbol = MinimizedGrammar.get(0).getKey();
        DetalleProduccion detalleAumentada = new DetalleProduccion();
        detalleAumentada.conjunto =  new ArrayList<>(Arrays.asList("$"));
        detalleAumentada.rhs.add(symbol);
        detalleAumentada.LeftSideKey = "S'";
        return detalleAumentada;
    }
}
