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

import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * TraceStorer implementation that sends the events over the wire. Implements
 * the Singleton design pattern.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class RemoteTraceStorer extends TraceStorer {

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 4444;

  private Socket socket;
  private ObjectOutputStream oOut;
  private String host;
  private Integer port;

  private static RemoteTraceStorer instance;

  /**
   * Returns the trace storer instance. Singleton method. Default host for the
   * server is used: <i>localhost</i> and the default port is: <i>4444</i>.
   * 
   * @return singleton instance of this trace storer
   */
  public static RemoteTraceStorer getInstance() {
    return getInstance(DEFAULT_HOST, DEFAULT_PORT);
  }

  /**
   * Returns the trace storer instance. Singleton method.
   * 
   * @param host
   *          server address
   * @param port
   *          server port to which trace storer will be connected to
   * @return singleton instance of this trace storer
   */
  public static RemoteTraceStorer getInstance(String host, Integer port) {
    if (instance == null) {
      instance = new RemoteTraceStorer(host, port);
    }
    return instance;
  }

  /**
   * Creates the object with default host and port.
   */
  private RemoteTraceStorer() {
    this.host = DEFAULT_HOST;
    this.port = DEFAULT_PORT;
  }

  /**
   * Creates the object with the given host and port.
   */
  private RemoteTraceStorer(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Connect the client (<code>this</code>) to the server represented by the
   * <code>host</code> and the <code>port</code>.
   */
  public void connect() {
    try {
      socket = new Socket(host, port);
      oOut = new ObjectOutputStream(new BufferedOutputStream(
          socket.getOutputStream()));
    } catch (UnknownHostException e) {
      throw new JPFListenerException("Don't know about host: " + host + ".", e);
    } catch (IOException e) {
      throw new JPFListenerException("Couldn't get I/O for "
          + "the connection to: " + host + ".", e);
    }
  }

  // sends the event's property to the server
  private void sendPropertyData(Event event, eventType eId) {
    LinkedList<PropertyID> keys = event.getPropertyKeys();
    LinkedList<Object> values = event.getPropertyValues(keys);

    // eventType numOfProperties
    try {
      oOut.writeInt(eId.ordinal());
      oOut.writeInt(keys.size());
      // propertyID propertyValue
      for (int i = 0; i < keys.size(); i++) {
        oOut.writeInt(keys.get(i).getID());
        oOut.writeObject(values.get(i));
      }
    } catch (Exception e) {
      throw new JPFListenerException("Sending data failed.", e);
    }
  }

  /**
   * Sends the PropertyID to the server.
   * 
   * @param pId
   *          PropertyID to be send
   */
  public void sendPropertyID(PropertyID pId) {
    try {
      // messageType numOfPropertyIDs propertyID propertyType propertyName
      oOut.writeInt(RemoteUtil.INIT_PROPERTY);
      oOut.writeInt(1);
      oOut.writeInt(pId.getID());
      oOut.writeInt(pId.getTypeID().ordinal());
      oOut.writeUTF(pId.getName());
    } catch (IOException e) {
      throw new JPFListenerException("Sending PropertyID failed.", e);
    }
  }

  private void sendPropertyIDs() {
    PropertyCollection.EVENT_TYPE.getID();
    Collection<PropertyID> props = (Collection<PropertyID>) PropertyID
        .getAllPropertyIDs();
    try {
      // messageType numOfPropertyIDs
      oOut.writeInt(RemoteUtil.INIT_PROPERTY);
      oOut.writeInt(props.size());
      // propertyID propertyType propertyName
      for (PropertyID pId : props) {
        oOut.writeInt(pId.getID());
        oOut.writeInt(pId.getTypeID().ordinal());
        oOut.writeUTF(pId.getName());
      }
      oOut.flush();
    } catch (Exception e) {
      throw new JPFListenerException("Sending PropertyID failed.", e);
    }
  }

  // sends the stop server message to the server
  private void sendStopServer() {
    try {
      oOut.writeInt(RemoteUtil.STOP_SERVER);
      oOut.flush();
      oOut.close();
      socket.close();
    } catch (IOException e) {
      throw new JPFListenerException("Stopping server failed.", e);
    }
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeStateAdvanced(Event event) {
    sendPropertyData(event, eventType.stateAdvanced);
  }

  /**
   * @see #storeStateAdvanced(Event)
   */
  public void storeStateProcessed(Event event) {
    sendPropertyData(event, eventType.stateProcessed);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeStateBacktracked(Event event) {
    sendPropertyData(event, eventType.stateBacktracked);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeStateStored(Event event) {
    sendPropertyData(event, eventType.stateStored);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeStateRestored(Event event) {
    sendPropertyData(event, eventType.stateRestored);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storePropertyViolated(Event event) {
    sendPropertyData(event, eventType.propertyViolated);
  }

  /**
   * Connects the client to the server, sends the PropertyID used, and finally
   * sends the searchStarted event.
   */
  public void storeSearchStarted(Event event) {
    connect();
    sendPropertyIDs();
    sendPropertyData(event, eventType.searchStarted);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeSearchConstraintHit(Event event) {
    sendPropertyData(event, eventType.searchConstraintHit);
  }

  /**
   * Sends the event to the server to be stored over the wire and then sends the
   * stop server command.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeSearchFinished(Event event) {
    sendPropertyData(event, eventType.searchFinished);
    sendStopServer();
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeStatePurged(Event event) {
    sendPropertyData(event, eventType.statePurged);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeInstructionExecuted(Event event) {
    sendPropertyData(event, eventType.instructionExecuted);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeExecuteInstruction(Event event) {
    sendPropertyData(event, eventType.executeInstruction);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectLocked(Event event) {
    sendPropertyData(event, eventType.objectLocked);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectUnlocked(Event event) {
    sendPropertyData(event, eventType.objectUnlocked);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectWait(Event event) {
    sendPropertyData(event, eventType.objectWait);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectNotify(Event event) {
    sendPropertyData(event, eventType.objectNotify);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectNotifyAll(Event event) {
    sendPropertyData(event, eventType.objectNotifyAll);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectCreated(Event event) {
    sendPropertyData(event, eventType.objectCreated);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeObjectReleased(Event event) {
    sendPropertyData(event, eventType.objectReleased);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadStarted(Event event) {
    sendPropertyData(event, eventType.threadStarted);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadBlocked(Event event) {
    sendPropertyData(event, eventType.threadBlocked);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadTerminated(Event event) {
    sendPropertyData(event, eventType.threadTerminated);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadWaiting(Event event) {
    sendPropertyData(event, eventType.threadWaiting);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadNotified(Event event) {
    sendPropertyData(event, eventType.threadNotified);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadInterrupted(Event event) {
    sendPropertyData(event, eventType.threadInterrupted);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeThreadScheduled(Event event) {
    sendPropertyData(event, eventType.threadScheduled);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeClassLoaded(Event event) {
    sendPropertyData(event, eventType.classLoaded);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeExceptionThrown(Event event) {
    sendPropertyData(event, eventType.exceptionThrown);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeExceptionBailout(Event event) {
    sendPropertyData(event, eventType.exceptionBailout);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeExceptionHandled(Event event) {
    sendPropertyData(event, eventType.exceptionHandled);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeChoiceGeneratorSet(Event event) {
    sendPropertyData(event, eventType.choiceGeneratorSet);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeChoiceGeneratorRegistered(Event event) {
    sendPropertyData(event, eventType.choiceGeneratorRegistered);
  }
  
  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeChoiceGeneratorAdvanced(Event event) {
    sendPropertyData(event, eventType.choiceGeneratorAdvanced);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeChoiceGeneratorProcessed(Event event) {
    sendPropertyData(event, eventType.choiceGeneratorProcessed);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeGcBegin(Event event) {
    sendPropertyData(event, eventType.gcBegin);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeGcEnd(Event event) {
    sendPropertyData(event, eventType.gcEnd);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeMethodEntered(Event event) {
    sendPropertyData(event, eventType.methodEntered);
  }

  /**
   * Sends the event to the server to be stored over the wire.
   * 
   * @param event
   *          event to be send to the server
   */
  public void storeMethodExited(Event event) {
    sendPropertyData(event, eventType.methodExited);
  }

}
