package ParserGenerator.SyntacticComponents;

/**
 * Created by vicra on 2/8/2017.
 */
public class SyntacticException extends Exception {
    public SyntacticException(String message){
        super(message);
    }

    public SyntacticException(String message, int row){
        super(message);
    }
}
