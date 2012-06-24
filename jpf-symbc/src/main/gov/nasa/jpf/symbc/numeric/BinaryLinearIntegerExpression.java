//
//Copyright (C) 2005 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.numeric;

import java.util.Map;

public class BinaryLinearIntegerExpression extends LinearIntegerExpression
{
	IntegerExpression left;
	Operator   op;
	IntegerExpression right;

	public BinaryLinearIntegerExpression (IntegerExpression l, Operator o, IntegerExpression r)
	{
		left = l;
		op = o;
		right = r;
	}

	public int solution()
	{
		int l = left.solution();
		int r = right.solution();
		switch(op){
 		  case PLUS:       return l + r;
		  case MINUS:      return l - r;
		  case MUL: return l * r;
		  case DIV: return l / r;
		  case AND: return l & r;
		  case OR: return l | r;
		  case XOR: return l ^ r;
		  case SHIFTL: return l << r;
		  case SHIFTR: return l >> r;
		  case SHIFTUR: return l >>> r;
		  default: throw new RuntimeException("## Error: BinaryLinearSolution solution: l " + l + " op " + op + " r " + r);
		}
	}

    public void getVarsVals(Map<String,Object> varsVals) {
    	left.getVarsVals(varsVals);
    	right.getVarsVals(varsVals);
    }

	public String toString ()
	{
		return "(" + left.toString() + op.toString() + right.toString() + ")";
	}

	public String stringPC ()
	{
		return "(" + left.stringPC() + op.toString() + right.stringPC() + ")";
	}

	public IntegerExpression getLeft() {
	    return left;
	}

	public IntegerExpression getRight() {
	    return right;
	}

	public Operator getOp() {
	    return op;
	}

	public boolean equals(Object o) {
	    return ((o instanceof BinaryLinearIntegerExpression) &&
	            ((BinaryLinearIntegerExpression) o).left.equals(this.left) &&
	            ((BinaryLinearIntegerExpression) o).op.equals(this.op) &&
	            ((BinaryLinearIntegerExpression) o).right.equals(this.right));
	}

	//protected void finalize() throws Throwable {
    //	System.out.println("Finalized BLIExp -> " + this);
    //}
}
