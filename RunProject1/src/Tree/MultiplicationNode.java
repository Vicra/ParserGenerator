package Tree;

public class MultiplicationNode extends Expression{
    Expression leftOperand;
    Expression rightOperand;
    public MultiplicationNode(Expression left, Expression right) {
        leftOperand = left;
        rightOperand = right;
    }
}
