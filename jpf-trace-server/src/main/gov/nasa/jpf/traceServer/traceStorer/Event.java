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

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

import java.util.LinkedList;

/**
 * Class that represents JPF notification. It can contain various number of
 * properties being Java primitives or java.lang.String or arrays of primitives
 * or java.lang.String. Properties are represented with {@link PropertyID}
 * class.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class Event {

  /**
   * Criteria used when printing the event. If priority is less than the given
   * threshold, the event will be printed.
   */
  protected int printingPriority = Integer.MAX_VALUE;

  /**
   * Associates the specified property value with the specified PropertyID. If
   * the event previously contained a mapping for the PropertyID, the old value
   * is replaced.
   * 
   * @param key
   *          PropertyID with which the specified value is to be associated
   * @param value
   *          value to be associated with the specified PropertyID
   */
  public abstract void addProperty(PropertyID key, Object value);

  /**
   * Returns the value to which the specified key is mapped, or null if this
   * event contains no mapping for the key.
   * 
   * @param key
   *          the PropertyID whose associated value is to be returned
   * @return the value to which the specified PropertyID is mapped, or null if
   *         this event contains no mapping for the PropertyID
   * 
   */
  public abstract Object getProperty(PropertyID key);

  /**
   * Returns true if this event contains a mapping for the specified key.
   * 
   * @param key
   *          the key whose presence in this event is to be tested
   * @return <code>true</code> if this event contains a mapping for the
   *         specified key.
   */
  public abstract boolean hasProperty(PropertyID key);

  /**
   * Removes the mapping for the specified key from this event if present.
   * 
   * @param key
   *          key whose mapping is to be removed from the event
   * @return the previous value associated with key, or null if there was no
   *         mapping for key.
   */
  public abstract Object removeProperty(PropertyID key);

  /**
   * Returns list of PropertyID keys for this event.
   * 
   * @return list of PropertyID keys for this event.
   */
  public abstract LinkedList<PropertyID> getPropertyKeys();

  /**
   * Returns list of property values for the given PropertyID collection for
   * this event.
   * 
   * @return list of property values for the given PropertyID collection for
   *         this event.
   */
  public abstract LinkedList<Object> getPropertyValues(Iterable<PropertyID> keys);

  /**
   * Shortcut method to get event type for each event faster.
   * 
   * @return event's type ({@link EventTypes.eventType}).
   */
  public eventType getEventType() {
    return eventType.values()[(Integer) this
        .getProperty(PropertyCollection.EVENT_TYPE)];
  }

  /**
   * Returns the priority value used when printing the event.
   * 
   * @return priority value
   */
  public int getPrintingPriority() {
    return printingPriority;
  }

  /**
   * Replaces the priority value. The default value is Integer.MAX_VALUE.
   * 
   * @param printingPriority
   *          new priority value
   */
  public void setPrintingPriority(int printingPriority) {
    this.printingPriority = printingPriority;
  }

}
