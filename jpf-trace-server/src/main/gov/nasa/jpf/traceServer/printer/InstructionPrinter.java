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
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;
import gov.nasa.jpf.util.Left;

import java.io.PrintWriter;

/**
 * Printer for the instruction
 * {@link gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
 * eventGroupType} (instructionExecuted, executeInstruction).
 * 
 * @author Igor Andjelkovic
 * 
 */
public class InstructionPrinter extends EventPrinter {

  /**
   * Show the source line of the printed instruction.
   */
  boolean show_line = true;
  /**
   * Show the source file location of the printed instruction.
   */
  boolean show_location = true;
  /**
   * Show the method of the printed instruction.
   */
  boolean show_method = true;

  public InstructionPrinter(Config conf, String tracePrinterPrefix) {
    super(conf, tracePrinterPrefix);

    show_line = conf.getBoolean(super.tracePrinterPrefix + getName()
        + ".showLine", true);
    show_location = conf.getBoolean(super.tracePrinterPrefix + getName()
        + ".showLocation", true);
    show_method = conf.getBoolean(super.tracePrinterPrefix + getName()
        + ".showMethod", true);
  }

  public void print(PrintWriter out, Event event) {
    out.println(event.getEventType());
    out.print(TAB);
    if (show_location) {
      out.print(Left.format(((String) event
          .getProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION)), 30));
    }
    if (show_line) {
      String line = (String) event
          .getProperty(PropertyCollection.INSTRUCTION_SOURCE_LINE);
      if (line != null && !line.equals("null")) {
        out.print(DELIMITER);
        out.print(line.trim());
      }
    }
    if (show_line || show_location)
      out.println();
    if (show_method) {
      String mi = (String) event
          .getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
      String mci = (String) event
          .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME);
      out.print(TAB + TAB);
      if (mci != null && !mci.equals("null")) {
        out.print(mci);
        out.print(".");
      }
      out.println(mi);
    }
    out.print(TAB + TAB + TAB);
    out.println((String) event
        .getProperty(PropertyCollection.INSTRUCTION_OPCODE));

    super.print(out, event);
  }

  public String getName() {
    return eventGroupType.instruction.toString();
  }
}