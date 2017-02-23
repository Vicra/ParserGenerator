package Automaton;

import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;

import java.util.ArrayList;

public class DetalleProduccion {
    public String LeftSideKey;
    public ArrayList<String> rhs = new ArrayList<>();
    public int puntero = 0;
    public ArrayList<String> conjunto = new ArrayList<>();
}
