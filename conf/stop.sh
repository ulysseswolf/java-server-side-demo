#!/bin/bash

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

cd $PRGDIR

[ -z "$ICS_HOME" ] && ICS_HOME=`pwd`

ICS_NUM=`ps -ef | grep $ICS_HOME/conf | grep -v grep | awk '{print $2}'`
CHECK_NUM=`ps -ef | grep $ICS_HOME/procics.sh | grep -v grep | awk '{print $2}'`

[ -n "$ICS_NUM" ] && kill -9 $ICS_NUM
[ -n "$CHECK_NUM" ] && kill -9 $CHECK_NUM
