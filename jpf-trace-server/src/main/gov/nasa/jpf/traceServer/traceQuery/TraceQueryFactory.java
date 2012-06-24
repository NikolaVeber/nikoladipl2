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

import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.traceServer.traceQuery.inMemory.InMemoryTraceQuery;
import gov.nasa.jpf.traceServer.traceQuery.neo4j.Neo4jTraceQuery;

import java.util.HashMap;

/**
 * Factory to create a <code>{@link TraceQuery}</code> instances based on the
 * name of the query.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TraceQueryFactory {

  protected static HashMap<String, TraceQuery> map = new HashMap<String, TraceQuery>();

  /**
   * Returns the <code>trace query</code> based on the name of the query and
   * database location. Not all queries use the database name.
   * 
   * @param queryName
   *          Name of the query. Currently supported: "neo4j" and "inMemory".
   * @param dbLocation
   *          Database location, if the desired query uses database from the
   *          file system.
   * 
   * @throws JPFListenerException
   *           If there is no query with the name <code>queryName</code>
   * @return Query created
   * 
   */
  public static TraceQuery getTraceQuery(String queryName, String dbLocation) {
    if (!map.containsKey(queryName)) {
      TraceQuery query = null;
      if (queryName.equals("neo4j")) {
        query = new Neo4jTraceQuery(dbLocation);
      } else if (queryName.equals("inMemory")) {
        query = new InMemoryTraceQuery();
      } else {
        throw new JPFListenerException("Not supported query: " + queryName,
            null);
      }
      map.put(queryName, query);
    }
    return map.get(queryName);
  }

}
