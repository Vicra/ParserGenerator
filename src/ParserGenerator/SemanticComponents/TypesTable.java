package ParserGenerator.SemanticComponents;

import ParserGenerator.SemanticComponents.Types.BaseType;
import ParserGenerator.SemanticComponents.Types.NonterminalType;
import ParserGenerator.SemanticComponents.Types.TerminalType;

import java.util.Hashtable;

public class TypesTable {
    private Hashtable<String, BaseType> _table;

    private TypesTable(){
        _table = new Hashtable<>();
        _table.put("nonterminal",new NonterminalType());
        _table.put("terminal",new TerminalType());
    }

    private static TypesTable singleton = new TypesTable();

    public static TypesTable getInstance( ) {
        return singleton;
    }

    public BaseType GetType(String name) throws SemanticException {
        if (_table.containsKey(name))
        {
            return _table.get(name);
        }

        throw new SemanticException("Type: "+ name +" doesn't exists.");
    }
}
