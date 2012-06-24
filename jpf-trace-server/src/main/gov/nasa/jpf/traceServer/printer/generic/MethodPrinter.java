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
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;

/**
 * Printer for the method
 * {@link gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType
 * eventGroupType} (methodEntered, methodExited).
 * 
 * @author Igor Andjelkovic
 * 
 */
public class MethodPrinter extends EventPrinter {

  public MethodPrinter(Config conf, String tracePrinterPrefix) {
    super(conf, tracePrinterPrefix);
    
    desiredProperties.add(PropertyCollection.METHOD_UNIQUE_NAME);
    desiredProperties.add(PropertyCollection.CLASS_NAME);
    
    defaultDesiredProperties.addAll(desiredProperties);
  }

  /*public void print(PrintWriter out, Event event) {
    out.print(String.format(FORMAT,event.getEventType()));
    out.print(TAB);
    String mi = (String) event
        .getProperty(PropertyCollection.METHOD_UNIQUE_NAME);
    String mci = (String) event.getProperty(PropertyCollection.CLASS_NAME);

    if (mci != null && !mci.equals("null")) {
      mi = mci + "." + mi;
    }
    
    out.print(String.format(FORMAT,mi));
  }*/

  public String getName() {
    return eventGroupType.method.toString();
  }
}
