package ParserGenerator.SyntacticComponents;

import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.LexerComponents.LexerException;
import ParserGenerator.LexerComponents.Token;
import ParserGenerator.LexerComponents.TokenTypes;
import ParserGenerator.TreeComponents.StatementNode;

import java.util.ArrayList;

public class Parser {
    private Lexer _lexer;
    private Token _currentToken;
    private ArrayList<Token> _tokens;
    private int _position = -1;

    public Parser(Lexer lexer) throws LexerException {
        _lexer = lexer;
        _tokens = _lexer.GetAllTokens();
        GoToNextToken();
    }

    public ArrayList<StatementNode> Parse() throws SyntacticException {
        ArrayList<StatementNode> code = Program();
        if (_currentToken.Type != TokenTypes.END_OF_FILE)
            throw new SyntacticException("Expected EOF Token, Row: " + GetPreviousToken().Row);
        return code;
    }

    private ArrayList<StatementNode> Program()
    {
        if (TokenValidations.TokenStartsAsStatement(_currentToken))
        {
            StatementNode statement = Statement();
            ArrayList<StatementNode> statementList = StatementList();
            statementList.add(0, statement);
            return statementList;
        }
        return null;
    }

    private ArrayList<StatementNode> StatementList() {
        return null;
    }

    private StatementNode Statement() {
        return null;
    }

    private void GoToNextToken()
    {
        _currentToken = GetNextToken();
    }

    private Token GetNextToken()
    {
        int myPosition = _position;
        _position++;
        return _tokens.get(myPosition + 1);
    }

    private Token GetPreviousToken()
    {
        int myPosition = _position;
        _position--;
        return _tokens.get(myPosition - 1);
    }
}
