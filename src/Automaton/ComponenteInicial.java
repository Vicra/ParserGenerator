package Automaton;

import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;

import java.util.ArrayList;

public class ComponenteInicial extends Componente{
    private static final String EOF = "$";

    public ComponenteInicial(ProductionStatementNode aumentada){
        DetalleProduccion produccionAumentada = new DetalleProduccion();
        ArrayList<String> conjuntoEOF = new ArrayList<>();
        conjuntoEOF.add(EOF);
        produccionAumentada.conjunto = conjuntoEOF;
        produccionAumentada.productionStatementNode = aumentada;
        this.Producciones.add(produccionAumentada);
    }
}

