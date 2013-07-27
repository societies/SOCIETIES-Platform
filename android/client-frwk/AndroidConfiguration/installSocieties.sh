#!/bin/bash
# Install the required Societies APKs on the specified AVD/Device

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

installSocietiesAPK() {
	echo ""
	echo "Install SocietiesComms on $1"
	echo "----------------------------------------------"
	adb -s $1 install -r ../SocietiesAndroidCommsApp/target/SocietiesAndroidCommsApp.apk
 
	
	echo ""
	echo "Install SocietiesAndroidApp on $1"
	echo "--------------------------"
 	adb -s $1 install -r ../SocietiesAndroidApp/target/SocietiesAndroidApp.apk

        #echo ""
        #echo "Install SocietiesLoginTester on $1"
        #echo "--------------------------"
        #adb -s $1 install -r ../SocietiesLibraries/SocietiesLoginTesterApp/target/SocietiesLoginTesterApp.apk

}

testParams $#
if [ $?  -eq 0 ]
 then
  installSocietiesAPK $1
fi
exit 0
