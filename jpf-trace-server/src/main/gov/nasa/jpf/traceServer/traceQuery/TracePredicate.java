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
package gov.nasa.jpf.traceServer.traceQuery;

import gov.nasa.jpf.traceServer.traceStorer.Event;

/**
 * A client hook for evaluating whether a specific event should be returned from
 * a trace query.
 * 
 * When a trace query method is called the client parameterizes it with an
 * instance of a TracePredicate. The trace query then invokes the
 * <code>{@link #filter(Event)}</code> operation just before returning a
 * specific event, allowing the client to either approve or disapprove of
 * returning that event.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class TracePredicate {

  /**
   * TracePredicate that approves the return of each event.
   */
  public static final TracePredicate ALL = new TracePredicate() {
    public boolean filter(Event currentEvent) {
      return true;
    }
  };

  /**
   * Method invoked by trace query to see if the currently processed event
   * should be added to the query result.
   * 
   * @param currentEvent
   *          Currently processed event
   * @return <code>True</code> if currently processed should event be added to
   *         the query result.
   */
  public abstract boolean filter(Event currentEvent);

}
