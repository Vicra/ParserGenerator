package ParserGenerator.TreeComponents.Statements;

import ParserGenerator.TreeComponents.StatementNode;
import com.google.gson.annotations.SerializedName;


public abstract class UserCodeStatementNode extends StatementNode{
    @SerializedName("Java Code")
    public String JavaCode;

    public UserCodeStatementNode(String javaCode){
        this.JavaCode = javaCode;
    }
}
