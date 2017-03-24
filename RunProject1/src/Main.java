import java.io.FileReader;

public class Main {
    static public void main(String argv[]) {
        try {
            parser p = new parser(new MyLexer(new FileReader("C:\\Users\\vicra\\IdeaProjects\\FlexCup\\RunProject1\\src\\input.txt")));
            p.parse();
            System.out.println("Valid input");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
