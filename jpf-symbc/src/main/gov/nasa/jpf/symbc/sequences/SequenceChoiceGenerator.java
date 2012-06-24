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
package gov.nasa.jpf.symbc.sequences;
import gov.nasa.jpf.jvm.choice.IntIntervalGenerator;


/**
* @author pcorina
*
*/

// I propose the following
// create a new choice generator that records the names of the methods that are executed
// symbolically + the concrete parameters and the symbolic values
// (in an array or something like that)
// it will not make any choices, but it will force JPF to remember this info on the current path
// so that it is easy to reconstruct and print it
// then we will not really need any more maps to print the information
public class SequenceChoiceGenerator extends IntIntervalGenerator {

  private String methodShortName;
  private Object [] argValues;
  private Object [] attributes;

  // will always make only one choice
@SuppressWarnings("deprecation")
public SequenceChoiceGenerator(String _methodShortName) {
      super(0, 0);
      methodShortName = _methodShortName;
  }


  public Object [] getArgValues() {
      return argValues;
  }

  public void setArgValues(Object [] _argValues) {
      argValues = _argValues;;
  }

  public Object [] getArgAttributes() {
      return attributes;
  }

  public void setArgAttributes(Object [] _attributes) {
      attributes = _attributes;
  }

  public String getMethodShortName() {
	return methodShortName;
  }

}