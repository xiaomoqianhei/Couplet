package canto.c1.ast;

public class AddExpression extends BinaryExpression {

	public AddExpression(Expression leftOperand, Expression rightOperand,
			int line, int column) {
		super(leftOperand, rightOperand, line, column);
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public int getASTType() {
		return ADD_EXPRESSION;
	}

}
