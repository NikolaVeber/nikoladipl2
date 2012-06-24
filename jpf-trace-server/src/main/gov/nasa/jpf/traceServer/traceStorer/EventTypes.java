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
package gov.nasa.jpf.traceServer.traceStorer;

/**
 * All JPF notifications represented as enum. It is used to distinguish
 * {@link Event} objects.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class EventTypes {

  /**
   * Types of events are grouped on the basis of common characteristics. For
   * example, we usually store the same property types (@link PropertyID} for
   * all events that have the same prefix. (This also applies to
   * instructionExecuted and executeInstruction)
   */
  public static enum eventGroupType {
    instruction, thread, classInfo, method,
    object, gc, exception, cg, search, state, violation
  };

  /**
   * Types of events. Each type corresponds to one notification method from
   * {@link gov.nasa.jpf.ListenerAdapter}.
   */
  public static enum eventType {
    instructionExecuted, executeInstruction, threadStarted, 
    threadWaiting, threadNotified, threadInterrupted,
    threadScheduled, threadBlocked, threadTerminated,
    classLoaded, objectCreated, objectReleased,
    objectLocked, objectUnlocked, objectWait,
    objectNotify, objectNotifyAll, gcBegin,
    gcEnd, exceptionBailout, exceptionThrown,
    exceptionHandled, choiceGeneratorSet, choiceGeneratorAdvanced,
    choiceGeneratorProcessed, stateAdvanced, stateProcessed,
    stateBacktracked, stateStored, stateRestored,
    propertyViolated, searchStarted, searchConstraintHit,
    searchFinished, statePurged, methodEntered,
    methodExited, choiceGeneratorRegistered
  };

  /**
   * Utility method for mapping {@link eventType} to corresponding
   * {@link eventGroupType}.
   * 
   * @param t
   *          <code>evetType</code> to be mapped
   * @return mapped <code>eventGroupType</code>
   */
  public static eventGroupType typeToGroupType(eventType t) {
    eventGroupType type = eventGroupType.instruction;
    switch (t) {
      case instructionExecuted:
      case executeInstruction: {
        return eventGroupType.instruction;
      }
      case threadStarted:
      case threadWaiting:
      case threadNotified:
      case threadInterrupted:
      case threadScheduled:
      case threadBlocked:
      case threadTerminated: {
        return eventGroupType.thread;
      }
      case classLoaded: {
        return eventGroupType.classInfo;
      }
      case objectCreated:
      case objectReleased:
      case objectLocked:
      case objectUnlocked:
      case objectWait:
      case objectNotify:
      case objectNotifyAll: {
        return eventGroupType.object;
      }
      case gcBegin:
      case gcEnd: {
        return eventGroupType.gc;
      }
      case exceptionBailout:
      case exceptionThrown:
      case exceptionHandled: {
        return eventGroupType.exception;
      }
      case choiceGeneratorSet:
      case choiceGeneratorAdvanced:
      case choiceGeneratorProcessed:
      case choiceGeneratorRegistered: {
        return eventGroupType.cg;
      }
      case stateAdvanced:
      case stateProcessed:
      case stateBacktracked:
      case stateStored:
      case stateRestored: 
      case statePurged: {
        return eventGroupType.state;
      }
      case propertyViolated: {
        return eventGroupType.violation;
      }
      case searchStarted:
      case searchConstraintHit:
      case searchFinished: {
        return eventGroupType.search;
      }
      case methodEntered:
      case methodExited: {
        return eventGroupType.method;
      }
    }

    return type;
  }

}
