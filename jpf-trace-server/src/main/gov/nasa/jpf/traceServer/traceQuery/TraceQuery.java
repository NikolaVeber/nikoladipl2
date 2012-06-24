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
package gov.nasa.jpf.traceServer.traceQuery;

import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;

import java.util.List;

/**
 * Interface for querying the trace database.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class TraceQuery {

  public static enum SearchOrder {
    DEPTH_FIRST, BREADTH_FIRST
  };

  protected TraceStorerFactory eventFactory;

  /**
   * Should be called before the first query method is called to allow query to
   * configure itself.
   */
  public void startTraceQuery() {
  }

  /**
   * Should be called after the last query method is called to allow query to
   * clean up behind.
   */
  public void stopTraceQuery() {
  }

  /**
   * Returns list of unique thread IDs.
   * 
   * @param path
   *          List of events to be searched
   * @return List of unique thread IDs
   */
  public abstract List<Integer> getThreadIdList(EventIterator path);

  /**
   * Returns successor events of the <code>state</code> event, i.e. events from
   * the state represented by the <code>state</code> event.
   * 
   * @param predicate
   *          Evaluates if event should be returned
   * @param state
   *          State to be searched for events
   * @param reversePath
   *          If <code>true</code> events are returned in reversed order
   * @return List of events that satisfy <code>predicate</code> argument
   * 
   * @see gov.nasa.jpf.traceServer.traceStorer.TraceStorer#storeStateAdvanced(Event)
   *      TraceStorer.storeStateAdvanced(Event)
   */
  protected abstract List<Event> getEventsFromState(TracePredicate predicate,
      Event state, boolean reversePath);

  /**
   * Returns the first successor of
   * <code>{@link gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType groupType}</code>
   * to <code>fromEvent</code> or empty event if there is no successor of
   * <code>type</code>..
   * 
   * @param fromEvent
   *          Starting point for search
   * @param type
   *          Event type we are looking for
   * @return Closest successor event of type <code>type</code>, to
   *         <code>fromEvent</code> or empty event if there is no successor of
   *         <code>type</code>.
   */
  public abstract Event getSuccessorEventOfGroupType(Event fromEvent,
      eventGroupType type);

  /**
   * Returns the first predecessor of
   * <code>{@link gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType groupType}</code>
   * to <code>fromEvent</code> or empty event if there is no predecessor of
   * <code>type</code>.
   * 
   * @param fromEvent
   *          Starting point for search
   * @param type
   *          Event type we are looking for
   * @return Closest predecessor event of type <code>type</code>, to
   *         <code>fromEvent</code> or empty event if there is no predecessor of
   *         <code>type</code>.
   */
  public abstract Event getPredecessorEventOfGroupType(Event fromEvent,
      eventGroupType type);

  /**
   * Returns all states marked as "end" by JPF.
   * 
   * @return All end states
   */
  protected abstract List<Event> getEndStates();

  /**
   * Returns the last execution path. It does not matter whether the search is
   * successfully finished or is terminated for any reason.
   * 
   * @param predicate
   *          Evaluates if event should be returned
   * @param reversePath
   *          If <code>true</code> events are returned in reversed order
   * @return Last execution path
   */
  public abstract EventIterator getLastPath(TracePredicate predicate,
      boolean reversePath);

  /**
   * Returns all execution paths. The path is recognized by it's end state.
   * 
   * @param predicate
   *          Evaluates if event should be returned
   * @param reversePath
   *          If <code>true</code> events are returned in reversed order
   * @return List of all execution paths
   */
  public abstract List<EventIterator> getAllPaths(TracePredicate predicate,
      boolean reversePath);

  /**
   * Returns all events that satisfy <code>predicate</code>. Whole trace
   * database is searched by using SearchOrder.BREADTH_FIRST as the default
   * traversing order.
   * 
   * @param predicate
   *          Evaluates if event should be returned
   * @return All events that satisfy <code>predicate</code>.
   */
  public abstract EventIterator getEvents(TracePredicate predicate);

  /**
   * Returns all events that satisfy <code>predicate</code>. Whole trace
   * database is searched by using the provided <code>orderOfSearch</code> as
   * traversing order.
   * 
   * @param predicate
   *          Evaluates if event should be returned
   * @return All events that satisfy <code>predicate</code>.
   */
  public abstract EventIterator getEvents(TracePredicate predicate,
      SearchOrder orderOfSearch);
}