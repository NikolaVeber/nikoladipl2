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

import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Factory that creates neo4j {@link Neo4jEvent events} and neo4j
 * {@link Neo4jTraceStorer trace storer}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Neo4jFactory extends TraceStorerFactory {

  private static int numOfEventsCreated = 0;

  private Neo4jFactory() {
  }

  private static class SingletonHolder {
    private static final Neo4jFactory INSTANCE = new Neo4jFactory();
  }

  /**
   * Returns the trace storer instance. Singleton method.
   * 
   * @return singleton instance of this trace storer
   */
  public static TraceStorerFactory getInstance() {
    return SingletonHolder.INSTANCE;
  }

  public Event createEvent() {
    Node node;
    Transaction tx = DbUtils.graphDb.beginTx();
    try {
      node = DbUtils.graphDb.createNode();
      tx.success();
    } finally {
      tx.finish();
    }
    numOfEventsCreated++;
    return new Neo4jEvent(node);
  }

  public static int getNumOfEventsCreated() {
    return numOfEventsCreated;
  }

  public Event createEvent(Object o) {
    return new Neo4jEvent((Node) o);
  }

  public TraceStorer createTraceStorer() {
    return Neo4jTraceStorer.getInstance();
  }

  public TraceStorer createTraceStorer(Object... args) {
    return Neo4jTraceStorer.getInstance((String) args[0]);
  }

}
