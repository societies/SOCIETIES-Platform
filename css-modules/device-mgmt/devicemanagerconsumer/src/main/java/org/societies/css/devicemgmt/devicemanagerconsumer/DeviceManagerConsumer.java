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
	
	public IDevice getDeviceService()
	{
		return deviceService;
	}
	
	public void setDeviceService(IDevice deviceService)
	{
		//LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% deviceService pre injection ");
		
		this.deviceService=deviceService;

	}
	
	public void initConsumer()
	{
		IDeviceService ds = getDeviceService().getService("lightSensor1");
				
				
		IAction ia = ds.getAction("getLightLevel");
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post ds.getAction ");
				
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post ia.invokeAction ");
		LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Action Name is: "+ ia.getName());
				
		int a = 1, b=2;
		while (a<2) 
		{
			try 
			{

				Dictionary dic = ia.invokeAction(null);
				LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Action Return is: "+ dic.get("outputLightLevel")); 
				Thread.sleep(4000);
			} 
			catch (InterruptedException e) 
			{
						
				e.printStackTrace();
			}
				
		}
	}

}