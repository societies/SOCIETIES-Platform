package org.societies.css.devicemgmt.devicemanager.impl;


import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.css.devicemgmt.devicemanager.ControllerWs;

import org.springframework.osgi.context.BundleContextAware;



public class DeviceManager implements ControllerWs, BundleContextAware{

	private static Logger LOG = LoggerFactory.getLogger(DeviceManager.class);

	private final Map<String, DeviceImpl> deviceInstanceContainer;
	
	private DeviceImpl deviceImpl;
	
	private BundleContext bundleContext;
	
	public DeviceManager() {
		
		deviceInstanceContainer = new HashMap<String, DeviceImpl>();

		LOG.info("DeviceMgmt: " + "=========++++++++++------ DeviceManager constructor");
	}
	
	public void setBundleContext(BundleContext bundleContext) {
		
		this.bundleContext = bundleContext;	
	}
	
	
	protected Map<String, DeviceImpl> getDeviceInstanceContainer() 
	{	
		return deviceInstanceContainer;
	}
	protected void setDeviceInstanceContainer(String deviceId, DeviceImpl deviceInstance) 
	{
		this.deviceInstanceContainer.put(deviceId, deviceInstance);
	}

	public void removeDeviceFromTable (String deviceId)
	{
		if (getDeviceInstanceContainer().get(deviceId) != null)
		{
			getDeviceInstanceContainer().remove(deviceId);
		}
	}
	
	
	public void regiterNewService(String deviceId) {
		
		LOG.info("DeviceMgmt: " + "****************************************************** Hi, I'm a web service : " + deviceId);
		
		
		deviceImpl = new DeviceImpl(bundleContext, this, deviceId);
		
	}
	
	
	
	public void removeDevice(String deviceId) 
	{	
		if (getDeviceInstanceContainer().get(deviceId) != null)
		{
			getDeviceInstanceContainer().get(deviceId).removeDevice();
		}
	}
}





