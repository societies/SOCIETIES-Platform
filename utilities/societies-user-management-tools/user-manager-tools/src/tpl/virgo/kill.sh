#!/bin/sh
echo '#############################'
echo 'Try to kill {{Parameter.userName}} node'
echo '#############################'

PORT_NUMBER='{{Parameter.openfirePortNumber}}'
if [ $# -ge 1 ]
then
	PORT_NUMBER=$1
fi

#taskkill /F /PID `netstat -o -n -a -b | findstr 0.0:$PORT_NUMBER | awk '{print $5}'`
#kill -9 `netstat -o -n -a -b | findstr 0.0:$PORT_NUMBER | awk '{print $5}'`
kill -9 `netstat -o -n -a -b | findstr 0.0:$PORT_NUMBER | awk '{print $5}'`