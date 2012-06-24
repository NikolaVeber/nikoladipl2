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
package gov.nasa.jpf.traceServer.traceStorer.remote;

import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;

/**
 * Dynamic dispatch of event to the proper storer method. Used to facilitate
 * method calls by calling just one method with the event type as argument.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class TraceStorerDispatcher {

  public static final int NUM_OF_STORER_METHODS = eventType.values().length;

  public abstract void storeEvent(TraceStorer storer, Event e);

  /**
   * "Pointers" to event storer methods.
   */
  public static TraceStorerDispatcher storeMethods[] = new TraceStorerDispatcher[NUM_OF_STORER_METHODS];

  static {
    storeMethods[eventType.instructionExecuted.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeInstructionExecuted(event);
      }
    };
    storeMethods[eventType.executeInstruction.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeExecuteInstruction(event);
      }
    };
    storeMethods[eventType.threadStarted.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadStarted(event);
      }
    };
    storeMethods[eventType.threadWaiting.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadWaiting(event);
      }
    };
    storeMethods[eventType.threadNotified.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadNotified(event);
      }
    };
    storeMethods[eventType.threadInterrupted.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadInterrupted(event);
      }
    };
    storeMethods[eventType.threadScheduled.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadScheduled(event);
      }
    };
    storeMethods[eventType.threadBlocked.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadBlocked(event);
      }
    };
    storeMethods[eventType.threadTerminated.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeThreadTerminated(event);
      }
    };
    storeMethods[eventType.classLoaded.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeClassLoaded(event);
      }
    };
    storeMethods[eventType.objectCreated.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectCreated(event);
      }
    };
    storeMethods[eventType.objectReleased.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectReleased(event);
      }
    };
    storeMethods[eventType.objectLocked.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectLocked(event);
      }
    };
    storeMethods[eventType.objectUnlocked.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectUnlocked(event);
      }
    };
    storeMethods[eventType.objectWait.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectWait(event);
      }
    };
    storeMethods[eventType.objectNotify.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectNotify(event);
      }
    };
    storeMethods[eventType.objectNotifyAll.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeObjectNotifyAll(event);
      }
    };
    storeMethods[eventType.gcBegin.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeGcBegin(event);
      }
    };
    storeMethods[eventType.gcEnd.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeGcEnd(event);
      }
    };
    storeMethods[eventType.exceptionBailout.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeExceptionBailout(event);
      }
    };
    storeMethods[eventType.exceptionThrown.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeExceptionThrown(event);
      }
    };
    storeMethods[eventType.exceptionHandled.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeExceptionHandled(event);
      }
    };
    storeMethods[eventType.choiceGeneratorSet.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeChoiceGeneratorSet(event);
      }
    };
    storeMethods[eventType.choiceGeneratorRegistered.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeChoiceGeneratorRegistered(event);
      }
    };
    storeMethods[eventType.choiceGeneratorAdvanced.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeChoiceGeneratorAdvanced(event);
      }
    };
    storeMethods[eventType.choiceGeneratorProcessed.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeChoiceGeneratorProcessed(event);
      }
    };
    storeMethods[eventType.stateAdvanced.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeStateAdvanced(event);
      }
    };
    storeMethods[eventType.stateProcessed.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeStateProcessed(event);
      }
    };
    storeMethods[eventType.stateBacktracked.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeStateBacktracked(event);
      }
    };
    storeMethods[eventType.stateStored.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeStateStored(event);
      }
    };
    storeMethods[eventType.stateRestored.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeStateRestored(event);
      }
    };
    storeMethods[eventType.propertyViolated.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storePropertyViolated(event);
      }
    };
    storeMethods[eventType.searchStarted.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeSearchStarted(event);
      }
    };
    storeMethods[eventType.searchConstraintHit.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeSearchConstraintHit(event);
      }
    };
    storeMethods[eventType.searchFinished.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeSearchFinished(event);
      }
    };
    storeMethods[eventType.statePurged.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeStatePurged(event);
      }
    };

    storeMethods[eventType.methodEntered.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeMethodEntered(event);
      }
    };
    storeMethods[eventType.methodExited.ordinal()] = new TraceStorerDispatcher() {
      public void storeEvent(TraceStorer storer, Event event) {
        storer.storeMethodExited(event);
      }
    };
  }

}
