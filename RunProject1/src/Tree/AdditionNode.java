package Tree;

public class AdditionNode extends Expression{
    Expression leftOperand;
    Expression rightOperand;
    public AdditionNode(Expression left, Expression right) {
        leftOperand = left;
        rightOperand = right;
    }
}
