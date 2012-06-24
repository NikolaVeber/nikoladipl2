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
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.PropertyID;

import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * Base class for event printers. Each eventGroupType has it's own default
 * printer.
 * 
 * @see gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class EventPrinter extends gov.nasa.jpf.traceServer.printer.EventPrinter {

  /**
   * Properties of the event which will be printed by the particular printer.
   */
  protected LinkedList<PropertyID> desiredProperties = new LinkedList<PropertyID>();
  
  /**
   * Backup of the {@link #desiredProperties}.
   */
  protected LinkedList<PropertyID> defaultDesiredProperties = new LinkedList<PropertyID>();

  /**
   * Minimum width of the characters needed to print properties of the event.
   */
  protected int minWidth = 20;
  
  /**
   * Maximum number of characters that will be printed when printing properties of the event.
   */
  protected int maxWidth = 20;

  /**
   * Format used for the printing properties of the event.
   */
  protected String FORMAT = " %1$-" + minWidth + "." + maxWidth + "s";

  public EventPrinter(Config conf, String tracePrinterPrefix) {
    super(conf, tracePrinterPrefix);
  }

  /**
   * Prints the given event to the provided output stream. Only properties from
   * the <code>desiredProperties</code> are printed in the given order.
   * 
   * @param out
   *          Output stream to which event is going to be printed
   * @param event
   *          Event that is going to be printed
   */
  public void print(PrintWriter out, Event event) {
    out.print(String.format(FORMAT, event.getEventType()));
    out.print(TAB);
    for (PropertyID propertyID : desiredProperties) {
      out.print(String.format(FORMAT, event.getProperty(propertyID)));
      out.print(TAB);
    }
    if (printExtraData
        && event.hasProperty(PropertyCollection.TRACE_EXTRA_DATA))
      out.print(TAB + DELIMITER
          + event.getProperty(PropertyCollection.TRACE_EXTRA_DATA));
  }

  /**
   * Prints the given event to the provided output stream. 
   * Every property of the event is printed.
   * 
   * @param out
   *          Output stream to which event is going to be printed
   * @param event
   *          Event that is going to be printed
   */
  public void printAllProperties(PrintWriter out, Event event) {
    for (PropertyID propertyID : event.getPropertyKeys()) {
      out.print(String.format(FORMAT, event.getProperty(propertyID)));
      out.print(TAB);
    }
  }

  public LinkedList<PropertyID> getDesiredProperties() {
    return desiredProperties;
  }

  public void setDesiredProperties(LinkedList<PropertyID> desiredProperties) {
    this.desiredProperties = desiredProperties;
  }

  public void addDesiredProperty(PropertyID property) {
    if (!desiredProperties.contains(property)) {
      desiredProperties.add(property);
    }
  }

  public void addDesiredProperty(int index, PropertyID property) {
    if (!desiredProperties.contains(property)) {
      desiredProperties.add(index, property);
    }
  }

  public void removeDesiredProperties(PropertyID property) {
    if (!desiredProperties.isEmpty())
      desiredProperties.remove(property);
  }

  public void resetToDefault() {
    desiredProperties.clear();
    desiredProperties.addAll(defaultDesiredProperties);
  }
}
