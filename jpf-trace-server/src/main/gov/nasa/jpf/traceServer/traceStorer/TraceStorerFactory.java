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

import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.traceServer.traceStorer.inMemory.InMemoryFactory;
import gov.nasa.jpf.traceServer.traceStorer.neo4j.Neo4jFactory;
import gov.nasa.jpf.traceServer.traceStorer.remote.RemoteFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for factories that create {@link Event events} and
 * {@link TraceStorer trace storers}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class TraceStorerFactory {

  /**
   * Creates trace event used with trace storer of the type this factory
   * creates.
   * 
   * @return newly created event object
   */
  public abstract Event createEvent();

  /**
   * Creates trace event used with trace storer of the type this factory
   * creates.
   * 
   * @param o
   *          it is up to the factory to now how to treat this argument
   * 
   * @return newly created event object
   */
  public abstract Event createEvent(Object o);

  /**
   * Repacks the event <code>e</code> to a event that this factory creates.
   * 
   * @param e
   *          event that is going to be repacked
   * @return new event created by this factory from the event <code>e</code>
   */
  public Event repackEvent(Event e) {
    Event event = createEvent();
    LinkedList<PropertyID> keys = e.getPropertyKeys();
    for (PropertyID key : keys) {
      event.addProperty(key, e.getProperty(key));
    }
    return event;
  }

  /**
   * Repacks events from the list <code>events</code> to a new list events that
   * this factory creates.
   * 
   * @param events
   *          list of events that are going to be re-packaged
   * @return new list of events created by this factory from <code>events</code>
   */
  public List<Event> repackEvents(List<Event> events) {
    List<Event> newEvents = new LinkedList<Event>();
    for (Event e : events) {
      Event newEvent = createEvent();
      LinkedList<PropertyID> keys = e.getPropertyKeys();
      for (PropertyID key : keys) {
        newEvent.addProperty(key, e.getProperty(key));
      }
      newEvents.add(newEvent);
    }
    return newEvents;
  }

  /**
   * Creates new trace storer.
   * 
   * @return newly created trace storer object
   */
  public abstract TraceStorer createTraceStorer();

  /**
   * Creates new trace storer.
   * 
   * @param args
   *          it is up to the factory to now how to treat this argument
   * 
   * @return newly created trace storer object
   */
  public abstract TraceStorer createTraceStorer(Object... args);

  /**
   * Returns TraceStorerFactory instance based on the given <code>name</code>.
   * 
   * @param name
   *          name of the required TraceStorerFactory. Factory currently
   *          supports three types of TraceStorerFactory: <code>neo4j</code>,
   *          <code>inMemory</code> and <code>remote</code>.
   * @return required TraceStorerFactory or throws
   *         {@link gov.nasa.jpf.JPFListenerException} if the given name not
   *         supported
   * @throws gov.nasa.jpf.JPFListenerException
   *           if the given name is not in the list of supported names
   */
  public static TraceStorerFactory getTraceStorerFactory(String name) {
    if (name.equals("neo4j")) {
      return Neo4jFactory.getInstance();
    } else if (name.equals("inMemory")) {
      return InMemoryFactory.getInstance();
    } else if (name.equals("remote")) {
      return RemoteFactory.getInstance();
    } else {
      throw new JPFListenerException("Not supported trace storer: " + name,
          null);
    }
  }

}
