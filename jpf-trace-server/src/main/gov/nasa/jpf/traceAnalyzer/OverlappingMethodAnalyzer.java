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
import gov.nasa.jpf.traceEmitter.MethodAnalyzerEmitter.OpType;
import gov.nasa.jpf.traceServer.traceQuery.EventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceStorer.Event;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a specialized MethodAnalyzer that looks for overlapping method
 * calls on the same object from different threads.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class OverlappingMethodAnalyzer extends MethodAnalyzer {

  public OverlappingMethodAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
  }

  private Event getReturnOp(Event op, boolean withinSameThread, EventIterator iter) {
    String mi = (String) op
        .getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
    int stackDepth = (Integer) op.getProperty(PropertyCollection.STACK_DEPTH);
    int ei = (Integer) op.getProperty(PropertyCollection.EI_INDEX);
    Integer ti = (Integer) op.getProperty(PropertyCollection.THREAD_ID);

    for (Event o : iter) {
      if (withinSameThread
          && ((Integer) o.getProperty(PropertyCollection.THREAD_ID)) != ti) {
        break;
      }
      if ((mi.equals(((String) o
          .getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME))))
          && (((Integer) o.getProperty(PropertyCollection.THREAD_ID)) == ti)
          && (((Integer) o.getProperty(PropertyCollection.STACK_DEPTH)) == stackDepth)
          && (((Integer) o.getProperty(PropertyCollection.EI_INDEX)) == ei)) {
        return o;
      }
    }

    return null;
  }

  // check if there is an open exec from another thread for the same ElementInfo
  private boolean isOpenExec(HashMap<Integer, Deque<Event>> openExecs, Event op) {
    int ti = (Integer) op.getProperty(PropertyCollection.THREAD_ID);
    int ei = (Integer) op.getProperty(PropertyCollection.EI_UNIQUE_ID);

    for (Map.Entry<Integer, Deque<Event>> e : openExecs.entrySet()) {
      if (e.getKey() != ti) {
        Deque<Event> s = e.getValue();
        for (Iterator<Event> it = s.descendingIterator(); it.hasNext();) {
          Event o = it.next();
          if ((Integer) o.getProperty(PropertyCollection.EI_UNIQUE_ID) == ei) {
            return true;
          }
        }
      }
    }

    return false;
  }

  // clean up (if necessary) - both RETURNS and exceptions
  private void cleanUpOpenExec(HashMap<Integer, Deque<Event>> openExecs, Event op) {
    int ti = (Integer) op.getProperty(PropertyCollection.THREAD_ID);
    int stackDepth = (Integer) op.getProperty(PropertyCollection.STACK_DEPTH);

    Deque<Event> stack = openExecs.get(ti);
    if (stack != null && !stack.isEmpty()) {
      for (Event o = stack.peek(); o != null
          && (Integer) o.getProperty(PropertyCollection.STACK_DEPTH) >= stackDepth; o = stack
          .peek()) {
        stack.pop();
      }
    }
  }

  private void addOpenExec(HashMap<Integer, Deque<Event>> openExecs, Event op) {
    int ti = (Integer) op.getProperty(PropertyCollection.THREAD_ID);
    Deque<Event> stack = openExecs.get(ti);

    if (stack == null) {
      stack = new ArrayDeque<Event>();
      stack.push(op);
      openExecs.put(ti, stack);

    } else {
      stack.push(op);
    }
  }

  public void printOn() {
    HashMap<Integer, Deque<Event>> openExecs = new HashMap<Integer, Deque<Event>>();

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

      cleanUpOpenExec(openExecs, op);

      if (super.isMethodEnter(op)) { // EXEC or CALL_EXEC
        Event retOp = getReturnOp(op, true, iterator.clone());
        if (retOp != null) { // completed, skip
          if (!isOpenExec(openExecs, op)) {
            iterator.goTo(retOp);
            continue;
          }
        } else { // this is an open method exec, record it
          addOpenExec(openExecs, op);
        }
      }

      op = consolidateOp(op, iterator.clone());

      printOn(op);
      out.println();
    }
  }

  private Event consolidateOp(Event op, EventIterator iterator) {
    OpType opType = OpType.values()[(Integer) op
        .getProperty(PropertyCollection.METHOD_CALL_TYPE)];
    int opStateId = iterator.getCurrentEventStateId();
    for (Event o : iterator) {
      int oStateId = iterator.getCurrentEventStateId();
      if (showTransition && (oStateId != opStateId)) {
        break;
      }
      if (super.isSameMethod(o, op)) {
        OpType oType = OpType.values()[(Integer) o
            .getProperty(PropertyCollection.METHOD_CALL_TYPE)];
        opType = OpType.values()[(Integer) op
            .getProperty(PropertyCollection.METHOD_CALL_TYPE)];
        switch (oType) {
        case RETURN:
          switch (opType) {
          case CALL_EXECUTE:
            op = o;
            opStateId = oStateId;
            op.addProperty(PropertyCollection.METHOD_CALL_TYPE,
                OpType.CALL_EXEC_RETURN);
            break;
          case EXECUTE:
            op = o;
            opStateId = oStateId;
            op.addProperty(PropertyCollection.METHOD_CALL_TYPE,
                OpType.EXEC_RETURN);
            break;
          }
          break;
        case EXEC_RETURN:
          switch (opType) {
          case CALL:
            op = o;
            opStateId = oStateId;
            op.addProperty(PropertyCollection.METHOD_CALL_TYPE,
                OpType.CALL_EXEC_RETURN);
            break;
          }
          break;
        case CALL_EXECUTE: // simple loop
          switch (opType) {
          case CALL_EXEC_RETURN:
            op = o;
            opStateId = oStateId;
          }
          break;
        }
      } else {
        break;
      }
    }
    return op;
  }
  
  public static void main(String... arg) {
    if (arg.length == 0) {
      throw new RuntimeException("You must provide database location");
    }
    String dbLocation = arg[0];
    String query = arg[1];

    OverlappingMethodAnalyzer overlappingMethodAnalyzer = new OverlappingMethodAnalyzer(query,
        dbLocation);
    overlappingMethodAnalyzer.analyze();
  }

}
