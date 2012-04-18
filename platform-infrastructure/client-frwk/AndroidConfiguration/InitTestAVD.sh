#!/bin/bash
# Initialise current AVD to work with AndroidAgent 
echo ""
echo "Remount AVD to allow hosts file to be modified"
echo "----------------------------------------------"
adb remount

echo ""
echo "Push the hosts file"
echo "-------------------"
adb push hosts /etc/hosts
echo ""
echo "Contents of AVD hosts file"
echo "--------------------------"
 
adb shell cat /etc/hosts
echo ""
echo "Push customised AndroidAgent XMPP properties"
echo "--------------------------------------------"

adb push AndroidAgent.properties /sdcard/Android/data/org.societies/files/AndroidAgent.properties
echo ""
echo "Contents of customised AndroidAgent XMPP properties"
echo "---------------------------------------------------"
adb shell cat /sdcard/Android/data/org.societies/files/AndroidAgent.properties

exit
