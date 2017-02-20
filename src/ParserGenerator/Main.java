package ParserGenerator;


import Automaton.Automata;
import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.LexerComponents.LexerException;
import ParserGenerator.LexerComponents.Token;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
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
        //System.out.print(cupFileContent);
        Lexer lexer = new Lexer(cupFileContent);
        Parser parser = new Parser(lexer);
        ArrayList<StatementNode> statements = parser.Parse();
        //String json = new GsonBuilder().setPrettyPrinting().create().toJson(statements);
        //System.out.println(json);
        for (StatementNode statement : statements) {
            statement.ValidateSemantic();
        }

        Hashtable<String, Pair<ArrayList<String>, ArrayList<String>>> primerosAndSegundos = new Hashtable<>();
        Automata automata = new Automata(statements);
        for(int i = statements.size()-1; i>=0 ;i--) {
            StatementNode statement = statements.get(i);
            if(statement instanceof ProductionStatementNode){
                ProductionStatementNode production = (ProductionStatementNode)statement;
                ArrayList<String> primeros = automata.getPrimero(production);
                primerosAndSegundos.put(production.LeftHandSide.Lexeme,
                        new Pair<>(primeros,new ArrayList<>()));
                System.out.println("b");
            }
        }
        String primeros_segundos = new GsonBuilder().setPrettyPrinting().create().toJson(primerosAndSegundos);
        System.out.println(primeros_segundos);
    }
}
