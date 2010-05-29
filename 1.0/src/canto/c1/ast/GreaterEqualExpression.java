package canto.c1.ast;

import canto.CantoException;

public class GreaterEqualExpression extends BinaryExpression {

	public GreaterEqualExpression(Expression leftOperand,
			Expression rightOperand, int line, int column) {
		super(leftOperand, rightOperand, line, column);
	}

	@Override
	public void accept(ASTVisitor visitor) throws CantoException {
		visitor.visit(this);
	}

	@Override
	public int getNodeType() {
		return GREATER_EQUAL_EXPRESSION;
	}

}
