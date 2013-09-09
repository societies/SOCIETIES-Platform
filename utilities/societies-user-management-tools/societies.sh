#!/bin/bash

SCRIPT="$0"

# SCRIPT may be an arbitrarily deep series of symlinks. Loop until we have the concrete path.
while [ -h "$SCRIPT" ] ; do
  ls=`ls -ld "$SCRIPT"`
  # Drop everything prior to ->
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT="$link"
  else
    SCRIPT=`dirname "$SCRIPT"`/"$link"
  fi
done

# determine kernel home
KERNEL_HOME=`dirname "$SCRIPT"`

# make KERNEL_HOME absolute
KERNEL_HOME=`cd $KERNEL_HOME; pwd`
KERNEL_HOME=$KERNEL_HOME/../virgo

# First param is the action. Rest are parameters - shift done in subroutines
ACTION=$1
shift;
DOACTION="0"
HELP_PARAM_NAMES=""
COMMAND=""
COMMAND_EXTENSION="sh"

# Launch the relevant action
if [ "$ACTION" = "start" ]
then
 DOACTION="1"
 COMMAND="startup"
 HELP_PARAM_NAMES="'container number' 'port number'"
elif [ "$ACTION" = "isstarted" ]
then
 DOACTION="1"
 COMMAND="isstarted"
 HELP_PARAM_NAMES="'container numbers' 'first port number'"
elif [ "$ACTION" = "stop" ]
then
 DOACTION="1"
 COMMAND="shutdown"
 HELP_PARAM_NAMES="'container numbers' 'first port number'"
elif [ "$ACTION" = "kill" ]
then
 DOACTION="1"
 COMMAND="kill"
 HELP_PARAM_NAMES="'container numbers' 'first port number'"
fi

######### Do action
if [ "$DOACTION" = "1" ]
then
 ## Param not available
 # Node number
 NODE_NUMBER=$1
 # Port number
 PORT_NUMBER=$2
 if [ "$NODE_NUMBER" = "" ]
 then
  echo "Bad command syntax"
  echo "Usage: $COMMAND $HELP_PARAM_NAMES"
  echo ""
  echo "'container number' is the container number. The first is '0'"
  echo "'port number' Openfire port number of the container."
  echo "E.g. 'start 0' will start the first container."
  echo "E.g. 'kill 0' will stop the first container"
  exit 0
 fi
 if [ "$PORT_NUMBER" = "" ]
 then
  echo $KERNEL_HOME/config$NODE_NUMBER/$COMMAND$NODE_NUMBER.$COMMAND_EXTENSION
  $KERNEL_HOME/config$NODE_NUMBER/$COMMAND$NODE_NUMBER.$COMMAND_EXTENSION
 else
 if [ "$COMMAND" = "startup" ]
 then
  $KERNEL_HOME/$COMMAND.$COMMAND_EXTENSION -clean -configDir $KERNEL_HOME/../config$NODE_NUMBER/ -jmxport $PORT_NUMBER
 elif [ "$COMMAND" = "shutdown" ]
 then
  $KERNEL_HOME/$COMMAND.$COMMAND_EXTENSION -configDir $KERNEL_HOME/../config$NODE_NUMBER/ -jmxport $PORT_NUMBER
 elif [ "$COMMAND" = "kill" ]
 then
  kill -9 `netstat -o -n -a -b | findstr 0.0:$PORT_NUMBER | awk '{print $5}'`
  elif [ "$COMMAND" = "isstarted" ]
 then
  # TODO: real condition
  if [ "netstat -o -n -a -b | findstr 0.0:$PORT_NUMBER" != "" ]
  then
   echo "1"
  else
   echo "0"
  fi
 fi
 fi
################### NO action
else
 if [ "$ACTION" != "" ]
 then
  echo "Unknown action: ${ACTION}"
  echo ""
 fi
 echo '### Societies Launcher Tools'
 echo '# Action list'
 echo '- isstarted  To know if a container is started or not.'
 echo '- start      To start a container.'
 echo '- stop       To stop one or several containers. A kill is often necessary.'
 echo '- kill       To stop abruptely one or several containers.'
fi
