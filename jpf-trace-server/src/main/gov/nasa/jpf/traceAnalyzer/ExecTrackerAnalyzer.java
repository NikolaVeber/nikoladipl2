//
// Copyright (C) 2010 Igor Andjelkovic (igor.andjelkovic@gmail.com).
// All Rights Reserved.
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
package gov.nasa.jpf.traceAnalyzer;

import gov.nasa.jpf.traceServer.printer.OldTracePrinter;
import gov.nasa.jpf.traceServer.printer.TracePrinter;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;

import java.io.PrintWriter;

/**
 * Analyzer that prints each event stored in the last path of the trace by using
 * the default TracePrinter implementation, i.e. printers from
 * traceServer.printer package.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ExecTrackerAnalyzer extends TraceAnalyzer {

  public static String ALL = "all";
  public static String CONSOLE_LIKE = "consoleLike";
  
  private TracePrinter tracePrinter;
  private OldTracePrinter oldTracePrinter;
  
  private String format;
  private String queryName;

  public ExecTrackerAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
    queryName = query;
  }

  protected void publishTrace() {
    if(format.equals(ALL)) {
      boolean reversePath = false;
      GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
          TracePredicate.ALL, reversePath));
      tracePrinter.printTrace(iterator);
    } else if(format.equals(CONSOLE_LIKE)) {
      oldTracePrinter.print(new PrintWriter(System.out, true));
    }
  }

  public void analyze() {
    query.startTraceQuery();
    publishTrace();
    query.stopTraceQuery();

  }
  
  public void configureAnalyzer(Object... args) {
    if (args.length > 0) {
      format = (String) args[0];
    } else {
      format = ALL;
    }
    
    if (format.equals(ALL)) {
      tracePrinter = new TracePrinter(new PrintWriter(System.out, true));
    } else if(format.equals(CONSOLE_LIKE)) {
      oldTracePrinter = new OldTracePrinter();
      
      //set details
      oldTracePrinter.setShowSteps(true);
      oldTracePrinter.setShowLocation(true);
      oldTracePrinter.setShowSource(true);
      oldTracePrinter.setShowMethod(true);
      oldTracePrinter.setShowCode(true);
      oldTracePrinter.setShowExtraData(true);
      oldTracePrinter.setShowCG(true);
      oldTracePrinter.setShowAPICalls(true);

      oldTracePrinter.setQueryName(queryName);
    }
  }

  public static void main(String... arg) {
    if (arg.length == 0) {
      throw new RuntimeException("You must provide database location");
    }
    String dbLocation = arg[0];
    String query = arg[1];
    
    ExecTrackerAnalyzer execTrackerAnalyzer = new ExecTrackerAnalyzer(query,
        dbLocation);
    
    if (arg.length > 2) {
      execTrackerAnalyzer.configureAnalyzer(arg[2]);
    } else {
      execTrackerAnalyzer.configureAnalyzer();
    }

    execTrackerAnalyzer.analyze();
  }
}
