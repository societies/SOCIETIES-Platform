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
package org.societies.css.devicemgmt.DeviceDriverExample.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
//import org.societies.comm.xmpp.event.EventFactory;
//import org.societies.comm.xmpp.event.EventStream;
//import org.societies.comm.xmpp.event.InternalEvent;
import org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs;



import org.springframework.osgi.context.BundleContextAware;

/**
 * Describe your class here...
 *
 * @author rafik
 *
 */
public class DeviceDriverExample implements ControllerWs, BundleContextAware{

	
	private BundleContext bundleContext;
	
	private IDeviceManager deviceManager;
	
	private String createNewDevice = "";
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceDriverExample.class);

	private final List<String> deviceMacAddressList;
	
	private Long lightLevel = new Long (0);
	
	private LightSensor lightSensor;
	
	//private EventStream myStream;
	
	private EventAdmin eventAdmin;
	
	public DeviceDriverExample() {
		
		deviceMacAddressList =  new ArrayList<String>();
		
	}
	
	public void setEventAdmin(EventAdmin eventAdmin)
	{
		
		LOG.info("DeviceDriverExample: " + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% setEventAdmin injection");
		this.eventAdmin = eventAdmin;
		
		LOG.info("DeviceDriverExample: " + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post setEventAdmin injection" + eventAdmin.toString());
	}
	
	
	
	public void setDeviceManager (IDeviceManager deviceManager)
	{
		this.deviceManager = deviceManager;
		
		//LOG.info("DeviceDriverExample: " + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% IDeviceManager dependency injection");
	}
	
	
	/** (non-Javadoc)
	 * @see org.springframework.osgi.context.BundleContextAware#setBundleContext(org.osgi.framework.BundleContext)
	 */
	public void setBundleContext(BundleContext bc) {
		bundleContext = bc;
	}


	/** (non-Javadoc)
	 * @see org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs#removeDevice(java.lang.String)
	 */
	public void removeDevice(String deviceId) {
		// TODO Auto-generated method stub
		
	}


	/** (non-Javadoc)
	 * @see org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs#sendNewData(java.lang.String, java.lang.String)
	 */
	public void sendNewData(String deviceId, String data) {
		// TODO Auto-generated method stub
		
	}

	
	/** (non-Javadoc)
	 * @see org.societies.css.devicemgmt.DeviceDriverExample.ControllerWs#createNewDevice(java.lang.String)
	 */
	public String createNewDevice(String deviceMacAddress, DeviceCommonInfo deviceCommonInfo) 
	{

		if (!deviceMacAddressList.contains(deviceMacAddress))
		{
			
			if (deviceCommonInfo.getDeviceType().equals("lightSensor")) 
			{	
				String [] serviceIds = {"lightSensor1"};
				
				createNewDevice =  deviceManager.fireNewDeviceConnected(deviceMacAddress, deviceCommonInfo, serviceIds);
				deviceMacAddressList.add(deviceMacAddress);
				
				lightSensor = new LightSensor(bundleContext, this, "lightSensor1", deviceMacAddress);
				
				LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceDriverExample info: new light sensor device created with a MAC Address: " + deviceMacAddress);
				return "Device created";
			}
			else
			{
				return "other device type to deal with";
			}
		}
		else
		{
			LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceDriverExample info: a device with mac address: " + deviceMacAddress + " already registred");
			return "Device already existes";
		}
	}
	
	
	public Long getLightLevel (String deviceMacAdress){
		
		LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceDriverExample info: getLightLevel " + lightLevel);
		
		return lightLevel;
	}

	
	@Override
	public void setLightLevel(String deviceMacAddress, Long lightLevel) {
		
		this.lightLevel = lightLevel;

		if (deviceMacAddressList.contains(deviceMacAddress))
		{
			LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% sending event by eventAdmin");
			Dictionary<String, Object> eventAdminDic = new Hashtable<String, Object>();
			
			eventAdminDic.put("lightLevel", lightLevel);
			eventAdminDic.put("macAddress", deviceMacAddress);
			
			
			//LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% EVENT_TOPIC Value: " + EventConstants.EVENT_TOPIC);
			//LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% EVENT_FILTER Value: " + EventConstants.EVENT_FILTER);
			
			eventAdmin.sendEvent(new Event("LightSensorEvent", eventAdminDic));

			LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% event sent by eventAdmin");
		}
		else
		{
			LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% The device with a MAC Address " + deviceMacAddress + " doesn't exist");
		}
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% sending event by Alec's eventing mecanism");
//		myStream = EventFactory.getStream("lightLevel");
//
//		Map<String, Long> dic = new HashMap<String, Long>();
//		
//		dic.put("lightLevel", lightLevel);
//		
//		InternalEvent myEvent = new InternalEvent(this, dic);
//		
//		LOG.info("DeviceDriverExample info:  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% myEvent.toString()" + myEvent.toString());
//
//		myStream.multicastEvent(myEvent);
//		
//		LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% event sent by Alec's eventing mecanism");
		
	}
}
