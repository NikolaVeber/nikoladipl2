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
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.traceAnalyzer.TraceAnalyzer;
import gov.nasa.jpf.traceEmitter.traceFilter.GeneralTailTraceFilter;
import gov.nasa.jpf.traceEmitter.traceFilter.TailTraceFilter;
import gov.nasa.jpf.traceEmitter.traceFilter.TraceFilter;
import gov.nasa.jpf.traceServer.printer.ConsoleTracePrinter;
import gov.nasa.jpf.traceServer.traceStorer.Event;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes;
import gov.nasa.jpf.traceServer.traceStorer.EventTypes.eventType;
import gov.nasa.jpf.traceServer.traceStorer.TraceStorerFactory;

import java.util.ArrayList;

/**
 * Base class for listening JPF notifications and storing them by using trace
 * storer interface. <br/>
 * It is the main class of the trace server system. It's responsible for system
 * initialization and starting, and acts like an interface between JPF and trace
 * storers. TraceEmitter provides methods for creating <code>event</code> object
 * with default set of properties. It should be extended to listen for various
 * sets of events. (by overriding the desired
 * <code>{@link gov.nasa.jpf.ListenerAdapter ListenerAdapter} methods.)
 * 
 * @author Igor Andjelkovic
 * 
 */
public class TraceEmitter extends ListenerAdapter {

  /**
   * Trace factory that will be used to create events and trace storer for this
   * trace server invokation.
   */
  protected TraceStorerFactory traceStorerFactory;
  /**
   * The head of the chain of trace filters.
   */
  protected TraceFilter traceFilter;
  /**
   * Should initialization of the system be skipped.
   */
  protected boolean skipInit;
  /**
   * Used with <code>skipInit</code> to start the trace storing mechanism when
   * the <code>main</code> starts.
   */
  protected MethodInfo miMain;
  /**
   * Trace database location from the file system.
   */
  protected String dbLocation;

  /**
   * Analyzers that will be invoked at the end of JPF search.
   */
  protected ArrayList<TraceAnalyzer> analyzers = new ArrayList<TraceAnalyzer>();

  public TraceEmitter(Config config, JPF jpf) {
    jpf.addPublisherExtension(ConsoleTracePrinter.class, this);

    dbLocation = config.getString("traceServer.db_location", "db");
    String traceStorerName = config.getString("traceServer.trace_storer",
        "neo4j");
    skipInit = config.getBoolean("traceServer.skip_init", true);
    boolean localStorer = config.getBoolean("traceServer.local_storer", true);
    String host = config.getString("traceServer.host", "localhost");
    Integer port = config.getInt("traceServer.port", 4444);

    String traceQueryName = config.getString("traceServer.trace_query",
        "inMemory");

    TraceFilter lastFilter = addFilters(config);

    if (localStorer) {
      traceStorerFactory = TraceStorerFactory
          .getTraceStorerFactory(traceStorerName);
      lastFilter.addLast(new TailTraceFilter(traceStorerFactory
          .createTraceStorer(dbLocation)));
    } else {
      traceStorerFactory = TraceStorerFactory.getTraceStorerFactory("remote");
      lastFilter.addLast(new TailTraceFilter(traceStorerFactory
          .createTraceStorer(host, port)));
    }

    addAdditionalStorers(config, host, port, lastFilter);

    addAnalyzers(config, traceQueryName);
  }

  private TraceFilter addFilters(Config config) {
    TraceFilter lastFilter = null;
    TraceFilter firstFilter = null;
    if (config.hasValue("traceServer.trace_filter")) {
      Class<?>[] filterArgTypes = { Config.class };
      Object[] filterArgs = { config };

      ArrayList<TraceFilter> filters = config.getInstances(
          "traceServer.trace_filter", TraceFilter.class, filterArgTypes,
          filterArgs);

      firstFilter = lastFilter = filters.size() > 0 ? filters.get(0) : null;
      for (int i = 1; i < filters.size(); i++) {
        lastFilter.addLast(filters.get(i));
        lastFilter = filters.get(i);
      }
    }
    if (firstFilter == null) {
      traceFilter = lastFilter = new TraceFilter();
    } else {
      traceFilter = firstFilter;
    }
    return lastFilter;
  }

  private void addAdditionalStorers(Config config, String host, Integer port,
      TraceFilter lastFilter) {
    if (config.hasValue("traceServer.additional_trace_storers")) {
      String extraStorers[] = config
          .getStringArray("traceServer.additional_trace_storers");
      for (String storerName : extraStorers) {
        TraceStorerFactory storerFactory = TraceStorerFactory
            .getTraceStorerFactory(storerName);
        if (storerName.equals("neo4j")) {
          lastFilter.addLast(new GeneralTailTraceFilter(storerFactory
              .createTraceStorer(dbLocation), storerFactory));
        } else if (storerName.equals("inMemory")) {
          lastFilter.addLast(new GeneralTailTraceFilter(storerFactory
              .createTraceStorer(), storerFactory));
        } else if (storerName.equals("remote")) {
          lastFilter.addLast(new GeneralTailTraceFilter(storerFactory
              .createTraceStorer(host, port), storerFactory));
        }
      }
    }
  }

