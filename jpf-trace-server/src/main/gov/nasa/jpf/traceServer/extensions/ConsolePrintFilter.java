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

import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceEmitter.traceFilter.TraceFilter;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Trace filter that filters out all instructions except the ones that invoke
 * the System.out.println() and the System.out.println(String) methods.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ConsolePrintFilter extends TraceFilter {

  protected String CONSOLE_PRINTLN_STRING = "gov.nasa.jpf.ConsoleOutputStream.println(Ljava/lang/String;)V";
  protected String CONSOLE_PRINT_STRING = "gov.nasa.jpf.ConsoleOutputStream.print(Ljava/lang/String;)V";

  public void processInstructionExecuted(Event event) {
    if (event
        .hasProperty(ExtraPropertiesCollection.INVOKED_METHOD_COMPLETE_NAME)) {
      String mi = (String) event
          .getProperty(ExtraPropertiesCollection.INVOKED_METHOD_COMPLETE_NAME);

      if (mi.equals(CONSOLE_PRINT_STRING) || mi.equals(CONSOLE_PRINTLN_STRING)) {
        String objRefToString = (String) event
            .getProperty(PropertyCollection.TRACE_EXTRA_DATA);
        int objRef = Integer.parseInt(objRefToString.split("@")[1]);
        String arg = JVM.getVM().getLastThreadInfo().getEnv()
            .getStringObject(Integer.parseInt(""+objRef, 16)); //ElementInfo.toString returns index as HEX integer
        event.addProperty(PropertyCollection.TRACE_EXTRA_DATA, arg);
        forward(event, eventType.instructionExecuted);
      }
    }
  }
}
