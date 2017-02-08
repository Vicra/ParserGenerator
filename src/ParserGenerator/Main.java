package ParserGenerator;


import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.LexerComponents.LexerException;
import ParserGenerator.LexerComponents.Token;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Main {
    static public void main(String argv[]) throws Exception {
        String cupFileContent = "";
        try{
            File file = new File("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\src\\ParserGenerator\\file.cup");
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
        ArrayList<Token> tokens = lexer.GetAllTokens();
        for (Token iterator : tokens) {
            System.out.print("Lexeme:" + iterator.Lexeme);
            System.out.print(" Row:" + iterator.Row);
            System.out.print(" Column:" + iterator.Column);
            System.out.print(" Type:" + iterator.Type + '\n');
        }

    }
}
