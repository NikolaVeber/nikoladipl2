package gov.nasa.jpf.listener;

import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;

public class TestListener extends ListenerAdapter {
	
	public void stateAdvanced (Search search){
		System.out.println("stateAdvanced, depth: " + search.getDepth());
	}

	 public void instructionExecuted (JVM vm){
		      Instruction insn = vm.getLastInstruction();
		      if (insn instanceof InvokeInstruction) {
		        InvokeInstruction iinsn = (InvokeInstruction)insn;
		        String clsName = iinsn.getInvokedMethodClassName();
		        String mthName = iinsn.getInvokedMethodName();
		        String mn = clsName + '.' + mthName;
		        System.out.println("instructionExecuted: " + mn);
		      }
		  }
	
}
