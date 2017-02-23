package Automaton;

public class Transition {
    public String origin;
    public String symbol;
    public String destiny;

    public Transition(String symbol, String origin, String destiny) {
        this.symbol = symbol;
        this.origin = origin;
        this.destiny = destiny;
    }
}
