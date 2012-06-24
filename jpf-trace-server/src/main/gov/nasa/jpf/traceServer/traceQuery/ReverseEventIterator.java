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
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

import java.util.LinkedList;
import java.util.List;

/**
 * Iterator that traverses given list of events backwards.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ReverseEventIterator extends EventIterator {

  public ReverseEventIterator(List<Event> events) {
    super(events);
    revertEvents();
  }

  public ReverseEventIterator(EventIterator iterator) {
    super(iterator.events);
    revertEvents();
  }

  private void revertEvents() {
    LinkedList<Event> revert = new LinkedList<Event>();
    for (Event event : events) {
      if (event.getEventType() == eventType.stateAdvanced) {
        revert.addFirst(event);
      } else {
        revert.add(1, event);
      }
    }

    events = revert;
  }

}
