#!/bin/bash

[ -z "$ICS_HOME" ] && ICS_HOME=`pwd`

[ ! -z "$ICS_HOME/conf" ] && CLASSPATH=$CLASSPATH:$ICS_HOME/conf

for file in $ICS_HOME/lib/*.jar
do
    [ -f $file ] && CLASSPATH=$CLASSPATH:$file
done

CLASSNAME=jetty.MyServer

JAVA_OPTS="-Djava.library.path=$ICS_HOME/conf -Xmx2048m -Xmn512m -verbose:gc"

RUNJAVA="java $JAVA_OPTS -cp $CLASSPATH $CLASSNAME"

while [ 1 ] ; do
    PROCESS_NUM=`ps -ef | grep $ICS_HOME/conf | grep -v grep | wc -l`
    [ $PROCESS_NUM -eq 0 ] && nohup $RUNJAVA > /dev/null &
    sleep 10
done