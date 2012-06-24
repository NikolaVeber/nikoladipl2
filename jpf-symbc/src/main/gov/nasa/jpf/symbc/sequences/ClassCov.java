package gov.nasa.jpf.symbc.sequences;

import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;

import java.util.HashMap;

public class ClassCov {
	public ClassInfo ci;
	HashMap<MethodInfo, MethodCov> methods = new HashMap<MethodInfo, MethodCov>();
	
	public ClassCov(ClassInfo ci){
		this.ci = ci;
		
        try {
			for (MethodInfo mi : ci.getDeclaredMethodInfos()) {
			  // <2do> what about MJI methods? we should report why we don't cover them
			  if (!mi.isNative() && !mi.isAbstract()) {
			    MethodCov mc = new MethodCov(mi);
			    methods.put(mi, mc);
			  }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void instructionExecuted(Instruction insn, Path p) {
		try {
			if(! insn.getMethodInfo().isNative()) {
				methods.get(insn.getMethodInfo()).instructionExecuted(insn, p);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
