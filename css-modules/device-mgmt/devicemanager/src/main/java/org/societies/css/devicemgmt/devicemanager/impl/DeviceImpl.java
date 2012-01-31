package org.societies.css.devicemgmt.devicemanager.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.css.devicemgmt.devicemanager.IDevice;



public class DeviceImpl implements IDevice{
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceManager.class);
	private String deviceId;
	private DeviceManager deviceManager;
	private ServiceRegistration registration;
	private BundleContext bundleContext;
	private Dictionary<String, String> properties;

	public DeviceImpl(BundleContext bc, DeviceManager deviceMgr, String deviceId) {
		
		this.bundleContext = bc;
		this.deviceManager = deviceMgr;
		this.deviceId = deviceId;
		//this.properties = properties;
		
		properties = new Hashtable<String, String>();
		
		properties.put("deviceId", deviceId);
		
		Object lock = new Object();

		synchronized(lock)
		{
			registration = bundleContext.registerService(IDevice.class.getName(), this, properties);
			
			LOG.info("-- A device service" + properties.get("deviceId") + " has been registred"); 
		}
		
	}
	
	public void removeDevice()
	{
		if (registration != null)
		{
			registration.unregister();
			deviceManager.removeDeviceFromTable(deviceId);
			
			LOG.info("-- The device " + properties.get("deviceId") + " has been removed");
		}
	}

	public String getDeviceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeviceId() {
		// TODO Auto-generated method stub
		return deviceId;
	}

	public String getDeviceType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeviceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeviceConnetionType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void enable() {
		// TODO Auto-generated method stub
		
	}

	public void disable() {
		// TODO Auto-generated method stub
		
	}

	public boolean isEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDeviceLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeviceProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isContextCompliant() {
		// TODO Auto-generated method stub
		return false;
	}

}
