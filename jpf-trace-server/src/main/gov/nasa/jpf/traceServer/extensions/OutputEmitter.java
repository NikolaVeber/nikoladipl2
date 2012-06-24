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
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.traceEmitter.DefaultTraceEmitter;
import gov.nasa.jpf.traceServer.extensions.ExtraPropertiesCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Extends the {@link gov.nasa.jpf.traceEmitter.DefaultTraceEmitter
 * DefaultTraceEmitter} by adding methodEntered event and arguments of the
 * System.out.println(String) method call as events property. Method arguments
 * are store by using the
 * {@link ExtraPropertiesCollection#CONSOLE_PRINTLN_STRING
 * CONSOLE_PRINTLN_STRING} property.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class OutputEmitter extends DefaultTraceEmitter {

  protected String CONSOLE_PRINTLN_STRING = "gov.nasa.jpf.ConsoleOutputStream.println(Ljava/lang/String;)V";
  protected String CONSOLE_PRINT_STRING = "gov.nasa.jpf.ConsoleOutputStream.print(Ljava/lang/String;)V";

  public OutputEmitter(Config config, JPF jpf) {
    super(config, jpf);
  }

  public void methodEntered(JVM vm) {
    String methodName = vm.getLastMethodInfo().getCompleteName();
    if (methodName.equals(CONSOLE_PRINTLN_STRING)
        || methodName.equals(CONSOLE_PRINT_STRING)) {
      Event event = this.createMethodEvent(vm, eventType.methodEntered);
      String arg = vm.getLastThreadInfo().getEnv()
          .getStringObject(vm.getLastThreadInfo().getCallerStackFrame().peek());
      
      event.addProperty(ExtraPropertiesCollection.CONSOLE_PRINTLN_STRING, arg);
      traceFilter.processEvent(event, eventType.methodEntered);
    }
  }
}
