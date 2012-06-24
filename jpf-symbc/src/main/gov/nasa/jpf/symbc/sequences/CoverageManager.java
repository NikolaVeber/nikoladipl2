package gov.nasa.jpf.symbc.sequences;
import java.util.HashMap;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.symbc.sequences.CoverageAnalyzer.MethodCoverage;

public class CoverageManager {
	private HashMap<ClassInfo, ClassCov> classes = new HashMap<ClassInfo, ClassCov> ();
	
	public void addClass(ClassInfo ci){
		ClassCov cc = new ClassCov(ci);
		classes.put(ci,  cc);
    
	}

	public void instructionExecuted(Instruction insn, Path p) {
		ClassInfo ci = insn.getMethodInfo().getClassInfo();
	
		if (ci != null) {
			if (!classes.containsKey(ci)) {
				addClass(ci);
			}
			classes.get(ci).instructionExecuted(insn, p);
		}
	}

	public void printClasses() {
		System.out.println("AA");
		
	}
}
