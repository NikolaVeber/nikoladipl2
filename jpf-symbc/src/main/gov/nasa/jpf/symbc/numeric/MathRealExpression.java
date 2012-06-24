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
// supports math lib

import java.util.Map;

public class MathRealExpression extends RealExpression
{
	public RealExpression arg1, arg2;
	public MathFunction   op;
	//int exp; // for power

	public MathRealExpression (MathFunction o, RealExpression a)
	{
		assert
		o == MathFunction.SIN || o == MathFunction.COS ||
		o == MathFunction.ROUND || o == MathFunction.EXP ||
		o == MathFunction.ASIN || o == MathFunction.ACOS ||
		o == MathFunction.ATAN || o == MathFunction.LOG ||
		o == MathFunction.TAN  || o == MathFunction.SQRT;

		op = o;
		arg1 = a;
	}

	//TODO: to generalize this...
//	public MathRealExpression (MathFunction o, RealExpression a, int e)
//	{
//		op = o;
//		arg1 = a;
//		if (o == MathFunction.POW)
//			exp = e;
//
//	}

	public MathRealExpression (MathFunction o, RealExpression a1, double a2)
	{
		assert
		o == MathFunction.POW || o == MathFunction.ATAN2;
		op = o;
		arg1 = a1;
		arg2 = new RealConstant(a2);

	}

	public MathRealExpression (MathFunction o, double a1, RealExpression a2)
	{
		assert
		o == MathFunction.POW || o == MathFunction.ATAN2;
		op = o;
		arg1 = new RealConstant(a1);
		arg2 = a2;

	}
	public MathRealExpression (MathFunction o, RealExpression a1, RealExpression a2)
	{
		assert
		o == MathFunction.POW || o == MathFunction.ATAN2;
		op = o;
		arg1 = a1;
		arg2 = a2;

	}

	public RealExpression getArg1() {
		return arg1;
	}

	public RealExpression getArg2() {
		return arg2;
	}

	public MathFunction getOp() {
		return op;
	}

	public double solution()
	{
		double a1 = (arg1==null?0:arg1.solution());
	    double a2 = (arg2==null?0:arg2.solution());
		switch(op){
		   case COS:  return Math.cos(a1);
		   case SIN:  return Math.sin(a1);
		   case ROUND: return Math.round(a1);
		   case EXP: return Math.exp(a1);
		   case ASIN: return Math.asin(a1);
		   case ACOS: return Math.acos(a1);
		   case ATAN: return Math.atan(a1);
		   case LOG: return Math.log(a1);
		   case TAN: return Math.tan(a1);
		   case SQRT: return Math.sqrt(a1);
		   case POW:  return Math.pow(a1,a2);
		   case ATAN2:  return Math.atan2(a1,a2);
           default:   throw new
           	RuntimeException("## Error: MathRealExpression solution: math function " + op);
		}
	}

    public void getVarsVals(Map<String,Object> varsVals) {
    	if (arg1 != null) arg1.getVarsVals(varsVals);
    	if (arg2 != null) arg2.getVarsVals(varsVals);
    }

	public String stringPC() {
		if (op == MathFunction.SIN || op == MathFunction.COS ||
			op == MathFunction.ROUND || op == MathFunction.EXP ||
			op == MathFunction.ASIN || op == MathFunction.ACOS ||
			op == MathFunction.ATAN || op == MathFunction.LOG ||
			op == MathFunction.TAN || op == MathFunction.SQRT)
			return "(" + op.toString() + "(" + arg1.stringPC() + "))";
		else //op == MathFunction.POW || op == MathFunction.ATAN2
			return "(" + op.toString() + "(" + arg1.stringPC() + "," + arg2.stringPC() + "))";
	}

	public String toString () {
		if (op == MathFunction.SIN || op == MathFunction.COS ||
				op == MathFunction.ROUND || op == MathFunction.EXP ||
				op == MathFunction.ASIN || op == MathFunction.ACOS ||
				op == MathFunction.ATAN || op == MathFunction.LOG ||
				op == MathFunction.TAN || op == MathFunction.SQRT)
			return  op.toString() + "(" + arg1.toString() + ")";
		else //op == MathFunction.POW || op == MathFunction.ATAN2
			return  op.toString() + "(" + arg1.toString() + "," + arg2.toString() + ")";
	}
}
