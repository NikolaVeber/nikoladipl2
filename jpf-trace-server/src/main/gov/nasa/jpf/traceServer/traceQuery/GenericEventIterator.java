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
 * Iteration is performed over all events, including the state events.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class GenericEventIterator extends EventIterator {

  public GenericEventIterator(List<Event> events) {
    super(events);
  }

  public GenericEventIterator(EventIterator iter) {
    super(iter.events);
  }

  public Event getFirst() {
    return events.size() > 0 ? events.get(0) : null;
  }

  public Event getLast() {
    return events.size() > 0 ? events.get(events.size()) : null;
  }

  public void resetIteration() {
    currentPosition = -1;
  }

  public boolean hasNext() {
    return currentPosition + 1 < events.size();
  }

  public Event next() {
    if (currentPosition + 1 >= events.size())
      return null;
    currentEvent = events.get(++currentPosition);
    if (currentEvent.getEventType() == eventType.stateAdvanced) {
      currentState = currentEvent;
    }

    return currentEvent;
  }

  public void remove() {
    int index = currentPosition;
    if (index < 0)
      return;
    currentState = currentEvent = events.remove(index--);

    while (index >= 0 && currentState.getEventType() != eventType.stateAdvanced) {
      currentState = events.get(index--);
    }
  }

  public void goTo(Event event) {
    do {
      if (currentPosition == events.size() - 1)
        return;
      currentEvent = events.get(++currentPosition);
      if (currentEvent.getEventType() == eventType.stateAdvanced) {
        currentState = currentEvent;
      }
    } while (currentEvent != event);
  }

  public Event goToNextStateEvent() {
    if (currentPosition + 1 >= events.size())
      return null;
    Event e = events.get(++currentPosition);

    while (currentPosition < events.size()
        && e.getEventType() != eventType.stateAdvanced) {
      e = events.get(++currentPosition);
    }
    if (e.getEventType() == eventType.stateAdvanced) {
      currentState = currentEvent = e;
    }
    return currentState;
  }

  public Event getCurrentEvent() {
    return currentEvent;
  }

  public int getCurrentEventStateId() {
    return currentState != null ? (Integer) currentState
        .getProperty(PropertyCollection.STATE_ID) : -1;
  }

  public Iterator<Event> iterator() {
    return this;
  }

  public GenericEventIterator clone() {
    GenericEventIterator iter = new GenericEventIterator(events);
    iter.currentEvent = currentEvent;
    iter.currentPosition = currentPosition;
    iter.currentState = currentState;
    return iter;
  }

  /**
   * Returns the number of elements in this list. If this list contains more
   * than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
   * 
   * @return the number of elements in this list
   */
  public int getSize() {
    return events.size();
  }

}
