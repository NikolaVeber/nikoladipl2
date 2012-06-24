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

import gov.nasa.jpf.traceServer.traceStorer.Event
import gov.nasa.jpf.traceServer.traceStorer.EventTypes
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator
import gov.nasa.jpf.traceServer.util.TraceServerConfig
import java.io.PrintWriter
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
import gov.nasa.jpf.Config

import java.lang.Class

class TracePrinter(val out: PrintWriter) {

  protected var printers = new Array[EventPrinter](eventGroupType.values().length)

  protected val keyPrefix = "traceServer.tracePrinter" + "."

  protected val packageName = getClass().getPackage().getName() + ".";
  
  val conf = new TraceServerConfig(new Array[String](0));
/*  
  for (t <- eventGroupType.values()) {
    val className = Character.toUpperCase(t.toString().charAt(0)) + t.toString().substring(1) + "Printer";
    conf.setProperty(keyPrefix + t + ".class", packageName + className);

    val argTypes = Array(classOf[Config], classOf[java.lang.String], classOf[java.lang.String]);
    val ar = Array(conf, keyPrefix, t.toString())
    printers(t.ordinal()) = conf.getInstance(packageName + className,
      classOf[EventPrinter], argTypes, ar);
  }
*/  
  
  printers(eventGroupType.cg.ordinal()) = new CgPrinter(conf, keyPrefix, eventGroupType.cg.toString())
  printers(eventGroupType.classInfo.ordinal()) = new ClassInfoPrinter(conf, keyPrefix, eventGroupType.classInfo.toString())
  printers(eventGroupType.exception.ordinal()) = new ExceptionPrinter(conf, keyPrefix, eventGroupType.exception.toString())
  printers(eventGroupType.gc.ordinal()) = new GcPrinter(conf, keyPrefix, eventGroupType.gc.toString())
  printers(eventGroupType.instruction.ordinal()) = new InstructionPrinter(conf, keyPrefix, eventGroupType.instruction.toString())
  printers(eventGroupType.method.ordinal()) = new MethodPrinter(conf, keyPrefix, eventGroupType.method.toString())
  printers(eventGroupType.`object`.ordinal()) = new ObjectPrinter(conf, keyPrefix, eventGroupType.`object`.toString())
  printers(eventGroupType.search.ordinal()) = new SearchPrinter(conf, keyPrefix, eventGroupType.search.toString())
  printers(eventGroupType.state.ordinal()) = new StatePrinter(conf, keyPrefix, eventGroupType.state.toString())
  printers(eventGroupType.thread.ordinal()) = new ThreadPrinter(conf, keyPrefix, eventGroupType.thread.toString())
  printers(eventGroupType.violation.ordinal()) = new ViolationPrinter(conf, keyPrefix, eventGroupType.violation.toString())


  def print(trace: GenericEventIterator, threshold: Int, all: Boolean) = {

    val justPrint = (ePrinter: EventPrinter, e: Event) => ePrinter.print(out, e)
    val printAll = (ePrinter: EventPrinter, e: Event) => ePrinter.printAllProperties(out, e)

    val alias = Array(justPrint, printAll)

    for (t <- eventGroupType.values()) {
      printers(t.ordinal()).configure()
    }

    val whatToPrint = if (all) 1 else 0

    while (trace.hasNext()) {
      val event = trace.next()
      if (event.getPrintingPriority() <= threshold) {
        val eType = event.getEventType()
        val t = EventTypes.typeToGroupType(eType)
        alias(whatToPrint)(printers(t.ordinal()), event)
        out.println();
      }
    }
  }

  def getPrinters = printers
  def setPrinters_=(pr: Array[EventPrinter]) {
    printers = pr
  }

}

object TracePrinter {
  val MAX_PRIORITY = Integer.MAX_VALUE;
}