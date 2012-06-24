//
// Copyright (C) 2009 United States Government as represented by the
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



/**
 * example to demonstrate creation of test suites for path coverage
 */
public class TestPaths2 {

  public static void main (String[] args){
   // testMe(42, false);
	System.out.println("!!!!!!!!!!!!!!! Start Testing! ");
    (new TestPaths2()).testMe2(0, 0);
  }

  // how many tests do we need to cover all paths?
  public static void testMe2 (int x, int y) {
	  if (x <= 1200){
		  System.out.println("A");
	    }
	  else{
		  if (y < 5)
			  System.out.println("B");
		  else 
			  System.out.println("C");
	}
  }

}
