//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.symbc.bytecode;

import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Types;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealExpression;

/**
 * Divide 2 doubles
 * ..., value1, value2 => ..., value2/value1
 */
public class DDIV extends gov.nasa.jpf.jvm.bytecode.DDIV  {

	public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {
		StackFrame sf = th.getTopFrame();

		RealExpression sym_v1 = (RealExpression) sf.getLongOperandAttr(); 
	    double v1 = Types.longToDouble(th.longPop());
	    RealExpression sym_v2 = (RealExpression) sf.getLongOperandAttr(); 
	    double v2 = Types.longToDouble(th.longPop());
	   
	    
	    if(sym_v1==null && sym_v2==null) {
	    	if(v1==0)
	    		return th.createAndThrowException("java.lang.ArithmeticException","division by 0");
	    	double r = v2 / v1;
	    	th.longPush(Types.doubleToLong(r));
	    }
	    else
	    	th.longPush(0); 
	    
	    RealExpression result = null;
		if(sym_v2!=null) {
			if (sym_v1!=null) {
				// here check if sym_v1 eq 0 and throw an error: to work more on it
				ChoiceGenerator<?> prev_cg = ss.getChoiceGenerator();
				while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
					prev_cg = prev_cg.getPreviousChoiceGenerator();
				}

				if (prev_cg != null) {
					PathCondition pc = ((PCChoiceGenerator)prev_cg).getCurrentPC();
					pc._addDet(Comparator.EQ, sym_v1, 0);
					if(pc.simplify())  {// satisfiable
						((PCChoiceGenerator) prev_cg).setCurrentPC(pc); // only for printing summary purposes
						return th.createAndThrowException("java.lang.ArithmeticException","division by 0");
					}
				}
				else
					// no prev path conditions
					return th.createAndThrowException("java.lang.ArithmeticException","division by 0");
				result = sym_v2._div(sym_v1);
			}
			else { // v1 is concrete 
				if(v1==0)
					return th.createAndThrowException("java.lang.ArithmeticException","division by 0");
				result = sym_v2._div(v1);
			}
		}
		else if (sym_v1!=null)
			result = sym_v1._div_reverse(v2);
		
		sf.setLongOperandAttr(result);
		
		//System.out.println("Execute DDIV: "+ sf.getLongOperandAttr());

	    return getNext(th);  
		
	}

}

