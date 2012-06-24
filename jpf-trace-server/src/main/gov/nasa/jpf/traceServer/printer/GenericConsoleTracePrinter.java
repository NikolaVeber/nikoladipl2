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
package gov.nasa.jpf.traceServer.printer;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceQuery.TraceQuery;
import gov.nasa.jpf.traceServer.traceQuery.TraceQueryFactory;

/**
 * Console printer parameterized with {@link TracePrinter}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class GenericConsoleTracePrinter extends ConsoleTracePrinter {

  protected TracePrinter tracePrinter;

  /**
   * TraceQuery instance for querying the trace.
   */
  protected TraceQuery query;
  /**
   * TraceQuery name for retrieving the TraceQuery instance.
   */
  protected String queryName;

  /**
   * Output stream to which event will be printed.
   */

  public GenericConsoleTracePrinter(Config conf, Reporter reporter) {
    super(conf, reporter);
    queryName = conf.getString("traceServer.trace_storer", "inMemory");
  }

  public String getName() {
    return "genericConsoleTracePrinter";
  }

  protected void publishTrace() {
    if (tracePrinter == null) {
      tracePrinter = new TracePrinter(conf, out);
    }
    TraceQuery query = TraceQueryFactory.getTraceQuery(queryName, null);
    boolean reversePath = false;
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        TracePredicate.ALL, reversePath));
    tracePrinter.printTrace(iterator);
  }

}
