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

/**
 * Current position in the graph traversal, represented by the node and a
 * relationship that led to it.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TraversalPosition {

  private Relationship lastRelationshipTraversed;
  private Node currentNode;
  private boolean isStartNode;

  public TraversalPosition(Relationship lastRelationshipTraversed,
      Node currentNode, boolean isStartNode) {
    this.lastRelationshipTraversed = lastRelationshipTraversed;
    this.currentNode = currentNode;
    this.isStartNode = isStartNode;
  }

  /**
   * Returns the last relationship traversed, relationship that led us to this
   * node.
   * 
   * @return the last relationship traversed
   */
  public Relationship getLastRelationshipTraversed() {
    return lastRelationshipTraversed;
  }

  /**
   * Returns the current node processed.
   * 
   * @return the current node processed.
   */
  public Node getCurrentNode() {
    return currentNode;
  }

  /**
   * Returns <code>true</code> if this is the start node of the traversal,
   * <code>false</code> otherwise.
   * 
   * @return <code>true</code> if this is the start node of the traversal,
   *         <code>false</code> otherwise.
   */
  public boolean isStartNode() {
    return isStartNode;
  }

}
