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

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.scala.printer.EventPrinter;
import gov.nasa.jpf.traceServer.scala.printer.TracePrinter;

import gov.nasa.jpf.traceServer.traceQuery.EventIterator;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;

import java.io.PrintWriter;

/**
 * Analyzer that searches for deadlock by observing thread interaction.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ScalaDeadlockAnalyzer extends TraceAnalyzer {

  private static final String PRINT_COLUMN = "column";
  private static final String PRINT_ESSENTIAL = "essential";
  private static final String PRINT_ALL = "all";
  
  private static final int ZERO_PRIORITY = 0;
  private static final int ONE_PRIORITY = 1;

  private String format;

  private TracePrinter tracePrinter;

  public ScalaDeadlockAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
    format = PRINT_ALL;
    tracePrinter = new TracePrinter(new PrintWriter(System.out, true));
  }

  public void printAllColumnOps() {
    TracePredicate predicate = deadlockAnalyzerPredicate();
    boolean reversePath = true;
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        predicate, reversePath));
    for (Event event : iterator) {
      event.setPrintingPriority(ONE_PRIORITY);
    }
    iterator.resetIteration();

    EventPrinter[] printers = tracePrinter.getPrinters();
    printers[EventTypes.eventGroupType.object.ordinal()].addDesiredProperty(1, PropertyCollection.THREAD_ID);
    printers[EventTypes.eventGroupType.thread.ordinal()].addDesiredProperty(0, PropertyCollection.OBJECT_REFERENCE);

    tracePrinter.print(iterator, ONE_PRIORITY, false);
  }

  private TracePredicate deadlockAnalyzerPredicate() {
    TracePredicate predicate = new TracePredicate() {
      public boolean filter(Event currentEvent) {
        EventTypes.eventType eType = currentEvent.getEventType();
        switch (eType) {
          case threadWaiting:
          case objectWait:
          case threadBlocked:
          case threadNotified:
          case objectNotify:
          case objectNotifyAll:
          case objectLocked:
          case objectUnlocked:
          case threadTerminated:
          case threadStarted: {
            return true;
          }
        }
        return false;
      }
    };
    return predicate;
  }

  /**
   * include all threads that are currently blocked or waiting, and all the
   * threads that had the last interaction with them. Note that we do this
   * completely on the basis of the recorded Events, i.e. don't rely on when
   * this is called
   */
  public void printEssentialOps() {
    DeadlockAnalyzerTracePredicate predicate = new DeadlockAnalyzerTracePredicate();
    boolean reversePath = true;
    EventIterator eventIteratorPath = query.getLastPath(predicate, reversePath);

    eventIteratorPath = predicate.postProcess(eventIteratorPath);
    eventIteratorPath.resetIteration();

    GenericEventIterator iterator = new GenericEventIterator(eventIteratorPath);

    for (Event event : iterator) {
      event.setPrintingPriority(ZERO_PRIORITY);
    }
    iterator.resetIteration();

    tracePrinter.print(iterator, ZERO_PRIORITY, false);

  }

  public void analyze() {
    query.startTraceQuery();
    if (format.equals(PRINT_COLUMN)) {
      printAllColumnOps();
    } else if (format.equals(PRINT_ESSENTIAL)) {
      printEssentialOps();
    } else {
      System.out.println("\nPrint column");
      printAllColumnOps();
      System.out.println("\nPrint essential");
      printEssentialOps();
    }
    query.stopTraceQuery();
  }

  public void configureAnalyzer(Object... args) {
    if (args.length > 0) {
      format = (String) args[0];
    } else {
      format = PRINT_ALL;
    }
  }

  public static void main(String... arg) {
    if (arg.length == 0) {
      throw new RuntimeException("You must provide database location");
    }
    String dbLocation = arg[0];
    String query = arg[1];

    ScalaDeadlockAnalyzer deadlockAnalyzer = new ScalaDeadlockAnalyzer(
        query, dbLocation);

    if (arg.length > 2) {
      deadlockAnalyzer.configureAnalyzer(arg[2]);
    } else {
      deadlockAnalyzer.configureAnalyzer();
    }

    deadlockAnalyzer.analyze();
  }
}
