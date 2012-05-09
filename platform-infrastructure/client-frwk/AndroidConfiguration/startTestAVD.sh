#!/bin/bash
# Start curr ent AVD 

testOS() {
	osName=`uname -o`

	echo "OS is: $osName"

	if [ $osName = "Cygwin" ]
	   then
		echo "Do not use this script on Windows"
		return 1
           else
		return 0 
	fi
}
testParams() {
	if [ $1 -eq 1 ]
	then
 		return 0 
	else
  		if [ $1 -eq 0 ]
    		then
      			echo "AVD name must be specified"
    		else
      			echo "Too many parameters"
  		fi
		return 1
	fi
	
}

startAVD() {
	echo ""
	echo "Start the test emulator: $1"
	echo "-----------------------"
	emulator -avd $1 -partition-size 256 

	if [ $? -ne 0 ]
 	then
		echo -e "\nIncorrect AVD name"
		return 1
        fi

}

testOS

if [ $?  -eq 1 ]
  then
   exit 1
fi 

testParams $#
if [ $?  -eq 0 ] 
 then
 startAVD $1	
 if [ $? -ne 0 ]
  then
	exit 1
 fi
fi


exit 0
