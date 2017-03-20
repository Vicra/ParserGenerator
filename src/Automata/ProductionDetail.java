package Automata;

import java.util.ArrayList;
import java.util.List;

public class ProductionDetail {
    public String LeftHandSideKey;
    public ArrayList<String> RightHandSideProductions;
    public Integer Pointer;
    public ArrayList<String> LookAhead;

    public ProductionDetail(String leftHandSideProducer) {
        RightHandSideProductions = new ArrayList<>();
        LookAhead = new ArrayList<>();
        Pointer = 0;
        LeftHandSideKey = leftHandSideProducer;
    }
}
