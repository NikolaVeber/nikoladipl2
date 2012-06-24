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
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;

/**
 * TraceFilter that forwards the events to <code>trace storer</code>.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TailTraceFilter extends TraceFilter {

  private TraceStorer traceStorer;

  public TailTraceFilter(TraceStorer storer) {
    traceStorer = storer;
  }

  public void processInstructionExecuted(Event event) {
    traceStorer.storeInstructionExecuted(event);
  }

  public void processExecuteInstruction(Event event) {
    traceStorer.storeExecuteInstruction(event);
  }

  public void processThreadStarted(Event event) {
    traceStorer.storeThreadStarted(event);
  }

  public void processThreadWaiting(Event event) {
    traceStorer.storeThreadWaiting(event);
  }

  public void processThreadNotified(Event event) {
    traceStorer.storeThreadNotified(event);
  }

  public void processThreadInterrupted(Event event) {
    traceStorer.storeThreadInterrupted(event);
  }

  public void processThreadScheduled(Event event) {
    traceStorer.storeThreadScheduled(event);
  }

  public void processThreadBlocked(Event event) {
    traceStorer.storeThreadBlocked(event);
  }

  public void processThreadTerminated(Event event) {
    traceStorer.storeThreadTerminated(event);
  }

  public void processClassLoaded(Event event) {
    traceStorer.storeClassLoaded(event);
  }

  public void processObjectCreated(Event event) {
    traceStorer.storeObjectCreated(event);
  }

  public void processObjectReleased(Event event) {
    traceStorer.storeObjectReleased(event);
  }

  public void processObjectLocked(Event event) {
    traceStorer.storeObjectLocked(event);
  }

  public void processObjectUnlocked(Event event) {
    traceStorer.storeObjectUnlocked(event);
  }

  public void processObjectWait(Event event) {
    traceStorer.storeObjectWait(event);
  }

  public void processObjectNotify(Event event) {
    traceStorer.storeObjectNotify(event);
  }

  public void processObjectNotifyAll(Event event) {
    traceStorer.storeObjectNotifyAll(event);
  }

  public void processGcBegin(Event event) {
    traceStorer.storeGcBegin(event);
  }

  public void processGcEnd(Event event) {
    traceStorer.storeGcEnd(event);
  }

  public void processExceptionThrown(Event event) {
    traceStorer.storeExceptionThrown(event);
  }

  public void processExceptionBailout(Event event) {
    traceStorer.storeExceptionBailout(event);
  }

  public void processExceptionHandled(Event event) {
    traceStorer.storeExceptionHandled(event);
  }

  public void processChoiceGeneratorSet(Event event) {
    traceStorer.storeChoiceGeneratorSet(event);
  }

  public void processChoiceGeneratorRegistered(Event event) {
    traceStorer.storeChoiceGeneratorRegistered(event);
  }
  
  public void processChoiceGeneratorAdvanced(Event event) {
    traceStorer.storeChoiceGeneratorAdvanced(event);
  }

  public void processChoiceGeneratorProcessed(Event event) {
    traceStorer.storeChoiceGeneratorProcessed(event);
  }

  public void processStateAdvanced(Event event) {
    traceStorer.storeStateAdvanced(event);
  }

  public void processStateProcessed(Event event) {
    traceStorer.storeStateProcessed(event);
  }

  public void processStateBacktracked(Event event) {
    traceStorer.storeStateBacktracked(event);
  }

  public void processStateStored(Event event) {
    traceStorer.storeStateStored(event);
  }

  public void processStateRestored(Event event) {
    traceStorer.storeStateRestored(event);
  }

  public void processPropertyViolated(Event event) {
    traceStorer.storePropertyViolated(event);
  }

  public void processSearchStarted(Event event) {
    traceStorer.storeSearchStarted(event);
  }

  public void processSearchConstraintHit(Event event) {
    traceStorer.storeSearchConstraintHit(event);
  }

  public void processSearchFinished(Event event) {
    traceStorer.storeSearchFinished(event);
  }

  public void processStatePurged(Event event) {
    traceStorer.storeStatePurged(event);
  }
  
  public void processMethodEntered(Event event) {
    traceStorer.storeMethodEntered(event);
  }

  public void processMethodExited(Event event) {
    traceStorer.storeMethodExited(event);
  }

}
