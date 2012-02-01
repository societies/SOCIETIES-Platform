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

package org.societies.css.devicemgmt.devicemanager.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.css.devicemgmt.devicemanager.IAction;
import org.societies.css.devicemgmt.devicemanager.IDeviceStateVariable;
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

	public IAction getAction(String actionName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IAction> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDeviceStateVariable getStateVariable(String stateVariableName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<IDeviceStateVariable> getStateVariables() {
		// TODO Auto-generated method stub
		return null;
	}

}
