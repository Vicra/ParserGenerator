package ParserGenerator.SemanticComponents;

import ParserGenerator.SemanticComponents.Types.BaseType;

import java.util.Hashtable;

public class SymbolTable {
    private static Hashtable<String, BaseType> _symbols;
    private static SymbolTable singleton = new SymbolTable( );

    private SymbolTable() {
        _symbols = new Hashtable<>();
    }

    public static SymbolTable getInstance( ) {
        return singleton;
    }

    public void DeclareVariable (String name, String type) throws SemanticException {
        if (_symbols.containsKey(name)){
            throw new SemanticException("Ya existe este simbolo");
        }
        _symbols.put(name, TypesTable.getInstance().GetType(type));
    }

    public BaseType GetType (String name) throws SemanticException {
        if (!_symbols.containsKey(name)){
            throw new SemanticException("No Existe este symbolo");
        }
        return _symbols.get(name);
    }
}
