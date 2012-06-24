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
package gov.nasa.jpf.traceServer.printer;

import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.traceServer.traceQuery.GenericEventIterator;
import gov.nasa.jpf.traceServer.traceQuery.TracePredicate;
import gov.nasa.jpf.traceServer.traceQuery.TraceQuery;
import gov.nasa.jpf.traceServer.traceQuery.TraceQueryFactory;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.util.Left;

import java.io.PrintWriter;

/**
 * Prints trace like gov.nasa.jpf.report.ConsoleTracePublisher.publishTrace().
 * 
 * @author Igor Andjelkovic
 * 
 */
public class OldTracePrinter {
  /**
   * TraceQuery instance for querying the trace.
   */
  protected TraceQuery query;
  /**
   * TraceQuery name for retrieving the TraceQuery instance.
   */
  protected String queryName;
  /**
   * Show the steps from inside the transition.
   */
  protected boolean showSteps;
  /**
   * Show the source code for executed instruction (line + location).
   */
  protected boolean showSource;
  /**
   * Show the instruction location from the source file. Used only if
   * {@link #showLocation} is set to <code>true</code>.
   */
  protected boolean showLocation;
  /**
   * Show the method name and the instruction opcode.
   */
  protected boolean showCode;
  /**
   * Show the method name. Used only if {@link #showLocation} is set to
   * <code>true</code>.
   */
  protected boolean showMethod;
  /**
   * Show the choice generator information for transitions.
   */
  protected boolean showCG;
  /**
   * Show the extra data added to trace by using
   * {@link PropertyCollection#TRACE_EXTRA_DATA} property.
   */
  protected boolean showExtraData;
  /**
   * Show details about Java API calls.
   * 
   */
  protected boolean showAPICalls;

  public OldTracePrinter() {
  }

  public OldTracePrinter(String queryName, boolean showSteps,
      boolean showLocation, boolean showSource, boolean showMethod,
      boolean showCode, boolean showExtraData, boolean showCG,
      boolean showAPICalls) {
    super();
    this.queryName = queryName;
    this.showSteps = showSteps;
    this.showLocation = showLocation;
    this.showSource = showSource;
    this.showMethod = showMethod;
    this.showCode = showCode;
    this.showExtraData = showExtraData;
    this.showCG = showCG;
    this.showAPICalls = showAPICalls;
  }

  /**
   * Prints the trace.
   * @param out Output stream to which trace will be written.
   */
  public void print(PrintWriter out) {
    query = TraceQueryFactory.getTraceQuery(queryName, null);
    TracePredicate predicate = instructionPredicate();
    boolean reversePath = false;
    GenericEventIterator iterator = new GenericEventIterator(query.getLastPath(
        predicate, reversePath));
    int i = 0;
    boolean skipAPICalls = false;

    Event event = iterator.next();
    while (iterator.hasNext()) {
      out.print("------------------------------------------------------ ");
      out.println("transition #" + i++ + " thread: "
          + ((Integer) event.getProperty(PropertyCollection.THREAD_ID)));

      if (showCG) {
        out.println(event
            .getProperty(PropertyCollection.CHOICE_GENERATOR_AS_STRING));
      }

      if (showSteps) {
        String lastLine = null;
        String lastMi = null;
        int nNoSrc = 0;

        event = iterator.next();

        while (event != null
            && (event.getEventType() != eventType.stateAdvanced)) {
          if (showSource) {
            String line = (String) event
                .getProperty(PropertyCollection.INSTRUCTION_SOURCE_LINE);
            if (line != null && !line.equals("null")) {
              if (!line.equals(lastLine)) {
                if (nNoSrc > 0) {
                  out.println("      [" + nNoSrc + " insn w/o sources]");
                }

                out.print("  ");
                if (showLocation) {
                  out.print(Left.format(
                      ((String) event
                          .getProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION)),
                      30));
                  out.print(" : ");
                }
                out.println(line.trim());
                nNoSrc = 0;
              }
            } else { // no source
              nNoSrc++;
            }
            lastLine = line;
          }

          if (showCode) {
            if (showMethod) {
              String mi = (String) event
                  .getProperty(PropertyCollection.INSTRUCTION_METHOD_NAME);
              
              if (!mi.equals(lastMi)) {                
                skipAPICalls = false;                
                String mci = (String) event
                    .getProperty(PropertyCollection.INSTRUCTION_CLASS_NAME);
                out.print("    ");
                if (mci != null && !mci.equals("null")) {
                  out.print(mci);
                  out.print(".");
                  if (mci.startsWith("java")) skipAPICalls = true;
                }
                out.println(mi);
                lastMi = mi;
              }
            }
            if(!skipAPICalls || showAPICalls) {
              out.print("      ");
              out.println((String) event
                  .getProperty(PropertyCollection.INSTRUCTION_OPCODE));
  
              if (showExtraData
                  && event.hasProperty(PropertyCollection.TRACE_EXTRA_DATA))
                out.print("        "
                    + event.getProperty(PropertyCollection.TRACE_EXTRA_DATA)
                    + "\n");
            }
          }
          event = iterator.next();
        }

        if (showSource && !showCode && (nNoSrc > 0)) {
          out.println("      [" + nNoSrc + " insn w/o sources]");
        }
      } else {
        event = iterator.goToNextStateEvent();
      }
    }
  }

  private TracePredicate instructionPredicate() {
    TracePredicate predicate = new TracePredicate() {
      public boolean filter(Event currentEvent) {
        return currentEvent.getEventType() == eventType.instructionExecuted;
      }
    };
    return predicate;
  }

  public TraceQuery getQuery() {
    return query;
  }

  public void setQuery(TraceQuery query) {
    this.query = query;
  }

  public String getQueryName() {
    return queryName;
  }

  public void setQueryName(String queryName) {
    this.queryName = queryName;
  }

  public boolean isShowSteps() {
    return showSteps;
  }

  public void setShowSteps(boolean showSteps) {
    this.showSteps = showSteps;
  }

  public boolean isShowLocation() {
    return showLocation;
  }

  public void setShowLocation(boolean showLocation) {
    this.showLocation = showLocation;
  }

  public boolean isShowSource() {
    return showSource;
  }

  public void setShowSource(boolean showSource) {
    this.showSource = showSource;
  }

  public boolean isShowMethod() {
    return showMethod;
  }

  public void setShowMethod(boolean showMethod) {
    this.showMethod = showMethod;
  }

  public boolean isShowCode() {
    return showCode;
  }

  public void setShowCode(boolean showCode) {
    this.showCode = showCode;
  }

  public boolean isShowExtraData() {
    return showExtraData;
  }

  public void setShowExtraData(boolean showExtraData) {
    this.showExtraData = showExtraData;
  }
  
  public boolean isShowCG() {
    return showCG;
  }

  public void setShowCG(boolean showCG) {
    this.showCG = showCG;
  }

  public boolean isShowAPICalls() {
    return showAPICalls;
  }

  public void setShowAPICalls(boolean showAPICalls) {
    this.showAPICalls = showAPICalls;
  }
  
}