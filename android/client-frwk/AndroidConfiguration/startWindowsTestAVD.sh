#!/bin/bash
# Start current AVD 

testParams() {
	if [ $1 -eq 2 ]
	then
 		return 0 
	else
  		if [ $1 -eq 0 ]
    		then
      			echo "AVD name must be specified and DNS server"
    		else
			if [ $1 -eq 1 ]
    			  then
      				echo "DNS server must be specified"
    			  else
      				echo "Too many parameters"
  			fi
   		fi
		return 1
	fi
	
}

startAVD() {
	echo ""
	echo "Start the test emulator"
	echo "AVD : $1"
	echo "DNS Server: $2"

	echo "-----------------------"
	emulator -avd $1 -partition-size 256 -dns-server $2 

	if [ $? -ne 0 ]
 	then
		echo -e "\nIncorrect AVD name"
		return 1
        fi

}

testParams $#
if [ $?  -eq 0 ] 
 then
 startAVD $1 $2	
 if [ $? -ne 0 ]
  then
	exit 1
 fi
fi


exit 0
