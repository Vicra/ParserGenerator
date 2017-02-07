package ParserGenerator;


import java.io.File;
import java.io.FileInputStream;

public class Main {
    static public void main(String argv[]) {
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
        System.out.print(cupFileContent);
        Lexer lexer = new Lexer(cupFileContent);
    }
}
