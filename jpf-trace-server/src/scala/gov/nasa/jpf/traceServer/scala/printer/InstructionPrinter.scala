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
import gov.nasa.jpf.traceEmitter.PropertyCollection._

class InstructionPrinter(conf: Config, tracePrinterPrefix: String, name: String)
  extends EventPrinter(conf, tracePrinterPrefix, name) {

  desiredProperties = INSTRUCTION_METHOD_NAME +: INSTRUCTION_FILE_LOCATION +: desiredProperties
  desiredProperties = INSTRUCTION_OPCODE +: INSTRUCTION_CLASS_NAME +: desiredProperties
  defaultDesiredProperties = defaultDesiredProperties ++ desiredProperties

  val show_line = conf.getBoolean(tracePrinterPrefix + name
    + ".showLine", true);
  val show_location = conf.getBoolean(tracePrinterPrefix + name
    + ".showLocation", true);
  val show_method = conf.getBoolean(tracePrinterPrefix + name
    + ".showMethod", true);

}