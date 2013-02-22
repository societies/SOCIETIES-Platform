#!/bin/bash
# Copy Virgo code and plans to another a testbed node
# Ensure that $VIRGO_HOME refers to correct location, i.e. Virgo top directory

testParams() {
        if [ $1 -eq 1 ]
        then
                return 0
        else
                if [ $1 -eq 0 ]
                then
                        echo "Specify ssh target e.g. <user>@<node address>"
                else
                        echo "Too many parameters"
                fi
                return 1
        fi
}


transferFiles() {
	
        echo ""
        echo "Tranfer files from $VIRGO_HOME to $1"
        echo "------------------------------------"

        #echo ""
        #echo "Transfer Virgo configuration files to $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/config"
        #echo "--------------------------------------------------------------------------------------------"

       
        #scp  $VIRGO_HOME/config/xc.properties $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/config
        #scp  $VIRGO_HOME/config/org.societies.platform.properties $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/config

        echo ""
        echo "Transfer Virgo Societies plans to $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/repository/usr"
        echo "------------------------------------------------------------------------------------------------"

        scp $VIRGO_HOME/repository/usr/*.plan $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/repository/usr

        echo ""
        echo "Transfer Virgo Societies bundles to $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/repository/usr"
        echo "--------------------------------------------------------------------------------------------------"
        

        scp $VIRGO_HOME/repository/usr/*.jar $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/repository/usr
        scp $VIRGO_HOME/repository/usr/*.war $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/repository/usr

        echo ""
        echo "Update Virgo startup plan to $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/pickup"
        echo "--------------------------------------------------------------------------------------------------"


        scp $VIRGO_HOME/repository/usr/org.societies.platform.plan $1:/usr/local/virgo-tomcat-server-3.0.1.RELEASE/pickup

}

testParams $#
if [ $?  -eq 0 ]
 then
  transferFiles $1 $2
fi


exit 0
