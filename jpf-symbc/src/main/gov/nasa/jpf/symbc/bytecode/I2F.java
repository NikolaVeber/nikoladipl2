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


import gov.nasa.jpf.jvm.KernelState;
import gov.nasa.jpf.jvm.SystemState;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ChoiceGenerator;

import gov.nasa.jpf.symbc.numeric.*;



/**
 * Convert int to float
 * ..., value  =>..., result
 */
public class I2F extends gov.nasa.jpf.jvm.bytecode.I2F {

	public Instruction execute (SystemState ss, KernelState ks, ThreadInfo th) {
		  
		  IntegerExpression sym_ival = (IntegerExpression) th.getTopFrame().getOperandAttr(); 
			
		  if(sym_ival == null) {
			  	  // System.out.println("Execute concrete I2F");
			  	  return super.execute(ss,ks,th); 
		  }
		  else {
			  	  //System.out.println("Execute symbolic I2F");	  
				  // here we get a hold of the current path condition and 
				  // add an extra mixed constraint sym_dval==sym_ival

				    ChoiceGenerator<?> cg; 
					if (!th.isFirstStepInsn()) { // first time around
						cg = new PCChoiceGenerator(1); // only one choice 
						ss.setNextChoiceGenerator(cg);
						return this;  	      
					} else {  // this is what really returns results
						cg = ss.getChoiceGenerator();
						assert (cg instanceof PCChoiceGenerator) : "expected PCChoiceGenerator, got: " + cg;
					}	
					
					// get the path condition from the 
					// previous choice generator of the same type 

				    PathCondition pc;
					ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGenerator();
					while (!((prev_cg == null) || (prev_cg instanceof PCChoiceGenerator))) {
						prev_cg = prev_cg.getPreviousChoiceGenerator();
					}

					if (prev_cg == null)
						pc = new PathCondition(); // TODO: handling of preconditions needs to be changed
					else 
						pc = ((PCChoiceGenerator)prev_cg).getCurrentPC();
					assert pc != null;
					
					th.pop();
					th.push(0,false); // for symbolic expressions, the concrete value does not matter
					SymbolicReal sym_dval = new SymbolicReal();
					StackFrame sf = th.getTopFrame();
					sf.setOperandAttr(sym_dval);
					
					pc._addDet(Comparator.EQ, sym_dval, sym_ival);
					
					if(!pc.simplify())  { // not satisfiable
						ss.setIgnored(true);
					} else {
						//pc.solve();
						((PCChoiceGenerator) cg).setCurrentPC(pc);
						//System.out.println(((PCChoiceGenerator) cg).getCurrentPC());
					}
					
					// System.out.println("Execute I2D: " + sf.getLongOperandAttr());
					return getNext(th);
		  }
		}
	}
