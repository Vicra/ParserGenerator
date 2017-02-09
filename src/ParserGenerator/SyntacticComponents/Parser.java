package ParserGenerator.SyntacticComponents;

import ParserGenerator.LexerComponents.Lexer;
import ParserGenerator.LexerComponents.LexerException;
import ParserGenerator.LexerComponents.Token;
import ParserGenerator.LexerComponents.TokenTypes;
import ParserGenerator.TreeComponents.StatementNode;
import ParserGenerator.TreeComponents.Statements.ImportStatementNode;
import ParserGenerator.TreeComponents.Statements.PackageStatementNode;
import ParserGenerator.TreeComponents.Statements.ProductionStatementNode;
import ParserGenerator.TreeComponents.Statements.Productions.JavaCodePart;
import ParserGenerator.TreeComponents.Statements.Productions.ProductionPart;
import ParserGenerator.TreeComponents.Statements.Productions.SymbolPart;
import ParserGenerator.TreeComponents.Statements.RightHandSideNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.NonTerminalSymbolNode;
import ParserGenerator.TreeComponents.Statements.SymbolListStatements.TerminalSymbolNode;
import ParserGenerator.TreeComponents.Statements.UserCodeStatements.ActionCodeNode;
import ParserGenerator.TreeComponents.Statements.UserCodeStatements.InitCodeNode;
import ParserGenerator.TreeComponents.Statements.UserCodeStatements.ParserCodeNode;
import ParserGenerator.TreeComponents.Statements.UserCodeStatements.ScanCodeNode;

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
        ArrayList<StatementNode> symbol_list = symbol_list();
//        ArrayList<StatementNode> start_spec = start_spec();
        ArrayList<StatementNode> production_list = production_list();
        returnList.addAll(package_spec);
        returnList.addAll(import_list);
        returnList.addAll(code_parts);
        returnList.addAll(symbol_list);
