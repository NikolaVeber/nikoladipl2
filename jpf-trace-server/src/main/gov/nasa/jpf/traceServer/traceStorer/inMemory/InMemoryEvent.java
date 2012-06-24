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

import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;

import java.util.LinkedList;
import java.util.Set;

/**
 * Event used with {@link InMemoryTraceStorer}. It wraps {@link Node} which is
 * the building block of the <code>inMemory</code> graph database.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class InMemoryEvent extends Event {

  /**
   * Wrap the {@link Node}, the real holder of information.
   * 
   * @param underlyingNode
   *          building part of the graph database
   */
  private Node underlyingNode;

  public InMemoryEvent(Node underlyingNode) {
    this.underlyingNode = underlyingNode;
  }

  /**
   * Returns the graph node wrapped by this event object.
   * 
   * @return wrapped node
   */
  public Node getUnderlyingNode() {
    return underlyingNode;
  }

  public void addProperty(PropertyID key, Object value) {
    underlyingNode.setProperty(key.getName(), value);
  }

  public Object getProperty(PropertyID key) {
    return underlyingNode.getProperty(key.getName());
  }

  public LinkedList<PropertyID> getPropertyKeys() {
    LinkedList<PropertyID> ids = new LinkedList<PropertyID>();
    Set<String> names = underlyingNode.getKeySet();
    for (String name : names) {
      ids.add(PropertyID.getPropertyIDByName(name));
    }
    return ids;
  }

  public LinkedList<Object> getPropertyValues(Iterable<PropertyID> keys) {
    LinkedList<Object> values = new LinkedList<Object>();
    for (PropertyID key : keys) {
      values.add(underlyingNode.getProperty(key.getName()));
    }
    return values;
  }

  public Object removeProperty(PropertyID key) {
    return underlyingNode.removeProperty(key.getName());
  }

  public boolean hasProperty(PropertyID key) {
    return underlyingNode.hasProperty(key.getName());
  }

}
