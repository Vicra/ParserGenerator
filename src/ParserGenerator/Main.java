package ParserGenerator;


import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.LexerComponents.LexerException;
import ParserGenerator.LexerComponents.Token;
import ParserGenerator.SyntacticComponents.Parser;
import ParserGenerator.TreeComponents.StatementNode;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

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
        //System.out.print(cupFileContent);
        Lexer lexer = new Lexer(cupFileContent);
        Parser parser = new Parser(lexer);
        ArrayList<StatementNode> lines = parser.Parse();
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(lines);
        System.out.println(json);
        for (StatementNode statement : lines) {
            statement.ValidateSemantic();
        }
    }
}
