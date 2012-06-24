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

import gov.nasa.jpf.traceServer.traceStorer.PropertyID;

/**
 * Collection of <code>{@link PropertyID}</code> instances used in
 * <code>trace server</code>.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class PropertyCollection {
  
  public static PropertyID STATE_ID = PropertyID.createPropertyID("stateId", PropertyID.dataTypeID.iint);
  public static PropertyID STATE_IS_NEW = PropertyID.createPropertyID("stateIsNew", PropertyID.dataTypeID.bboolean);
  public static PropertyID STATE_IS_END = PropertyID.createPropertyID("stateIsEnd", PropertyID.dataTypeID.bboolean);

  public static PropertyID THREAD_ID = PropertyID.createPropertyID("threadId", PropertyID.dataTypeID.iint);
  public static PropertyID THREAD_NAME = PropertyID.createPropertyID("threadName", PropertyID.dataTypeID.sstring);
  public static PropertyID THREAD_STATE = PropertyID.createPropertyID("threadState", PropertyID.dataTypeID.sstring);
  
  public static PropertyID METHOD_UNIQUE_NAME = PropertyID.createPropertyID("methodUniqueName", PropertyID.dataTypeID.sstring);
  public static PropertyID METHOD_CALL_TYPE = PropertyID.createPropertyID("methodCallType", PropertyID.dataTypeID.iint);
  public static PropertyID METHOD_IS_STATIC = PropertyID.createPropertyID("methodIsStatic", PropertyID.dataTypeID.bboolean);
  
  public static PropertyID CLASS_NAME = PropertyID.createPropertyID("className", PropertyID.dataTypeID.sstring);
  public static PropertyID CLASS_UNIQUE_ID = PropertyID.createPropertyID("classUniqueId", PropertyID.dataTypeID.iint);
  
  public static PropertyID EI_UNIQUE_ID = PropertyID.createPropertyID("eiUniqueId", PropertyID.dataTypeID.iint);
  public static PropertyID EI_INDEX = PropertyID.createPropertyID("eiIndex", PropertyID.dataTypeID.iint);
  public static PropertyID EI_CLASS_NAME = PropertyID.createPropertyID("eiClassName", PropertyID.dataTypeID.sstring);

  public static PropertyID INSTRUCTION_OPCODE = PropertyID.createPropertyID("instructionOpcode", PropertyID.dataTypeID.sstring);
  public static PropertyID INSTRUCTION_FILE_LOCATION = PropertyID.createPropertyID("instructionFileLocation", PropertyID.dataTypeID.sstring);
  public static PropertyID INSTRUCTION_METHOD_NAME = PropertyID.createPropertyID("instructionMethodName", PropertyID.dataTypeID.sstring);
  public static PropertyID INSTRUCTION_CLASS_NAME = PropertyID.createPropertyID("instructionClassName", PropertyID.dataTypeID.sstring);
  public static PropertyID INSTRUCTION_SOURCE_LINE = PropertyID.createPropertyID("instructionSourceLine", PropertyID.dataTypeID.sstring);

  public static PropertyID OBJECT_TYPE = PropertyID.createPropertyID("objectType", PropertyID.dataTypeID.sstring);
  public static PropertyID OBJECT_REFERENCE = PropertyID.createPropertyID("objectRef", PropertyID.dataTypeID.iint);
  public static PropertyID OBJECT_EVENT_TYPE = PropertyID.createPropertyID("objectType", PropertyID.dataTypeID.iint);

  public static PropertyID EXCEPTION_TYPE = PropertyID.createPropertyID("exceptionType", PropertyID.dataTypeID.sstring);
  public static PropertyID EXCEPTION_MESSAGE = PropertyID.createPropertyID("exceptionMessage", PropertyID.dataTypeID.sstring);

  public static PropertyID CHOICE_GENERATOR_TYPE = PropertyID.createPropertyID("choiceGeneratorType", PropertyID.dataTypeID.sstring);
  public static PropertyID CHOICE_GENERATOR_CHOICE = PropertyID.createPropertyID("choiceGeneratorChoice", PropertyID.dataTypeID.sstring);
  public static PropertyID CHOICE_GENERATOR_AS_STRING = PropertyID.createPropertyID("choiceGeneratorAsString", PropertyID.dataTypeID.sstring);

  public static PropertyID STACK_DEPTH = PropertyID.createPropertyID("stackDepth", PropertyID.dataTypeID.iint);
  
  public static PropertyID EVENT_TYPE = PropertyID.createPropertyID("eventType", PropertyID.dataTypeID.iint);
  
  public static PropertyID TRACE_EXTRA_DATA = PropertyID.createPropertyID("traceExtraData", PropertyID.dataTypeID.sstring);

}
