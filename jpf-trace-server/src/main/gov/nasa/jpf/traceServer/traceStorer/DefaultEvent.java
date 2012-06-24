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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * HashMap implementation of {@link Event}, primarily used for
 * {@link gov.nasa.jpf.traceServer.traceStorer.remote.RemoteTraceStorer
 * RemoteTraceStorer}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class DefaultEvent extends Event {

  private HashMap<String, Object> properties;

  public DefaultEvent() {
    properties = new HashMap<String, Object>();
  }

  public DefaultEvent(DefaultEvent event) {
    this.properties = event.properties;
  }

  public void addProperty(PropertyID key, Object value) {
    properties.put(key.getName(), value);
  }

  public Object getProperty(PropertyID key) {
    return properties.get(key.getName());
  }

  public LinkedList<PropertyID> getPropertyKeys() {
    LinkedList<PropertyID> ids = new LinkedList<PropertyID>();
    Set<String> names = properties.keySet();
    for (String name : names) {
      ids.add(PropertyID.getPropertyIDByName(name));
    }
    return ids;
  }

  public LinkedList<Object> getPropertyValues(Iterable<PropertyID> keys) {
    LinkedList<Object> values = new LinkedList<Object>();
    for (PropertyID key : keys) {
      values.add(properties.get(key.getName()));
    }
    return values;
  }

  public Object removeProperty(PropertyID key) {
    return properties.remove(key.getName());
  }

  public boolean hasProperty(PropertyID key) {
    return properties.containsKey(key.getName());
  }

}
