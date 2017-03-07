package Automaton;

import ParserGenerator.SemanticComponents.SemanticException;
import ParserGenerator.SemanticComponents.SymbolTable;
import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.TerminalType;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;
import javafx.util.Pair;

import java.util.ArrayList;


public class ParserTable {

    public static RowSortedTable<Integer, String, String> getTable(ArrayList<State> states, ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedAutomata) throws SemanticException {
        RowSortedTable<Integer, String, String> parserTable = TreeBasedTable.create();
        for (State stateIterator : states) {

            for (Transition transition : stateIterator.transitions)
            {
                String symbol =  transition.symbol;
                String stateName = transition.destiny;
                BaseType type = SymbolTable.getInstance().GetType(symbol);
                if (type instanceof TerminalType){
                    parserTable.put(Integer.parseInt(stateIterator.name.replace("I", "")), symbol, "S" + stateName.replace("I", ""));
                }
                else{
                    parserTable.put(Integer.parseInt(stateIterator.name.replace("I", "")), symbol, stateName.replace("I", ""));
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
                            parserTable.put(Integer.parseInt(stateIterator.name.replace("I", "")),lookAhead, "Accepted");
                        }
                        else
                        {
                            ArrayList<Pair<String, ArrayList<String>>> simplifiedAutomata = simplifyGrammar(minimizedAutomata);
                            String reduction = getReduction(detalleProduccion, simplifiedAutomata);
                            parserTable.put(Integer.parseInt(stateIterator.name.replace("I", "")),lookAhead, reduction);
                        }
                    }
                }
            }
        }
        return parserTable;
    }

    private static ArrayList<Pair<String, ArrayList<String>>> simplifyGrammar(ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedAutomata) {
        ArrayList<Pair<String, ArrayList<String>>> simpleGrammar = new ArrayList<>();
        for (Pair<String, ArrayList<ArrayList<String>>> pair : minimizedAutomata){
            for(ArrayList<String> elementos : pair.getValue()){
                simpleGrammar.add(new Pair<>(pair.getKey(), elementos));
            }
        }
        return simpleGrammar;
    }

    private static String getReduction(DetalleProduccion detalleProduccion, ArrayList<Pair<String, ArrayList<String>>> minimizedAutomata){
        for (int i =0; i< minimizedAutomata.size();i++){
            String lefths = minimizedAutomata.get(i).getKey();
            ArrayList<String> rhs = minimizedAutomata.get(i).getValue();
            if (rhs.equals(detalleProduccion.rhs) && lefths.equals(detalleProduccion.LeftSideKey)){
                return "R" + (i+1);
            }
        }
        return null;
    }
}
