package ParserGenerator.SyntacticComponents;

import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.LexerComponents.LexerException;
import ParserGenerator.LexerComponents.Token;
import ParserGenerator.LexerComponents.TokenTypes;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ImportStatementNode;
import ParserGenerator.TreeComponents.Statements.PackageStatementNode;

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

    private ArrayList<StatementNode> Program() throws SyntacticException {
        return java_cup_spec();
    }

    private ArrayList<StatementNode> java_cup_spec() throws SyntacticException {
        ArrayList<StatementNode> returnList = new ArrayList<>();
        ArrayList<StatementNode> package_spec = package_spec();
        ArrayList<StatementNode> import_list = import_list();
        ArrayList<StatementNode> code_parts = code_parts();
//        ArrayList<StatementNode> symbol_list = symbol_list();
//        ArrayList<StatementNode> precedence_list = precedence_list();
//        ArrayList<StatementNode> start_spec = start_spec();
//        ArrayList<StatementNode> production_list = production_list();
        returnList.addAll(package_spec);
        returnList.addAll(import_list);
        returnList.addAll(code_parts);
//        returnList.addAll(symbol_list);
//        returnList.addAll(precedence_list);
//        returnList.addAll(start_spec);
//        returnList.addAll(production_list);
        return returnList;
    }

    private ArrayList<StatementNode> code_parts() {
        if(TokenValidations.TokenStartsAsCodeParts(_currentToken)){
            
        }
    }

    private ArrayList<StatementNode> import_list() throws SyntacticException {
        if(TokenValidations.TokenStartsAsImportStatement(_currentToken)){
            ImportStatementNode importStatement = import_spec();
            ArrayList<StatementNode> importList = import_list();
            importList.add(0, importStatement);
            return importList;
        }
        else{
            Epsilon();
            return new ArrayList<StatementNode>();
        }
    }

    private ImportStatementNode import_spec() throws SyntacticException {
        GoToNextToken();
        ArrayList<String> identifiers = import_id();
        if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
            throw new SyntacticException("Expected semicolon token");
        }
        else{
            GoToNextToken();
            return new ImportStatementNode(identifiers);
        }
    }

    private ArrayList<String> import_id() {
        ArrayList<String> identifiers = new ArrayList<>();
        while(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_ASTERISK || _currentToken.Type == TokenTypes.SYM_DOT){
            if(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_ASTERISK){
                identifiers.add(_currentToken.Lexeme);
            }
            GoToNextToken();
        }
        return identifiers;
    }

    private ArrayList<StatementNode> package_spec() throws SyntacticException {
        ArrayList<StatementNode> returnPackageStatements = new ArrayList<>();
        if(_currentToken.Type == TokenTypes.RESERVED_PACKAGE){
            GoToNextToken();
            ArrayList<String> multipart_id = multipart_id();
            returnPackageStatements.add(new PackageStatementNode(multipart_id));
        }
        if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
            throw new SyntacticException("Expected semicolon token");
        }
        GoToNextToken();
        return returnPackageStatements;
    }

    private ArrayList<String> multipart_id() {
        ArrayList<String> identifiers = new ArrayList<>();
        while(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_DOT){
            if(_currentToken.Type == TokenTypes.IDENTIFIER ){
                identifiers.add(_currentToken.Lexeme);
            }
            GoToNextToken();
        }
        return identifiers;
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

    private void Epsilon(){

    }
}
