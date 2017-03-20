package ParserGenerator;

import Automata.GrammarDetail;
import Automata.GrammarUtilies;
import Automata.ProductionDetail;
import Automata.State;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;

import java.util.ArrayList;
import java.util.Map;

public class ParserTable {
    private static final String PrimeSymbol = "S'";

    public static RowSortedTable<String, String, String> getTable(ArrayList<State> thisStates, ArrayList<GrammarDetail> grammarDetails) throws TableException {
        RowSortedTable<String, String, String> parserTable = TreeBasedTable.create();
        for (State state : thisStates) {

            for (Object terminalTransition : state.TerminalTransitions.entrySet()) {
                Map.Entry pair = (Map.Entry) terminalTransition;
                String symbol = (String) pair.getKey();
                State currentState = (State) pair.getValue();
                if (parserTable.get(state.Name.replace("I", ""), symbol) != null) {
                    throw new TableException("Error on Table Generation, value already exist");
                }
                parserTable.put(state.Name.replace("I", ""), symbol, "s" + currentState.Name.replace("I", ""));
            }

            for (Object nonTerminalTransition : state.NonTerminalTransitions.entrySet()) {
                Map.Entry pair = (Map.Entry) nonTerminalTransition;
                String symbol = (String) pair.getKey();
                State currentState = (State) pair.getValue();
                if (parserTable.get(state.Name.replace("I", ""), symbol) != null) {
                    throw new TableException("Error on Table Generation, value already exist");
                }
                parserTable.put(state.Name.replace("I", ""), symbol, currentState.Name.replace("I", ""));
            }

            for (ProductionDetail productionDetail : state.Productions) {
                if (productionDetail.Pointer == productionDetail.RightHandSideProductions.size()) {
                    for (String lookAheadItem : productionDetail.LookAhead) {
                        if (productionDetail.LeftHandSideKey.equals(PrimeSymbol)) {
                            if (parserTable.get(state.Name.replace("I", ""), lookAheadItem) != null) {
                                throw new TableException("Error on Table Generation, value already exist");
                            }
                            parserTable.put(state.Name.replace("I", ""), lookAheadItem, "acc");
                        } else {
                            if (parserTable.get(state.Name.replace("I", ""), lookAheadItem) != null) {
                                throw new TableException("Error on Table Generation, value already exist");
                            }
                            parserTable.put(state.Name.replace("I", ""), lookAheadItem, GrammarUtilies.getReductionName(productionDetail, grammarDetails));
                        }
                    }
                }
            }
        }
        return parserTable;
    }
}
