package ParserGenerator.LexerComponents;


public class Token {
    public int Row;
    public int Column;
    public String Lexeme;
    public TokenTypes Type;

    public Token(int row, int column, String lexeme, TokenTypes type){
        this.Row = row;
        this.Column = column;
        this.Lexeme = lexeme;
        this.Type = type;
    }
}
