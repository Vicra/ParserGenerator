package ParserGenerator;

import Automaton.Automaton;
import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

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
            }
        }


        for(int i = 0 ; i<statements.size() ; i++) {
            StatementNode statement = statements.get(i);
            if(statement instanceof ProductionStatementNode){
                ProductionStatementNode production = (ProductionStatementNode)statement;
                ArrayList<String> siguientes = automata.getNext(production.LeftHandSide.Lexeme);
                siguientesProducciones.put(production.LeftHandSide.Lexeme, siguientes);
            }
        }
        automata.FirstsTable = primerosProducciones;
        automata.SecondsTable = siguientesProducciones;
        automata.generateStates();

        String primeros = new GsonBuilder().setPrettyPrinting().create().toJson(primerosProducciones);
        PrintWriter writer = new PrintWriter("primeros.txt", "UTF-8");writer.println(primeros);writer.close();

        String siguientes = new GsonBuilder().setPrettyPrinting().create().toJson(siguientesProducciones);
        writer = new PrintWriter("siguientes.txt", "UTF-8");writer.println(siguientes);writer.close();

        System.out.println("Archivos generados: primeros.txt");
        System.out.println("Archivos generados: siguientes.txt");
        System.out.println("Archivos generados: automata.txt");

    }
}
