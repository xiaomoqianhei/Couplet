package canto.c1.ic;

/**
 * 中间代码的赋值语句
 */
public class Mov extends Instruction {

	private final Operand src;
	
	private final Operand dst;
	
	public Mov(Operand src, Operand dst) {
		this.src = src;
		this.dst = dst;
	}

	@Override
	public void accept(ICVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public int getICType() {
		return MOVE;
	}

	public Operand getSrc() {
		return src;
	}

	public Operand getDst() {
		return dst;
	}

}