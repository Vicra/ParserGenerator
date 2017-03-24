package Tree;

public class AssignmentNode  extends ExpressionPart{
    String id;
    Expression expression;
    public AssignmentNode(String id, Expression expression) {
        this.id = id;
        this.expression = expression;
    }
}
