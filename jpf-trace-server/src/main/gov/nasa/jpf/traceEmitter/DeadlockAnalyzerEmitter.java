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
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.bytecode.EXECUTENATIVE;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Trace emitter used with
 * <code>{@link gov.nasa.jpf.traceAnalyzer.DeadlockAnalyzer DeadlockAnalyzer}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class DeadlockAnalyzerEmitter extends TraceEmitter {

  public DeadlockAnalyzerEmitter(Config config, JPF jpf) {
    super(config, jpf);
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

  // we are augmenting the default thread event with custom properties
  private void addThreadProperties(JVM vm, eventType type) {
    Event event = this.createThreadEvent(vm, type);
    event.addProperty(PropertyCollection.OBJECT_REFERENCE, vm
        .getLastElementInfo().getObjectRef());
    addPCInformation(vm, event);
    traceFilter.processEvent(event, type);
  }

  // we are augmenting the default object event with custom properties
  private void addObjectProperties(JVM vm, eventType type) {
    Event event = this.createObjectEvent(vm, type);
    event.addProperty(PropertyCollection.THREAD_ID, vm.getLastThreadInfo()
        .getId());
    addPCInformation(vm, event);
    traceFilter.processEvent(event, type);
  }

  // add information about the current PC
  private void addPCInformation(JVM vm, Event event) {

    StackFrame frame = vm.getCurrentThread().getTopFrame();
    if (frame != null) {
      Instruction insn = frame.getPC();
      if (insn instanceof EXECUTENATIVE) {
        frame = frame.getPrevious();
        if (frame != null) {
          insn = frame.getPC();
        }
      }
      String mnemonic = insn != null ? insn.getMnemonic() : "";
      event.addProperty(PropertyCollection.INSTRUCTION_OPCODE, mnemonic);
      String location = insn != null ? insn.getFileLocation() : "";
      if (mnemonic.equals("nativereturn")) {
        location = insn.toString();
        if (location.contains(" "))
          location = location.split(" ")[1];
      }
      event.addProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION, location);
    } else {
      event.addProperty(PropertyCollection.INSTRUCTION_OPCODE, "noMnemonic");
      event.addProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION,
          "noLocation");
    }
  }

}
