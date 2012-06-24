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
package gov.nasa.jpf.traceEmitter.traceFilter;

import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Dynamic dispatch of event to the proper processing method. Used to facilitate
 * method calls by calling just one method with the event type as argument.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class FilterDispatcher {
  public abstract void filterEvent(TraceFilter filter, Event e);

  /**
   * "Pointers" to event processing methods.
   */
  public static FilterDispatcher filterMethods[] = new FilterDispatcher[eventType
      .values().length];

  static {
    filterMethods[eventType.instructionExecuted.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processInstructionExecuted(event);
      }
    };
    filterMethods[eventType.executeInstruction.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processExecuteInstruction(event);
      }
    };
    filterMethods[eventType.threadStarted.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadStarted(event);
      }
    };
    filterMethods[eventType.threadWaiting.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadWaiting(event);
      }
    };
    filterMethods[eventType.threadNotified.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadNotified(event);
      }
    };
    filterMethods[eventType.threadInterrupted.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadInterrupted(event);
      }
    };
    filterMethods[eventType.threadScheduled.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadScheduled(event);
      }
    };
    filterMethods[eventType.threadBlocked.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadBlocked(event);
      }
    };
    filterMethods[eventType.threadTerminated.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processThreadTerminated(event);
      }
    };
    filterMethods[eventType.classLoaded.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processClassLoaded(event);
      }
    };
    filterMethods[eventType.objectCreated.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectCreated(event);
      }
    };
    filterMethods[eventType.objectReleased.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectReleased(event);
      }
    };
    filterMethods[eventType.objectLocked.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectLocked(event);
      }
    };
    filterMethods[eventType.objectUnlocked.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectUnlocked(event);
      }
    };
    filterMethods[eventType.objectWait.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectWait(event);
      }
    };
    filterMethods[eventType.objectNotify.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectNotify(event);
      }
    };
    filterMethods[eventType.objectNotifyAll.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processObjectNotifyAll(event);
      }
    };
    filterMethods[eventType.gcBegin.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processGcBegin(event);
      }
    };
    filterMethods[eventType.gcEnd.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processGcEnd(event);
      }
    };
    filterMethods[eventType.exceptionBailout.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processExceptionBailout(event);
      }
    };
    filterMethods[eventType.exceptionThrown.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processExceptionThrown(event);
      }
    };
    filterMethods[eventType.exceptionHandled.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processExceptionHandled(event);
      }
    };
    filterMethods[eventType.choiceGeneratorSet.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processChoiceGeneratorSet(event);
      }
    };
    filterMethods[eventType.choiceGeneratorRegistered.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processChoiceGeneratorRegistered(event);
      }
    };
    filterMethods[eventType.choiceGeneratorAdvanced.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processChoiceGeneratorAdvanced(event);
      }
    };
    filterMethods[eventType.choiceGeneratorProcessed.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processChoiceGeneratorProcessed(event);
      }
    };
    filterMethods[eventType.stateAdvanced.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processStateAdvanced(event);
      }
    };
    filterMethods[eventType.stateProcessed.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processStateProcessed(event);
      }
    };
    filterMethods[eventType.stateBacktracked.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processStateBacktracked(event);
      }
    };
    filterMethods[eventType.stateStored.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processStateStored(event);
      }
    };
    filterMethods[eventType.stateRestored.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processStateRestored(event);
      }
    };
    filterMethods[eventType.propertyViolated.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processPropertyViolated(event);
      }
    };
    filterMethods[eventType.searchStarted.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processSearchStarted(event);
      }
    };
    filterMethods[eventType.searchConstraintHit.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processSearchConstraintHit(event);
      }
    };
    filterMethods[eventType.searchFinished.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processSearchFinished(event);
      }
    };
    filterMethods[eventType.statePurged.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processStatePurged(event);
      }
    };
    filterMethods[eventType.methodEntered.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processMethodEntered(event);
      }
    };
    filterMethods[eventType.methodExited.ordinal()] = new FilterDispatcher() {
      public void filterEvent(TraceFilter filter, Event event) {
        filter.processMethodExited(event);
      }
    };
  }
}
