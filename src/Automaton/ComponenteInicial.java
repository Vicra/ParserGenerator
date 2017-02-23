package Automaton;

import java.util.ArrayList;

public class ComponenteInicial extends Componente{
    private static final String EOF = "$";

    public ComponenteInicial(DetalleProduccion aumentada){
        DetalleProduccion produccionAumentada = new DetalleProduccion();
        ArrayList<String> conjuntoEOF = new ArrayList<>();
        conjuntoEOF.add(EOF);
        produccionAumentada.conjunto = conjuntoEOF;
        produccionAumentada.LeftSideKey = aumentada.LeftSideKey;
        produccionAumentada.rhs = aumentada.rhs;
        this.Producciones.add(produccionAumentada);
    }
}

