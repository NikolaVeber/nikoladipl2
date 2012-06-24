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

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceQuery.EventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

import java.util.Collection;
import java.util.List;

/**
 * Analyzer that searches for deadlock by observing thread interaction.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class DeadlockAnalyzer extends TraceAnalyzer {

  private static final String PRINT_COLUMN = "column";
  private static final String PRINT_ESSENTIAL = "essential";
  private static final String PRINT_ALL = "all";

  private String format;

  public DeadlockAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
    format = PRINT_ALL;
  }

  void printHeader(Collection<Integer> tlist) {
    for (Integer ti : tlist) {
      out.print(String.format("  %1$2d    ", ti));
    }
    out.print(" trans      insn          loc");
    out.println();

    for (int i = 0; i < tlist.size(); i++) {
      out.print("------- ");
    }
    out.print("---------------------------------------------------");
    out.println();
  }

  public void printAllColumnOps() {
    TracePredicate predicate = deadlockAnalyzerPredicate();
    boolean reversePath = true;
    EventIterator iterator = query.getLastPath(predicate, reversePath);
    List<Integer> threadList = query.getThreadIdList(iterator);
    printHeader(threadList);
    iterator.resetIteration();
    printOps(iterator, threadList);
  }

  public void printColumnOps(EventIterator events,
      Collection<Integer> threadList) {
    printOps(events, threadList);
  }

  private char eventTypeToChar(eventType type) {
    switch (type) {
      case threadWaiting:
      case objectWait:
        return 'W';
      case threadBlocked:
        return 'B';
      case objectNotify:
      case threadNotified:
        return 'N';
      case objectNotifyAll:
        return 'A';
      case objectLocked:
        return 'L';
      case objectUnlocked:
        return 'U';
      case threadTerminated:
        return 'T';
      case threadStarted:
        return 'S';
      default:
        return ' ';
    }
  }

  private void printOps(EventIterator events, Collection<Integer> threadList) {
    for (Event event : events) {
      int threadId = (Integer) event.getProperty(PropertyCollection.THREAD_ID);
      for (Integer id : threadList) {
        if (threadId == id) {
          EventTypes.eventType eType = event.getEventType();
          if (eType == eventType.threadStarted
              || eType == eventType.threadTerminated) {
            out.print(String.format("   %1$s    ", eventTypeToChar(eType)));
          } else {
            out.print(String.format("%1$s:%2$-5d ", eventTypeToChar(eType),
                event.getProperty(PropertyCollection.OBJECT_REFERENCE)));
          }
        } else {
          out.print("   |    ");
        }
      }
      out.print(String.format("%6d", events.getCurrentEventStateId()));
      String opcode = (String) event
          .getProperty(PropertyCollection.INSTRUCTION_OPCODE);
      String location = (String) event
          .getProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION);
      out.print(String.format(" %1$18.18s %2$s", opcode, location));
      out.println();
    }
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

    // now we are ready to print
    printHeader(predicate.threads);
    printColumnOps(eventIteratorPath, predicate.threads);
  }

  public void analyze() {
    query.startTraceQuery();
    if (format.equals(PRINT_COLUMN)) {
      printAllColumnOps();
    } else if (format.equals(PRINT_ESSENTIAL)) {
      printEssentialOps();
    } else {
      System.out.println("\nPrint collumn");
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

    DeadlockAnalyzer deadlockAnalyzer = new DeadlockAnalyzer(query, dbLocation);

    if (arg.length > 2) {
      deadlockAnalyzer.configureAnalyzer(arg[2]);
    } else {
      deadlockAnalyzer.configureAnalyzer();
    }

    deadlockAnalyzer.analyze();
  }
}
