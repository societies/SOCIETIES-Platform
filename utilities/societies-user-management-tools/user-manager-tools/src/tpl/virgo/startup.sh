#!/bin/sh
echo '#############################'
echo 'Start {{Parameter.userName}} node'
echo '#############################'
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
$KERNEL_HOME/startup.sh -clean -configDir $KERNEL_HOME/../{{Parameter.configFolderNameShort}} -jmxport {{Parameter.openfirePortNumber}}
