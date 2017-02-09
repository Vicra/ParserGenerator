package ParserGenerator.LexerComponents;

import java.util.Hashtable;

public class ReservedWords
{
    public static Hashtable<String, TokenTypes> GetReservedWords()
    {
        Hashtable<String, TokenTypes> returnHashMap = new Hashtable<>();
        returnHashMap.put("terminal", TokenTypes.RESERVED_TERMINAL);
        returnHashMap.put("nonterminal", TokenTypes.RESERVED_NONTERMINAL);
        returnHashMap.put("non", TokenTypes.RESERVED_NON);
        returnHashMap.put("package", TokenTypes.RESERVED_PACKAGE);
        returnHashMap.put("import", TokenTypes.RESERVED_IMPORT);
        returnHashMap.put("action", TokenTypes.RESERVED_ACTION);
        returnHashMap.put("parser", TokenTypes.RESERVED_PARSER);
        returnHashMap.put("init", TokenTypes.RESERVED_INIT);
        returnHashMap.put("scan", TokenTypes.RESERVED_SCAN);
        returnHashMap.put("precedence", TokenTypes.RESERVED_PRECEDENCE);
        returnHashMap.put("start", TokenTypes.RESERVED_START);
        returnHashMap.put("with", TokenTypes.RESERVED_WITH);
        returnHashMap.put("left", TokenTypes.RESERVED_LEFT);
        returnHashMap.put("right", TokenTypes.RESERVED_RIGHT);
        returnHashMap.put("nonassoc", TokenTypes.RESERVED_NONASSOC);
        returnHashMap.put("code", TokenTypes.RESERVED_CODE);

        return returnHashMap;
    }

    public static Hashtable<String, TokenTypes> GetSimpleSymbols()
    {
        Hashtable<String, TokenTypes> returnHashMap = new Hashtable<>();
        returnHashMap.put("|", TokenTypes.SYM_PIPE);
        returnHashMap.put(";", TokenTypes.SYM_SEMICOLON);
        returnHashMap.put(":", TokenTypes.SYM_COLON);
        returnHashMap.put(".", TokenTypes.SYM_DOT);
        returnHashMap.put("*", TokenTypes.SYM_ASTERISK);
        returnHashMap.put(",", TokenTypes.SYM_COMMA);
        return returnHashMap;
    }

    public static Hashtable<String, TokenTypes> GetTripleSymbols()
    {
        Hashtable<String, TokenTypes> returnHashMap = new Hashtable<>();
        returnHashMap.put("::=", TokenTypes.SYM_PRODUCTION);
        return returnHashMap;
    }
}
