package canto.c1.x86;

public class MOV extends Instruction {

	private final Location dst;
	
	private final Operand src;

	public MOV(Location dst, Operand src) {
		this.dst = dst;
		this.src = src;
	}

	@Override
	public int getTCType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Location getDst() {
		return dst;
	}

	public Operand getSrc() {
		return src;
	}
	
}
