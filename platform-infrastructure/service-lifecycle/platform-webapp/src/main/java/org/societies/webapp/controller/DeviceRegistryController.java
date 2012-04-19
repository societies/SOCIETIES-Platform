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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.webapp.models.DeviceRegistryForm;
import org.societies.webapp.models.ServiceDiscoveryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.internal.css.devicemgmt.IDeviceRegistry;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;


@Controller
public class DeviceRegistryController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IDeviceRegistry deviceRegistry;
	
	public IDeviceRegistry getdeviceRegistry() {
		return deviceRegistry;
	}

	public void getSDService(IDeviceRegistry deviceRegistry) {
		this.deviceRegistry = deviceRegistry;
	}

	@RequestMapping(value = "/deviceregistry.html", method = RequestMethod.GET)
	public ModelAndView Servicediscovery() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		DeviceRegistryForm deviceForm = new DeviceRegistryForm();
		model.put("deviceForm", deviceForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("addDevice", "Add a Device");
		methods.put("findAllDevices", "Find all Devices");
		model.put("methods", methods);
		
		return new ModelAndView("deviceregistry", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/deviceregistry.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid DeviceRegistryForm deviceForm, BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "Device Registry form error");
			return new ModelAndView("deviceregistry", model);
		}

		if (getdeviceRegistry() == null) {
			model.put("errormsg", "Device Registry Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String method = deviceForm.getMethod();
		String res = null;
		try {
		
			if (method.equalsIgnoreCase("addDevice")) {
				res="Device Added";
				DeviceCommonInfo deviceInfo = new DeviceCommonInfo(
						deviceForm.getDeviceFamilyIdentity(),
						deviceForm.getDeviceName(), 
						deviceForm.getDeviceType(),
						deviceForm.getDeviceDescription(), 
						deviceForm.getDeviceConnectionType(),
						deviceForm.getDeviceLocation(), 
						deviceForm.getDeviceProvider(), 
						deviceForm.getDeviceID(),
						deviceForm.isContextSource() );
				String cssNodeId = deviceForm.getCssNodeId();
				res = deviceRegistry.addDevice(deviceInfo, cssNodeId);
				
			}else if (method.equalsIgnoreCase("findAllDevices")) {
				
				Collection<DeviceCommonInfo> devices = deviceRegistry.findAllDevices();
				model.put("devices", devices);
					
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (Exception ex)
		{
			res = "Oops!!!! <br/>";
		};
		return new ModelAndView("deviceregistry", model);
	}
}