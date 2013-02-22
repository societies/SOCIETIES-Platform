#!/bin/bash
# Copy relevant files to another CSS node

testParams() {
        if [ $1 -eq 2 ]
        then
                return 0
        else
                if [ $1 -lt 2 ]
                then
                        echo "ssh target and target home must be specified"
                else 
		     if [ $1 -gt 2 ]
			 then
                        echo "Too many parameters"
 		     fi
                fi
                return 1
        fi
}


transferFiles() {
	
        echo ""
        echo "Tranfer files to $1"
        echo "----------------------------------------------"

        echo ""
        echo "Transfer Virgo configuration files to $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/config"
        echo "-------------------"

       
        scp  config/xc.properties $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/config
        scp  config/serviceability.xml $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/config
        scp  config/org.societies.platform.properties $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/config

        echo ""
        echo "Transfer Virgo Societies plans to $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/repository/usr"
        echo "-------------------"

        scp repository/usr/*.plan $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/repository/usr

        echo ""
        echo "Transfer Virgo Societies bundles to $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/repository/usr"
        echo "-------------------"
        

        scp repository/usr/*.jar $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/repository/usr
        scp repository/usr/*.war $1:$2/local/dev/virgo/virgo-tomcat-server-3.0.3.RELEASE/repository/usr

}

testParams $#
if [ $?  -eq 0 ]
 then
  transferFiles $1 $2
fi


exit 0
