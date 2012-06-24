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
package gov.nasa.jpf.traceServer.traceStorer.inMemory;

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Direction;

/**
 * TraceStorer implementation using {@link Graph InMemory graph database}.
 * Implements the Singleton design pattern.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class InMemoryTraceStorer extends TraceStorer {

  // databases instance
  private Graph graph;
  private Node lastState;
  private Node rootState;
  // Represents current state. If state is visited, this node and all it's
  // events are discarded
  private Node potentiallyNewState;
  private Node lastEvent;
  private boolean visitedState = false;
  
  private static InMemoryTraceStorer instance;
  
  /**
   * Returns the trace storer instance. Singleton method.
   * 
   * @return singleton instance of this trace storer
   */
  public static InMemoryTraceStorer getInstance() {
    if (instance == null) {
      instance = new InMemoryTraceStorer();
    }
    return instance;
  }

  private InMemoryTraceStorer() {
    graph = Graph.getInstance();
    rootState = lastState = graph.createNode();
    potentiallyNewState = lastEvent = graph.createNode();
    graph.getReferenceNode().createRelationshipTo(rootState,
        RelationshipTypes.ROOT_STATE);
  }

  public void storeStateAdvanced(Event event) {
    boolean isNew = (Boolean) event
        .getProperty(PropertyCollection.STATE_IS_NEW);
    boolean isEnd = (Boolean) event
        .getProperty(PropertyCollection.STATE_IS_END);
    // don't store "visited" states
    if (isNew || isEnd) {
      Iterable<PropertyID> properties = event.getPropertyKeys();
      for (PropertyID key : properties) {
        potentiallyNewState.setProperty(key.getName(), event.getProperty(key));
      }
      lastState.createRelationshipTo(potentiallyNewState,
          RelationshipTypes.TRANSITION);

      // if there are event in the last transition, create connection between
      // state node and the last event in the transition
      if (potentiallyNewState != lastEvent) {
        potentiallyNewState.createRelationshipTo(lastEvent,
            RelationshipTypes.LAST_EVENT);
      }

      // create connection to the "end" state
      if (isEnd) {
        graph.getReferenceNode().createRelationshipTo(potentiallyNewState,
            RelationshipTypes.END_STATE);
      }
      lastState = potentiallyNewState;
      graph.addState((Integer) event.getProperty(PropertyCollection.STATE_ID),
          lastState);
      
      connectLastState();
    } else {
      visitedState = true;
    }
    potentiallyNewState = lastEvent = graph.createNode();
  }

  // create a connection to the last "new" state, important for fetching the
  // last path
	private void connectLastState() {
	  if(graph.getReferenceNode().hasRelationship(RelationshipTypes.LAST_STATE,
	      Direction.OUTGOING)) {
	    graph.getReferenceNode().getSingleRelationship(RelationshipTypes.LAST_STATE,
	        Direction.OUTGOING).delete();
	  }
	  graph.getReferenceNode().createRelationshipTo(lastState,
	      RelationshipTypes.LAST_STATE);
  }

  public void storeStateBacktracked(Event event) {
    addPostAdvanceEvent(event, eventGroupType.state);

    if (visitedState) {
      // since we don't store visited states, we don't have to backtrack, the
      // current state is the desired one
      visitedState = false;
    } else {
      Node node = lastState.getSingleRelationship(RelationshipTypes.TRANSITION,
          Direction.INCOMING).getStartNode();
      lastState = node;
      connectLastState();
    }
    potentiallyNewState = lastEvent = graph.createNode();
  }

  public void storeStateRestored(Event event) {
    addPostAdvanceEvent(event, eventGroupType.state);
    lastState = graph.getState((Integer) event
        .getProperty(PropertyCollection.STATE_ID));
    potentiallyNewState = lastEvent = graph.createNode();
    connectLastState();
  }

  public void storeSearchStarted(Event event) {
    addEvent(event, eventGroupType.search);
  }

  public void storeSearchFinished(Event event) {
    addPostAdvanceEvent(event, EventTypes.eventGroupType.search);
  }
  
  public void storePropertyViolated(Event event) {
    addPostAdvanceEvent(event, EventTypes.eventGroupType.violation);
  }

  // Adds the event to the trace. This event is generated after the state
  // advances, but we want to add it to the end of the event chain of last
  // "new" event.
  private void addPostAdvanceEvent(Event event, eventGroupType relType) {
    addEvent(event, relType);

    Node firstEventAfterAdvance = potentiallyNewState.getSingleRelationship(
        RelationshipTypes.EVENT, Direction.OUTGOING).getEndNode();
    firstEventAfterAdvance.getSingleRelationship(RelationshipTypes.EVENT,
        Direction.INCOMING).delete();
    
    if (lastState.hasRelationship(RelationshipTypes.LAST_EVENT,
        Direction.OUTGOING)) {
      Node previousLastEvent = lastState.getSingleRelationship(
          RelationshipTypes.LAST_EVENT, Direction.OUTGOING).getEndNode();
      Relationship rel = previousLastEvent.createRelationshipTo(
          firstEventAfterAdvance, RelationshipTypes.EVENT);
      rel.setProperty(PropertyCollection.EVENT_TYPE.getName(), relType
          .ordinal());
      lastState.getSingleRelationship(RelationshipTypes.LAST_EVENT,
          Direction.OUTGOING).delete();
    } else {
      Relationship rel = lastState.createRelationshipTo(
          firstEventAfterAdvance, RelationshipTypes.EVENT);
      rel.setProperty(PropertyCollection.EVENT_TYPE.getName(), relType
          .ordinal());
    }
    lastState.createRelationshipTo(lastEvent, RelationshipTypes.LAST_EVENT);
    lastEvent = potentiallyNewState;
  }
	

  // utility method to add event to the trace
  private void addEvent(Event event, EventTypes.eventGroupType relType) {
    Node node = ((InMemoryEvent) event).getUnderlyingNode();
    Relationship rel = lastEvent.createRelationshipTo(node,
        RelationshipTypes.EVENT);
    rel.setProperty(PropertyCollection.EVENT_TYPE.getName(), relType.ordinal());
    lastEvent = node;
  }

  public void storeInstructionExecuted(Event event) {
    addEvent(event, EventTypes.eventGroupType.instruction);
  }

  public void storeExecuteInstruction(Event event) {
    addEvent(event, EventTypes.eventGroupType.instruction);
  }

  public void storeObjectLocked(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeObjectUnlocked(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeObjectWait(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeObjectNotify(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeObjectNotifyAll(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeObjectCreated(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeObjectReleased(Event event) {
    addEvent(event, EventTypes.eventGroupType.object);
  }

  public void storeThreadStarted(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeThreadBlocked(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeThreadTerminated(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeThreadWaiting(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeThreadNotified(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeThreadInterrupted(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeThreadScheduled(Event event) {
    addEvent(event, EventTypes.eventGroupType.thread);
  }

  public void storeClassLoaded(Event event) {
    addEvent(event, EventTypes.eventGroupType.classInfo);
  }

  public void storeExceptionThrown(Event event) {
    addEvent(event, EventTypes.eventGroupType.exception);
  }

  public void storeExceptionBailout(Event event) {
    addEvent(event, EventTypes.eventGroupType.exception);
  }

  public void storeExceptionHandled(Event event) {
    addEvent(event, EventTypes.eventGroupType.exception);
  }

  public void storeChoiceGeneratorSet(Event event) {
    addEvent(event, EventTypes.eventGroupType.cg);
  }

  public void storeChoiceGeneratorRegistered(Event event) {
    addEvent(event, EventTypes.eventGroupType.cg);
  }
  
  public void storeChoiceGeneratorAdvanced(Event event) {
    addEvent(event, EventTypes.eventGroupType.cg);
  }

  public void storeChoiceGeneratorProcessed(Event event) {
    addEvent(event, EventTypes.eventGroupType.cg);
  }

  public void storeGcBegin(Event event) {
    addEvent(event, EventTypes.eventGroupType.gc);
  }

  public void storeGcEnd(Event event) {
    addEvent(event, EventTypes.eventGroupType.gc);
  }

  public void storeMethodEntered(Event event) {
    addEvent(event, EventTypes.eventGroupType.method);
  }

  public void storeMethodExited(Event event) {
    addEvent(event, EventTypes.eventGroupType.method);
  }

  public void storeStateProcessed(Event event) {
    addEvent(event, EventTypes.eventGroupType.state);
  }

  public void storeStateStored(Event event) {
    addEvent(event, EventTypes.eventGroupType.state);
  }

  public void storeSearchConstraintHit(Event event) {
    addEvent(event, EventTypes.eventGroupType.search);
  }

  public void storeStatePurged(Event event) {
    addEvent(event, EventTypes.eventGroupType.state);
  }
}
