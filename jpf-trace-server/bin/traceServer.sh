#!/bin/sh

JPF_HOME="../../jpf-core/build/RunJPF.jar"

JVM_FLAGS="-Xmx1024m -ea"

java $JVM_FLAGS -jar "$JPF_HOME" $@
