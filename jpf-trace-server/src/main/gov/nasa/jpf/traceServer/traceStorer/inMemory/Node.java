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

import gov.nasa.jpf.JPFListenerException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Building block of the in-memory {@link Graph graph}. <br/>
 * <br/>
 * It can contain properties. Valid property value types are all the Java
 * primitives ( <code>boolean, byte,
 * short, int, long, float, double</code> and <code>char</code>) and
 * <code>java.lang.String</code>s.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Node {

  /**
   * Defines relationship directions used when getting relationships from a
   * node.
   */
  public enum Direction {
    /**
     * Defines incoming relationship.
     */
    INCOMING,
    /**
     * Defines outgoing relationship.
     */
    OUTGOING
  };

  /**
   * Defines a traversal order for traversing the graph or a subgraph from the
   * specified node.
   */
  public enum Order {
    DEPTH_FIRST, BREADTH_FIRST
  };

  private LinkedList<Relationship> incomingRelationships = new LinkedList<Relationship>();
  private LinkedList<Relationship> outgoingRelationships = new LinkedList<Relationship>();

  private HashMap<String, Object> properties = new HashMap<String, Object>();

  public Node() {
  }

  /**
   * Creates a relationship between this node and another node.
   * 
   * @param endNode
   *          the end node of the new relationship
   * @param relType
   *          the type of the relationship
   * @return the newly created relationship
   */
  public Relationship createRelationshipTo(Node endNode,
      RelationshipTypes relType) {
    return addRelationship(endNode, Direction.OUTGOING, relType);
  }

  private Relationship addRelationship(Node node, Direction dir,
      RelationshipTypes relType) {
    Relationship rel = null;
    if (dir == Direction.INCOMING) {
      rel = new Relationship(relType, node, this);
      incomingRelationships.add(rel);
      node.outgoingRelationships.add(rel);
    } else if (dir == Direction.OUTGOING) {
      rel = new Relationship(relType, this, node);
      outgoingRelationships.add(rel);
      node.incomingRelationships.add(rel);
    }
    return rel;
  }

  /**
   * Returns single relationship of <code>type</code> or <code>null</code> if
   * there is none or more then one relationship of that type..
   * 
   * @return single relationship or <code>null</code> if there is none or more
   *         then one relationship
   */
  public Relationship getSingleRelationship(RelationshipTypes type,
      Direction dir) {
    Relationship rel = null;
    int numOfRels = 0;
    LinkedList<Relationship> relationships = incomingRelationships;
    if (dir == Direction.INCOMING) {
      relationships = incomingRelationships;
    } else if (dir == Direction.OUTGOING) {
      relationships = outgoingRelationships;
    }
    for (Relationship r : relationships) {
      if (r.getType() == type) {
        rel = r;
        numOfRels++;
      }
    }
    if (numOfRels != 1) {
      throw new JPFListenerException("" + numOfRels
          + " relationships found, expected one.", null);
    }
    return rel;
  }

  /**
   * Returns the list of the incoming relationships to this node.
   * 
   * @return list of incoming relationships
   */
  public LinkedList<Relationship> getIncomingRelationships() {
    return incomingRelationships;
  }

  /**
   * Returns the list of the outgoing relationships from this node.
   * 
   * @return list of outgoing relationships
   */
  public LinkedList<Relationship> getOutgoingRelationships() {
    return outgoingRelationships;
  }

  /**
   * Disconnects this node from the node at the other part of the relationship
   * <code>rel</code>.
   * 
   * @param rel
   *          relationship that will be removed
   * @param dir
   *          the direction of the relationship
   * @return true if this node has the specified relationship
   */
  public boolean removeRelationship(Relationship rel, Direction dir) {
    LinkedList<Relationship> relationships = incomingRelationships;
    if (dir == Direction.INCOMING) {
      relationships = incomingRelationships;
    } else if (dir == Direction.OUTGOING) {
      relationships = outgoingRelationships;
    }
    return relationships.remove(rel);
  }

  /**
   * Returns all the relationships of this node, both incoming and outgoing.
   * 
   * @return all the relationships of this node.
   */
  public LinkedList<Relationship> getRelationships() {
    LinkedList<Relationship> all = new LinkedList<Relationship>();
    all.addAll(incomingRelationships);
    all.addAll(outgoingRelationships);
    return all;
  }

  /**
   * Returns <code>true</code> if there are any relationships of the given
   * relationship type and direction attached to this node, <code>false</code>
   * otherwise.
   * 
   * @param type
   *          the given type
   * @param dir
   *          the given direction, where {@link Node.Direction#OUTGOING
   *          Direction.OUTGOING} means all relationships that have this node as
   *          start node and {@link Node.Direction#INCOMING Direction.INCOMING}
   *          means all relationships that have this node as end node
   * @return <code>true</code> if there are any relationships of the given
   *         relationship type and direction attached to this node,
   *         <code>false</code> otherwise
   */
  public boolean hasRelationship(RelationshipTypes type, Direction dir) {
    if (dir == Direction.INCOMING) {
      for (Relationship rel : incomingRelationships) {
        if (rel.getType() == type)
          return true;
      }
    } else if (dir == Direction.OUTGOING) {
      for (Relationship rel : outgoingRelationships) {
        if (rel.getType() == type)
          return true;
      }
    }
    return false;
  }

  /**
   * Returns all relationships with the given type and direction that are
   * attached to this node. If there are no matching relationships, an empty
   * list will be returned.
   * 
   * @param type
   *          the given type
   * @param dir
   *          the given direction, where {@link Node.Direction#OUTGOING
   *          Direction.OUTGOING} means all relationships that have this node as
   *          start node and {@link Node.Direction#INCOMING Direction.INCOMING}
   *          means all relationships that have this node as end node
   * @return all relationships attached to this node that match the given type
   *         and direction
   */
  public LinkedList<Relationship> getRelationships(RelationshipTypes type,
      Direction dir) {
    LinkedList<Relationship> rels = new LinkedList<Relationship>();
    if (dir == Direction.INCOMING) {
      for (Relationship rel : incomingRelationships) {
        if (rel.getType() == type)
          rels.add(rel);
      }
    } else if (dir == Direction.OUTGOING) {
      for (Relationship rel : outgoingRelationships) {
        if (rel.getType() == type)
          rels.add(rel);
      }
    }
    return rels;
  }

  /**
   * Sets the property value for the given key to value. Valid property value
   * types are all the Java primitives ( <code>boolean, byte,
   * short, int, long, float, double</code> and <code>char</code>) and
   * <code>java.lang.String</code>s.
   * 
   * @param key
   *          the key with which the new property value will be associated
   * @param value
   *          the new property value, of one of the valid property types
   */
  public void setProperty(String key, Object value) {
    properties.put(key, value);
  }

  /**
   * Returns the property value associated with the given key, or a null if
   * there is no mapping for the key.
   * 
   * @param key
   *          the key whose associated value is to be returned
   * @return the property value associated with the given key, or a null if
   *         there is no mapping for the key.
   */
  public Object getProperty(String key) {
    return properties.get(key);
  }

  /**
   * Returns true if this node contains a mapping for the specified key.
   * 
   * @param key
   *          The key whose presence in this map is to be tested
   * @return true if this node contains a mapping for the specified key.
   */
  public boolean hasProperty(String key) {
    return properties.containsKey(key);
  }

  /**
   * Removes the mapping for the specified key from this node if present.
   * 
   * @param key
   *          Removes the mapping for the specified key from this map if
   *          present.
   * @return the previous value associated with key, or null if there was no
   *         mapping for key
   */
  public Object removeProperty(String key) {
    return properties.remove(key);
  }

  /**
   * Returns a Set view of the keys contained in this node.
   * 
   * @return a set view of the keys contained in this node
   */
  public Set<String> getKeySet() {
    return properties.keySet();
  }

  /**
   * Traverses the graph starting from this node.
   * 
   * @param order
   *          {@link Node.Order order} of traversal
   * @param stopCondition
   *          tells the traversal algorithm when to stop
   * @param traversalCondition
   *          tells the traversal if it should return the node
   * @param relationshipTypesAndDirections
   *          a variable-length list of relationship types and their directions,
   *          where the first argument is a relationship type, the second
   *          argument the first type's direction, the third a relationship
   *          type, the fourth its direction, etc...
   * @return the list of nodes that satisfies all the traversal condition
   */
  public LinkedList<Node> traverse(Order order, StopCondition stopCondition,
      TraversalCondition traversalCondition,
      Object... relationshipTypesAndDirections) {
    if (relationshipTypesAndDirections.length % 2 == 1) {
      throw new JPFListenerException("Wrong number of types and directions",
          null);
    }
    int length = relationshipTypesAndDirections.length / 2;
    RelationshipTypes types[] = new RelationshipTypes[length];
    Direction dirs[] = new Direction[length];
    for (int i = 0; i < length; i++) {
      types[i] = (RelationshipTypes) relationshipTypesAndDirections[i * 2];
      dirs[i] = (Direction) relationshipTypesAndDirections[i * 2 + 1];
    }

    Traverser traverser;
    if (order == Order.BREADTH_FIRST) {
      traverser = new BreadthFirstTraverser(this, types, dirs, stopCondition,
          traversalCondition);
    } else if (order == Order.DEPTH_FIRST) {
      traverser = new DepthFirstTraverser(this, types, dirs, stopCondition,
          traversalCondition);
    } else {
      throw new JPFListenerException("Unknown traversal order, " + order, null);
    }

    return traverser.traverse();
  }

  private abstract class Traverser {
    protected LinkedList<Relationship> relationshipQueue = new LinkedList<Relationship>();
    protected LinkedList<Node> nodeQueue = new LinkedList<Node>();
    protected Node startNode;
    protected TraversalPosition currPosition;
    protected RelationshipTypes types[];
    protected Direction dirs[];
    protected TraversalCondition traversalCondition;
    protected StopCondition stopCondition;

    public Traverser(Node startNode, RelationshipTypes[] types,
        Direction[] dirs, StopCondition stopCondition,
        TraversalCondition traversalCondition) {
      this.startNode = startNode;
      this.types = types;
      this.dirs = dirs;
      this.traversalCondition = traversalCondition;
      this.stopCondition = stopCondition;
      nodeQueue.addFirst(startNode);
    }

    public LinkedList<Node> traverse() {
      LinkedList<Node> result = new LinkedList<Node>();
      Node next = nodeQueue.poll();
      while (next != null) {
        currPosition = new TraversalPosition(relationshipQueue.poll(), next,
            (next == startNode));
        expandNode(currPosition.getCurrentNode());
        boolean stopOk = stopCondition.condition(currPosition);
        if (stopOk) {
          break;
        }
        boolean traverseOk = traversalCondition.isReturnable(currPosition);
        if (traverseOk) {
          result.add(next);
        }
        next = nodeQueue.poll();
      }
      return result;
    }

    protected abstract void expandNode(Node node);
  }

  private class BreadthFirstTraverser extends Traverser {

    public BreadthFirstTraverser(Node startNode, RelationshipTypes[] types,
        Direction[] dirs, StopCondition stopCondition,
        TraversalCondition condition) {
      super(startNode, types, dirs, stopCondition, condition);
    }

    protected void expandNode(Node node) {
      for (int i = 0; i < types.length; i++) {
        LinkedList<Relationship> rels = node
            .getRelationships(types[i], dirs[i]);
        if (rels.size() != 0)
          for (Relationship r : rels) {
            relationshipQueue.add(r);
            if (dirs[i] == Direction.INCOMING) {
              nodeQueue.add(r.getStartNode());
            } else if (dirs[i] == Direction.OUTGOING) {
              nodeQueue.add(r.getEndNode());
            }
          }
      }
    }

  }

  private class DepthFirstTraverser extends Traverser {

    public DepthFirstTraverser(Node startNode, RelationshipTypes[] types,
        Direction[] dirs, StopCondition stopCondition,
        TraversalCondition condition) {
      super(startNode, types, dirs, stopCondition, condition);
    }

    protected void expandNode(Node node) {
      LinkedList<Relationship> rQueue = new LinkedList<Relationship>();
      LinkedList<Node> nQueue = new LinkedList<Node>();
      for (int i = 0; i < types.length; i++) {
        LinkedList<Relationship> rels = node
            .getRelationships(types[i], dirs[i]);
        if (rels.size() != 0)
          for (Relationship r : rels) {
            rQueue.add(r);
            if (dirs[i] == Direction.INCOMING) {
              nQueue.add(r.getStartNode());
            } else if (dirs[i] == Direction.OUTGOING) {
              nQueue.add(r.getEndNode());
            }
          }
      }
      nodeQueue.addAll(0, nQueue);
      relationshipQueue.addAll(0, rQueue);
    }

  }
}
