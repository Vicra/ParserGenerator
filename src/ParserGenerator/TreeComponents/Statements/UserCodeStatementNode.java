package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.StatementNode;


public abstract class UserCodeStatementNode extends StatementNode{
    public String JavaCode;

    public UserCodeStatementNode(String javaCode){
        this.JavaCode = javaCode;
    }
}
