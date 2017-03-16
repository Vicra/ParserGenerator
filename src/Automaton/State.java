package Automaton;

import java.util.ArrayList;

public class State {
    public String name;
    public ArrayList<Transition> transitions = new ArrayList<>();
    public boolean isInitialState;
    public boolean isAcceptanceState;
    public Componente componente;

    public State(String name) {
        this.name = name;
    }

    public State() {
        componente = new ComponenteNormal();
        componente.Producciones = new ArrayList<>();
    }

    public State clone(){
        State returnState = new State();
        returnState.name = this.name;
        returnState.transitions = this.transitions;
        returnState.componente.Producciones = this.componente.Producciones;
        returnState.isAcceptanceState = this.isAcceptanceState;
        returnState.isInitialState = this.isInitialState;
        return returnState;
    }
}
