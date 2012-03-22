package org.societies.webapp.controller;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.webapp.models.ServiceDiscoveryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;


@Controller
public class ServiceDiscoveryController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IServiceDiscovery sdService;
	
	public IServiceDiscovery getSDService() {
		return sdService;
	}

	public void getSDService(IServiceDiscovery sdService) {
		this.sdService = sdService;
	}

	@RequestMapping(value = "/servicediscovery.html", method = RequestMethod.GET)
	public ModelAndView Servicediscovery() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		ServiceDiscoveryForm sdForm = new ServiceDiscoveryForm();
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("GetServices", "Get Service for node");
		methods.put("GetLocalServices", "Get Local Services");
		model.put("methods", methods);
		model.put("sdForm", sdForm);
		model.put("servicediscoveryResult", "Service Discovery Result :");
		return new ModelAndView("servicediscovery", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servicediscovery.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid ServiceDiscoveryForm sdForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "service discovery form error");
			return new ModelAndView("servicediscovery", model);
		}

		if (getSDService() == null) {
			model.put("errormsg", "Service Discovery Service reference not avaiable");
			return new ModelAndView("error", model);
		}

		String node = sdForm.getNode();
		String method = sdForm.getMethod();
		Future<List<Service>> asynchResult = null;
		List<Service> services =  new ArrayList<Service>();
		
		String res;
		
		try {
		
			if (method.equalsIgnoreCase("GetLocalServices")) {
				asynchResult=this.getSDService().getLocalServices();
				res="ServiceDiscovery Result For Local Node ";
				
				services = asynchResult.get();
				model.put("services", services);
				
			}else if (method.equalsIgnoreCase("GetServices")) {
				
				asynchResult=this.getSDService().getServices(node);
				res="ServiceDiscovery Result for Node : [" + node + "]";
					
				services = asynchResult.get();
				model.put("services", services);
					
			}else{
				res="error unknown metod";
			}
		
			model.put("result", res);
			
		}
		catch (ServiceDiscoveryException e)
		{
			res = "Oops!!!!<br/>";
		}
		catch (Exception ex)
		{
			res = "Oops!!!! <br/>";
		};
		
		
		return new ModelAndView("servicediscoveryresult", model);
		

	}
}