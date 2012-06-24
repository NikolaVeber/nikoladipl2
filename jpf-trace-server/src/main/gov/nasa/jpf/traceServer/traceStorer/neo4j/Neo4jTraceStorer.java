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
package gov.nasa.jpf.traceServer.traceStorer.neo4j;

import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;

import java.util.Collection;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * TraceStorer implementation using Neo4j graph database. Implements the
 * Singleton design pattern.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Neo4jTraceStorer extends TraceStorer {

  // database instance
  private GraphDatabaseService graphDb;
  private Node lastState;
  private Node rootState;
  // Represents current state. If state is visited, this node and all it's
  // events are discarded  
  private Node potentiallyNewState;
  private Node lastEvent;
  private Transaction tx;
  private boolean visitedState = false;
  private int numOfCommits = 1;
  // commit the transaction if this number of events are created
  private int numOfEventsPerCommit = 100000;
  
  private static Neo4jTraceStorer instance;

  /**
   * Returns the trace storer instance. Singleton method.
   * 
   * @param dbLocation
   *          database location in the file system
   * @return singleton instance of this trace storer
   */
  public static Neo4jTraceStorer getInstance(String dbLocation) {
    if (instance == null) {
      instance = new Neo4jTraceStorer(dbLocation);
    }
    return instance;
  }
  
  /**
   * Returns the trace storer instance. Singleton method. Default location for
   * database is used: <i>db/</i>.
   * 
   * @return singleton instance of this trace storer
   */
  public static Neo4jTraceStorer getInstance() {
    return getInstance(null);
  }

  private Neo4jTraceStorer() {
    this(null);
  }

  private Neo4jTraceStorer(String dbLocation) {
    if (dbLocation != null && !dbLocation.isEmpty()) {
      DbUtils.setGraphDbLocation(dbLocation);
    }
    DbUtils.delete(DbUtils.DB_LOCATION);
    graphDb = DbUtils.getGraphDbInstance();
    tx = graphDb.beginTx();
    try {
      rootState = lastState = graphDb.createNode();
      potentiallyNewState = lastEvent = graphDb.createNode();
      graphDb.getReferenceNode().createRelationshipTo(rootState,
          RelationshipTypes.ROOT_STATE);
      tx.success();
    } finally {
      tx.finish();
    }
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
        graphDb.getReferenceNode().createRelationshipTo(potentiallyNewState,
            RelationshipTypes.END_STATE);
      }

      lastState = potentiallyNewState;
      connectLastState();
    } else {
      visitedState = true;
    }
    potentiallyNewState = lastEvent = graphDb.createNode();
    if (Neo4jFactory.getNumOfEventsCreated() / numOfEventsPerCommit > numOfCommits) {
      numOfCommits++;
      tx.success();
      tx.finish();
      tx = graphDb.beginTx();
    }
  }
  
  // create a connection to the last "new" state, important for fetching the
  // last path
  private void connectLastState() {
	  if(graphDb.getReferenceNode().hasRelationship(RelationshipTypes.LAST_STATE,
	      Direction.OUTGOING)) {
	    graphDb.getReferenceNode().getSingleRelationship(RelationshipTypes.LAST_STATE,
	        Direction.OUTGOING).delete();
	  }
	  graphDb.getReferenceNode().createRelationshipTo(lastState,
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
    potentiallyNewState = lastEvent = graphDb.createNode();
  }
  
  public void storeStateRestored(Event event) {
    addPostAdvanceEvent(event, eventGroupType.state);
    
    final int id = (Integer) event.getProperty(PropertyCollection.STATE_ID);
    Traverser trav = rootState.traverse(Order.DEPTH_FIRST, new StopEvaluator() {
      public boolean isStopNode(TraversalPosition currentPos) {
        return !currentPos.isStartNode()
            && currentPos.currentNode().getProperty(
                PropertyCollection.STATE_ID.getName()).equals(id);
      }
    }, new ReturnableEvaluator() {
      public boolean isReturnableNode(TraversalPosition pos) {
        return !pos.isStartNode()
            && pos.lastRelationshipTraversed().isType(
                RelationshipTypes.TRANSITION)
            && pos.currentNode().getProperty(
                PropertyCollection.STATE_ID.getName()).equals(id);
      }
    }, RelationshipTypes.TRANSITION, Direction.OUTGOING);
    Collection<Node> nodes = trav.getAllNodes();
    if (nodes.isEmpty()) {
      throw new JPFListenerException("No such state id: " + id, new Throwable());
    } else {
      lastState = (Node) nodes.iterator().next();
    }
    potentiallyNewState = lastEvent = graphDb.createNode();
    connectLastState();
  }

  /**
   * Starts the database transaction and stores the event.
   */
  public void storeSearchStarted(Event event) {
    tx = graphDb.beginTx();
    addEvent(event, eventGroupType.search);
  }

  /**
   * Finishes the transaction, shutdowns the database and stores the event.
   */
  public void storeSearchFinished(Event event) {
    addPostAdvanceEvent(event, eventGroupType.search);	
    
    tx.success();
    tx.finish();
    DbUtils.shutdownDb();
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

  //utility method to add event to the trace
  private void addEvent(Event event, EventTypes.eventGroupType relType) {
    Node node = ((Neo4jEvent) event).getUnderlyingNode();
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
