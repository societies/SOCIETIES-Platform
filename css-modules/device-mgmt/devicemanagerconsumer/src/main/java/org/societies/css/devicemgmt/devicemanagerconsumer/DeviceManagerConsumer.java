package org.societies.css.devicemgmt.devicemanagerconsumer;

import java.util.Dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDeviceService;




public class DeviceManagerConsumer {

	private IDevice deviceService;
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceManagerConsumer.class);
	
	public DeviceManagerConsumer() {

		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device manager consumer constructor");
	}
	
	public void setDeviceService(IDevice deviceService)
	{
		//LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% deviceService pre injection ");
		
		this.deviceService=deviceService;
		
		
		//LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% deviceService post injection ");
		
		
		IDeviceService ds = deviceService.getService("lightSensor1");
		
		//LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post deviceService.getService ");
		
		IAction ia = ds.getAction("getLightLevel");
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post ds.getAction ");
		
		Dictionary<String, String> dic = ia.invokeAction(null);
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post ia.invokeAction ");

		LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Action Name is: "+ ia.getName());
		LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Action Return is: "+ dic.get("lightLevel")); 
	}

}