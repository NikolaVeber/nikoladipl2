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

import java.util.LinkedList;

/**
 * Filter that is capable of forwarding events in the same way they are
 * received. It can act as an emitter to another trace filter(s), or a trace
 * storer(s). Uninteresting events can be filtered out by breaking the chain. <br/>
 * <br/>
 * Implemented as a Chain of Responsibility design pattern.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TraceFilter {

  protected LinkedList<TraceFilter> successors = new LinkedList<TraceFilter>();

  /**
   * Inserts <code>filter</code> at the end of the successors list.
   * 
   * @param filter
   *          Filter to be inserted in the successors list
   */
  public void addLast(TraceFilter filter) {
    successors.addLast(filter);
  }

  /**
   * Inserts <code>filter</code> at the beginning of the successors list.
   * 
   * @param filter
   *          Filter to be inserted in the successors list
   */
  public void addFirst(TraceFilter filter) {
    successors.addFirst(filter);
  }

  /**
   * Inserts the specified filter at the specified position in the successors
   * list. Shifts the filter currently at that position (if any) and any
   * subsequent filters to the right (adds one to their indices).
   * 
   * @param index
   *          index at which the specified element is to be inserted
   * @param filter
   *          Filter to be inserted in the successors list
   */
  public void add(int index, TraceFilter filter) {
    successors.add(index, filter);
  }

  /**
   * Convenience method that forwards <code>event</code> of type
   * <code>eType</code> to the proper process method.
   * 
   * @param event
   *          Event to process
   * @param eType
   *          Event's type
   */
  public void processEvent(Event event, eventType eType) {
    FilterDispatcher.filterMethods[eType.ordinal()].filterEvent(this, event);
  }

  /**
   * Forwards <code>event</code> to all the successors. It is called when an
   * event needs to travel down the chain. The chain will be broken if this
   * method is not called.
   * 
   * @param event
   *          Event to process
   * @param eType
   *          Event's type
   */
  public void forward(Event event, eventType eType) {
    for (TraceFilter filter : successors) {
      filter.processEvent(event, eType);
    }
  }

  /**
   * The real "processing" method for <code>instructionExecuted event</code>.
   * The default implementation just forwards the event down the filter chain.
   * 
   * @param event
   *          Event to process
   */
  public void processInstructionExecuted(Event event) {
    forward(event, eventType.instructionExecuted);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processExecuteInstruction(Event event) {
    forward(event, eventType.executeInstruction);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadStarted(Event event) {
    forward(event, eventType.threadStarted);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadWaiting(Event event) {
    forward(event, eventType.threadWaiting);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadNotified(Event event) {
    forward(event, eventType.threadNotified);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadInterrupted(Event event) {
    forward(event, eventType.threadInterrupted);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadScheduled(Event event) {
    forward(event, eventType.threadScheduled);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadBlocked(Event event) {
    forward(event, eventType.threadBlocked);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processThreadTerminated(Event event) {
    forward(event, eventType.threadTerminated);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processClassLoaded(Event event) {
    forward(event, eventType.classLoaded);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectCreated(Event event) {
    forward(event, eventType.objectCreated);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectReleased(Event event) {
    forward(event, eventType.objectReleased);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectLocked(Event event) {
    forward(event, eventType.objectLocked);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectUnlocked(Event event) {
    forward(event, eventType.objectUnlocked);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectWait(Event event) {
    forward(event, eventType.objectWait);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectNotify(Event event) {
    forward(event, eventType.objectNotify);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processObjectNotifyAll(Event event) {
    forward(event, eventType.objectLocked);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processGcBegin(Event event) {
    forward(event, eventType.gcBegin);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processGcEnd(Event event) {
    forward(event, eventType.gcEnd);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processExceptionThrown(Event event) {
    forward(event, eventType.exceptionThrown);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processExceptionBailout(Event event) {
    forward(event, eventType.exceptionBailout);
  }
  
  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processExceptionHandled(Event event) {
    forward(event, eventType.exceptionHandled);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processChoiceGeneratorSet(Event event) {
    forward(event, eventType.choiceGeneratorSet);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processChoiceGeneratorRegistered(Event event) {
    forward(event, eventType.choiceGeneratorRegistered);
  }
  
  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processChoiceGeneratorAdvanced(Event event) {
    forward(event, eventType.choiceGeneratorAdvanced);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processChoiceGeneratorProcessed(Event event) {
    forward(event, eventType.choiceGeneratorProcessed);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processStateAdvanced(Event event) {
    forward(event, eventType.stateAdvanced);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processStateProcessed(Event event) {
    forward(event, eventType.stateProcessed);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processStateBacktracked(Event event) {
    forward(event, eventType.stateBacktracked);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processStateStored(Event event) {
    forward(event, eventType.stateStored);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processStateRestored(Event event) {
    forward(event, eventType.stateRestored);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processPropertyViolated(Event event) {
    forward(event, eventType.propertyViolated);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processSearchStarted(Event event) {
    forward(event, eventType.searchStarted);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processSearchConstraintHit(Event event) {
    forward(event, eventType.searchConstraintHit);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processSearchFinished(Event event) {
    forward(event, eventType.searchFinished);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processStatePurged(Event event) {
    forward(event, eventType.statePurged);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processMethodEntered(Event event) {
    forward(event, eventType.methodEntered);
  }

  /**
   * @see #processInstructionExecuted(Event)
   */
  public void processMethodExited(Event event) {
    forward(event, eventType.methodExited);
  }

}
