package couplet.c1.ast;

public class NegExpression extends UnaryExpression {

	public NegExpression(Expression operand, int line, int column) {
		super(operand, line, column);
	}

	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public int getASTType() {
		return NEG_EXPRESSION;
	}

}
