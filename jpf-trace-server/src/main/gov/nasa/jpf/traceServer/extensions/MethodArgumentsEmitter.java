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
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.traceEmitter.DefaultTraceEmitter;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Extends the {@link gov.nasa.jpf.traceEmitter.DefaultTraceEmitter
 * DefaultTraceEmitter}'s instructionExecuted events with method arguments for
 * the INVOKE instructions. The method arguments are stored by using the
 * {@link gov.nasa.jpf.traceEmitter.PropertyCollection#TRACE_EXTRA_DATA
 * TRACE_EXTRA_DATA} property.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class MethodArgumentsEmitter extends DefaultTraceEmitter {

  public MethodArgumentsEmitter(Config config, JPF jpf) {
    super(config, jpf);
  }

  // we are augmenting the default object event with custom properties
  protected void addInstructionProperties(JVM vm, Instruction insn,
      MethodInfo mi) {
    Event event = this.createInstructionEvent(insn, mi,
        eventType.instructionExecuted);
    event.addProperty(PropertyCollection.INSTRUCTION_SOURCE_LINE,
        getLineString(insn));

    // redefine the default value for property INSTRUCTION_OPCODE
    event.addProperty(PropertyCollection.INSTRUCTION_OPCODE, insn.toString());
    if (insn instanceof InvokeInstruction) {
      String invokedMethodName = ((InvokeInstruction) insn).getInvokedMethod()
          .getCompleteName();
      event.addProperty(ExtraPropertiesCollection.INVOKED_METHOD_COMPLETE_NAME,
          invokedMethodName);

      Object args[] = ((InvokeInstruction) insn).getArgumentValues(vm
          .getLastThreadInfo());
      StringBuilder sb = new StringBuilder();
      for (int i=0; i<args.length; i++) {
        if(i>0) sb.append(" # ");
        sb.append(args[i] != null ? args[i].toString() : "null");
      }
      if (sb.length() != 0) {
        event.addProperty(PropertyCollection.TRACE_EXTRA_DATA, sb.toString());
      }
    }
    traceFilter.processEvent(event, eventType.instructionExecuted);
  }
}
