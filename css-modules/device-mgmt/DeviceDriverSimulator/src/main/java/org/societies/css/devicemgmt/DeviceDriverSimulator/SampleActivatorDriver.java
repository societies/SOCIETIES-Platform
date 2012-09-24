/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.internal.css.devicemgmt.model.DeviceMgmtDriverServiceConstants;
import org.societies.api.osgi.event.IEventMgr;
import org.springframework.osgi.context.BundleContextAware;


public class SampleActivatorDriver implements BundleContextAware{
	/**Bundle Context */
	private BundleContext bc;
	
	private IDeviceManager deviceManager;
	private IEventMgr eventManager;
	
	/* sensors */
	private LightSensor ls;
	private LightSensor ls2;
	private LightSensor ls3;
	private Screen screen;
	
	private String physicalDeviceId1 = "3b:6d:01";
	private String physicalDeviceId2 = "3b:6d:02";
	private String physicalDeviceId3 = "3b:6d:03";
	
	private String screenPhysicalDeviceId = "23:75:01";
	
	private DeviceCommonInfo deviceCommonInfo1;
	private DeviceCommonInfo deviceCommonInfo2;
	private DeviceCommonInfo deviceCommonInfo3;
	
	private DeviceCommonInfo screenCommonInfo;
	
	private String [] lightDriverServiceNamesList = {DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE};
	
	private String [] screenDriverServiceNamesList = {DeviceMgmtDriverServiceNames.SCREEN_DRIVER_SERVICE};
	
	private Dictionary<String, String> properties;
	/** Service registration */

    
    private ServiceRegistration lsReg;
    private ServiceRegistration lsReg2;
    private ServiceRegistration lsReg3;
    
    private ServiceRegistration screenReg;


    private int lightSensorCount = 1;
   

    private static Logger LOG = LoggerFactory.getLogger(SampleActivatorDriver.class);
  

    
	public void setBundleContext(BundleContext bc) {
		this.bc =  bc;
		
	}
	
	
	/* --- Injections --- */
	public IEventMgr getEventManager()
	{ 
		return eventManager; 
	}
	
	public void setEventManager(IEventMgr eventManager) { 
		if (null == eventManager) {
			LOG.error("[COMM02] EventManager not available");
		}
		this.eventManager = eventManager;
	}
	
	
	
	public void setDeviceManager (IDeviceManager deviceManager)
	{
		this.deviceManager = deviceManager;
	}
	
	public void initSimul()
	{
		startSimul();
	}
	
	
	/*
	 * starts each actuator/sensor
	 */
	public void startSimul() 
	{
		deviceCommonInfo1 = new DeviceCommonInfo("org.societies.DeviceDriverSimulator", "Light Sensor", DeviceTypeConstants.LIGHT_SENSOR, "Light Sensor 1 test", "Zigbee", "Room1", "Trialog", null, true);
		deviceCommonInfo2 = new DeviceCommonInfo("org.societies.DeviceDriverSimulator", "Light Sensor", DeviceTypeConstants.LIGHT_SENSOR, "Light Sensor 2 test", "Zigbee", "Room2", "Trialog", null, true);
		deviceCommonInfo3 = new DeviceCommonInfo("org.societies.DeviceDriverSimulator", "Light Sensor", DeviceTypeConstants.LIGHT_SENSOR, "Light Sensor 3 test", "Zigbee", "Room3", "Trialog", null, true);

		screenCommonInfo = new DeviceCommonInfo("org.societies.DeviceDriverSimulator", "Sony Screen", DeviceTypeConstants.SCREEN, "Screen display test", "HDMI", "Corridor1", "Trialog", null, false);
		/* creation of sensors */
		
		ls = new LightSensor(this, DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE, physicalDeviceId1, lightSensorCount);
		lightSensorCount++;
		ls2 = new LightSensor(this, DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE, physicalDeviceId2, lightSensorCount);
		lightSensorCount++;
		ls3 = new LightSensor(this, DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE, physicalDeviceId3, lightSensorCount);
		lightSensorCount++;

		screen = new Screen(this, DeviceMgmtDriverServiceNames.SCREEN_DRIVER_SERVICE, screenPhysicalDeviceId);
			
		Object lock = new Object();			

		synchronized(lock) 
		{
			LOG.info("DeviceDriverExample: " + "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii registration process");
			
			properties = new Hashtable<String, String>();
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME, 
					DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, 
					physicalDeviceId1);
			//properties.put("deviceId", lsDeviceId1);
			lsReg = bc.registerService(IDriverService.class.getName(), ls, properties);
			
			properties = new Hashtable<String, String>();
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME, 
					DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, 
					physicalDeviceId2);
			//properties.put("deviceId", lsDeviceId2);
			lsReg2 = bc.registerService(IDriverService.class.getName(), ls2, properties);
			
			properties = new Hashtable<String, String>();
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME, 
					DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, 
					physicalDeviceId3);
			//properties.put("deviceId", lsDeviceId3);
			lsReg3 = bc.registerService(IDriverService.class.getName(), ls3, properties);
			
			properties = new Hashtable<String, String>();
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME,
					DeviceMgmtDriverServiceNames.SCREEN_DRIVER_SERVICE);
			properties.put(DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID, screenPhysicalDeviceId);
			//properties.put("deviceId", screenDeviceId);
			screenReg = bc.registerService(IDriverService.class.getName(), screen, properties);
		}
		
		
		String lsDeviceId1 = deviceManager.fireNewDeviceConnected(physicalDeviceId1, deviceCommonInfo1, lightDriverServiceNamesList);
		String lsDeviceId2 = deviceManager.fireNewDeviceConnected(physicalDeviceId2, deviceCommonInfo2, lightDriverServiceNamesList);
		String lsDeviceId3 = deviceManager.fireNewDeviceConnected(physicalDeviceId3, deviceCommonInfo3, lightDriverServiceNamesList);	
		String screenDeviceId = deviceManager.fireNewDeviceConnected(screenPhysicalDeviceId, screenCommonInfo, screenDriverServiceNamesList);
		
		
		LOG.info("DeviceDriverExample: " + "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii " + lsDeviceId1);
		
		ls.setDeviceId(lsDeviceId1);
		ls2.setDeviceId(lsDeviceId2);
		ls3.setDeviceId(lsDeviceId3);
		screen.setDeviceId(screenDeviceId);
		
	}
	
	
	public void stop(BundleContext context) throws Exception {
		
		stopappli();
		
		if(lsReg != null){
			lsReg.unregister();
		}	
		if(lsReg2 != null){
			lsReg2.unregister();
		}
		if(lsReg3 != null){
			lsReg3.unregister();
		}
		if(screenReg != null){
			screenReg.unregister();
		}
		
	}
	
	public void stopappli(){

		/* Light sensors */
		if (ls!=null){
			deviceManager.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", physicalDeviceId1);
			ls = null;
		}
		else {
			LOG.info("no ls");
		}
		if (ls2!=null){
			deviceManager.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", physicalDeviceId2);
			ls2 = null;
		}
		else {
			LOG.info("no ls2");
		}
		if (ls3!=null){
			deviceManager.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", physicalDeviceId3);
			ls3 = null;
		}
		else {
			LOG.info("no ls3");
		}
		
		/* Screen */
		if (screen!=null){
			deviceManager.fireDeviceDisconnected("org.societies.DeviceDriverSimulator", screenPhysicalDeviceId);
			screen = null;
		}
		else {
			LOG.info("no screen");
		}
	}
}
