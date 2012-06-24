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
package gov.nasa.jpf.traceServer.printer;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.traceServer.util.TraceServerConfig;

import java.io.PrintWriter;

/**
 * Printer that can print each event by using {@link EventPrinter} instances.
 * Default printers for each
 * {@link gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
 * eventGroupType}, implemented in the {@link gov.nasa.jpf.traceServer.printer}
 * package, can be replaced with custom event printers by setting the
 * appropriate property when creating the TracePrinter.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TracePrinter {
  /**
   * Instances of {@link EventPrinter}. In order to replace the default
   * eventGroupType printer, printer property should be set. Property names are
   * in the form of: <code>traceServer.tracePrinter.printerName.class</code>
   * where <code>printerName</code> is the event printer's name (
   * {@link EventPrinter#getName()}) and <code>class</code> is the name of the
   * custom event printer class.
   */
  protected EventPrinter printers[] = new EventPrinter[eventGroupType.values().length];

  /**
   * Array of boolean properties to decide what events will be printed. Property
   * names are in the form of:
   * <code>traceServer.tracePrinter.printerName.show</code> where
   * <code>printerName</code> is the event printer's name (
   * {@link EventPrinter#getName()}).
   */
  protected boolean showEvent[] = new boolean[eventGroupType.values().length];

  protected PrintWriter out;
  /**
   * Prefix for the names of the configuration parameters.
   */
  protected String keyPrefix = "traceServer.tracePrinter" + ".";
  
  protected String packageName = getClass().getPackage().getName() + ".";

  /**
   * Constructs TracePrinter with all the default printers turned ON.
   * 
   * @param out
   *          Output stream to which event will be printed.
   */
  public TracePrinter(PrintWriter out) {
    this.out = out;

    String[] confArgs = {};
    Config conf = new TraceServerConfig(confArgs);

    for (eventGroupType type : eventGroupType.values()) {
      showEvent[type.ordinal()] = true;
      String className = Character.toUpperCase(type.toString().charAt(0))
          + type.toString().substring(1) + "Printer";
      conf.setProperty(keyPrefix + type + ".class", packageName + className);

      Class<?>[] argTypes = { Config.class, String.class };
      Object[] args = { conf, keyPrefix };
      printers[type.ordinal()] = conf.getInstance(packageName + className,
          EventPrinter.class, argTypes, args);
    }
  }

  /**
   * Constructs TracePrinter by configuring it with properties provided from the
   * <code>Config</code> instance.
   * 
   * @param conf
   *          JPF's Config instance
   * @param out
   *          Output stream to which event will be printed.
   */
  public TracePrinter(Config conf, PrintWriter out) {
    this.out = out;

    for (eventGroupType type : eventGroupType.values()) {
      showEvent[type.ordinal()] = conf
          .getBoolean(
              keyPrefix + type + ".show",
              (type == eventGroupType.instruction || type == eventGroupType.state) ? true
                  : false);
      if (!conf.hasValue(keyPrefix + type + ".class")) {
        String className = Character.toUpperCase(type.toString().charAt(0))
            + type.toString().substring(1) + "Printer";
        conf.setProperty(keyPrefix + type + ".class", packageName + className);
      }
      Class<?>[] argTypes = { Config.class, String.class };
      Object[] args = { conf, keyPrefix };
      printers[type.ordinal()] = conf.getInstance(keyPrefix + type + ".class",
          EventPrinter.class, argTypes, args);
    }
  }

  /**
   * Prints the trace by invoking the {@link #printers event printers}.
   * 
   * @param trace
   *          Trace that is going to be printed
   */
  public void printTrace(GenericEventIterator trace) {
    for (eventGroupType type : eventGroupType.values()) {
      printers[type.ordinal()].configure();
    }

    Event event = trace.next();
    while (trace.hasNext()) {
      eventType eType = event.getEventType();
      eventGroupType type = EventTypes.typeToGroupType(eType);
      if (showEvent[type.ordinal()]) {
        printers[type.ordinal()].print(out, event);
      }
      event = trace.next();
    }
  }

  public EventPrinter[] getPrinters() {
    return printers;
  }

  public void setPrinters(EventPrinter[] ps) {
    printers = ps;
  }

  public boolean[] getShowEvent() {
    return showEvent;
  }

  public void setShowEvent(boolean[] swEvent) {
    showEvent = swEvent;
  }

  public PrintWriter getOut() {
    return out;
  }

  public void setOut(PrintWriter out) {
    this.out = out;
  }

}