  private void addAnalyzers(Config config, String traceQueryName) {
    if (config.hasValue("traceServer.trace_analyzer")) {
      Class<?>[] argTypes = { String.class, String.class };
      Object[] args = { traceQueryName, dbLocation };

      analyzers = config.getInstances("traceServer.trace_analyzer",
          TraceAnalyzer.class, argTypes, args);

      String analyzerArgs[] = config
          .getStringArray("traceServer.trace_analyzer.params");

      for (TraceAnalyzer analyzer : analyzers) {
        analyzer.configureAnalyzer((Object[]) analyzerArgs);
      }
    }
  }

  /**
   * @see #augmentStateAdvanced(Search)
   */
  protected Event augmentSearchStarted(Search search) {
    Event event = traceStorerFactory.createEvent();
    return event;
  }

  /**
   * Default implementation of method that should augment stateAdvanced event
   * with custom properties. Part of a template method pattern.
   */
  protected Event augmentStateAdvanced(Search search) {
    Event event = traceStorerFactory.createEvent();
    return event;
  }

  /**
   * @see #augmentStateAdvanced(Search)
   */
  protected Event augmentStateBacktracked(Search search) {
    Event event = traceStorerFactory.createEvent();
    return event;
  }

  /**
   * @see #augmentStateAdvanced(Search)
   */
  protected Event augmentStateRestored(Search search) {
    Event event = traceStorerFactory.createEvent();
    return event;
  }

  /**
   * @see #augmentStateAdvanced(Search)
   */
  protected Event augmentSearchFinished(Search search) {
    Event event = traceStorerFactory.createEvent();
    return event;
  }

  /**
   * @see #stateAdvanced(Search)
   */
  public final void searchStarted(Search search) {
    Event event = augmentSearchStarted(search);
    if (skipInit) {
      ClassInfo ci = search.getVM().getMainClassInfo();
      miMain = ci.getMethod("main([Ljava/lang/String;)V", false);
    }
    event.addProperty(PropertyCollection.EVENT_TYPE,
        eventType.searchStarted.ordinal());
    traceFilter.processEvent(event, eventType.searchStarted);
  }

  /**
   * Template method, adds mandatory properties to <code>stateAdvanced</code>
   * event that possibly contains custom properties added by user, by overriding
   * <code>{@link #augmentStateAdvanced(Search)} method. Since it is essential 
   * for maintaining the trace structure, it is made <code>final</code>, to
   * prevent accidental corruption of the trace.
   */
  public final void stateAdvanced(Search search) {
    Event event = augmentStateAdvanced(search);
    event.addProperty(PropertyCollection.STATE_ID, search.getStateId());
    event.addProperty(PropertyCollection.STATE_IS_NEW, search.isNewState());
    event.addProperty(PropertyCollection.STATE_IS_END, search.isEndState());
    event.addProperty(PropertyCollection.EVENT_TYPE,
        eventType.stateAdvanced.ordinal());
    event.addProperty(PropertyCollection.THREAD_ID, search.getVM()
        .getThreadNumber());
    event.addProperty(PropertyCollection.CHOICE_GENERATOR_AS_STRING, search
        .getTransition().getChoiceGenerator().toString());
    traceFilter.processEvent(event, eventType.stateAdvanced);
  }

  /**
   * @see #stateAdvanced(Search)
   */
  public final void stateBacktracked(Search search) {
    Event event = augmentStateBacktracked(search);
    event.addProperty(PropertyCollection.STATE_ID, search.getStateId());
    event.addProperty(PropertyCollection.EVENT_TYPE,
        eventType.stateBacktracked.ordinal());
    traceFilter.processEvent(event, eventType.stateBacktracked);
  }

  /**
   * @see #stateAdvanced(Search)
   */
  public final void stateRestored(Search search) {
    Event event = augmentStateRestored(search);
    event.addProperty(PropertyCollection.STATE_ID, search.getStateId());
    event.addProperty(PropertyCollection.EVENT_TYPE,
        eventType.stateRestored.ordinal());
    traceFilter.processEvent(event, eventType.stateRestored);
  }

  /**
   * @see #stateAdvanced(Search)
   */
  public final void searchFinished(Search search) {
    Event event = augmentSearchFinished(search);
    event.addProperty(PropertyCollection.EVENT_TYPE,
        eventType.searchFinished.ordinal());
    traceFilter.processEvent(event, eventType.searchFinished);

    for (TraceAnalyzer analyzer : analyzers) {
      analyzer.getOut().print(
          "====================================================== ");
      analyzer.getOut().println(analyzer.getClass().getName());
      analyzer.analyze();
    }
  }

