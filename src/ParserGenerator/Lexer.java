package ParserGenerator;

import java.util.*;

public class Lexer {
    private String _sourceCode;
    private int _column;
    private int _currentPointer;
    private Dictionary<String, TokenTypes> _reservedWords;

    public Lexer(String sourceCode){
        _currentPointer = 0;
        this._sourceCode = sourceCode;
    }

    public Token getNextToken(){
        String lexeme = "";
        char currentCharacter = getCurrentCharacter();
        if (Character.isLetter(currentCharacter)){
            return getTokenStartingWithLetter(lexeme, currentCharacter);
        }
        return null;
    }

    public Token getTokenStartingWithLetter(String lexeme, char currentCharacter){
        lexeme += currentCharacter;
        MovePointer(1);
        char nextCharacter = getCurrentCharacter();
        while (Character.isLetterOrDigit(nextCharacter))
        {
            lexeme += nextCharacter;
            MovePointer(1);
            nextCharacter = getCurrentCharacter();
        }
        return ;
    }

    public char getCurrentCharacter(){
        if (_currentPointer < _sourceCode.length())
            return _sourceCode.charAt(_currentPointer);
        return '\0';
    }

    public void MovePointer(int length){
        _currentPointer += length;
        _column += length;
    }
}
