//
// Copyright (C) 2011 Igor Andjelkovic (igor.andjelkovic@gmail.com).
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
package gov.nasa.jpf.traceServer.printer.generic;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

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
public class TracePrinter extends gov.nasa.jpf.traceServer.printer.TracePrinter {
  
  public static final int MAX_PRIORITY = Integer.MAX_VALUE;
  
  public TracePrinter(PrintWriter out) {
    super(out);
  }
  
  public TracePrinter(Config conf, PrintWriter out) {
    super(conf, out);
  }

  /**
   * Prints the trace by invoking the {@link #printers event printers}. 
   * Appropriate {@link EventPrinter#print(PrintWriter, Event)} method is called.
   * Prints only the events that are below the given threshold. 
   * 
   * @param trace
   *          Trace that is going to be printed
   * @param threshold
   *          Upper limit for the printing of the event
   */
  public void printTrace(GenericEventIterator trace,
      int threshold) {
    for (eventGroupType type : eventGroupType.values()) {
      printers[type.ordinal()].configure();
    }

    while (trace.hasNext()) {
      Event event = trace.next();
      if (event.getPrintingPriority() <= threshold) {
        eventType eType = event.getEventType();
        eventGroupType type = EventTypes.typeToGroupType(eType);
        if (showEvent[type.ordinal()]) {
          printers[type.ordinal()].print(out, event);
          out.println();
        }
      }
    }
  }

  /**
   * Prints the trace by invoking the {@link #printers event printers}. 
   * Appropriate {@link EventPrinter#printAllProperties(PrintWriter, Event)} method is called.
   * Prints only the events that are below the given threshold. 
   * 
   * @param trace
   *          Trace that is going to be printed
   * @param threshold
   *          Upper limit for the printing of the event
   */
  public void printTraceWithAllProperties(GenericEventIterator trace,
      int threshold) {
    for (eventGroupType type : eventGroupType.values()) {
      printers[type.ordinal()].configure();
    }

    while (trace.hasNext()) {
      Event event = trace.next();
      if (event.getPrintingPriority() <= threshold) {
        eventType eType = event.getEventType();
        eventGroupType type = EventTypes.typeToGroupType(eType);
        if (showEvent[type.ordinal()]) {
          printers[type.ordinal()].printAllProperties(out, event);
          out.println();
        }
      }
    }
  }
  
}
