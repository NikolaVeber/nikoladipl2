//
// Copyright (C) 2011 Igor Andjelkovic (igor.andjelkovic@gmail.com).
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

import gov.nasa.jpf.traceServer.printer.generic.TracePrinter;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

import java.io.PrintWriter;

/**
 * Analyzer that prints each event stored in the last path of the trace by using
 * the default TracePrinter implementation, i.e. printers from
 * traceServer.printer package.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class GenericOutputExecTrackerAnalyzer extends TraceAnalyzer {

  private TracePrinter tracePrinter;

  private static final int ZERO_PRIORITY = 0;
  private static final int ONE_PRIORITY = 1;
  private static final int TEN_PRIORITY = 10;

  private int threshold;

  public GenericOutputExecTrackerAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
  }

  protected void publishTrace() {
    GenericEventIterator iterator = calculatePriorities();
    tracePrinter.printTrace(iterator, threshold);
  }

  private GenericEventIterator calculatePriorities() {
    boolean reversePath = false;
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        TracePredicate.ALL, reversePath));

    for (Event event : iterator) {
      eventType type = event.getEventType();
      if (type == eventType.stateAdvanced) {
        event.setPrintingPriority(ZERO_PRIORITY);
      } else if (EventTypes.typeToGroupType(type) == EventTypes.eventGroupType.object
          || EventTypes.typeToGroupType(type) == EventTypes.eventGroupType.thread) {
        event.setPrintingPriority(ONE_PRIORITY);
      } else {
        event.setPrintingPriority(TEN_PRIORITY);
      }
    }
    iterator.resetIteration();
    return iterator;
  }

  public void analyze() {
    query.startTraceQuery();
    publishTrace();
    query.stopTraceQuery();

  }

  public void configureAnalyzer(Object... args) {
    if (args.length > 0) {
      try {
      threshold = Integer.parseInt((String) args[2]);
      } catch(NumberFormatException e) {
        threshold = TEN_PRIORITY;
      }
    } else {
      threshold = TEN_PRIORITY;
    }
    tracePrinter = new TracePrinter(new PrintWriter(System.out, true));
  }

  public static void main(String... arg) {
    if (arg.length == 0) {
      throw new RuntimeException("You must provide database location");
    }
    String dbLocation = arg[0];
    String query = arg[1];

    GenericOutputExecTrackerAnalyzer execTrackerAnalyzer = new GenericOutputExecTrackerAnalyzer(
        query, dbLocation);

    if (arg.length > 2) {
      execTrackerAnalyzer.configureAnalyzer((Object[]) arg);
    } else {
      execTrackerAnalyzer.configureAnalyzer();
    }

    execTrackerAnalyzer.analyze();
  }
}
