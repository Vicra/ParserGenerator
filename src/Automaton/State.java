package Automaton;

import javafx.util.Pair;

import java.util.ArrayList;

public class State {
    public String name;
    public ArrayList<Transition> transitions;
    public boolean isInitialState;
    public boolean isAcceptanceState;
    public Componente componente;

    public State(String name, ArrayList<Transition> transitions, boolean isInitialState, boolean isAcceptanceState) {
        this.name = name;
        this.transitions = transitions;
        this.isInitialState = isInitialState;
        this.isAcceptanceState = isAcceptanceState;
    }

    public State(String name) {
        this.name = name;
    }
}
