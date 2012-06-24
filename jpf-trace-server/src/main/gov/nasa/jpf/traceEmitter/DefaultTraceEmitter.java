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
package gov.nasa.jpf.traceEmitter;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.util.Source;

/**
 * Default trace emitter. Notifies about several important events, created to
 * show extensibility of TraceEmitter.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class DefaultTraceEmitter extends TraceEmitter {

  public DefaultTraceEmitter(Config config, JPF jpf) {
    super(config, jpf);
  }

  public void instructionExecuted(JVM vm) {
    Instruction insn = vm.getLastInstruction();
    // we are skipping init instructions
    // recording starts when main() is called
    MethodInfo mi = insn.getMethodInfo();
    if (skipInit) {
      if (mi == miMain) {
        skipInit = false; // start recording
      } else {
        return; // skip
      }
    }

    addInstructionProperties(vm, insn, mi);
  }

  public void objectLocked(JVM vm) {
    addObjectProperties(vm, eventType.objectLocked);
  }

  public void objectUnlocked(JVM vm) {
    addObjectProperties(vm, eventType.objectUnlocked);
  }

  public void objectWait(JVM vm) {
    addObjectProperties(vm, eventType.objectWait);
  }

  public void objectNotify(JVM vm) {
    addObjectProperties(vm, eventType.objectNotify);
  }

  public void objectNotifyAll(JVM vm) {
    addObjectProperties(vm, eventType.objectNotifyAll);
  }

  public void threadBlocked(JVM vm) {
    addThreadProperties(vm, eventType.threadBlocked);
  }

  public void threadStarted(JVM vm) {
    addThreadProperties(vm, eventType.threadStarted);
  }

  public void threadTerminated(JVM vm) {
    addThreadProperties(vm, eventType.threadTerminated);
  }

  /**
   * Augments the default thread event with custom properties.
   * 
   */
  protected void addThreadProperties(JVM vm, eventType type) {
    Event event = this.createThreadEvent(vm, type);
    event.addProperty(PropertyCollection.THREAD_STATE, vm.getLastThreadInfo()
        .getStateName());
    event.addProperty(PropertyCollection.OBJECT_TYPE, vm.getLastElementInfo()
        .getType());
    event.addProperty(PropertyCollection.OBJECT_REFERENCE, vm
        .getLastElementInfo().getObjectRef());
    traceFilter.processEvent(event, type);
  }

  /**
   * Augments the default object event with custom properties.
   */
  protected void addObjectProperties(JVM vm, eventType type) {
    Event event = this.createObjectEvent(vm, type);
    event.addProperty(PropertyCollection.THREAD_ID, vm.getLastThreadInfo()
        .getId());
    event.addProperty(PropertyCollection.THREAD_NAME, vm.getLastThreadInfo()
        .getName());
    event.addProperty(PropertyCollection.THREAD_STATE, vm.getLastThreadInfo()
        .getStateName());
    traceFilter.processEvent(event, type);
  }

  /**
   * Augments the default object event with custom properties.
   */
  protected void addInstructionProperties(JVM vm, Instruction insn,
      MethodInfo mi) {
    Event event = this.createInstructionEvent(insn, mi,
        eventType.instructionExecuted);
    event.addProperty(PropertyCollection.INSTRUCTION_SOURCE_LINE,
        getLineString(insn));

    // redefine the default value for property INSTRUCTION_OPCODE
    event.addProperty(PropertyCollection.INSTRUCTION_OPCODE, insn.toString());

    traceFilter.processEvent(event, eventType.instructionExecuted);
  }

  protected String getLineString(Instruction insn) {
    MethodInfo mi = insn.getMethodInfo();
    if (mi != null) {
      Source source = Source.getSource(mi.getSourceFileName());
      if (source != null) {
        int line = mi.getLineNumber(insn);
        if (line > 0) {
          return source.getLine(line);
        }
      }
    }
    return null;
  }
}
