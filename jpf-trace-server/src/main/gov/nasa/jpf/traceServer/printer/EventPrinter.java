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

import java.io.PrintWriter;

/**
 * Base class for event printers. Each eventGroupType has it's own default
 * printer.
 * 
 * @see gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class EventPrinter {

  /**
   * Delimits event properties printed in the same line.
   */
  public String DELIMITER = " # ";
  /**
   * Line indentation.
   */
  public String TAB = "  ";

  /**
   * JPF's Config instance.
   */
  protected Config config;
  /**
   * Should
   * {@link gov.nasa.jpf.traceEmitter.PropertyCollection#TRACE_EXTRA_DATA
   * TRACE_EXTRA_DATA} be printed.
   */
  protected boolean printExtraData;
  /**
   * Prefix for the names of the configuration parameters.
   */
  protected String tracePrinterPrefix;

  public EventPrinter(Config conf, String tracePrinterPrefix) {
    config = conf;
    this.tracePrinterPrefix = tracePrinterPrefix;
    printExtraData = conf.getBoolean(tracePrinterPrefix + getName()
        + ".printExtraData", true);
  }

  /**
   * Prints the given event to the provided output stream.
   * 
   * @param out
   *          Output stream to which event is going to be printed
   * @param event
   *          Event that is going to be printed
   */
  public void print(PrintWriter out, Event event) {
    if (printExtraData
        && event.hasProperty(PropertyCollection.TRACE_EXTRA_DATA))
      out.println(TAB + TAB
          + event.getProperty(PropertyCollection.TRACE_EXTRA_DATA));
  }
  
  public void printAllProperties(PrintWriter out, Event event) {
  }

  /**
   * Gets the name of the printer. The name should match corresponding
   * {@link gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
   * eventGroupType}.
   * 
   * @return Printer's name
   */
  public abstract String getName();

  /**
   * Method to configure the printer. The method should be called before the the
   * first call to <code>{@link #print(PrintWriter, Event)}</code>, but can also
   * be called later on, if printer needs to be reconfigured.
   * 
   */
  public void configure() {
  }
}
