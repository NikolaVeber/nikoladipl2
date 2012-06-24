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

import gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Direction;

import java.util.HashMap;
import java.util.Set;

/**
 * Represents relationship between two nodes in the graph. It can contain
 * properties, just as well as the {@link Node}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Relationship {

  private Node startNode;
  private Node endNode;
  private HashMap<String, Object> properties = new HashMap<String, Object>();
  private RelationshipTypes type;

  /**
   * Creates the relationship of the given type between the start and the end
   * nodes.
   * 
   * @param type
   *          relationship type
   * @param start
   *          start node of the relationship
   * @param end
   *          end node of the relationship
   */
  public Relationship(RelationshipTypes type, Node start, Node end) {
    this.type = type;
    this.startNode = start;
    this.endNode = end;
  }

  /**
   * Deletes this relationship. It removes it from the outgoing relationships of
   * the start node, and the incoming relationships of the end node.
   */
  public void delete() {
    startNode.removeRelationship(this, Direction.OUTGOING);
    endNode.removeRelationship(this, Direction.INCOMING);
  }

  /**
   * Returns the start node of this relationship.
   * 
   * @return the start node of this relationship.
   */
  public Node getStartNode() {
    return startNode;
  }

  /**
   * Returns the end node of this relationship.
   * 
   * @return the end node of this relationship.
   */
  public Node getEndNode() {
    return endNode;
  }

  /**
   * Returns the type of this relationship.
   * 
   * @return the type of this relationship.
   */
  public RelationshipTypes getType() {
    return type;
  }

  /**
   * Associates the specified property value with the specified key. If the
   * relationship previously contained a mapping for the key, the old value is
   * replaced.
   * 
   * @param key
   *          key with which the specified value is to be associated
   * @param value
   *          value to be associated with the specified PropertyID
   */
  public void setProperty(String key, Object value) {
    properties.put(key, value);
  }

  /**
   * Returns the value to which the specified key is mapped, or null if this
   * relationship contains no mapping for the key.
   * 
   * @param key
   *          the key whose associated value is to be returned
   * @return the value to which the specified key is mapped, or null if this
   *         relationship contains no mapping for the key
   * 
   */
  public Object getProperty(String key) {
    return properties.get(key);
  }

  /**
   * Returns a Set view of the keys contained in this relationship.
   * 
   * @return a set view of the keys contained in this relationship
   */
  public Set<String> getKeySet() {
    return properties.keySet();
  }

}
