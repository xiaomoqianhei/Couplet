package couplet.c1.ic;

/**
 * 中间代码的小于等于跳转指令（当第一个操作数小于等于第二个操作数时跳转到目的地）
 */
public class JLE extends CJump {

	/**
	 * 构造一条小于等于跳转的中间代码
	 * @see couplet.c1.ic.CJump#CJump(Operand, Operand, Label)
	 */
	public JLE(Operand operand1, Operand operand2, Label target) {
		super(operand1, operand2, target);
	}

	@Override
	public void accept(ICVisitor visitor) throws Exception {
		visitor.visit(this);
	}

	@Override
	public int getICType() {
		return LE_JUMP;
	}

}
