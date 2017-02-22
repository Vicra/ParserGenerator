package Automaton;

import javafx.util.Pair;

import java.util.ArrayList;

public class State {
    public String name;
    public ArrayList<String> transitions;
    public boolean isInitialState;
    public boolean isAcceptanceState;

    public State(String name, ArrayList<String> transitions, boolean isInitialState, boolean isAcceptanceState) {
        this.name = name;
        this.transitions = transitions;
        this.isInitialState = isInitialState;
        this.isAcceptanceState = isAcceptanceState;
    }

    public State(String name) {
        this.name = name;
    }
}
