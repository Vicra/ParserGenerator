package Automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {

    public String Name;
    public ArrayList<ProductionDetail> Productions;
    public Map<String, State> TerminalTransitions;
    public Map<String, State> NonTerminalTransitions;

    public State() {
        this.Productions = new ArrayList<>();
        this.TerminalTransitions = new HashMap<>();
        this.NonTerminalTransitions = new HashMap<>();
    }
}
