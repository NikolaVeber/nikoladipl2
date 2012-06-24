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
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.printer.EventPrinter;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceQuery.TraceQuery;
import gov.nasa.jpf.traceServer.traceQuery.TraceQueryFactory;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventGroupType;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Printer for printing the location of the object initialization. The location
 * is obtained from the NEW instruction by preprocessing the trace. Used with
 * {@link ExtraDataTraceEmitter}.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ObjectPrinter extends EventPrinter {

  private HashMap<Integer, String> map = new HashMap<Integer, String>();
  String queryName;

  public ObjectPrinter(Config conf, String tracePrinterPrefix) {
    super(conf, tracePrinterPrefix);
    queryName = conf.getString("traceServer.trace_storer", "inMemory");
  }

  /**
   * Collects the information about all objects location of creation.
   */
  public void configure() {
    TracePredicate predicate = createTracePredicate();
    boolean reversePath = false;
    TraceQuery query = TraceQueryFactory.getTraceQuery(queryName, null);
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        predicate, reversePath));

    for (Event event : iterator) {
      if (event.hasProperty(ExtraPropertiesCollection.NEW_OBJ_ID)) {
        int objRef = (Integer) event
            .getProperty(ExtraPropertiesCollection.NEW_OBJ_ID);
        map.put(objRef, (String) event
            .getProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION));
      }
    }
  }

  // return NEW instructions only
  private TracePredicate createTracePredicate() {
    TracePredicate predicate = new TracePredicate() {
      public boolean filter(Event currentEvent) {
        String opcode = ((String) currentEvent
            .getProperty(PropertyCollection.INSTRUCTION_OPCODE));
        return (opcode != null && opcode.startsWith("new"));
      }
    };
    return predicate;
  }

  public void print(PrintWriter out, Event event) {
    out.println(event.getEventType());
    out.print(TAB);
    int objRef = (Integer) event
        .getProperty(PropertyCollection.OBJECT_REFERENCE);
    out.print(objRef);
    out.print(DELIMITER);
    out.print(event.getProperty(PropertyCollection.OBJECT_TYPE));
    if (printExtraData && map.containsKey(objRef)) {
      out.print(DELIMITER);
      out.print("init at: " + map.get(objRef));
    }
    out.println();
  }

  public String getName() {
    return eventGroupType.object.toString();
  }
}
