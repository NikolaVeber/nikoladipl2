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
 * A client hook for evaluating whether the traverser should traverse beyond a
 * specific node.
 * 
 * When a
 * {@link Node#traverse(gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Order, StopCondition, TraversalCondition, Object...)
 * Node.traverse()} is called the client parameterizes it with an instance of a
 * StopCondition. The traversal algorithm then invokes the
 * <code>{@link #condition(TraversalPosition)}</code> operation just before
 * returning a specific node, allowing the client to either continue or stop the
 * traversal.
 * 
 * @author Igor Andjelkovic
 * 
 */
public interface StopCondition {

  /**
   * StopCondition that always allows traversing to the end of the graph.
   */
  public static final StopCondition END_OF_GRAPH = new StopCondition() {
    public boolean condition(TraversalPosition currentPosition) {
      return false;
    }
  };

  /**
   * Method invoked by
   * {@link Node#traverse(gov.nasa.jpf.traceServer.traceStorer.inMemory.Node.Order, StopCondition, TraversalCondition, Object...)
   * Node.traverse()} to see if the traversal should continue or stop.
   * 
   * @param currentPosition
   *          Currently processed position
   * @return <code>true</code> if the traversal should stop
   */
  public boolean condition(TraversalPosition currentPosition);
}
