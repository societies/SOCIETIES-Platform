package org.societies.css.devicemgmt.devicemanagerconsumer;

import java.util.Dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;




public class DeviceManagerConsumer {

	private IDevice deviceService;
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceManagerConsumer.class);
	
	public DeviceManagerConsumer() {

		LOG.info("DeviceMgmtConsumer: " + "============++++++++++------ Device manager consumer constructor");
	}
	
	public void setDeviceService(IDevice deviceService)
	{
		this.deviceService=deviceService;
		
		IAction ia = deviceService.getAction("getLightLevel");
		
		
		Dictionary<String, String> dic = ia.invokeAction(null);

		LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Action Name is: "+ ia.getName());
		LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Action Return is: "+ dic.get("lightLevel")); 
	}

}