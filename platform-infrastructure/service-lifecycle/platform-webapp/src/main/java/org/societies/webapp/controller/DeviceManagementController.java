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
package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.internal.css.devicemgmt.IDeviceControl;
import org.societies.webapp.models.DeviceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Describe your class here...
 *
 * @author Rafik
 *
 */
@Controller
public class DeviceManagementController implements ServiceTrackerCustomizer, BundleContextAware{
	
	private BundleContext bundleContext;
	private ServiceTracker serviceTracker;
	private Collection<IDevice> devicesTracked = Collections.synchronizedCollection(new ArrayList<IDevice>());
	private Collection<IDeviceControl> iDevicesControlTracked = Collections.synchronizedCollection(new ArrayList<IDeviceControl>());
	
	
	@Autowired
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		
		this.bundleContext = bundleContext;
		
		System.out.println("1. DeviceManagementController");
		
		this.serviceTracker = new ServiceTracker(bundleContext, IDevice.class.getName(), this);
		this.serviceTracker.open();
		
		System.out.println("2. DeviceManagementController");
	}
	
	
	@RequestMapping(value = "/devicemgmt.html", method = RequestMethod.GET)
	public ModelAndView deviceManagement() {
		
		System.out.println("3'. DeviceManagementController: " + devicesTracked.size());
		
		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();

		
		Collection<DeviceData> devices =  new ArrayList<DeviceData>();
		
		for(IDevice iDevice : devicesTracked)
		{
			devices.add(new DeviceData(iDevice.getDeviceName(), iDevice.getDeviceNodeId(), iDevice.getDeviceId(), iDevice.getDeviceType(), iDevice.getDeviceDescription(), iDevice.getDeviceConnectionType(), iDevice.isEnable(), iDevice.getDeviceLocation(), iDevice.getDeviceProvider(), iDevice.isContextSource())); 
		}
		
		model.put("devices", devices);
		
		return new ModelAndView("devicemgmt", model);
	}
	
	
	@RequestMapping(value = "/device-detail.html", method = RequestMethod.GET)
	public ModelAndView getDeviceDetail(
											@RequestParam(value="deviceId", required=false) String deviceId, 
											@RequestParam(value="deviceNodeId", required=false) String deviceNodeId,
											Map<String, Object> model
										) 
	{
		
		if (null != deviceId && null != deviceNodeId) 
		{
			IDevice[] iDeviceArray = (IDevice[]) devicesTracked.toArray(new IDevice[devicesTracked.size()]);
			IDevice deviceSelected = null;
			
			for (int i = 0; i < iDeviceArray.length; i++) 
			{
				if (iDeviceArray[i].getDeviceId().equals(deviceId) && iDeviceArray[i].getDeviceNodeId().equals(deviceNodeId)) 
				{
					deviceSelected = iDeviceArray[i];
				}
			}
			
			if (null != deviceSelected) 
			{
				model.put("iDevice", deviceSelected);
				
				IDriverService[] driverServices = deviceSelected.getServices();
						
				if (null != driverServices) 
				{
					model.put("driverServices", driverServices);
					
					for (int i = 0; i < driverServices.length; i++) 
					{
						IAction[] iActions = driverServices[i].getActions();
						if (null != iActions) 
						{
							model.put("iActions", iActions);
						}
					}
				}
			}
		}
		
		return new ModelAndView("device-detail");
	}
	
	

	@RequestMapping(value = "/devicemgmt.html", method = RequestMethod.POST)
	public ModelAndView deviceManagement
										(	
											@RequestParam(value="deviceId", required=true) String deviceId, 
											@RequestParam(value="deviceNodeId", required=true) String deviceNodeId,
											@RequestParam(value="enable", required=true) String enable, 
											ModelMap model
										) throws Exception 
	{
		
		Collection<DeviceData> devices =  new ArrayList<DeviceData>();
		
		IDevice[] iDeviceArray = (IDevice[]) devicesTracked.toArray(new IDevice[devicesTracked.size()]);
		
		IDeviceControl[] iDeviceControlArray = (IDeviceControl[]) iDevicesControlTracked.toArray(new IDeviceControl[iDevicesControlTracked.size()]);
		

		for (int i = 0; i < iDeviceArray.length; i++) 
		{		
			if (iDeviceArray[i].getDeviceId().equals(deviceId) && iDeviceArray[i].getDeviceNodeId().equals(deviceNodeId)) 
			{
				if (enable.equals("Enable")) {
					iDeviceControlArray[i].enable(deviceNodeId, deviceId);
				}
				else if (enable.equals("Disable"))
				{
					iDeviceControlArray[i].disable(deviceNodeId, deviceId);
				}
				
			}
			devices.add(new DeviceData(iDeviceArray[i].getDeviceName(), iDeviceArray[i].getDeviceNodeId(), 
					iDeviceArray[i].getDeviceId(), iDeviceArray[i].getDeviceType(), iDeviceArray[i].getDeviceDescription(), 
					iDeviceArray[i].getDeviceConnectionType(), iDeviceArray[i].isEnable(), iDeviceArray[i].getDeviceLocation(),
					iDeviceArray[i].getDeviceProvider(), iDeviceArray[i].isContextSource()));
		}
		
		
		
		model.put("devices", devices);
		
		
		System.out.println("deviceId: "+deviceId + "  deviceNodeId: " +deviceNodeId +  "  status: " +enable);
		
		return new ModelAndView("devicemgmt");
	}



	@Override
	public Object addingService(ServiceReference reference) 
	{
		
		System.out.println("6. DeviceManagementController");
		
		Object obj = bundleContext.getService(reference);
		
		IDevice iDevice = (IDevice) obj;
		
		IDeviceControl iDeviceControl = (IDeviceControl) obj;
		
		iDevicesControlTracked.add(iDeviceControl);
		
		System.out.println("7. DeviceManagementController");
		
		bindDevice(iDevice);
		
		System.out.println("8. DeviceManagementController");
		
		return obj;
	}


	@Override
	public void modifiedService(ServiceReference reference, Object service) 
	{
		System.out.println("9. DeviceManagementController");
	}


	@Override
	public void removedService(ServiceReference reference, Object service) {
		
		unbindDevice((IDevice)service);
		
		iDevicesControlTracked.remove((IDeviceControl)service);
		
		System.out.println("10. DeviceManagementController");
	}


	protected void bindDevice(IDevice device) {
		devicesTracked.add(device);
		System.out.println("DeviceManagementController: Device added");
	}

	protected void unbindDevice(IDevice device) {
		devicesTracked.remove(device);
		System.out.println("DeviceManagementController: Device removed");
	}
}
