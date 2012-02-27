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


import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.springframework.osgi.context.BundleContextAware;



public class DeviceManager implements IDeviceManager, BundleContextAware{

	private static Logger LOG = LoggerFactory.getLogger(DeviceManager.class);

	private Map<String, DeviceImpl> deviceInstanceContainer;
	
	private final Map<String, Map<String, DeviceImpl>> deviceFamilyContainer; 
	
	private DeviceImpl deviceImpl;
	
	private BundleContext bundleContext;
	
	private Map<String, String> deviceBindingMap;
	
	private long idIncrement= 0;
	
	private String deviceId;
	
	
	public DeviceManager() {

		deviceFamilyContainer = new HashMap<String, Map<String,DeviceImpl>>();
		
		deviceBindingMap = new HashMap<String, String>();
		
		//LOG.info("DeviceMgmt: " + "=========++++++++++------ DeviceManager constructor");
	}
	
	public void setBundleContext(BundleContext bundleContext) {
		
		this.bundleContext = bundleContext;	
	}

	public void removeDeviceFromContainer (String deviceId)
	{
		if (deviceInstanceContainer.get(deviceId) != null)
		{
			deviceInstanceContainer.remove(deviceId);
		}
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
//////Interfaces exposed to the device drivers
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * TODO Add in this method a call to a device binding table class to generate an Id to each new device connected
	 */
	public String fireNewDeviceConnected(String deviceMacAddress, DeviceCommonInfo deviceCommonInfo) 
	{
		
		LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: fireNewDeviceConnected ");
		
		// Check if the device Family container contains device Instance container for this family of devices
		if (deviceFamilyContainer.get(deviceCommonInfo.getDeviceFamilyIdentity()) == null) 
		{
			
			//LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: deviceFamilyContainer == null ");
			
			//Create a new device instance container
			deviceInstanceContainer = new HashMap<String, DeviceImpl>();
			
			//TODO here generate the deviceId
			
			//create a new IDevice implementation
			deviceImpl = new DeviceImpl(bundleContext, this, /*TODO here set the id*/deviceMacAddress, deviceCommonInfo);
			//LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% DeviceManager info: post deviceImpl ");
			deviceInstanceContainer.put(/*TODO here set the id*/deviceMacAddress, deviceImpl);
			
			deviceFamilyContainer.put(deviceCommonInfo.getDeviceFamilyIdentity(), deviceInstanceContainer);
			
			return "A new device family bundle. A new device instance containers created and stored to the device family container";
		}
		else
		{
			//The bundle is Known, so get the device instance container
			deviceInstanceContainer = deviceFamilyContainer.get(deviceCommonInfo.getDeviceFamilyIdentity());
			
			if (deviceInstanceContainer.get(deviceMacAddress) == null)
			{
				deviceImpl = new DeviceImpl(bundleContext, this, deviceMacAddress, deviceCommonInfo);
				deviceInstanceContainer.put(/*TODO here set the id*/deviceMacAddress, deviceImpl);
				
				deviceFamilyContainer.put(deviceCommonInfo.getDeviceFamilyIdentity(), deviceInstanceContainer);
				
				return "A new device instance stored to the existing device instance container";
			}
			return"The device already exist in the container";
		}
	}

	/**
	 * 
	 */
	public void fireDeviceDisconnected(String deviceFamily, String deviceMacAddress) 
	{	
		if (deviceFamilyContainer.get(deviceFamily) != null)
		{
			deviceFamilyContainer.get(deviceFamily).get(deviceMacAddress).removeDevice();
		}
	}
	
	

	/**
	 * 
	 */
	public void fireNewDataReceived(String deviceFamily, String deviceMacAddress, String data) {
		
	}
}