//        returnList.addAll(start_spec);
        returnList.addAll(production_list);
        return returnList;
    }

    private ArrayList<StatementNode> production_list() throws SyntacticException {
        if (TokenValidations.TokenStartsAsProductionList(_currentToken)){
            StatementNode productionPart = production();
            ArrayList<StatementNode> productionList = production_list();
            productionList.add(0, productionPart);
            return productionList;
        }
        else{
            Epsilon();
            return new ArrayList<StatementNode>();
        }
    }

    private StatementNode production() throws SyntacticException {
        Token nonterminal_id = _currentToken;
        GoToNextToken();
        if (_currentToken.Type != TokenTypes.SYM_PRODUCTION){
            throw new SyntacticException("Expected colon_colon_equals token");
        }
        GoToNextToken();
        ArrayList<RightHandSideNode> rightHandSideList = rhs_list();
        if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
            throw new SyntacticException("Expected semicolon token");
        }
        GoToNextToken();

        return new ProductionStatementNode(nonterminal_id, rightHandSideList);
    }

    private ArrayList<RightHandSideNode> rhs_list() throws SyntacticException {
        if (_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.JAVA_CODE){
            RightHandSideNode rightHandSide = rhs();
            ArrayList<RightHandSideNode> rightHandSideList = rightHandSide_List();
            rightHandSideList.add(0, rightHandSide);
            return rightHandSideList;
        }
        else{
            throw new SyntacticException("Expected identifier or java code");
        }
    }

    private ArrayList<RightHandSideNode> rightHandSide_List() throws SyntacticException {
        if (TokenValidations.TokenIsPipe(_currentToken)){
            GoToNextToken();
            RightHandSideNode rightHandSide = rhs();
            ArrayList<RightHandSideNode> rightHandSideList = rightHandSide_List();
            rightHandSideList.add(0, rightHandSide);
            return rightHandSideList;
        }
        return new ArrayList<RightHandSideNode>();
    }

    private RightHandSideNode rhs() throws SyntacticException {
        ArrayList<ProductionPart> productionParts = prod_part_list();
        return new RightHandSideNode(productionParts);
    }

    private ArrayList<ProductionPart> prod_part_list() throws SyntacticException {
        if (_currentToken.Type == TokenTypes.IDENTIFIER){
            ProductionPart productionPart = symbol_prod_part();
            ArrayList<ProductionPart> productionParts = prod_part_list();
            productionParts.add(0, productionPart);
            return productionParts;
        }
        else if(_currentToken.Type == TokenTypes.JAVA_CODE){
            ProductionPart productionPart = java_prod_part();
            ArrayList<ProductionPart> productionParts = prod_part_list();
            productionParts.add(0, productionPart);
            return productionParts;
        }
        else{
            return new ArrayList<>();
        }
    }

    private ProductionPart java_prod_part() {
        Token symbolToken = _currentToken;
        GoToNextToken();
        return new JavaCodePart(symbolToken);
    }

    private ProductionPart symbol_prod_part() throws SyntacticException {
        Token symbolToken = _currentToken;
        GoToNextToken();
        if (_currentToken.Type == TokenTypes.SYM_COLON){
            GoToNextToken();
            if (_currentToken.Type != TokenTypes.IDENTIFIER){
                throw new SyntacticException("Expected Identifier token");
            }
            Token optionalLabel = _currentToken;
            GoToNextToken();
            return new SymbolPart(symbolToken, optionalLabel);
        }
        return new SymbolPart(symbolToken);
    }

    private ArrayList<StatementNode> symbol_list() throws SyntacticException {
        if (TokenValidations.TokenStartsAsSymbolList(_currentToken)){
            StatementNode symbolListStatement = symbol();
            ArrayList<StatementNode> symbolList = symbol_list();
            symbolList.add(0, symbolListStatement);
            return symbolList;
        }
        else{
            Epsilon();
            return new ArrayList<StatementNode>();
        }
    }

    private StatementNode symbol() throws SyntacticException {
        if(_currentToken.Type == TokenTypes.RESERVED_TERMINAL){
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.IDENTIFIER){
                throw new SyntacticException("Expected Identifier token");
            }
            Token nextToken = _tokens.get(_position + 1);
            if (nextToken.Type == TokenTypes.SYM_DOT){
                ArrayList<Token> multiPart = multipart_id();

                ArrayList<Token> declaresTerm = declares_term();
                if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                    throw new SyntacticException("Expected semicolon token");
                }
                GoToNextToken();
                return new TerminalSymbolNode(multiPart, declaresTerm);
            }
            else if (nextToken.Type == TokenTypes.SYM_COMMA){
                ArrayList<Token> declaresTerm = declares_term();
                if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                    throw new SyntacticException("Expected semicolon token");
                }
                GoToNextToken();
                return new TerminalSymbolNode(declaresTerm);
            }
            else if (nextToken.Type == TokenTypes.IDENTIFIER){
                Token nextNextToken = _tokens.get(_position + 2);
                if (nextNextToken.Type == TokenTypes.SYM_COMMA){
                    ArrayList<Token> multiPart = new ArrayList<>();
                    multiPart.add(_currentToken);
                    ArrayList<Token> declaresTerm = declares_term(nextToken, _position + 1);
                    for(int i=0;i<declaresTerm.size();i++){
                        GoToNextToken();
                        GoToNextToken();
                    }
                    if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                        throw new SyntacticException("Expected semicolon token");
                    }
                    GoToNextToken();
                    return new TerminalSymbolNode(multiPart,declaresTerm);
                }
                else if (nextNextToken.Type == TokenTypes.SYM_SEMICOLON){
                    ArrayList<Token> multiPart = new ArrayList<>();
                    ArrayList<Token> declaresTerm = new ArrayList<>();
                    multiPart.add(_currentToken);
                    declaresTerm.add(nextToken);
                    GoToNextToken();
                    GoToNextToken();
                    GoToNextToken();
                    return new TerminalSymbolNode(multiPart, declaresTerm);
                }
            }
        }
        else if(_currentToken.Type == TokenTypes.RESERVED_NONTERMINAL){
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.IDENTIFIER){
                throw new SyntacticException("Expected Identifier token");
            }
            Token nextToken = _tokens.get(_position + 1);
            if (nextToken.Type == TokenTypes.SYM_DOT){
                ArrayList<Token> multiPart = multipart_id();

                ArrayList<Token> declaresTerm = declares_term();
                if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                    throw new SyntacticException("Expected semicolon token");
                }
                GoToNextToken();
                return new NonTerminalSymbolNode(multiPart, declaresTerm);
            }
            else if (nextToken.Type == TokenTypes.SYM_COMMA){
                ArrayList<Token> declaresTerm = declares_term();
                if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                    throw new SyntacticException("Expected semicolon token");
                }
                GoToNextToken();
                return new NonTerminalSymbolNode(declaresTerm);
            }
            else if (nextToken.Type == TokenTypes.IDENTIFIER){
                Token nextNextToken = _tokens.get(_position + 2);
                if (nextNextToken.Type == TokenTypes.SYM_COMMA){
                    ArrayList<Token> multiPart = new ArrayList<>();
                    multiPart.add(_currentToken);
                    ArrayList<Token> declaresTerm = declares_term(nextToken, _position + 1);
                    for(int i=0;i<declaresTerm.size();i++){
                        GoToNextToken();
                        GoToNextToken();
                    }
                    if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                        throw new SyntacticException("Expected semicolon token");
                    }
                    GoToNextToken();
                    return new NonTerminalSymbolNode(multiPart, declaresTerm);
                }
                else if (nextNextToken.Type == TokenTypes.SYM_SEMICOLON){
                    ArrayList<Token> multiPart = new ArrayList<>();
                    ArrayList<Token> declaresTerm = new ArrayList<>();
                    multiPart.add(_currentToken);
                    declaresTerm.add(nextToken);
                    GoToNextToken();
                    GoToNextToken();
                    GoToNextToken();
                    return new NonTerminalSymbolNode(multiPart, declaresTerm);
                }
            }
        }
        else{
            GoToNextToken();
            if(_currentToken.Type == TokenTypes.RESERVED_TERMINAL){
                GoToNextToken();
                if(_currentToken.Type != TokenTypes.IDENTIFIER){
                    throw new SyntacticException("Expected Identifier token");
                }
                Token nextToken = _tokens.get(_position + 1);
                if (nextToken.Type == TokenTypes.SYM_DOT){
                    ArrayList<Token> multiPart = multipart_id();

                    ArrayList<Token> declaresTerm = declares_term();
                    if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                        throw new SyntacticException("Expected semicolon token");
                    }
                    GoToNextToken();
                    return new NonTerminalSymbolNode(multiPart, declaresTerm);
                }
                else if (nextToken.Type == TokenTypes.SYM_COMMA){
                    ArrayList<Token> declaresTerm = declares_term();
                    if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                        throw new SyntacticException("Expected semicolon token");
                    }
                    GoToNextToken();
                    return new NonTerminalSymbolNode(declaresTerm);
                }
                else if (nextToken.Type == TokenTypes.IDENTIFIER){
                    Token nextNextToken = _tokens.get(_position + 2);
                    if (nextNextToken.Type == TokenTypes.SYM_COMMA){
                        ArrayList<Token> multiPart = new ArrayList<>();
                        multiPart.add(_currentToken);
                        ArrayList<Token> declaresTerm = declares_term(nextToken, _position + 1);
                        for(int i=0;i<declaresTerm.size();i++){
                            GoToNextToken();
                            GoToNextToken();
                        }
                        if (_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                            throw new SyntacticException("Expected semicolon token");
                        }
                        GoToNextToken();
                        return new NonTerminalSymbolNode(multiPart, declaresTerm);
                    }
                    else if (nextNextToken.Type == TokenTypes.SYM_SEMICOLON){
                        ArrayList<Token> multiPart = new ArrayList<>();
                        ArrayList<Token> declaresTerm = new ArrayList<>();
                        multiPart.add(_currentToken);
                        declaresTerm.add(nextToken);
                        GoToNextToken();
                        GoToNextToken();
                        GoToNextToken();
                        return new NonTerminalSymbolNode(multiPart, declaresTerm);
                    }
                }
            }
            else{
                throw new SyntacticException("Expected reserved terminal token");
            }
        }
        return null;
    }

    private ArrayList<Token> declares_term(Token nextToken, int pos) {
        ArrayList<Token> identifiers = new ArrayList<>();
        while(nextToken.Type == TokenTypes.IDENTIFIER || nextToken.Type == TokenTypes.SYM_COMMA){
            if(nextToken.Type == TokenTypes.IDENTIFIER ){
                identifiers.add(nextToken);
            }
            nextToken = _tokens.get(++pos);
        }
        return identifiers;
    }

    private ArrayList<StatementNode> code_parts() throws SyntacticException {
        if(TokenValidations.TokenStartsAsCodeParts(_currentToken)){
            StatementNode codePartStatement = code_part();
            ArrayList<StatementNode> codeParts = code_parts();
            codeParts.add(0, codePartStatement);
            return codeParts;
        }
        else{
            Epsilon();
            return new ArrayList<StatementNode>();
        }
    }

    private StatementNode code_part() throws SyntacticException {
        if(_currentToken.Type == TokenTypes.RESERVED_ACTION){
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.RESERVED_CODE){
                throw new SyntacticException("Expected reserved CODE token");
            }
            GoToNextToken();
            String javaCode = _currentToken.Lexeme;
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                throw new SyntacticException("Expected semicolon token");
            }
            GoToNextToken();
            return new ActionCodeNode(javaCode);
        }
        else if(_currentToken.Type == TokenTypes.RESERVED_SCAN){
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.RESERVED_WITH){
                throw new SyntacticException("Expected reserved WITH token");
            }
            GoToNextToken();
            String javaCode = _currentToken.Lexeme;
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                throw new SyntacticException("Expected semicolon token");
            }
            GoToNextToken();
            return new ScanCodeNode(javaCode);
        }
        else if(_currentToken.Type == TokenTypes.RESERVED_INIT){
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.RESERVED_WITH){
                throw new SyntacticException("Expected reserved WITH token");
            }
            GoToNextToken();
            String javaCode = _currentToken.Lexeme;
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                throw new SyntacticException("Expected semicolon token");
            }
            GoToNextToken();
            return new InitCodeNode(javaCode);
        }
        else{
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.RESERVED_CODE){
                throw new SyntacticException("Expected reserved CODE token");
            }
            GoToNextToken();
            String javaCode = _currentToken.Lexeme;
            GoToNextToken();
            if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                throw new SyntacticException("Expected semicolon token");
            }
            GoToNextToken();
            return new ParserCodeNode(javaCode);
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
        ArrayList<Token> identifiers = import_id();
        if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
            throw new SyntacticException("Expected semicolon token");
        }
        else{
            GoToNextToken();
            return new ImportStatementNode(identifiers);
        }
    }

    private ArrayList<Token> import_id() {
        ArrayList<Token> identifiers = new ArrayList<>();
        while(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_ASTERISK || _currentToken.Type == TokenTypes.SYM_DOT){
            if(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_ASTERISK){
                identifiers.add(_currentToken);
            }
            GoToNextToken();
        }
        return identifiers;
    }

    private ArrayList<StatementNode> package_spec() throws SyntacticException {
        ArrayList<StatementNode> returnPackageStatements = new ArrayList<>();
        if(_currentToken.Type == TokenTypes.RESERVED_PACKAGE){
            GoToNextToken();
            ArrayList<Token> multipart_id = multipart_id();
            returnPackageStatements.add(new PackageStatementNode(multipart_id));

            if(_currentToken.Type != TokenTypes.SYM_SEMICOLON){
                throw new SyntacticException("Expected semicolon token");
            }
            GoToNextToken();
        }
        return returnPackageStatements;
    }

    private ArrayList<Token> multipart_id() {
        ArrayList<Token> identifiers = new ArrayList<>();
        while(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_DOT){
            if(_currentToken.Type == TokenTypes.IDENTIFIER ){
                identifiers.add(_currentToken);
            }
            GoToNextToken();
        }
        return identifiers;
    }

    private ArrayList<Token> declares_term() {
        ArrayList<Token> identifiers = new ArrayList<>();
        while(_currentToken.Type == TokenTypes.IDENTIFIER || _currentToken.Type == TokenTypes.SYM_COMMA){
            if(_currentToken.Type == TokenTypes.IDENTIFIER ){
                identifiers.add(_currentToken);
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
