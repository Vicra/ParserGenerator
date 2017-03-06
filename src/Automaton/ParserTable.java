package Automaton;

import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Objects;


public class ParserTable {

    public static RowSortedTable<String, String, String> getTable(ArrayList<State> states, ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedAutomata) throws SemanticException {
        RowSortedTable<String, String, String> parserTable = TreeBasedTable.create();
        for (State stateIterator : states) {

            for (Transition transition : stateIterator.transitions)
            {
                String symbol =  transition.symbol;
                String stateName = transition.destiny;
                BaseType type = SymbolTable.getInstance().GetType(symbol);
                if (type instanceof TerminalType){
                    parserTable.put(stateIterator.name.replace("I", ""), symbol, "S" + stateName.replace("I", ""));
                }
                else{
                    parserTable.put(stateIterator.name.replace("I", ""), symbol, stateName.replace("I", ""));
                }
            }

            for(DetalleProduccion detalleProduccion : stateIterator.componente.Producciones)
            {
                if(detalleProduccion.puntero == detalleProduccion.rhs.size())
                {
                    for ( String lookAhead : detalleProduccion.conjunto)
                    {
                        if(detalleProduccion.LeftSideKey.equals("S'"))
                        {
                            parserTable.put(stateIterator.name.replace("I", ""),lookAhead, "Accepted");
                        }
                        else
                        {
                            String reduction = getReduction(detalleProduccion,states, minimizedAutomata);
                            parserTable.put(stateIterator.name.replace("I", ""),lookAhead, reduction);
                        }
                    }
                }
            }
        }
        return parserTable;
    }

    private static String getReduction(DetalleProduccion detalleProduccion, ArrayList<State> states, ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedAutomata) {
        for (State stateIterator : states){
            for (DetalleProduccion detalle : stateIterator.componente.Producciones){
                if (DetalleAreEqual(detalle, detalleProduccion)){
                    int i = getProductionPosition(detalleProduccion, minimizedAutomata);
                    return "R" + i;
                }
            }
        }
        return null;
    }

    private static int getProductionPosition(DetalleProduccion detalleProduccion, ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedAutomata) {
        for (Pair<String, ArrayList<ArrayList<String>>> pair : minimizedAutomata){
            if (pair.getKey().equals(detalleProduccion.LeftSideKey))
                return minimizedAutomata.indexOf(pair) + 1;
        }
        return -1;
    }

    private static boolean DetalleAreEqual(DetalleProduccion detalle, DetalleProduccion detalleProduccion) {
        if (detalle.rhs.size()!= detalleProduccion.rhs.size())
            return false;
        if (!Objects.equals(detalle.LeftSideKey, detalleProduccion.LeftSideKey))
            return false;
        if (detalle.puntero != detalleProduccion.puntero)
            return false;
        if (!detalle.conjunto.equals(detalleProduccion.conjunto))
            return false;
        if (!detalle.rhs.equals(detalleProduccion.rhs))
            return false;
        return true;
    }
}
