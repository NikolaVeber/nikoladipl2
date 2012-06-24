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

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;
import java.util.LinkedList;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;

/**
 * Event used with {@link Neo4jTraceStorer}. It wraps
 * {@link org.neo4j.graphdb.Node} which is the building block of the neo4j graph
 * database.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Neo4jEvent extends Event {

  private final Node underlyingNode;

  /**
   * Wrap the {@link org.neo4j.graphdb.Node}, the real holder of information.
   * 
   * @param underlyingNode
   *          building part of the graph database
   */
  public Neo4jEvent(Node underlyingNode) {
    this.underlyingNode = underlyingNode;
  }

  /**
   * Returns the graph node wrapped by this event object.
   * 
   * @return wrapped node
   */
  public Node getUnderlyingNode() {
    return this.underlyingNode;
  }

  public void addProperty(PropertyID key, Object value) {
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      underlyingNode.setProperty(key.getName(), (value == null) ? "null"
          : value);
      tx.success();
    } finally {
      tx.finish();
    }
  }

  public Object getProperty(PropertyID key) {
    Object value;
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      value = underlyingNode.getProperty(key.getName());
      tx.success();
    } catch (NotFoundException e) {
      value = null;
    } finally {
      tx.finish();
    }
    return value;
  }

  public LinkedList<PropertyID> getPropertyKeys() {
    Iterable<String> names;
    LinkedList<PropertyID> ids = new LinkedList<PropertyID>();
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      names = underlyingNode.getPropertyKeys();
      tx.success();
    } finally {
      tx.finish();
    }
    for (String name : names) {
      ids.add(PropertyID.getPropertyIDByName(name));
    }
    return ids;
  }

  public LinkedList<Object> getPropertyValues(Iterable<PropertyID> keys) {
    LinkedList<Object> values = new LinkedList<Object>();
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      for (PropertyID key : keys) {
        values.add(underlyingNode.getProperty(key.getName()));
      }
      tx.success();
    } finally {
      tx.finish();
    }
    return values;
  }

  public Object removeProperty(PropertyID key) {
    Object value;
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      value = underlyingNode.removeProperty(key.getName());
      tx.success();
    } finally {
      tx.finish();
    }
    return value;
  }

  public boolean hasProperty(PropertyID key) {
    boolean value;
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      value = underlyingNode.hasProperty(key.getName());
      tx.success();
    } finally {
      tx.finish();
    }
    return value;
  }

  public eventType getEventType() {
    return eventType.values()[(Integer) this
        .getProperty(PropertyCollection.EVENT_TYPE)];
  }
}
