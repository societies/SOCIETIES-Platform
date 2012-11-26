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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.webapp.models.ServiceControlForm;
import org.societies.webapp.models.ServiceDiscoveryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;


@Controller
public class ServiceDiscoveryController {
		
	static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryController.class);
	
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

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICommManager commManager;
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICisManager cisManager;
	
	public ICisManager getCisManager() {
		return cisManager;
	}

	public void getCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}
	
	@RequestMapping(value = "/servicediscovery.html", method = RequestMethod.GET)
	public ModelAndView Servicediscovery() {

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		
		//ADD THE BEAN THAT CONTAINS ALL THE FORM DATA FOR THIS PAGE
		ServiceDiscoveryForm sdForm = new ServiceDiscoveryForm();
		model.put("sdForm", sdForm);
		
		//ADD ALL THE SELECT BOX VALUES USED ON THE FORM
		Map<String, String> methods = new LinkedHashMap<String, String>();
		methods.put("GetServices", "Get Service for node");
		methods.put("GetLocalServices", "Get Local Services");
		model.put("methods", methods);
		
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
		
		String res = "";
		
		try {
			model.put("myNode", getCommManager().getIdManager().getThisNetworkNode().getJid());
			
			if (method.equalsIgnoreCase("GetLocalServices")) {
				asynchResult=this.getSDService().getLocalServices();
				res="ServiceDiscovery Result For Local Node ";
				
				services = asynchResult.get();
				model.put("services", services);
				
			} else if (method.equalsIgnoreCase("GetServices")) {
				
				asynchResult=this.getSDService().getServices(node);
				res="ServiceDiscovery Result for Node : [" + node + "]";
					
				services = asynchResult.get();
				model.put("services", services);
					
			} else if (method.equalsIgnoreCase("GetServicesCis")) {
				//LOCAL
				res="My Services to Share: ";
				asynchResult=this.getSDService().getLocalServices();
				services = asynchResult.get();
				model.put("services", services);
				if(logger.isDebugEnabled())
					logger.debug("GetServicesCis: Found "+ services.size()+ " services in our local node.");
				//REMOTE
				asynchResult=this.getSDService().getServices(node);
				services = asynchResult.get();
				model.put("cisservices", services);
				if(logger.isDebugEnabled())
					logger.debug("GetServicesCis: Found "+ services.size()+ " services in CIS: " + node);
				
				//Get CIS Name
				ICis cis = this.getCisManager().getCis(node);
				model.put("cis", cis);
					
			} else{
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
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servdiscpilot.html", method = RequestMethod.GET)
	public ModelAndView ServiceDiscPilot() {

		Map<String, Object> model = new HashMap<String, Object>();
		Future<List<Service>> asynchResult = null;
		List<Service> services =  new ArrayList<Service>();
		
		
		String res = "";
		
		try {
		
			asynchResult=this.getSDService().getLocalServices();
			res="My Services: ";
				
			services = asynchResult.get();
			model.put("services", services);
			model.put("result", res);
			ServiceControlForm scForm = new ServiceControlForm();
			Map<String, String> methods = new LinkedHashMap<String, String>();
			model.put("method","GetLocalServices");
			model.put("methods", methods);
			scForm.setNode(getCommManager().getIdManager().getThisNetworkNode().getJid());
			scForm.setMethod("GetLocalServices");
			model.put("scForm", scForm);
			model.put("myNode", getCommManager().getIdManager().getThisNetworkNode().getJid());
		}
		catch (ServiceDiscoveryException e)
		{
			//TODO : Make this nice
			res = "Error <br/>";
		}
		catch (Exception ex)
		{
			//TODO : Make this nice
			res = "Error <br/>";
		};
		
		
		return new ModelAndView("servicediscoveryresult", model);

	}
	
}