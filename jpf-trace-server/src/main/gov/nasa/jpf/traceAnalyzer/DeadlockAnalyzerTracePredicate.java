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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Filters out all events that are uninteresting to the deadlock analyzer.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class DeadlockAnalyzerTracePredicate extends TracePredicate {
  LinkedHashSet<Integer> threads = new LinkedHashSet<Integer>();
  ArrayList<Event> ops = new ArrayList<Event>();
  HashMap<Integer, Integer> waits = new HashMap<Integer, Integer>();
  HashMap<Integer, Integer> blocks = new HashMap<Integer, Integer>();
  HashSet<Integer> runnables = new HashSet<Integer>();

  public boolean filter(Event event) {
    EventTypes.eventType ot = event.getEventType();

    if (!event.hasProperty(PropertyCollection.THREAD_ID)) {
      return false;
    }

    Integer oti = (Integer) event.getProperty(PropertyCollection.THREAD_ID);
    boolean toReturn = false;

    switch (ot) {
      case threadWaiting:
      case objectWait:
      case threadBlocked: {
        if (!runnables.contains(oti) && !threads.contains(oti)) {
          HashMap<Integer, Integer> map = (ot == EventTypes.eventType.threadBlocked) ? blocks
              : waits;
          threads.add(oti);
          map.put(
              (Integer) (event.getProperty(PropertyCollection.OBJECT_REFERENCE)),
              oti);
          ops.add(event);
          toReturn = true;
        }
        break;
      }
      case threadNotified:
      case objectNotify:
      case objectNotifyAll:
      case objectLocked: {
        HashMap<Integer, Integer> map = (ot == EventTypes.eventType.objectLocked) ? blocks
            : waits;
        Integer ti = map.get((Integer) (event
            .getProperty(PropertyCollection.OBJECT_REFERENCE)));

        if (ti != null && ti != oti) {
          if (!threads.contains(oti)) {
            threads.add(oti);
          }
          map.remove((Integer) (event
              .getProperty(PropertyCollection.OBJECT_REFERENCE)));
          ops.add(event);
          toReturn = true;
        }
        runnables.add(oti);
        break;
      }
      case objectUnlocked: {
        // not relevant
        runnables.add(oti);
        break;
      }
      case threadTerminated:
      case threadStarted: {
        ops.add(event); // might be removed later-on
        toReturn = true;
      }
    }

    return toReturn;
  }

  /**
   * Remove all starts/terminates of irrelevant threads.
   * 
   * @param it
   *          EventIterator that includes all events selected by the deadlock
   *          analyzer.
   * @return EventIterator without starts/terminates of irrelevant threads.
   */
  public EventIterator postProcess(EventIterator it) {
    while (it.hasNext()) {
      Event tOp = it.next();
      EventTypes.eventType ot = tOp.getEventType();
      if (ot == EventTypes.eventType.threadTerminated
          || ot == EventTypes.eventType.threadStarted) {
        if (!threads.contains((Integer) tOp
            .getProperty(PropertyCollection.THREAD_ID))) {
          it.remove();
        }
      }
    }

    return it;
  }
}
