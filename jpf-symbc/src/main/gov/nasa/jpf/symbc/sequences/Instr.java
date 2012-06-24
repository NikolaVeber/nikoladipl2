package gov.nasa.jpf.symbc.sequences;

import java.util.Vector;

import gov.nasa.jpf.jvm.bytecode.Instruction;

public class Instr{
	public int threadId;
	public Instruction inst;
	public Vector<Path> executedBy = new Vector<Path>();
	public boolean isExecuted = false;
	public Block block;
	
	public Instr(Instruction i, int threadId){
		inst = i;
		this.threadId = threadId;
	}
	
	public Instr(Instruction instruction) {
		inst = instruction;
	}

	
	
}
