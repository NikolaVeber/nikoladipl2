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
package gov.nasa.jpf.traceServer.traceQuery.inMemory;

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceQuery.EventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceQuery.TraceQuery;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.Graph;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.InMemoryEvent;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.InMemoryFactory;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.Node;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.Relationship;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.RelationshipTypes;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.StopCondition;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.TraversalCondition;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.TraversalPosition;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Direction;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Order;

import java.util.LinkedList;
import java.util.List;

/**
 * Trace query implementation for querying the in-memory graph database.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class InMemoryTraceQuery extends TraceQuery {

  private Graph graph;
  private Node rootState;

  public InMemoryTraceQuery() {
    eventFactory = InMemoryFactory.getInstance();
    graph = Graph.getInstance();
    rootState = graph
        .getReferenceNode()
        .getSingleRelationship(RelationshipTypes.ROOT_STATE, Direction.OUTGOING)
        .getEndNode();
  }

  public List<Integer> getThreadIdList(EventIterator path) {
    List<Integer> threadList = new LinkedList<Integer>();

    for (Event node : path) {
      if (node.hasProperty(PropertyCollection.THREAD_ID)) {
        int threadId = (Integer) node.getProperty(PropertyCollection.THREAD_ID);
        boolean shouldAdd = true;
        for (Integer id : threadList) {
          if (id == threadId) {
            shouldAdd = false;
            break;
          }
        }
        if (shouldAdd)
          threadList.add(threadId);
      }
    }

    return threadList;
  }

  public Event getSuccessorEventOfGroupType(Event fromEvent, eventGroupType type) {
    Node start = ((InMemoryEvent) fromEvent).getUnderlyingNode();

    Relationship rel;
    Node last = start;
    while (last.hasRelationship(RelationshipTypes.EVENT, Direction.OUTGOING)) {
      rel = last.getSingleRelationship(RelationshipTypes.EVENT,
          Direction.OUTGOING);

      if ((Integer) rel.getProperty(PropertyCollection.EVENT_TYPE.getName()) == type
          .ordinal()) {
        return eventFactory.createEvent(rel.getEndNode());
      }
      last = rel.getEndNode();
    }

    return eventFactory.createEvent();
  }

  public Event getPredecessorEventOfGroupType(Event fromEvent,
      eventGroupType type) {
    Node start = ((InMemoryEvent) fromEvent).getUnderlyingNode();

    Relationship rel;
    Node last = start;
    while (last.hasRelationship(RelationshipTypes.EVENT, Direction.INCOMING)) {
      rel = last.getSingleRelationship(RelationshipTypes.EVENT,
          Direction.INCOMING);

      if ((Integer) rel.getProperty(PropertyCollection.EVENT_TYPE.getName()) == type
          .ordinal()) {
        return eventFactory.createEvent(rel.getStartNode());
      }
      last = rel.getStartNode();
    }

    return eventFactory.createEvent();
  }

  protected List<Event> getEndStates() {
    LinkedList<Relationship> rels = graph.getReferenceNode().getRelationships(
        RelationshipTypes.END_STATE, Direction.OUTGOING);

    LinkedList<Event> endStates = new LinkedList<Event>();
    for (Relationship r : rels) {
      endStates.add(eventFactory.createEvent(r.getEndNode()));
    }

    return endStates;
  }

  public List<EventIterator> getAllPaths(TracePredicate predicate,
      boolean reversePath) {
    LinkedList<EventIterator> iterators = new LinkedList<EventIterator>();

    List<Event> endStates = getEndStates();
    for (Event endState : endStates) {
      iterators.add(getPath(predicate,
          ((InMemoryEvent) endState).getUnderlyingNode(), reversePath));
    }
    return iterators;
  }

  public EventIterator getLastPath(TracePredicate predicate, boolean reversePath) {
    Node lastState = graph
        .getReferenceNode()
        .getSingleRelationship(RelationshipTypes.LAST_STATE, Direction.OUTGOING)
        .getEndNode();
    return getPath(predicate, lastState, reversePath);
  }

  protected EventIterator getPath(TracePredicate predicate, Node lastState,
      boolean reversePath) {
    List<Event> lastPath = getStatePathFromLastState(lastState, reversePath);
    List<Event> events = new LinkedList<Event>();
    for (Event state : lastPath) {
      events.add(state);
      events.addAll(getEventsFromState(predicate, state, reversePath));
    }
    return new EventIterator(events);
  }

  protected List<Event> getStatePathFromLastState(Node state,
      boolean reversePath) {
    LinkedList<Event> path = new LinkedList<Event>();
    path.addFirst(eventFactory.createEvent(state));
    Node parent = state.getSingleRelationship(RelationshipTypes.TRANSITION,
        Direction.INCOMING).getStartNode();
    while (!parent.equals(rootState)) {
      if (reversePath) {
        path.addLast(eventFactory.createEvent(parent));
      } else {
        path.addFirst(eventFactory.createEvent(parent));
      }
      parent = parent.getSingleRelationship(RelationshipTypes.TRANSITION,
          Direction.INCOMING).getStartNode();
    }
    return path;
  }

  protected List<Event> getEventsFromState(TracePredicate predicate,
      Event state, boolean reversePath) {
    LinkedList<Event> eventPath = new LinkedList<Event>();
    boolean isEmptyState = false;
    Node node;
    LinkedList<Node> eventTraverser = null;
    TraversalCondition traversalCondition; 
    if (reversePath) {
      if (((InMemoryEvent) state).getUnderlyingNode().hasRelationship(
          RelationshipTypes.LAST_EVENT, Direction.OUTGOING)) {
        traversalCondition = createTraversalCondition(predicate,
            true);
        node = ((InMemoryEvent) state)
            .getUnderlyingNode()
            .getSingleRelationship(RelationshipTypes.LAST_EVENT,
                Direction.OUTGOING).getEndNode();

        eventTraverser = node.traverse(Order.DEPTH_FIRST,
            StopCondition.END_OF_GRAPH, traversalCondition,
            RelationshipTypes.EVENT, Direction.INCOMING);
      } else {
        isEmptyState = true;
      }
    } else {
      node = ((InMemoryEvent) state).getUnderlyingNode();
      traversalCondition = createTraversalCondition(predicate,
          false);
      eventTraverser = node.traverse(Order.DEPTH_FIRST,
          StopCondition.END_OF_GRAPH, traversalCondition,
          RelationshipTypes.EVENT, Direction.OUTGOING);
    }

    if (!isEmptyState) {
      for (Node event : eventTraverser) {
        eventPath.add(eventFactory.createEvent(event));
      }
    }
    return eventPath;
  }

  public EventIterator getEvents(TracePredicate predicate) {
    return getEvents(predicate, SearchOrder.BREADTH_FIRST);
  }

  public EventIterator getEvents(TracePredicate predicate,
      SearchOrder orderOfSearch) {
    LinkedList<Event> eventPath = new LinkedList<Event>();

    if (orderOfSearch == SearchOrder.DEPTH_FIRST) {
      LinkedList<Node> eventTraverser = rootState.traverse(Order.DEPTH_FIRST,
          StopCondition.END_OF_GRAPH, TraversalCondition.ALL_BUT_START_NODE,
          RelationshipTypes.TRANSITION, Direction.OUTGOING);
      for (Node event : eventTraverser) {
        eventPath.add(eventFactory.createEvent(event));
        List<Event> events = getEventsFromState(predicate, eventPath.getLast(),
            false);
        eventPath.addAll(events);
      }
    } else if (orderOfSearch == SearchOrder.BREADTH_FIRST) {
      LinkedList<Node> eventTraverser = rootState.traverse(Order.BREADTH_FIRST,
          StopCondition.END_OF_GRAPH, TraversalCondition.ALL_BUT_START_NODE,
          RelationshipTypes.TRANSITION, Direction.OUTGOING);
      for (Node event : eventTraverser) {
        eventPath.add(eventFactory.createEvent(event));
        List<Event> events = getEventsFromState(predicate, eventPath.getLast(),
            false);
        eventPath.addAll(events);
      }
    }
    return new EventIterator(eventPath);
  }

  protected TraversalCondition createTraversalCondition(
      final TracePredicate predicate, final boolean dontIgnoreFirstNode) {
    TraversalCondition traversalCondition = new TraversalCondition() {
      public boolean isReturnable(TraversalPosition pos) {
        boolean isOk = false;
        if (!pos.isStartNode() || dontIgnoreFirstNode) {
          isOk = predicate
              .filter(eventFactory.createEvent(pos.getCurrentNode()));
        }
        return isOk;
      }
    };
    return traversalCondition;
  }

}
