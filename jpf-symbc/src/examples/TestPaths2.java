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

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.util.*;

/**
 * example to demonstrate creation of test suites for path coverage
 */
public class TestPaths2 {

  public static void main (String[] args){
   // testMe(42, false);
	System.out.println("!!!!!!!!!!!!!!! Start Testing! ");
    (new TestPaths2()).testMe2(0,false, "");
  }

  // how many tests do we need to cover all paths?
  public static void testMe (int x, boolean b) {
    System.out.println("x = " + x);
    
    
	  if (x <= 1200){
		  System.out.println("  <= 1200");
    }
	  if(x >= 1200){
		  System.out.println("  >= 1200");
    }
  }

  public int fouad(int paramx){
	if (paramx < 10)
		return paramx + 1;

	return paramx * 10;	
  }

  public void testMe2 (int x, boolean b, String s) {
	  
	  DescriptiveStatistics stat = new DescriptiveStatistics();
	    
	    for (int q = 0; q < 5; q++) {
	    	  double responseTime = 5;
	    	  stat.addValue(responseTime);
	    	}
	    	double stddev = stat.getStandardDeviation();
	    	double mean = stat.getMean();
	    	System.out.println("Mean: " + mean);
	  
	  
	  System.out.println("!!!!!!!!!!!!!!! First step! ");
	    //System.out.println("x = " + x);
        if (b) {
        	if (x < fouad(x)){
        		if(s.endsWith("la"))
        			System.out.println("  <= 1200 la");
        		else 
        			System.out.println("  <= 1200");
        	}
        	if(x > fouad(x)){
        		System.out.println("  >= 1200");
        	}
        	
        }
	  }

}
