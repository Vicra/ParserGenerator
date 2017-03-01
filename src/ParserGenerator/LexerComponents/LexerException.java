package ParserGenerator.LexerComponents;

public class LexerException extends Exception{
    public  LexerException(String message){
        super(message);
    }

    public  LexerException(String message, int row, int column){
        super(message + "\nRow: " + row +"\nColumn: " + column);
    }
}
