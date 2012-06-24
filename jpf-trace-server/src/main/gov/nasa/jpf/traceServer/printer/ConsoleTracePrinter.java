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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.Error;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.Path;
import gov.nasa.jpf.jvm.Transition;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.report.Statistics;
import gov.nasa.jpf.traceEmitter.PropertyCollection;
import gov.nasa.jpf.util.RepositoryEntry;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Publisher like <code>gov.nasa.jpf.report.ConsolePublisher</code>, but adapted
 * to print the new trace. The trace output is fixed, but can be augmented with
 * extra information if events from trace contain
 * {@link PropertyCollection#TRACE_EXTRA_DATA} property.
 * 
 * @author Igor Andjelkovic
 * 
 */
public class ConsoleTracePrinter extends Publisher {

  /**
   * Printer used to print the <code>trace</code> when {@link #publishTrace()}
   * is called.
   */
  protected OldTracePrinter oldTracePrinter;
  protected FileOutputStream fos;
  protected String fileName;
  protected String port;

  /**
   * Configures publisher and {@link #oldTracePrinter}, by using
   * <code>Config</code> instance.
   */
  public ConsoleTracePrinter(Config conf, Reporter reporter) {
    super(conf, reporter);

    // options controlling the output destination
    fileName = conf.getString("report." + getName() + ".file");
    port = conf.getString("report." + getName() + ".port");

    // options controlling what info should be included in a trace
    oldTracePrinter = new OldTracePrinter();
    oldTracePrinter.setShowSteps(conf.getBoolean("report." + getName()
        + ".show_steps", true));
    oldTracePrinter.setShowLocation(conf.getBoolean("report." + getName()
        + ".show_location", true));
    oldTracePrinter.setShowSource(conf.getBoolean("report." + getName()
        + ".show_source", true));
    oldTracePrinter.setShowMethod(conf.getBoolean("report." + getName()
        + ".show_method", false));
    oldTracePrinter.setShowCode(conf.getBoolean("report." + getName()
        + ".show_code", false));
    oldTracePrinter.setShowExtraData(conf.getBoolean("report." + getName()
        + ".show_extra_data", true));
    oldTracePrinter.setShowCG(conf.getBoolean("report." + getName()
        + ".show_cg", true));
    oldTracePrinter.setShowAPICalls(conf.getBoolean("report." + getName()
        + ".show_api_calls", true));

    oldTracePrinter.setQueryName(conf.getString("traceServer.trace_storer",
        "inMemory"));
  }

  public String getName() {
    return "consoleTracePrinter";
  }

  protected void openChannel() {

    if (fileName != null) {
      try {
        fos = new FileOutputStream(fileName);
        out = new PrintWriter(fos);
      } catch (FileNotFoundException x) {
        // fall back to System.out
      }
    } else if (port != null) {
      // <2do>
    }

    if (out == null) {
      out = new PrintWriter(System.out, true);
    }
  }

  protected void closeChannel() {
    if (fos != null) {
      out.close();
    }
  }

  public void publishTopicStart(String topic) {
    out.println();
    out.print("====================================================== ");
    out.println(topic);
  }

  public void publishTopicEnd(String topic) {
    // nothing here
  }

  public void publishStart() {
    super.publishStart();

    if (startTopics.length > 0) { // only report if we have output for this
                                  // phase
      publishTopicStart("search started: " + formatDTG(reporter.getStartDate()));
    }
  }

  public void publishFinished() {
    super.publishFinished();

    if (finishedTopics.length > 0) { // only report if we have output for this
                                     // phase
      publishTopicStart("search finished: "
          + formatDTG(reporter.getFinishedDate()));
    }
  }

  protected void publishJPF() {
    out.println(reporter.getJPFBanner());
    out.println();
  }

  protected void publishDTG() {
    out.println("started: " + reporter.getStartDate());
  }

  protected void publishUser() {
    out.println("user: " + reporter.getUser());
  }

  protected void publishJPFConfig() {
    publishTopicStart("JPF configuration");

    TreeMap<Object, Object> map = conf.asOrderedMap();
    Set<Map.Entry<Object, Object>> eSet = map.entrySet();

    for (Object src : conf.getSources()) {
      out.print("property source: ");
      out.println(conf.getSourceName(src));
    }

    out.println("properties:");
    for (Map.Entry<Object, Object> e : eSet) {
      out.println("  " + e.getKey() + "=" + e.getValue());
    }
  }

  protected void publishPlatform() {
    publishTopicStart("platform");
    out.println("hostname: " + reporter.getHostName());
    out.println("arch: " + reporter.getArch());
    out.println("os: " + reporter.getOS());
    out.println("java: " + reporter.getJava());
  }

  protected void publishSuT() {
    publishTopicStart("system under test");

    String mainCls = conf.getTarget();
    if (mainCls != null) {
      String mainPath = reporter.getSuT();
      if (mainPath != null) {
        out.println("application: " + mainPath);

        RepositoryEntry rep = RepositoryEntry.getRepositoryEntry(mainPath);
        if (rep != null) {
          out.println("repository: " + rep.getRepository());
          out.println("revision: " + rep.getRevision());
        }
      } else {
        out.println("application: " + mainCls + ".class");
      }
    } else {
      out.println("application: ?");
    }

    String[] args = conf.getTargetArgs();
    if (args.length > 0) {
      out.print("arguments:   ");
      for (String s : args) {
        out.print(s);
        out.print(' ');
      }
      out.println();
    }
  }

  protected void publishError() {
    Error e = reporter.getLastError();

    publishTopicStart("error " + reporter.getLastErrorId());
    out.println(e.getDescription());

    String s = e.getDetails();
    if (s != null) {
      out.println(s);
    }

  }

  protected void publishConstraint() {
    String constraint = reporter.getLastSearchConstraint();
    publishTopicStart("search constraint");
    out.println(constraint); // not much info here yet
  }

  protected void publishResult() {
    List<Error> errors = reporter.getErrors();

    publishTopicStart("results");

    if (errors.isEmpty()) {
      out.println("no errors detected");
    } else {
      for (Error e : errors) {
        out.print("error #");
        out.print(e.getId());
        out.print(": ");
        out.print(e.getDescription());

        String s = e.getDetails();
        if (s != null) {
          s = s.replace('\n', ' ');
          s = s.replace('\t', ' ');
          s = s.replace('\r', ' ');
          out.print(" \"");
          if (s.length() > 50) {
            out.print(s.substring(0, 50));
            out.print("...");
          } else {
            out.print(s);
          }
          out.print('"');
        }

        out.println();
      }
    }
  }

  /**
   * This is done as part of the property violation reporting, i.e. we have an
   * error.
   */

  protected void publishTrace() {
    publishTopicStart("trace " + reporter.getLastErrorId());
    oldTracePrinter.print(out);
  }

  protected void publishOutput() {
    Path path = reporter.getPath();

    if (path.size() == 0) {
      return; // nothing to publish
    }

    publishTopicStart("output " + reporter.getLastErrorId());

    if (path.hasOutput()) {
      for (Transition t : path) {
        String s = t.getOutput();
        if (s != null) {
          out.print(s);
        }
      }
    } else {
      out.println("no output");
    }
  }

  protected void publishSnapshot() {
    JVM vm = reporter.getVM();

    // not so nice - we have to delegate this since it's using a lot of
    // internals, and is also
    // used in debugging
    publishTopicStart("snapshot " + reporter.getLastErrorId());

    if (vm.getPathLength() > 0) {
      vm.printLiveThreadStatus(out);
    } else {
      out.println("initial program state");
    }
  }

  protected void publishStatistics() {
    Statistics stat = reporter.getStatistics();
    publishTopicStart("statistics");
    out.println("elapsed time:       " + formatHMS(reporter.getElapsedTime()));
    out.println("states:             new=" + stat.newStates + ", visited=" + stat.visitedStates
            + ", backtracked=" + stat.backtracked + ", end=" + stat.endStates);
    out.println("search:             maxDepth=" + stat.maxDepth + ", constraints hit=" + stat.constraints);
    out.println("choice generators:  thread=" + stat.threadCGs
            + " (signal=" + stat.signalCGs + ", lock=" + stat.monitorCGs + ", shared ref=" + stat.sharedAccessCGs
            + "), data=" + stat.dataCGs);
    out.println("heap:               " + "new=" + stat.nNewObjects
            + ", released=" + stat.nReleasedObjects
            + ", max live=" + stat.maxLiveObjects
            + ", gc-cycles=" + stat.gcCycles);
    out.println("instructions:       " + stat.insns);
    out.println("max memory:         " + (stat.maxUsed >> 20) + "MB");

    out.println("loaded code:        classes=" + ClassInfo.getNumberOfLoadedClasses() + ", methods="
            + MethodInfo.getNumberOfLoadedMethods());
  }

}
