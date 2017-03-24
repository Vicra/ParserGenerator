package Tree;

public class DivisionNode extends Expression{
    Expression leftOperand;
    Expression rightOperand;
    public DivisionNode(Expression left, Expression right) {
        leftOperand = left;
        rightOperand = right;
    }
}
