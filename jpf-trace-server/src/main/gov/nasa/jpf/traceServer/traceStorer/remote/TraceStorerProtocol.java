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

import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID.dataTypeID;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * The protocol by which client and server communicate.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TraceStorerProtocol {

  private TraceStorerFactory traceStorerFactory;
  private TraceStorer traceStorer;
  private ObjectInputStream oIn;

  public TraceStorerProtocol(ObjectInputStream oIn,
      TraceStorerFactory traceStorerFactory, TraceStorer traceStorer) {
    this.oIn = oIn;
    this.traceStorerFactory = traceStorerFactory;
    this.traceStorer = traceStorer;
  }

  /**
   * Process input from the client. The message can be: stop the server,
   * initialize the PropertyID collection or store the event.
   * 
   * @throws IOException
   */
  public void processInput() throws IOException {
    int msgID = oIn.readInt();
    while (true) {
      Event event = null;

      switch (msgID) {
        case RemoteUtil.STOP_SERVER: {
          return;
        }
        case RemoteUtil.INIT_PROPERTY: {
          int numOfPropertyIDs = oIn.readInt();
          for (int i = 0; i < numOfPropertyIDs; i++) {
            int pID = oIn.readInt();
            int tID = oIn.readInt();

            dataTypeID dTypeID = dataTypeID.values()[tID];
            String name = oIn.readUTF();

            PropertyID.createPropertyID(name, pID, dTypeID);
          }
          break;
        }
        default: {
          event = traceStorerFactory.createEvent();
          int numOfProperties = oIn.readInt();
          for (int i = 0; i < numOfProperties; i++) {
            addEventProperty(event);
          }
          storeEvent(event, msgID);
          break;
        }
      }
      msgID = oIn.readInt();
    }
  }

  private void addEventProperty(Event event) throws IOException {
    int pId = oIn.readInt();
    try {
      PropertyID id = PropertyID.getPropertyIDById(pId);
      Object data = oIn.readObject();
      event.addProperty(id, data);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void storeEvent(Event event, int eventType) {
    TraceStorerDispatcher.storeMethods[eventType]
        .storeEvent(traceStorer, event);
  }

}
