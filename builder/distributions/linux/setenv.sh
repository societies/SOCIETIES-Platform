#!/bin/bash
#increase the PermGen size to allow the Virgo Societies platform to deploy and work correctly
export JAVA_OPTS=-XX:MaxPermSize=128m
echo $JAVA_OPTS
