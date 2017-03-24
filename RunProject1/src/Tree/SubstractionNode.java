package Tree;

public class SubstractionNode extends Expression{
    Expression leftOperand;
    Expression rightOperand;
    public SubstractionNode(Expression left, Expression right) {
        leftOperand = left;
        rightOperand = right;
    }
}
