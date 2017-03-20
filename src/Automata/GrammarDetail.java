package Automata;

import java.util.ArrayList;
import java.util.List;

public class GrammarDetail {
    public String LeftHandSideKey;
    public ArrayList<String> RightHandSide;
    public ArrayList<Label> LabelList = new ArrayList<>();
    public ArrayList<String> JavaCodeList = new ArrayList<>();

    public GrammarDetail(String leftHandSideKey, ArrayList<String> rightHandSide, ArrayList<Label> labelList, ArrayList<String> javaCodeList) {
        LeftHandSideKey = leftHandSideKey;
        RightHandSide = rightHandSide;
        LabelList = labelList;
        JavaCodeList = javaCodeList;
    }

    public GrammarDetail(String leftHandSideKey, ArrayList<String> rightHandSide) {
        LeftHandSideKey = leftHandSideKey;
        RightHandSide = rightHandSide;
    }
}
