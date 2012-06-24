#!/bin/sh

JPF_HOME=".."
JPF_CORE="../../jpf-core/build/jpf.jar"
NEO_KERNEL="${JPF_HOME}/lib/neo4j-kernel-1.0.jar"
NEO_GERONIMO="${JPF_HOME}/lib/geronimo-jta_1.1_spec-1.1.1.jar"

CLASSPATH="${JPF_HOME}/build/main:${NEO_KERNEL}:${NEO_GERONIMO}:${JPF_CORE}"

if [ "`uname | grep -i 'CYGWIN'`" != "" ]
then
  CLASSPATH="`echo ${CLASSPATH} | sed -e 's/:/\\;/g' -e 's,/,\\\\,g'`"
fi

java -classpath "${CLASSPATH}" gov.nasa.jpf.traceAnalyzer.GenericOutputExecTrackerAnalyzer dbTrace $@
