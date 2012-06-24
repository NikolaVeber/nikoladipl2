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

import gov.nasa.jpf.traceEmitter.MethodAnalyzerEmitter.OpType;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceQuery.EventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Analyzes call/execute sequences of methods.
 * 
 * @author Igor Andjelkovic
 */
public class MethodAnalyzer extends TraceAnalyzer {

  protected boolean skipInit;
  protected boolean showTransition;
  protected boolean showDepth;

  public MethodAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
    showTransition = true;
    skipInit = true;
    showDepth = true;
  }

  protected boolean isMethodEnter(Event e) {
    int type = (Integer) e.getProperty(PropertyCollection.METHOD_CALL_TYPE);
    return (type == OpType.CALL_EXECUTE.ordinal())
        || (type == OpType.EXECUTE.ordinal());
  }

  protected boolean isSameMethod(Event e1, Event e2) {
    String mi1 = (String) e1
        .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME)
        + (String) e1.getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
    String mi2 = (String) e2
        .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME)
        + (String) e2.getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
    int ti1 = (Integer) e1.getProperty(PropertyCollection.THREAD_ID);
    int ti2 = (Integer) e2.getProperty(PropertyCollection.THREAD_ID);
    int stackDepth1 = (Integer) e1.getProperty(PropertyCollection.STACK_DEPTH);
    int stackDepth2 = (Integer) e2.getProperty(PropertyCollection.STACK_DEPTH);
    int ei1 = (Integer) e1.getProperty(PropertyCollection.EI_UNIQUE_ID);
    int ei2 = (Integer) e2.getProperty(PropertyCollection.EI_UNIQUE_ID);
    return (mi1.equals(mi2)) && (ti1 == ti2) && (ei1 == ei2)
        && (stackDepth1 == stackDepth2);
  }

  public void printOn() {
    int lastStateId = Integer.MIN_VALUE;
    int transition = skipInit ? 1 : 0;
    int lastTid = -1;

    TracePredicate predicate = methodAnalyzerPredicate();
    boolean reversePath = false;
    EventIterator iterator = query.getLastPath(predicate, reversePath);

    for (Event op : iterator) {

      if (showTransition) {
        int opStateId = iterator.getCurrentEventStateId();
        if (opStateId != lastStateId) {
          lastStateId = opStateId;
          out.print("------------------------------------------ #");
          out.println(transition++);
        }
      } else {
        int tid = (Integer) op.getProperty(PropertyCollection.THREAD_ID);
        if (tid != lastTid) {
          lastTid = tid;
          out.println("------------------------------------------");
        }
      }
      printOn(op);
      out.println();
    }
  }

  protected void printOn(Event event) {
    out.print(event.getProperty(PropertyCollection.THREAD_ID));
    out.print(": ");
    out.print(OpType.values()[(Integer) event
        .getProperty(PropertyCollection.METHOD_CALL_TYPE)]);
    out.print(' ');

    if (showDepth) {
      int stackDepth = (Integer) event
          .getProperty(PropertyCollection.STACK_DEPTH);
      for (int i = 0; i < stackDepth; i++) {
        out.print('.');
      }
      out.print(' ');
    }

    String methodClassName = (String) event
        .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME);

    if (!(Boolean) event.getProperty(PropertyCollection.METHOD_IS_STATIC)) {
      int eiIndex = (Integer) event.getProperty(PropertyCollection.EI_INDEX);
      if (event.getProperty(PropertyCollection.EI_UNIQUE_ID) != event
          .getProperty(PropertyCollection.CLASS_UNIQUE_ID)) {// method is in
        // superclass
        out.print(methodClassName);
        out.print('<');
        out.print(event.getProperty(PropertyCollection.EI_CLASS_NAME) + "@"
            + eiIndex);
        out.print('>');
      } else { // method is in concrete object class
        out.print(event.getProperty(PropertyCollection.EI_CLASS_NAME) + "@"
            + eiIndex);
      }
    } else {
      out.print(methodClassName);
    }
    out.print('.');
    out.print(event.getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME));
  }

  protected TracePredicate methodAnalyzerPredicate() {
    TracePredicate predicate = new TracePredicate() {
      public boolean filter(Event currentEvent) {
        return currentEvent.getEventType() == eventType.instructionExecuted;
      }
    };
    return predicate;
  }

  public void analyze() {
    query.startTraceQuery();
    printOn();
    query.stopTraceQuery();
  }

  public static void main(String... arg) {
    if (arg.length == 0) {
      throw new RuntimeException("You must provide database location");
    }
    String dbLocation = arg[0];
    String query = arg[1];

    MethodAnalyzer newMethodAnalyzer = new MethodAnalyzer(query, dbLocation);
    newMethodAnalyzer.analyze();
  }
}
