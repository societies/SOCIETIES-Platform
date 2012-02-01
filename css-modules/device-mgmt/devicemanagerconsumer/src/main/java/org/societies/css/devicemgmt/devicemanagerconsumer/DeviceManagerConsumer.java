package org.societies.css.devicemgmt.devicemanagerconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.css.devicemgmt.devicemanager.IDevice;




public class DeviceManagerConsumer {

	private IDevice deviceService;
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceManagerConsumer.class);
	
	public DeviceManagerConsumer() {

		LOG.info("DeviceMgmtConsumer: " + "============++++++++++------ Device manager consumer constructor");
	}
	
	public void setDeviceService(IDevice deviceService)
	{
		this.deviceService=deviceService;
		
		

		LOG.info("DeviceMgmtConsumer: " + "================++++++++++------ Device Id: "+deviceService.getDeviceId());
	}

}