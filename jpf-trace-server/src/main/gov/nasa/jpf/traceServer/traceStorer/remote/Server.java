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
package gov.nasa.jpf.traceServer.traceStorer.remote;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.traceAnalyzer.TraceAnalyzer;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;
import gov.nasa.jpf.traceServer.util.TraceServerConfig;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server that receives events from a client over the wire and store them by
 * using the "local" trace storer. Both
 * {@link gov.nasa.jpf.traceServer.traceStorer.inMemory.InMemoryTraceStorer
 * InMemoryTraceStorer} and
 * {@link gov.nasa.jpf.traceServer.traceStorer.neo4j.Neo4jTraceStorer
 * Neo4jTraceStorer} are supported.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class Server {

  private TraceStorer traceStorer;
  private Integer port;
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private ObjectInputStream oIn;
  private TraceStorerFactory eventFactory;

  public Server(TraceStorer traceStorer, TraceStorerFactory eventFactory,
      Integer port) {
    this.traceStorer = traceStorer;
    this.port = port;
    this.eventFactory = eventFactory;
  }

  /**
   * Listens for a connection to be made to this server by the client. The
   * method blocks until a connection is made.
   */
  public void acceptClient() {
    try {
      clientSocket = serverSocket.accept();
      oIn = new ObjectInputStream(new BufferedInputStream(
          clientSocket.getInputStream()));
    } catch (IOException e) {
      System.err.println("Accept failed.");
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates the new server socket.
   * 
   * @return the newly created server
   */
  public ServerSocket createServer() {
    serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      System.err.println("Could not listen on port: " + port + " .");
      System.exit(1);
    }
    return serverSocket;
  }

  /**
   * Closes this server.
   */
  public void closeServer() {
    try {
      oIn.close();
      clientSocket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Starts the communication with the client.
   */
  public void communicate() {
    TraceStorerProtocol tsp = new TraceStorerProtocol(oIn, eventFactory,
        traceStorer);
    try {
      tsp.processInput();
    } catch (IOException e) {
      e.printStackTrace();
      traceStorer.storeSearchFinished(null);
    }
  }

  private ArrayList<TraceAnalyzer> loadTraceAnalyzers(Config config,
      String traceQueryName, String dbLocation) {
    Class<?>[] argTypes = { String.class, String.class };
    Object[] args = { traceQueryName, dbLocation };

    ArrayList<TraceAnalyzer> analyzers = config.getInstances(
        "traceServer.trace_analyzer", TraceAnalyzer.class, argTypes, args);

    String emptyParamArgList[] = new String[] {};
    if (analyzers != null) {
      for (TraceAnalyzer analyzer : analyzers) {
        String analyzerArgs[] = config.getStringArray(
            "traceServer.trace_analyzer." + analyzer.getClass().getName()
                + ".params", emptyParamArgList);
        analyzer.configureAnalyzer((Object[]) analyzerArgs);
      }
    }
    return analyzers;
  }

  public static void main(String[] args) throws IOException {
    Config config = new TraceServerConfig(args);

    Integer port = config.getInt("traceServer.port", 4444);
    String traceStorerName = config.getString("traceServer.trace_storer",
        "neo4j");
    String dbLocation = config.getString("traceServer.db_location", "db");
    String traceQueryName = config
        .getString("traceServer.trace_query", "neo4j");

    TraceStorerFactory traceStorerFactory = TraceStorerFactory
        .getTraceStorerFactory(traceStorerName);

    TraceStorer traceStorer = traceStorerFactory.createTraceStorer(dbLocation);

    Server server = new Server(traceStorer, traceStorerFactory, port);
    server.createServer();
    server.acceptClient();

    server.communicate();

    server.closeServer();

    ArrayList<TraceAnalyzer> analyzers = server.loadTraceAnalyzers(config,
        traceQueryName, dbLocation);
    if (analyzers != null) {
      for (TraceAnalyzer analyzer : analyzers) {
        System.out.println("\n\n" + analyzer.getClass().getName() + "\n");
        analyzer.analyze();
      }
    }
  }

}
