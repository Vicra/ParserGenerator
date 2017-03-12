package ParserGenerator;

import Automaton.Automaton;
import Automaton.ParserTable;
import Automaton.State;
import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import com.google.common.collect.RowSortedTable;
import com.google.gson.GsonBuilder;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.util.Pair;
import Automaton.ComponenteInicial;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Main {
    public static void main(String argv[]) throws Exception {
        String cupFileContent = "";
        try{
            File file = new File("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\src\\ParserGenerator\\file.cup");
            //File file = new File( "/Users/viramirez/Documents/Compiler/ParserGenerator/src/ParserGenerator/file.cup");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            cupFileContent = new String(data, "UTF-8");
        }
        catch(Exception e){
            System.out.print("error:" + e.getMessage());
        }
        /*          LEXER            */
        Lexer lexer = new Lexer(cupFileContent);

        /*          PARSER            */
        Parser parser = new Parser(lexer);
        ArrayList<StatementNode> statements = parser.Parse();

        /*          SEMANTIC            */
        for (StatementNode statement : statements) {
            statement.ValidateSemantic();
        }

        /*          GENERATE AUTOMATA            */
        Automaton automata = new Automaton(statements);
        ArrayList<State> thisStates = automata.generateStates();
        ArrayList<State> reduced = reduceStates(thisStates);

        String cadena = automata.printAutomata();
        System.out.println(getMinimizedGrammar(automata.MinimizedGrammar));
        System.out.println(cadena);

        /*          GENERATE TABLE            */
        RowSortedTable<Integer, String, String> table = ParserTable.getTable(reduced,automata.MinimizedGrammar);
        String tableString = new GsonBuilder().setPrettyPrinting().create().toJson(table);
        System.out.println(tableString);
    }

    private static ArrayList<State> reduceStates(ArrayList<State> thisStates) {
        ArrayList<State> returnStates = new ArrayList<>();
        for (State state : thisStates){
            if (!listContainsState(returnStates, state)){
                returnStates.add(state);
            }
        }
        return returnStates;
    }

    private static boolean listContainsState(ArrayList<State> thisStates, State state) {
        for (State stateIterator : thisStates){
            if(stateIterator.name.equals(state.name)){
                if(stateIterator.componente.Producciones.equals(state.componente.Producciones)){
                    if (stateIterator.transitions.equals(state.transitions)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String getMinimizedGrammar(ArrayList<Pair<String, ArrayList<ArrayList<String>>>> minimizedGrammar) {
        String cadena = "/*GRAMMAR*/\n";
        for (Pair<String, ArrayList<ArrayList<String>>> pair : minimizedGrammar){
            for (ArrayList<String> array : pair.getValue()){
                cadena += pair.getKey();
                cadena += "->";
                for (String valor : array){
                    cadena += valor;
                }
                cadena+="\n";
            }
        }
        return cadena;
    }
}
