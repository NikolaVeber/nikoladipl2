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
package gov.nasa.jpf.traceAnalyzer;

import java.io.PrintWriter;

import gov.nasa.jpf.traceEmitter.MethodAnalyzerEmitter.OpType;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.printer.generic.EventPrinter;
import gov.nasa.jpf.traceServer.printer.generic.TracePrinter;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;

/**
 * Analyzes call/execute sequences of methods.
 * 
 * @author Igor Andjelkovic
 */
public class GenericOutputMethodAnalyzer extends TraceAnalyzer {

  protected boolean skipInit;
  protected boolean showTransition;
  protected boolean showDepth;
  
  private static final String PRINT_ESSENTIAL = "essential";
  private static final String PRINT_ALL = "all";

  private TracePrinter tracePrinter;
  private String format;

  public GenericOutputMethodAnalyzer(String query, String dbLocation) {
    super(query, dbLocation);
    showTransition = true;
    skipInit = true;
    showDepth = true;
    
    tracePrinter = new TracePrinter(new PrintWriter(System.out, true));
  }

  protected boolean isMethodEnter(Event e) {
    int type = (Integer) e.getProperty(PropertyCollection.METHOD_CALL_TYPE);
    return (type == OpType.CALL_EXECUTE.ordinal())
        || (type == OpType.EXECUTE.ordinal());
  }

  protected boolean isSameMethod(Event e1, Event e2) {
    String mi1 = (String) e1
        .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME)
        + (String) e1.getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
    String mi2 = (String) e2
        .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME)
        + (String) e2.getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
    int ti1 = (Integer) e1.getProperty(PropertyCollection.THREAD_ID);
    int ti2 = (Integer) e2.getProperty(PropertyCollection.THREAD_ID);
    int stackDepth1 = (Integer) e1.getProperty(PropertyCollection.STACK_DEPTH);
    int stackDepth2 = (Integer) e2.getProperty(PropertyCollection.STACK_DEPTH);
    int ei1 = (Integer) e1.getProperty(PropertyCollection.EI_UNIQUE_ID);
    int ei2 = (Integer) e2.getProperty(PropertyCollection.EI_UNIQUE_ID);
    return (mi1.equals(mi2)) && (ti1 == ti2) && (ei1 == ei2)
        && (stackDepth1 == stackDepth2);
  }

  public void print() {

    TracePredicate predicate = methodAnalyzerPredicate();
    boolean reversePath = false;
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        predicate, reversePath));

    gov.nasa.jpf.traceServer.printer.EventPrinter[] printers = tracePrinter.getPrinters();
    ((EventPrinter)printers[EventTypes.eventGroupType.instruction.ordinal()])
        .addDesiredProperty((PropertyCollection.EI_INDEX));
    ((EventPrinter)printers[EventTypes.eventGroupType.instruction.ordinal()])
        .addDesiredProperty((PropertyCollection.EI_UNIQUE_ID));
    ((EventPrinter)printers[EventTypes.eventGroupType.instruction.ordinal()])
        .addDesiredProperty((PropertyCollection.THREAD_NAME));
    ((EventPrinter)printers[EventTypes.eventGroupType.instruction.ordinal()])
        .addDesiredProperty((PropertyCollection.METHOD_CALL_TYPE));
    ((EventPrinter)printers[EventTypes.eventGroupType.instruction.ordinal()])
        .addDesiredProperty((PropertyCollection.EI_CLASS_NAME));

    tracePrinter.printTrace(iterator, TracePrinter.MAX_PRIORITY);

  }
  
  public void printAll() {

    TracePredicate predicate = methodAnalyzerPredicate();
    boolean reversePath = false;
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        predicate, reversePath));

    tracePrinter.printTraceWithAllProperties(iterator, TracePrinter.MAX_PRIORITY);

  }

  protected TracePredicate methodAnalyzerPredicate() {
    TracePredicate predicate = new TracePredicate() {
      public boolean filter(Event currentEvent) {
        return currentEvent.getEventType() == eventType.instructionExecuted;
      }
    };
    return predicate;
  }
  
  public void configureAnalyzer(Object... args) {
    if (args.length > 0) {
      format = (String) args[0];
    } else {
      format = PRINT_ALL;
    }
  }

  public void analyze() {
    query.startTraceQuery();
    if (format.equals(PRINT_ESSENTIAL)) {
      System.out.println("\nPrint events with certain properties.");
      print();
    } else {
      System.out.println("\nPrint events with all their properties.");
      printAll();
    }
    query.stopTraceQuery();
  }

  public static void main(String... arg) {
    if (arg.length == 0) {
      throw new RuntimeException("You must provide database location");
    }
    String dbLocation = arg[0];
    String query = arg[1];
    String args = arg[2];
    
    System.out.println(args);

    GenericOutputMethodAnalyzer newMethodAnalyzer = new GenericOutputMethodAnalyzer(
        query, dbLocation);
    
    newMethodAnalyzer.configureAnalyzer(args);
    newMethodAnalyzer.analyze();
  }
}
