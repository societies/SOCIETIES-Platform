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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.webapp.models.ServiceControlForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.ServiceControlException;


@Controller
public class ServiceControlController {

	static final Logger logger = LoggerFactory.getLogger(ServiceControlController.class);
	
	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private IServiceControl scService;
	
	public IServiceControl getSCService() {
		return scService;
	}

	public void setSCService(IServiceControl scService) {
		this.scService = scService;
	}

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
	
	@RequestMapping(value = "/servicecontrol.html", method = RequestMethod.GET)
	public ModelAndView Servicediscovery() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Please input values and submit");
		ServiceControlForm scForm = new ServiceControlForm();
		Map<String, String> methods = new LinkedHashMap<String, String>();
		Map<String, String> services = new LinkedHashMap<String, String>();
		List<Service> serviceList = new ArrayList<Service>();
		
		try {
			
			Future<List<Service>> asynchResult = this.getSDService().getLocalServices();
			serviceList = asynchResult.get();
			
			Iterator<Service> servIt = serviceList.iterator();
			while(servIt.hasNext()){
				Service next = servIt.next();
				
				String serviceId = next.getServiceIdentifier().getServiceInstanceIdentifier()+ "_" +next.getServiceIdentifier().getIdentifier().toString();
				
				if(logger.isDebugEnabled()) logger.debug("Service: " + serviceId);
				
				services.put(serviceId, next.getServiceName());
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		methods.put("StartService", "Start a Service");
		methods.put("StopService", "Stop a Service");
		methods.put("UninstallService", "Uninstall a Service");
		methods.put("InstallService", "Install a Service");
		//methods.put("InstallServiceRemote", "Install a Service on a Remote Node");
		model.put("services", services);
		model.put("methods", methods);
		model.put("scForm", scForm);
		model.put("servicecontrolResult", "Service Control Result :");
		return new ModelAndView("servicecontrol", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/servicecontrol.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid ServiceControlForm scForm,
			BindingResult result, Map model) {

		if (result.hasErrors()) {
			model.put("result", "service control form error");
			return new ModelAndView("servicecontrol", model);
		}

		if (getSCService() == null) {
			model.put("errormsg", "Service Control Service reference not available");
			return new ModelAndView("error", model);
		}

		String node = scForm.getNode();
		String method = scForm.getMethod();
		String url = scForm.getUrl();
		//Service service = scForm.getService();
		String serviceUri = scForm.getService();
		String endpoint = scForm.getEndpoint();
		

		
		if(logger.isDebugEnabled()){
			logger.debug("node =  " + node );
			logger.debug("method=" + method );
			logger.debug("url: " + url );
			logger.debug("Service Id:" + serviceUri );
			logger.debug("Endpoint: " + endpoint);
		}
		
		if(method.equalsIgnoreCase("NONE")){
			
			model.put("result", "No method selected");
			model.put("scResult", "NOTHING");
			return new ModelAndView("servicecontrolresult", model);
		}
		
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		
		if(!serviceUri.equals("NONE") && !serviceUri.equals("REMOTE")){
			int index = serviceUri.indexOf('_');	
			String bundleExtract = serviceUri.substring(0, index);
			String identifierExtract = serviceUri.substring(index+1);
			
			if(logger.isDebugEnabled()) logger.debug("Creating ServiceResourceIdentifier with: " +bundleExtract + " - " + identifierExtract);
			
			serviceId.setServiceInstanceIdentifier(bundleExtract);
			try {
				serviceId.setIdentifier(new URI(identifierExtract));
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		if(!method.equalsIgnoreCase("InstallService") && !endpoint.isEmpty() && (serviceUri.equals("REMOTE") || serviceUri.equals("NONE"))){
			if(logger.isDebugEnabled()) logger.debug("It's a remote service, so we need to check it: " + endpoint);
			
			Future<List<Service>> asynchResult = null;
			List<Service> services =  new ArrayList<Service>();
			int index = endpoint.indexOf('/');	
			String remoteJid = endpoint.substring(0, index);
			
			if(logger.isDebugEnabled()) logger.debug("Remote JID is: " + remoteJid);
			
			try {
				asynchResult=this.getSDService().getServices(remoteJid);
				services = asynchResult.get();
			} catch (Exception e) {
				logger.error("exception getting services from remote node: " + e.getMessage());
				e.printStackTrace();
			}
			
			for(Service remoteService: services){
				if(logger.isDebugEnabled()) logger.debug("Remote service: " + remoteService.getServiceName());
				if(remoteService.getServiceEndpoint().equals(endpoint)){
					if(logger.isDebugEnabled()) logger.debug("Found the correct service: " + remoteService.getServiceEndpoint());
					serviceId = remoteService.getServiceIdentifier();
					break;
				}
			}
			
		}
		
		
		Future<ServiceControlResult> asynchResult = null;
		ServiceControlResult scresult;
		
		String res;
		
		try {
	
	
			if (method.equalsIgnoreCase("InstallService")) {
				
				URL serviceUrl = new URL(url);
				
				/*
				if(logger.isDebugEnabled()) logger.debug("InstallService Method on:" + serviceUrl);
				
				asynchResult=this.getSCService().installService(serviceUrl);
				
				res="ServiceControl Result Installing in Local Node: ";
				*/
				
				if(logger.isDebugEnabled()) logger.debug("InstallService Remote Method on:" + serviceUrl +" on node " + node);
				
				asynchResult=this.getSCService().installService(serviceUrl, node);
				if(!node.isEmpty())
					res="ServiceControl Result for Node : [" + node + "]";
				else
					res="ServiceControl Result Installing in Local Node: ";
					
				scresult = asynchResult.get();
				model.put("serviceResult", scresult);
				
			}else if (method.equalsIgnoreCase("InstallServiceRemote")) {
				
				URL serviceUrl = new URL(url);
				
				if(logger.isDebugEnabled()) logger.debug("InstallService Remote Method on:" + serviceUrl +" on node " + node);
				
				asynchResult=this.getSCService().installService(serviceUrl, node);
				res="ServiceControl Result for Node : [" + node + "]";
				
				scresult = asynchResult.get();
				model.put("serviceResult", scresult);
					
			}else if (method.equalsIgnoreCase("StartService")){
				
				if(logger.isDebugEnabled()) logger.debug("StartService:" + serviceId);
				
				asynchResult=this.getSCService().startService(serviceId);
				scresult = asynchResult.get();
				model.put("serviceResult", scresult);
				
				res="Started service: " + serviceId;

				
			} else if (method.equalsIgnoreCase("StopService")){
				
				if(logger.isDebugEnabled()) logger.debug("StopService:" + serviceId);

				asynchResult=this.getSCService().stopService(serviceId);
				scresult = asynchResult.get();
				model.put("serviceResult", scresult);
				
				res="Stopped service: " + serviceId;
	
			} else if (method.equalsIgnoreCase("UninstallService")){
				
				if(logger.isDebugEnabled()) logger.debug("UninstallService:" + serviceId);

				asynchResult=this.getSCService().uninstallService(serviceId);
				scresult = asynchResult.get();
				model.put("serviceResult", scresult);
				
				res="Uninstall service: " + serviceId;
	
			} else {
				res="error unknown metod";
			}
		
			//model.put("result", res);
			
		}
		catch (ServiceControlException e)
		{
			res = "Oops!!!! Service Control Exception <br/>";
		}
		catch (Exception ex)
		{
			res = "Oops!!!! " +ex.getMessage() +" <br/>";
		};
		
		
		model.put("result", res);
		
		return new ModelAndView("servicecontrolresult", model);
		

	}
}