package couplet.c1.x86;

/**
 * X86目标码的JGE指令
 */
public class JGE extends Jump {

	/**
	 * 构造一条JGE指令
	 * @param target 跳转目的地
	 */
	public JGE(Label target) {
		super(target);
	}
	
	@Override
	public void accept(X86Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	
	@Override
	public int getTCType() {
		return JGE;
	}

}
