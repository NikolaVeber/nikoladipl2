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

package gov.nasa.jpf.traceServer.scala.printer

import gov.nasa.jpf.Config
import gov.nasa.jpf.traceEmitter.PropertyCollection
import gov.nasa.jpf.traceServer.traceStorer.Event
import gov.nasa.jpf.traceServer.traceStorer.PropertyID

import collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

import java.io.PrintWriter;

abstract class EventPrinter(protected val conf: Config,
  protected val tracePrinterPrefix: String, val name: String) {

  protected var desiredProperties: ArrayBuffer[PropertyID] = ArrayBuffer();
  protected var defaultDesiredProperties: ArrayBuffer[PropertyID] = ArrayBuffer();

  protected val minWidth = 20;
  protected val maxWidth = 20;
  protected val FORMAT = " %1$-" + minWidth + "." + maxWidth + "s";

  require(conf != null)
  val printExtraData = conf.getBoolean(tracePrinterPrefix + name
    + ".printExtraData", true);

  val DELIMITER = " # "
  val TAB = "  "

  def print(out: PrintWriter, event: Event) = {
    out.print(String.format(FORMAT, event.getEventType()));
    out.print(TAB);
    for (propertyID <- desiredProperties)
      out.print(String.format(FORMAT, event.getProperty(propertyID)) + TAB);

    if (printExtraData
      && event.hasProperty(PropertyCollection.TRACE_EXTRA_DATA))
      out.println(TAB + TAB
        + event.getProperty(PropertyCollection.TRACE_EXTRA_DATA));
  }
  
  def printAllProperties(out: PrintWriter, event: Event) {
    val list:Iterator[PropertyID] = event.getPropertyKeys().iterator()
    
    for (propertyID <- list) {
      out.print(String.format(FORMAT, event.getProperty(propertyID)));
      out.print(TAB);
    }
  }

  def configure(): Unit = {}
  
  def addDesiredProperty(property: PropertyID) {
    if (!desiredProperties.contains(property)) {
      desiredProperties = property +: desiredProperties 
    }
  }
  
  def addDesiredProperty(index: Int, property: PropertyID) {
    if (!desiredProperties.contains(property)) {
      desiredProperties.insert(index, property)
    }
  }

  def removeDesiredProperties(property: PropertyID) {
    if (!desiredProperties.isEmpty())
      desiredProperties.remove(property);
  }

  def resetToDefault() {
    desiredProperties.clear();
    desiredProperties.addAll(defaultDesiredProperties);
  }

}