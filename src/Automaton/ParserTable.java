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

    public static RowSortedTable<Integer, String, String> getTable(ArrayList<State> states, ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedAutomata) throws SemanticException, TableException {
        RowSortedTable<Integer, String, String> parserTable = TreeBasedTable.create();
        for (State stateIterator : states) {

            for (Transition transition : stateIterator.transitions)
            {
                String column =  transition.symbol;
                String stateName = transition.destiny;
                BaseType type = SymbolTable.getInstance().GetType(column);
                if (parserTable.get(stateIterator.name.replace("I",""), transition.symbol) != null){
                    throw new TableException("Table Construction Error");
                }
                if (type instanceof TerminalType){
                    Integer row = Integer.parseInt(stateIterator.name.replace("I", ""));
                    String value = "S" + stateName.replace("I", "");
                    parserTable.put(row, column, value);
                }
                else{
                    Integer row = Integer.parseInt(stateIterator.name.replace("I", ""));
                    String value = stateName.replace("I", "");
                    parserTable.put(row, column, value);
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
                            Integer row = Integer.parseInt(stateIterator.name.replace("I", ""));
                            if (parserTable.get(row, lookAhead) != null){
                                throw new TableException("Table Construction Error");
                            }
                            parserTable.put(row,lookAhead, "Accepted");
                        }
                        else
                        {
                            ArrayList<Pair<String, ArrayList<String>>> simplifiedAutomata = simplifyGrammar(minimizedAutomata);
                            String reduction = getReduction(detalleProduccion, simplifiedAutomata);
                            Integer row = Integer.parseInt(stateIterator.name.replace("I", ""));
                            if (parserTable.get(row, lookAhead) != null){
                                throw new TableException("Table Construction Error");
                            }
                            parserTable.put(row, lookAhead, reduction);
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
