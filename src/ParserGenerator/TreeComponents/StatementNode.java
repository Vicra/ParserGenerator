package ParserGenerator.TreeComponents;

import ParserGenerator.SemanticComponents.SemanticException;

public abstract class StatementNode {
    public abstract void ValidateSemantic() throws SemanticException;
}
