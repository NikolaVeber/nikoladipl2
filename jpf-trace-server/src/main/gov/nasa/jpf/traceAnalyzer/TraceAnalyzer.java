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
package gov.nasa.jpf.traceAnalyzer;

import gov.nasa.jpf.traceServer.traceQuery.TraceQuery;
import gov.nasa.jpf.traceServer.traceQuery.TraceQueryFactory;

import java.io.PrintStream;

/**
 * Base class for trace analyzers. Provides a uniform way to trigger
 * the analyzer, from either standalone programs or at the end of JPF search.
 * 
 * @author Igor Andjelkovic
 * 
 */
public abstract class TraceAnalyzer {

  protected PrintStream out = System.out;
  protected TraceQuery query;

  protected TraceAnalyzer(String queryName, String dbLocation) {
    query = TraceQueryFactory.getTraceQuery(queryName, dbLocation);
  }

  /**
   * This is analyzer's "run" method, called when the analyzer services are
   * requested. All analyzer's code should be placed here, or calls to methods
   * that make the analyzer. The analysis is printed by using
   * <code>{@link java.io.PrintStream}</code>. The default value for the stream
   * is <code>{@link java.lang.System#out}</code>.
   */
  public abstract void analyze();

  /**
   * The method to configure the analyzer. Accepts a variable list of arguments,
   * and thus provides a uniform way for their creation and configuration. Each
   * analyzer has to know how to use the given arguments. The method is called
   * after creating the analyzer, and before the first call to
   * <code>{@link #analyze()}</code>.
   * 
   * @param args
   *          Parameters specific for each analyzer.
   */
  public void configureAnalyzer(Object... args) {
  }

  /**
   * Gets the output stream used.
   * 
   * @return if a output stream has already been set for the current analyzer,
   *         then that output stream is returned; otherwise, the default stream,
   *         <code>{@link java.lang.System#out}</code> is returned.
   */
  public PrintStream getOut() {
    return out;
  }

  /**
   * Sets the output stream.
   * 
   * @param out
   *          The new stream.
   */
  public void setOut(PrintStream out) {
    this.out = out;
  }

}
