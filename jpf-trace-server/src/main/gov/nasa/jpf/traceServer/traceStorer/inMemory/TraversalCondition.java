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
 * A client hook for evaluating whether a specific node should be returned from
 * a traverser.
 * 
 * When a
 * {@link Node#traverse(gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Order, StopCondition, TraversalCondition, Object...)
 * Node.traverse()} is called the client parameterizes it with an instance of a
 * TraversalCondition. The traversal algorithm then invokes the
 * <code>{@link #isReturnable(TraversalPosition)}</code> operation just before
 * returning a specific node, allowing the client to either approve or
 * disapprove of returning that node.
 * 
 * @author Igor Andjelkovic
 * 
 */
public interface TraversalCondition {

  /**
   * TraversalCondition that approves the return of each node.
   */
  public static final TraversalCondition ALL = new TraversalCondition() {
    public boolean isReturnable(TraversalPosition currentPosition) {
      return true;
    }
  };

  /**
   * TraversalCondition that approves the return of each node, except for the
   * traversal starting node.
   */
  public static final TraversalCondition ALL_BUT_START_NODE = new TraversalCondition() {
    public boolean isReturnable(TraversalPosition currentPosition) {
      return !currentPosition.isStartNode();
    }
  };

  /**
   * Method invoked by traveser to see if the currently processed node should be
   * returned by traverser.
   * 
   * @param pos
   *          Currently processed node
   * @return <code>True</code> if the currently processed node should be added
   *         to the traversal result.
   */
  public boolean isReturnable(TraversalPosition pos);

}
