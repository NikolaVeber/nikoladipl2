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

import gov.nasa.jpf.traceServer.traceStorer.DefaultEvent;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorer;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;

/**
 * Factory that creates remote
 * {@link gov.nasa.jpf.traceServer.traceStorer.DefaultEvent events} and remote
 * {@link RemoteTraceStorer trace storer}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class RemoteFactory extends TraceStorerFactory {

  private RemoteFactory() {
  }

  private static class SingletonHolder {
    private static final RemoteFactory INSTANCE = new RemoteFactory();
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
    return new DefaultEvent();
  }

  public Event createEvent(Object o) {
    return new DefaultEvent((DefaultEvent) o);
  }

  public TraceStorer createTraceStorer() {
    return RemoteTraceStorer.getInstance();
  }

  public TraceStorer createTraceStorer(Object... args) {
    return RemoteTraceStorer.getInstance((String) args[0], (Integer) args[1]);
  }

}
