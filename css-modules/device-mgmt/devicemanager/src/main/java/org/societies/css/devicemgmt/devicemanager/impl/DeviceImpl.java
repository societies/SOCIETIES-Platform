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

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;




public class DeviceImpl implements IDevice{
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceImpl.class);
	private DeviceManager deviceManager;
	private BundleContext bundleContext;
	private DeviceCommonInfo deviceCommonInfo;
	private String deviceId;
	private boolean status;


	public DeviceImpl(BundleContext bc, DeviceManager deviceMgr, String deviceId, DeviceCommonInfo deviceCommonInfo) {
		
		this.bundleContext = bc;
		this.deviceManager = deviceMgr;
		this.deviceCommonInfo = deviceCommonInfo;
		this.deviceId = deviceId;
	}
	
	public void removeDevice()
	{
		deviceManager.removeDeviceFromContainer(deviceCommonInfo.getDeviceFamilyIdentity(), deviceId);
			
			LOG.info("-- The device " + deviceId + " has been removed");
	}

	public String getDeviceName() {
		
		return deviceCommonInfo.getDeviceName();
	}

	public String getDeviceId() {
		
		return this.deviceId;
	}

	public String getDeviceType() {
		
		return deviceCommonInfo.getDeviceType();
	}

	public String getDeviceDescription() {

		return deviceCommonInfo.getDeviceDescription();
	}

	public String getDeviceConnetionType() {
		return deviceCommonInfo.getDeviceConnectionType();
	}

	public void enable() {
		
		this.status = true;
	}

	public void disable() {
		
		this.status = false;
	}

	public boolean isEnable() {
		
		return this.status;
	}

	public String getDeviceLocation() {
		
		return deviceCommonInfo.getDeviceLocation();
	}

	public String getDeviceProvider() {
		
		return deviceCommonInfo.getDeviceProvider();
	}

	public boolean isContextSource() {
		
		return deviceCommonInfo.getContextSource();
	}

	@Override
	public IDriverService getService(String serviceId) {
	
		LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ getService 1 " + serviceId); 
		
		String physicalDeviceId = deviceManager.getPhysicalDeviceId(deviceId);
		
		LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ getService 2 physicalDeviceId: " + physicalDeviceId);
		
		List <String> serviceList = deviceManager.getDeviceServiceIds(deviceId);
		
		LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ getService 3"); 
		
		if (serviceList != null && physicalDeviceId != null) 
		{
			LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ serviceList non null"); 
			if (serviceList.contains(serviceId))
			{
				LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ serviceList.contains(serviceId) get service by: service id = " + serviceId +" and device Id = " + deviceId); 
				ServiceReference[] sr = null;
				try 
				{
					sr = bundleContext.getServiceReferences(IDriverService.class.getName(), "(&(driverServiceId="+serviceId+")(physicalDeviceId="+physicalDeviceId+"))");
				} 
				catch (InvalidSyntaxException e) 
				{
					e.printStackTrace();
				}
				if (sr != null)
				{
					LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ sr"); 
					
					IDriverService iDeviceService = (IDriverService)bundleContext.getService(sr[0]);

					return iDeviceService;
				}
				LOG.info("++++++++++++++++++++++++++++++++++++++++++++++ sr null"); 
				return null;
			}
			return null;
		}
		return null;
	}

	@Override
	public IDriverService [] getServices() {

		String physicalDeviceId = deviceManager.getPhysicalDeviceId(this.deviceId);
		List <String> serviceList = deviceManager.getDeviceServiceIds(this.deviceId);
		
		List<IDriverService> deviceServiceList = new ArrayList<IDriverService>();
		
		if (serviceList != null && physicalDeviceId != null) 
		{
			ServiceReference[] sr = null;
			for(String serviceId : serviceList)
			{
				try 
				{
					sr = bundleContext.getServiceReferences(IDriverService.class.getName(), "(&(driverServiceId="+serviceId+")(physicalDeviceId="+physicalDeviceId+"))");
				
				} catch (InvalidSyntaxException e) {
					e.printStackTrace();
				}
				if (sr != null)
				{
					IDriverService iDeviceService = (IDriverService)bundleContext.getService(sr[0]);

					deviceServiceList.add(iDeviceService);
				}
			}
			return (IDriverService [])deviceServiceList.toArray(new IDriverService []{});
		}
		return null;
	}
	
	@Override
	public List<String> getEventNameList(){
		return null;
	}

}