  public void propertyViolated(Search search) {
    Event event = traceStorerFactory.createEvent();
    event.addProperty(PropertyCollection.EVENT_TYPE,
        eventType.propertyViolated.ordinal());
    traceFilter.processEvent(event, eventType.propertyViolated);
  }

  /**
   * Creates thread event with mandatory properties.
   * 
   * @param vm
   *          JVM instance.
   * @param type
   *          Thread event type (threadStarted, threadWaiting, threadNotified,
   *          threadInterrupted, threadScheduled, threadBlocked,
   *          threadTerminated).
   */
  protected Event createThreadEvent(JVM vm, EventTypes.eventType type) {
    Event event = traceStorerFactory.createEvent();
    event.addProperty(PropertyCollection.THREAD_ID, vm.getLastThreadInfo()
        .getId());
    event.addProperty(PropertyCollection.THREAD_NAME, vm.getLastThreadInfo()
        .getName());
    event.addProperty(PropertyCollection.EVENT_TYPE, type.ordinal());
    return event;
  }

  /**
   * Creates object event with mandatory properties.
   * 
   * @param vm
   *          JVM instance.
   * @param type
   *          Object event type (objectCreated, objectReleased, objectLocked,
   *          objectUnlocked, objectWait, objectNotify, objectNotifyAll).
   */
  protected Event createObjectEvent(JVM vm, EventTypes.eventType type) {
    Event event = traceStorerFactory.createEvent();
    event.addProperty(PropertyCollection.OBJECT_TYPE, vm.getLastElementInfo()
        .getType());
    event.addProperty(PropertyCollection.OBJECT_REFERENCE, vm
        .getLastElementInfo().getObjectRef());
    event.addProperty(PropertyCollection.EVENT_TYPE, type.ordinal());

    return event;
  }

  /**
   * Creates instruction event with mandatory properties.
   * 
   * @param mi
   *          Method information.
   * @param insn
   *          Instruction information.
   * @param type
   *          Instruction event type (instructionExecuted, executeInstruction).
   */
  protected Event createInstructionEvent(Instruction insn, MethodInfo mi,
      EventTypes.eventType type) {
    Event event = traceStorerFactory.createEvent();
    event
        .addProperty(PropertyCollection.INSTRUCTION_OPCODE, insn.getMnemonic());

    ClassInfo mci = mi.getClassInfo();
    event.addProperty(PropertyCollection.INSTRUCTION_CLASS_NAME,
        (mci != null) ? mci.getName() : null);
    event.addProperty(PropertyCollection.INSTRUCTION_METHOD_NAME,
        mi.getUniqueName());
    event.addProperty(PropertyCollection.INSTRUCTION_FILE_LOCATION,
        insn.getFileLocation());
    event.addProperty(PropertyCollection.EVENT_TYPE, type.ordinal());
    return event;
  }

  /**
   * Creates cg event with mandatory properties.
   * 
   * @param vm
   *          JVM instance.
   * @param type
   *          Cg event type (choiceGeneratorSet, choiceGeneratorAdvanced,
   *          choiceGeneratorProcessed, choiceGeneratorRegistered).
   */
  protected Event createCGEvent(JVM vm, EventTypes.eventType type) {
    Event event = traceStorerFactory.createEvent();
    event.addProperty(PropertyCollection.CHOICE_GENERATOR_TYPE, vm
        .getLastChoiceGenerator().getChoiceType().getName());
    event.addProperty(PropertyCollection.CHOICE_GENERATOR_CHOICE, vm
        .getLastChoiceGenerator().getNextChoice());
    event.addProperty(PropertyCollection.EVENT_TYPE, type.ordinal());
    return event;
  }

  /**
   * Creates exception event with mandatory properties.
   * 
   * @param vm
   *          JVM instance.
   * @param type
   *          Exception event type (exceptionBailout, exceptionThrown,
   *          exceptionHandled).
   */
  protected Event createExceptionEvent(JVM vm, EventTypes.eventType type) {
    Event event = traceStorerFactory.createEvent();
    event.addProperty(PropertyCollection.EXCEPTION_TYPE, vm.getException()
        .getClass().getName());
    event.addProperty(PropertyCollection.EXCEPTION_MESSAGE, vm.getException()
        .getMessage());
    event.addProperty(PropertyCollection.EVENT_TYPE, type.ordinal());
    return event;
  }

  /**
   * Creates method event with mandatory properties.
   * 
   * @param vm
   *          JVM instance.
   * @param type
   *          Method event type (methodEntered or methodExited).
   */
  protected Event createMethodEvent(JVM vm, EventTypes.eventType type) {
    MethodInfo mi = vm.getLastMethodInfo();
    Event event = traceStorerFactory.createEvent();
    event
        .addProperty(PropertyCollection.METHOD_UNIQUE_NAME, mi.getUniqueName());
    event.addProperty(PropertyCollection.CLASS_NAME, mi.getClassName());
    event.addProperty(PropertyCollection.EVENT_TYPE, type.ordinal());
    return event;
  }

}
