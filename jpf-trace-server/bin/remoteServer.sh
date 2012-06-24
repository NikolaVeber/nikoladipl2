#!/bin/sh

JPF_HOME=".."
JPF_CORE="../../jpf-core/build"

CLASSPATH="${JPF_HOME}/build/main:${JPF_HOME}/lib/neo4j-kernel-1.0.jar:${JPF_HOME}/lib/geronimo-jta_1.1_spec-1.1.1.jar:${JPF_CORE}/jpf.jar"

if [ "`uname | grep -i 'CYGWIN'`" != "" ]
then
  CLASSPATH="`echo ${CLASSPATH} | sed -e 's/:/\\;/g' -e 's,/,\\\\,g'`"
fi

java -classpath "${CLASSPATH}" gov.nasa.jpf.traceServer.traceStorer.remote.Server \
+traceServer.port=4444 \
+traceServer.trace_storer=inMemory \
+traceServer.trace_query=inMemory \
+traceServer.db_location=../dbTrace \
+traceServer.trace_analyzer=gov.nasa.jpf.traceAnalyzer.DeadlockAnalyzer,gov.nasa.jpf.traceAnalyzer.GenericOutputDeadlockAnalyzer \
+traceServer.trace_analyzer.gov.nasa.jpf.traceAnalyzer.DeadlockAnalyzer.params=essential \
traceServer.trace_analyzer.gov.nasa.jpf.traceAnalyzer.GenericOutputDeadlockAnalyzer.params=essential
