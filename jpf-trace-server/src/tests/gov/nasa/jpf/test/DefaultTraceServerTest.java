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
package gov.nasa.jpf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.traceServer.traceQuery.EventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceQuery.neo4j.Neo4jTraceQuery;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.neo4j.DbUtils;
import gov.nasa.jpf.traceServer.traceStorer.neo4j.RelationshipTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * @author Igor Andjelkovic
 * 
 */
public class DefaultTraceServerTest {

  private static int NUM_OF_STATES_ON_LAST_PATH = 9;
  private static int NUM_OF_THREADS_IN_OLDCLASSIC = 3;
  private static Transaction tx;

  private Neo4jTraceQuery defaultTraceQuery;
  private static GraphDatabaseService graphDb;

  @BeforeClass
  public static void runJPFoldclassic() {
    String args[] = { "+target=oldclassic",
        "+search.class=gov.nasa.jpf.search.heuristic.BFSHeuristic",
        "+listener=gov.nasa.jpf.traceEmitter.DefaultFilteringEmitter" };
    Config conf = JPF.createConfig(args);

    JPF jpf = new JPF(conf);
    jpf.run();

    DbUtils.setGraphDbLocation(DbUtils.DB_LOCATION);

    graphDb = DbUtils.getGraphDbInstance();
    tx = graphDb.beginTx();
  }

  @AfterClass
  public static void shutdownDB() {
    tx.failure();
    tx.finish();
    DbUtils.shutdownDb();
    DbUtils.delete(DbUtils.DB_LOCATION);
  }

  /**
   * Test if there is only one root node.
   */
  @Test
  public void testSingleRootNode() {
    try {
      graphDb.getReferenceNode().getSingleRelationship(
          RelationshipTypes.ROOT_STATE, Direction.OUTGOING).getEndNode();
    } catch (Throwable e) {
      fail("More than one root state.");
    }
  }

  /**
   * Test total number of threads in oldclassic example.
   */
  @Test
  public void testThreadIdListSize() {
    defaultTraceQuery = new Neo4jTraceQuery();
    TracePredicate predicate = new TracePredicate() {
      public boolean filter(Event currentEvent) {
        EventTypes.eventType eType = currentEvent.getEventType();
        switch(eType) {
          case threadWaiting: case objectWait:
          case threadBlocked: case threadNotified:
          case objectNotify: case objectNotifyAll:
          case objectLocked: case objectUnlocked:
          case threadTerminated: case threadStarted: { 
            return true;
          }
        }
        return false;
      }
    };
    EventIterator iterator = defaultTraceQuery.getLastPath(predicate, false);
    List<Integer> threadList = defaultTraceQuery.getThreadIdList(iterator);
    assertEquals("More than " + NUM_OF_THREADS_IN_OLDCLASSIC
        + " threads found.", NUM_OF_THREADS_IN_OLDCLASSIC, threadList.size());
  }

  /**
   * Test number of states on last path in oldclassic example.
   */
  @Test
  public void testLastPathSize() {
    defaultTraceQuery = new Neo4jTraceQuery();
    EventIterator lastPath = defaultTraceQuery.getLastPath(TracePredicate.ALL, false);
    Set<Integer> set = new HashSet<Integer>();
    for(@SuppressWarnings("unused") Event e : lastPath) {
      set.add(lastPath.getCurrentEventStateId());
    }
    assertEquals("More than " + NUM_OF_STATES_ON_LAST_PATH + " threads found.",
        NUM_OF_STATES_ON_LAST_PATH, set.size());
  }
}
