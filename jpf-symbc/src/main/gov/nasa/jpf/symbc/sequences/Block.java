package gov.nasa.jpf.symbc.sequences;

public class Block {
	public boolean isCovered = false;
	public int firstInst;
	public int length;

	public Block(int firstInst) {
		this.firstInst = firstInst;
	}
}