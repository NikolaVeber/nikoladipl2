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
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;

/**
 * TraceFilter that forwards the events to a different <code>trace storer</code>
 * than the one used in the trace emitter.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class GeneralTailTraceFilter extends TraceFilter {

  private TraceStorer traceStorer;
  private TraceStorerFactory traceStorerFactory;

  public GeneralTailTraceFilter(TraceStorer storer, TraceStorerFactory factory) {
    traceStorerFactory = factory;
    traceStorer = storer;
  }

  public void processInstructionExecuted(Event event) {
    traceStorer.storeInstructionExecuted(traceStorerFactory.repackEvent(event));
  }

  public void processExecuteInstruction(Event event) {
    traceStorer.storeExecuteInstruction(traceStorerFactory.repackEvent(event));
  }

  public void processThreadStarted(Event event) {
    traceStorer.storeThreadStarted(traceStorerFactory.repackEvent(event));
  }

  public void processThreadWaiting(Event event) {
    traceStorer.storeThreadWaiting(traceStorerFactory.repackEvent(event));
  }

  public void processThreadNotified(Event event) {
    traceStorer.storeThreadNotified(traceStorerFactory.repackEvent(event));
  }

  public void processThreadInterrupted(Event event) {
    traceStorer.storeThreadInterrupted(traceStorerFactory.repackEvent(event));
  }

  public void processThreadScheduled(Event event) {
    traceStorer.storeThreadScheduled(traceStorerFactory.repackEvent(event));
  }

  public void processThreadBlocked(Event event) {
    traceStorer.storeThreadBlocked(traceStorerFactory.repackEvent(event));
  }

  public void processThreadTerminated(Event event) {
    traceStorer.storeThreadTerminated(traceStorerFactory.repackEvent(event));
  }

  public void processClassLoaded(Event event) {
    traceStorer.storeClassLoaded(traceStorerFactory.repackEvent(event));
  }

  public void processObjectCreated(Event event) {
    traceStorer.storeObjectCreated(traceStorerFactory.repackEvent(event));
  }

  public void processObjectReleased(Event event) {
    traceStorer.storeObjectReleased(traceStorerFactory.repackEvent(event));
  }

  public void processObjectLocked(Event event) {
    traceStorer.storeObjectLocked(traceStorerFactory.repackEvent(event));
  }

  public void processObjectUnlocked(Event event) {
    traceStorer.storeObjectUnlocked(traceStorerFactory.repackEvent(event));
  }

  public void processObjectWait(Event event) {
    traceStorer.storeObjectWait(traceStorerFactory.repackEvent(event));
  }

  public void processObjectNotify(Event event) {
    traceStorer.storeObjectNotify(traceStorerFactory.repackEvent(event));
  }

  public void processObjectNotifyAll(Event event) {
    traceStorer.storeObjectNotifyAll(traceStorerFactory.repackEvent(event));
  }

  public void processGcBegin(Event event) {
    traceStorer.storeGcBegin(traceStorerFactory.repackEvent(event));
  }

  public void processGcEnd(Event event) {
    traceStorer.storeGcEnd(traceStorerFactory.repackEvent(event));
  }

  public void processExceptionThrown(Event event) {
    traceStorer.storeExceptionThrown(traceStorerFactory.repackEvent(event));
  }

  public void processExceptionBailout(Event event) {
    traceStorer.storeExceptionBailout(traceStorerFactory.repackEvent(event));
  }

  public void processExceptionHandled(Event event) {
    traceStorer.storeExceptionHandled(traceStorerFactory.repackEvent(event));
  }

  public void processChoiceGeneratorSet(Event event) {
    traceStorer.storeChoiceGeneratorSet(traceStorerFactory.repackEvent(event));
  }

  public void processChoiceGeneratorRegistered(Event event) {
    traceStorer.storeChoiceGeneratorRegistered(traceStorerFactory
        .repackEvent(event));
  }
  
  public void processChoiceGeneratorAdvanced(Event event) {
    traceStorer.storeChoiceGeneratorAdvanced(traceStorerFactory
        .repackEvent(event));
  }

  public void processChoiceGeneratorProcessed(Event event) {
    traceStorer.storeChoiceGeneratorProcessed(traceStorerFactory
        .repackEvent(event));
  }

  public void processStateAdvanced(Event event) {
    traceStorer.storeStateAdvanced(traceStorerFactory.repackEvent(event));
  }

  public void processStateProcessed(Event event) {
    traceStorer.storeStateProcessed(traceStorerFactory.repackEvent(event));
  }

  public void processStateBacktracked(Event event) {
    traceStorer.storeStateBacktracked(traceStorerFactory.repackEvent(event));
  }

  public void processStateStored(Event event) {
    traceStorer.storeStateStored(traceStorerFactory.repackEvent(event));
  }

  public void processStateRestored(Event event) {
    traceStorer.storeStateRestored(traceStorerFactory.repackEvent(event));
  }

  public void processPropertyViolated(Event event) {
    traceStorer.storePropertyViolated(traceStorerFactory.repackEvent(event));
  }

  public void processSearchStarted(Event event) {
    traceStorer.storeSearchStarted(traceStorerFactory.repackEvent(event));
  }

  public void processSearchConstraintHit(Event event) {
    traceStorer.storeSearchConstraintHit(traceStorerFactory.repackEvent(event));
  }

  public void processSearchFinished(Event event) {
    traceStorer.storeSearchFinished(traceStorerFactory.repackEvent(event));
  }

  public void processStatePurged(Event event) {
    traceStorer.storeStatePurged(traceStorerFactory.repackEvent(event));
  }

  public void processMethodEntered(Event event) {
    traceStorer.storeMethodEntered(traceStorerFactory.repackEvent(event));
  }

  public void processMethodExited(Event event) {
    traceStorer.storeMethodExited(traceStorerFactory.repackEvent(event));
  }

}
