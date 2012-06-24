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

import java.util.Collection;
import java.util.HashMap;

/**
 * In-memory graph database. Implements the Singleton Design Pattern.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Graph {

  private HashMap<Integer, Node> states = new HashMap<Integer, Node>();
  private Node referenceNode;

  private Graph() {
    referenceNode = createNode();
  }

  /**
   * Returns a collection of all nodes representing JPF states.
   * 
   * @return a collection of all nodes representing JPF states.
   */
  public Collection<Node> getAllStates() {
    return states.values();
  }

  /**
   * Returns the reference node, which is a "starting point" in the node space.
   * Usually, a client attaches relationships to this node that leads into
   * various parts of the node space.
   * 
   * @return the reference node
   * 
   * @see org.neo4j.graphdb.GraphDatabaseService#getReferenceNode()
   */
  public Node getReferenceNode() {
    return referenceNode;
  }

  /**
   * Creates the new node.
   * 
   * @return the newly created node
   */
  public Node createNode() {
    return new Node();
  }

  /**
   * Associates the specified node that represents JPF state with the specified
   * key in this graph. Utility method that speeds up the graph traversal in
   * search for state nodes.
   * 
   * @param stateId
   *          state's id
   * @param node
   *          node that represents JPF state
   */
  public void addState(Integer stateId, Node node) {
    states.put(stateId, node);
  }

  /**
   * Returns the state node to which the specified key is mapped, or null if
   * this map contains no mapping for the key.
   * 
   * @param stateId
   *          the key whose associated state node is to be returned
   * 
   * @return the state node to which the specified key is mapped, or null if
   *         this map contains no mapping for the key.
   */
  public Node getState(Integer stateId) {
    return states.get(stateId);
  }

  private static class SingletonHolder {
    private static final Graph INSTANCE = new Graph();
  }

  /**
   * Returns the graph instance. Singleton method.
   * 
   * @return singleton instance of this graph
   */
  public static Graph getInstance() {
    return SingletonHolder.INSTANCE;
  }

}