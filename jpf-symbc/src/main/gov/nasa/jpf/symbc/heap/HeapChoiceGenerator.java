//
// Copyright (C) 2007 United States Government as represented by the
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
package gov.nasa.jpf.symbc.heap;

import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;


public class HeapChoiceGenerator extends IntIntervalGenerator {

	protected PathCondition [] PCheap; // maintains constraints on the heap: one PC per choice
    protected SymbolicInputHeap [] symInputHeap; // maintains list of input symbolic nodes; one list per choice

	@SuppressWarnings("deprecation")
	public HeapChoiceGenerator(int size) {
		super(0, size - 1);
		PCheap = new PathCondition[size];
		symInputHeap = new SymbolicInputHeap[size];
	}

	// sets the heap constraints for the current choice
	public void setCurrentPCheap(PathCondition pc) {
		PCheap[getNextChoice()] = pc;

	}

	// returns the heap constraints for the current choice
	public PathCondition getCurrentPCheap() {
		PathCondition pc;

		pc = PCheap[getNextChoice()];
		if (pc != null) {
			return pc.make_copy();
		} else {
			return null;
		}
	}

    // sets the heap constraints for the current choice
	public void setCurrentSymInputHeap(SymbolicInputHeap ih) {
		symInputHeap[getNextChoice()] = ih;

	}

	// returns the heap constraints for the current choice
	public SymbolicInputHeap getCurrentSymInputHeap() {
		SymbolicInputHeap ih;

		ih = symInputHeap[getNextChoice()];
		if (ih != null) {
			return ih.make_copy();
		} else {
			return null;
		}
	}


}
