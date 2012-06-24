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

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

/**
 * Utility class for accessing neo4j graph instance.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class DbUtils {

  /**
   * Default location of graph database in the file system.
   */
  public static String DB_LOCATION = "db";
  /**
   * The graph instance.
   */
  public static GraphDatabaseService graphDb;

  /**
   * Returns the graph instance. There can only be one instance of the graph.
   * 
   * @return graph instance
   */
  public static GraphDatabaseService getGraphDbInstance() {
    if (graphDb == null) {
      graphDb = new EmbeddedGraphDatabase(DB_LOCATION);
    }
    return graphDb;
  }

  /**
   * Sets the new graph location and deletes the graph instance. First time the
   * graph instance is asked, it will be created with the new location. If the
   * new location is the same as the one already used, nothing is changed.
   * 
   * @param dbLocation
   *          new database location
   */
  public static void setGraphDbLocation(String dbLocation) {
    if (!DB_LOCATION.equals(dbLocation)) {
      DB_LOCATION = dbLocation;
      graphDb = null;
    }
  }

  /**
   * Shutdowns the database.
   */
  public static void shutdownDb() {
    graphDb.shutdown();
  }

  /**
   * Deletes the file or the folder represented by <code>path</code>
   * 
   * @param path
   *          name of the file or folder that will be deleted
   */
  public static void delete(String path) {
    delete(new File(path));
  }

  private static void delete(File file) {
    if (!file.exists()) {
      System.out.println("DB " + file.getPath() + " does not exist");
      return;
    }

    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        delete(child);
      }
    }
    file.delete();
  }
}
