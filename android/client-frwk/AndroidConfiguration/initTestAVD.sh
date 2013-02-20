#!/bin/bash
# Initialise current AVD to work with AndroidAgent 

testParams() {
        if [ $1 -eq 1 ]
        then
                return 0
        else
                if [ $1 -eq 0 ]
                then
                        echo "AVD/Device serial number must be specified"
			echo "Use adb devices to list AVDs/devices and their serial numbers"
                else
                        echo "Too many parameters"
                fi
                return 1
        fi

}

configAVD() {
	echo ""
	echo "Remount AVD to allow hosts file to be modified on $1"
	echo "----------------------------------------------"
	adb -s $1 remount

	echo ""
	echo "Push the hosts file  on $1"
	echo "-------------------"
	adb -s $1 push hosts /etc/hosts
	echo ""
	echo "Contents of AVD hosts file on $1"
	echo "--------------------------"
 	
	adb -s $1 shell cat /etc/hosts
#	echo ""
#	echo "Push customised AndroidAgent XMPP properties on $1"
#	echo "--------------------------------------------"

#	adb -s $1 push AndroidAgent.properties /sdcard/Android/data/org.societies/files/AndroidAgent.properties
#	echo ""	
#	echo "Contents of customised AndroidAgent XMPP properties on $1"
#	echo "---------------------------------------------------"
#	adb -s $1 shell cat /sdcard/Android/data/org.societies/files/AndroidAgent.properties
}

testParams $#
if [ $?  -eq 0 ]
 then
 configAVD $1
fi
exit 0
