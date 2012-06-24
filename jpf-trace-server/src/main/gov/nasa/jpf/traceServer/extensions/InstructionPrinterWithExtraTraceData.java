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
package gov.nasa.jpf.traceServer.extensions;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.printer.EventPrinter;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;

import java.io.PrintWriter;

/**
 * Printer for printing INVOKE instructions and the arguments of the
 * System.out.println(String) method call, stored by using the
 * {@link gov.nasa.jpf.traceEmitter.PropertyCollection#TRACE_EXTRA_DATA
 * TRACE_EXTRA_DATA} property. Used with {@link MethodArgumentsEmitter}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class InstructionPrinterWithExtraTraceData extends EventPrinter {

  public InstructionPrinterWithExtraTraceData(Config conf,
      String tracePrinterPrefix) {
    super(conf, tracePrinterPrefix);
  }

  public void print(PrintWriter out, Event event) {
    if (event.hasProperty(PropertyCollection.TRACE_EXTRA_DATA)) {
      String methodName = (String) event
          .getProperty(ExtraPropertiesCollection.INVOKED_METHOD_COMPLETE_NAME);
      out.println("System.out."
          + methodName.replaceAll("gov.nasa.jpf.ConsoleOutputStream.", ""));
      out.print(TAB + "\"");
      out.println(event.getProperty(PropertyCollection.TRACE_EXTRA_DATA) + "\"");
    }
  }

  public String getName() {
    return eventGroupType.method.toString();
  }
}