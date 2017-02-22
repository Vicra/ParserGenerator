package ParserGenerator;


import Automaton.Automaton;
import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Hashtable;

public class Main {
    public static void main(String argv[]) throws Exception {
        String cupFileContent = "";
        try{
            //File file = new File("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\src\\ParserGenerator\\file.cup");
            File file = new File( "/Users/viramirez/Documents/Compiler/ParserGenerator/src/ParserGenerator/file.cup");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            cupFileContent = new String(data, "UTF-8");
        }
        catch(Exception e){
            System.out.print("error:" + e.getMessage());
        }
        Lexer lexer = new Lexer(cupFileContent);
        Parser parser = new Parser(lexer);
        ArrayList<StatementNode> statements = parser.Parse();
        for (StatementNode statement : statements) {
            statement.ValidateSemantic();
        }

        Hashtable<String, ArrayList<String>> primerosProducciones = new Hashtable<>();
        Hashtable<String, ArrayList<String>> siguientesProducciones = new Hashtable<>();
        Automaton automata = new Automaton(statements);
        for(int i = statements.size()-1; i>=0 ;i--) {
            StatementNode statement = statements.get(i);
            if(statement instanceof ProductionStatementNode){
                ProductionStatementNode production = (ProductionStatementNode)statement;
                ArrayList<String> primeros = automata.getFirst(production.LeftHandSide.Lexeme);
                primerosProducciones.put(production.LeftHandSide.Lexeme, primeros);
                System.out.println("");
            }
        }


        for(int i = 0 ; i<statements.size() ; i++) {
            StatementNode statement = statements.get(i);
            if(statement instanceof ProductionStatementNode){
                ProductionStatementNode production = (ProductionStatementNode)statement;
                System.out.println(production.LeftHandSide.Lexeme);
                ArrayList<String> siguientes = automata.getNext(production.LeftHandSide.Lexeme);
                siguientesProducciones.put(production.LeftHandSide.Lexeme, siguientes);
                System.out.println("");
            }
        }
        automata.FirstsTable = primerosProducciones;
        automata.SecondsTable = siguientesProducciones;
        automata.GenerateStates();
        System.out.println("primeros");
        String primeros = new GsonBuilder().setPrettyPrinting().create().toJson(primerosProducciones);System.out.println(primeros);
        System.out.println("siguientes");
        String siguientes = new GsonBuilder().setPrettyPrinting().create().toJson(siguientesProducciones);System.out.println(siguientes);

    }

    private static ArrayList<Pair<String, ArrayList<ArrayList<String>>>> GenerateProductions(ArrayList<StatementNode> statements) {
        ArrayList<Pair<String, ArrayList<ArrayList<String>>>> returnTable = new ArrayList<>();
        for (StatementNode statementNode : statements){
            if(statementNode instanceof ProductionStatementNode){
                String leftName = ((ProductionStatementNode) statementNode).LeftHandSide.Lexeme;
                ArrayList<ArrayList<String>> produccionesDerecha = new ArrayList<>();
                for (RightHandSideNode rightHandSideNode : ((ProductionStatementNode) statementNode).RightHandSideList){
                    ArrayList<String> parts = new ArrayList<>();
                    for (ProductionPart productionPart : rightHandSideNode.ProductionParts){
                        if (productionPart instanceof SymbolPart){
                            parts.add(((SymbolPart) productionPart).LeftLabel.Lexeme);
                        }
                    }
                    produccionesDerecha.add(parts);
                }
                returnTable.add(new Pair<>(leftName, produccionesDerecha));
            }
        }
        return returnTable;
    }
}
