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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.internal.css.devicemgmt.IDeviceControl;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.internal.css.devicemgmt.model.DeviceMgmtDriverServiceConstants;




public class DeviceImpl implements IDevice, IDeviceControl{
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceImpl.class);
	private DeviceManager deviceManager;
	private DeviceCommonInfo deviceCommonInfo;
	private String deviceId;
	private boolean status = true;
	private String deviceNodeId;


	public DeviceImpl(DeviceManager deviceMgr, String deviceNodeId, String deviceId, DeviceCommonInfo deviceCommonInfo) {
		
		this.deviceManager = deviceMgr;
		this.deviceCommonInfo = deviceCommonInfo;
		this.deviceId = deviceId;
		this.deviceNodeId = deviceNodeId;
	}

	@Override
	public String getDeviceName() {
		
		return deviceCommonInfo.getDeviceName();
	}

	@Override
	public String getDeviceId() {
		
		return this.deviceId;
	}

	@Override
	public String getDeviceType() {
		
		return deviceCommonInfo.getDeviceType();
	}

	@Override
	public String getDeviceDescription() {

		return deviceCommonInfo.getDeviceDescription();
	}

	@Override
	public String getDeviceConnectionType() {
		return deviceCommonInfo.getDeviceConnectionType();
	}


//	public void enable() {
//		
//		this.status = true;
//	}
//
//
//	public void disable() {
//		
//		this.status = false;
//	}

	@Override
	public boolean isEnable() {
		
		return this.status;
	}

	@Override
	public String getDeviceLocation() {
		
		return deviceCommonInfo.getDeviceLocation();
	}

	@Override
	public String getDeviceProvider() {
		
		return deviceCommonInfo.getDeviceProvider();
	}

	@Override
	public boolean isContextSource() {
		
		return deviceCommonInfo.getContextSource();
	}
	
	@Override
	public List<String> getEventNameList(){
		return null;
	}

	
	@Override
	public String getDeviceNodeId() {
		return this.deviceNodeId;
	}

	@Override
	public IDriverService getService(String serviceName) {
		
		if (status) 
		{
			LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ getService 1 " + serviceName); 
			
			String physicalDeviceId = deviceManager.getPhysicalDeviceId(deviceId);
			
			LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ getService 2 physicalDeviceId: " + physicalDeviceId);
			
			List <String> serviceList = deviceManager.getDeviceServiceNames(deviceId);
			
			LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ getService 3"); 
			
			if (serviceList != null && physicalDeviceId != null) 
			{
				LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ serviceList non null"); 
				if (serviceList.contains(serviceName))
				{
					LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ serviceList.contains(serviceId) get service by: service id = " + serviceName +" and device Id = " + deviceId); 
					ServiceReference[] sr = null;
					try 
					{
						sr = DeviceManager.bundleContext.getServiceReferences(IDriverService.class.getName(),  "(&("+DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME+"="+serviceName+")("+
								DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID+"="+physicalDeviceId+"))");
					} 
					catch (InvalidSyntaxException e) 
					{
						e.printStackTrace();
					}
					if (sr != null)
					{
						LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ sr"); 
						
						IDriverService iDeviceService = (IDriverService)DeviceManager.bundleContext.getService(sr[0]);

						return iDeviceService;
					}
					LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ sr null"); 
					return null;
				}
				return null;
			}
			return null;
		}
		else
		{
			return null;
		}
	}

	@Override
	public IDriverService [] getServices() 
	{
		
		if (status) 
		{
			String physicalDeviceId = deviceManager.getPhysicalDeviceId(this.deviceId);
			List <String> serviceNameList = deviceManager.getDeviceServiceNames(this.deviceId);
			
			List<IDriverService> deviceServiceList = new ArrayList<IDriverService>();
			
			if (serviceNameList != null && physicalDeviceId != null) 
			{
				ServiceReference[] sr = null;
				for(String serviceName : serviceNameList)
				{
					try 
					{
						sr = DeviceManager.bundleContext.getServiceReferences(IDriverService.class.getName(), "(&("+DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_SERVICE_NAME+"="+serviceName+")("+
								DeviceMgmtDriverServiceConstants.DEVICE_DRIVER_PHYSICAL_DEVICE_ID+"="+physicalDeviceId+"))");
					
					} catch (InvalidSyntaxException e) {
						e.printStackTrace();
					}
					if (sr != null)
					{
						IDriverService iDeviceService = (IDriverService)DeviceManager.bundleContext.getService(sr[0]);

						deviceServiceList.add(iDeviceService);
					}
				}
				return (IDriverService [])deviceServiceList.toArray(new IDriverService []{});
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	public void removeDevice()
	{
		deviceManager.removeDeviceFromContainer(deviceCommonInfo.getDeviceFamilyIdentity(), deviceId);
			
			LOG.info("-- The device " + deviceId + " has been removed");
	}

	@Override
	public void enable(String nodeId, String deviceId) {

		this.status = true;
		
		LOG.info("-- IDevice: a device with Id: " + deviceId + " is enabled");
	}

	@Override
	public void disable(String nodeId, String deviceId) {
	
		this.status = false;
		
		LOG.info("-- IDevice: a device with Id: " + deviceId + " is disable");
		
	}


	@Override
	public void share(String nodeId, String deviceId) {
		
		
	}

	@Override
	public void unshare(String nodeId, String deviceId) {
		
		
	}
}
