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

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator over the list of events returned by one of the corresponding
 * methods of
 * {@link gov.nasa.jpf.traceServer.traceQuery.TraceQuery TraceQuery}.
 * State events are skipped from the iteration.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class EventIterator implements Iterable<Event>, Iterator<Event>,
    Cloneable {

  protected List<Event> events;
  protected int currentPosition = -1;
  protected Event currentState;
  protected Event currentEvent;

  public EventIterator(List<Event> events) {
    this.events = events;
  }

  /** Copy constructor. */
  public EventIterator(EventIterator that) {
     this.events = that.events;
     this.currentPosition = that.currentPosition;
     this.currentState = that.currentState;
     this.currentEvent = that.currentEvent;
  }

  /**
   * Returns the first element in this list.
   * 
   * @return First event, or null if there is no event in the collection
   */
  public Event getFirst() {
    int index = 0;
    Event toReturn;
    do {
      if (index == events.size())
        return null;
      toReturn = events.get(index++);
    } while (toReturn.getEventType() == eventType.stateAdvanced);
    return currentEvent = toReturn;
  }

  /**
   * Returns the first element in this list.
   * 
   * @return Last event, or null if there is no event in the collection
   */
  public Event getLast() {
    int index = events.size();
    Event toReturn;
    do {
      if (index == 0)
        return null;
      toReturn = events.get(--index);
    } while (toReturn.getEventType() == eventType.stateAdvanced);
    return currentEvent = toReturn;
  }

  /**
   * Resets the iteration. Iteration market is placed before the first element.
   */
  public void resetIteration() {
    currentPosition = -1;
  }

  public boolean hasNext() {
    Event toReturn;
    int index = currentPosition;
    do {
      if (index == events.size() - 1)
        return false;
      toReturn = events.get(++index);
    } while (toReturn.getEventType() == eventType.stateAdvanced);
    return true;
  }

  public Event next() {
    Event toReturn = currentEvent;
    Event previous = null;
    do {
      previous = toReturn;
      if (currentPosition == events.size() - 1)
        return null;
      toReturn = events.get(++currentPosition);
    } while (toReturn.getEventType() == eventType.stateAdvanced);
    if (previous != null && previous.getEventType() == eventType.stateAdvanced) {
      currentState = previous;
    }
    currentEvent = toReturn;
    return toReturn;
  }

  public void remove() {
    Event toRemove;
    int index = currentPosition;
    do {
      if (index < 0)
        return;
      toRemove = events.get(index--);
    } while (toRemove.getEventType() == eventType.stateAdvanced);
    currentPosition = index;
    events.remove(++index);
    if (currentPosition < 0) {
      currentEvent = null;
      currentState = null;
    } else {
      currentEvent = events.get(currentPosition);
      do {
        currentState = events.get(--index);
      } while (currentState.getEventType() != eventType.stateAdvanced);
    }
  }

  /**
   * Moves iteration to the specified <code>event</code>, if the
   * <code>event</code> exists in the collection.
   * 
   * @param event
   *          Element to which the iteration moves.
   */
  public void goTo(Event event) {
    Event e;
    do {
      if (currentPosition == events.size() - 1)
        return;
      e = events.get(++currentPosition);
      if (e.getEventType() != eventType.stateAdvanced) {
        currentEvent = e;
      } else {
        currentState = e;
      }
    } while (e != event);
  }

  /**
   * Returns the current event.
   * 
   * @return Current event
   */
  public Event getCurrentEvent() {
    return currentEvent;
  }

  /**
   * Returns the stateId of the current event.
   * 
   * @return Current event's stateId
   */
  public int getCurrentEventStateId() {
    return currentState != null ? (Integer) currentState
        .getProperty(PropertyCollection.STATE_ID) : -1;
  }

  public Iterator<Event> iterator() {
    return this;
  }

  public EventIterator clone() {
    EventIterator iter = new EventIterator(events);
    iter.currentEvent = currentEvent;
    iter.currentPosition = currentPosition;
    iter.currentState = currentState;
    return iter;
  }

}
