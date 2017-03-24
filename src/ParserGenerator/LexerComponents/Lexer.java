package ParserGenerator.LexerComponents;

import java.util.*;

public class Lexer {
    private String _sourceCode;
    private int _column;
    private int _row;
    private int _currentPointer;
    private Hashtable<String, TokenTypes> _reservedWords;
    private Hashtable<String, TokenTypes> _simpleSymbols;
    private Hashtable<String, TokenTypes> _tripleSymbols;

    public Lexer(String sourceCode){
        _row = 1;
        _currentPointer = 0;
        this._sourceCode = sourceCode;

        _reservedWords = ReservedWords.GetReservedWords();
        _simpleSymbols = ReservedWords.GetSimpleSymbols();
        _tripleSymbols = ReservedWords.GetTripleSymbols();
    }

    public Token getNextToken() throws LexerException {
        String lexeme = "";
        char currentCharacter = getCurrentCharacter();
        while (CharacterIsNewLine(currentCharacter)){
            _row++;
            _column = -1;
            MovePointer(1);
            currentCharacter = getCurrentCharacter();
        }
        while (Character.isWhitespace(currentCharacter)){
            MovePointer(1);
            currentCharacter = getCurrentCharacter();
            while (CharacterIsNewLine(currentCharacter)){
                _row++;
                _column = -1;
                MovePointer(1);
                currentCharacter = getCurrentCharacter();
            }
        }
        if (CharacterIsEndOfFile(currentCharacter)){
            return GetToken(lexeme, TokenTypes.END_OF_FILE);
        }
        if (currentCharacter == '{'){
            lexeme += currentCharacter;
            if (_sourceCode.length() - 1 >= _currentPointer){
                char nextCharacter = _sourceCode.charAt(_currentPointer + 1);
                if (nextCharacter == ':'){
                    MovePointer(1);
                    while (true)
                    {
                        char currentSymbol = getCurrentCharacter();
                        if (currentSymbol == ':')
                        {
                            lexeme += currentSymbol;
                            MovePointer(1);
                            char nextSymbol = getCurrentCharacter();
                            if (nextSymbol == '}')
                            {
                                lexeme += nextSymbol;
                                MovePointer(1);
                                return GetToken(lexeme, TokenTypes.JAVA_CODE);
                            }
                        }
                        else if (CharacterIsEndOfFile(currentSymbol)){
                            throw new LexerException("No cerro el bloque de codigo de java " ,_row, _column);
                        }
                        else{
                            if (CharacterIsNewLine(currentSymbol)){
                                _row++;
                                _column = -1;
                            }
                            lexeme += currentSymbol;
                            MovePointer(1);
                        }
                    }
                }
                else{
                    throw new LexerException("Expected colon character (:) after brace left character ('{')", _row, _column);
                }
            }
        }
        if(CharacterIsSymbol(currentCharacter)){
            return getTokenStartingAsSymbol(lexeme, currentCharacter);
        }
        if (Character.isLetter(currentCharacter)){
            return getTokenStartingWithLetter(lexeme, currentCharacter);
        }
        return null;
    }

    private Token getTokenStartingAsSymbol(String lexeme, char currentCharacter) {
        if (CanBeTripleCharacterOperator(currentCharacter)){
            if (_sourceCode.length() > _currentPointer + 2)
            {
                char secondCharacter = _sourceCode.charAt(_currentPointer + 1);
                char thirdCharacter = _sourceCode.charAt(_currentPointer + 2);
                String posibleThreeCharacterOperator = "" + currentCharacter;
                posibleThreeCharacterOperator += secondCharacter;
                posibleThreeCharacterOperator += thirdCharacter;

                if (_tripleSymbols.containsKey(posibleThreeCharacterOperator))
                {
                    lexeme = posibleThreeCharacterOperator;
                    MovePointer(lexeme.length());
                    return GetToken(lexeme, _tripleSymbols.get(lexeme));
                }
            }
        }
        lexeme += currentCharacter;
        MovePointer(lexeme.length());
        return GetToken(lexeme, _simpleSymbols.get(lexeme));
    }

    private boolean CanBeTripleCharacterOperator(char currentCharacter) {
        if(currentCharacter == ':')
            return true;
        return false;
    }

    private boolean CharacterIsSymbol(char currentCharacter) {
        if(currentCharacter == '|'
            || currentCharacter == ';'
            || currentCharacter == ':'
            || currentCharacter == '.'
            || currentCharacter == '*'
            || currentCharacter == ','
            ){
            return true;
        }
        return false;
    }

    private boolean CharacterIsEndOfFile(char currentCharacter) {
        return currentCharacter == '\0';
    }

    private boolean CharacterIsNewLine(char currentCharacter) {
        return currentCharacter == '\n';
    }

    public Token getTokenStartingWithLetter(String lexeme, char currentCharacter){
        lexeme += currentCharacter;
        MovePointer(1);
        char nextCharacter = getCurrentCharacter();
        while (Character.isLetterOrDigit(nextCharacter) || nextCharacter == '_' || nextCharacter == '<' || nextCharacter == '>')
        {
            lexeme += nextCharacter;
            MovePointer(1);
            nextCharacter = getCurrentCharacter();
        }
        return GetToken(lexeme, _reservedWords.containsKey(lexeme)?_reservedWords.get(lexeme):TokenTypes.IDENTIFIER);
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

    public Token GetToken(String lexeme, TokenTypes type)
    {
        return new Token(_row, _column - lexeme.length(), lexeme, type);
    }

    public ArrayList<Token> GetAllTokens() throws LexerException {
        ArrayList<Token> returnTokens = new ArrayList<>();
        Token currentToken = getNextToken();
        returnTokens.add(currentToken);
        while (currentToken.Type != TokenTypes.END_OF_FILE)
        {
            currentToken = getNextToken();
            returnTokens.add(currentToken);
        }
        return returnTokens;
    }
}
