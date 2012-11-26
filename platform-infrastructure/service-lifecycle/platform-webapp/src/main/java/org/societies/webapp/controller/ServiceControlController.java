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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.webapp.models.ServiceControlForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView serviceControlGet() {

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
				
				String serviceId = ServiceModelUtils.getServiceId64Encode(next.getServiceIdentifier());
				
				if(logger.isDebugEnabled())
					logger.debug("Service: " + serviceId);
				
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
	public ModelAndView serviceControlPost(@Valid ServiceControlForm scForm,
			BindingResult result, Map model) {

		String returnPage = "servicediscoveryresult";
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
		CommonsMultipartFile filePart = scForm.getFileData();

		if(logger.isDebugEnabled()){
			logger.debug("node =  " + node );
			logger.debug("method=" + method );
			logger.debug("url: " + url );
			logger.debug("Service Id (encoded): " + serviceUri );
			logger.debug("Endpoint: " + endpoint);
			logger.debug("filePart: " + filePart);
			if(filePart != null){
				logger.debug("filePart.getName: " + filePart.getName());
				logger.debug("filePart.getOriginalFilename: " + filePart.getOriginalFilename());
				logger.debug("filePart.getStorageDescription: " + filePart.getStorageDescription());
			}
		}
		
		if(method.equalsIgnoreCase("NONE")){
			
			model.put("result", "No method selected");
			model.put("scResult", "NOTHING");
			return new ModelAndView("servicecontrolresult", model);
		}
		
		ServiceResourceIdentifier serviceId = null;
		if(serviceUri != null && !serviceUri.equals("NONE"))
			serviceId = ServiceModelUtils.getServiceId64Decode(serviceUri);

		/*
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
		*/
		
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
		
		String res = "";
		
		try {
			model.put("myNode", getCommManager().getIdManager().getThisNetworkNode().getJid());

			if (method.equalsIgnoreCase("InstallService")) {
				
				if(filePart != null && !filePart.isEmpty()){
					
					InputStream is = filePart.getFileItem().getInputStream();
					String originalFileName = filePart.getOriginalFilename();
					
					File tmpFile = new File("3p-services/server/"+originalFileName);
					File directory = tmpFile.getParentFile();
					
					if (!directory.isDirectory()) {
						if(logger.isDebugEnabled())
							logger.debug("folder " + directory + " doesn't exist yet, creating it...");
						directory.mkdirs();
					}
					FileOutputStream os = new FileOutputStream(tmpFile);
					
					// Read in the bytes and write on the fly
					int numRead = 0;
					byte[] bytes = new byte[1024 * 1024];
					
					while ((bytes.length > 0) && (numRead = is.read(bytes, 0, bytes.length)) >= 0) {
						os.write(bytes, 0, numRead);
					}
	
					// Close input and output streams
					is.close();
					os.close();
					
					URL serviceUrl = tmpFile.toURI().toURL();

					if(logger.isDebugEnabled()) logger.debug("InstallService Remote Method on: " + serviceUrl +" on node " + node);
					
					asynchResult=this.getSCService().installService(serviceUrl);					
					scresult = asynchResult.get();
					
					if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());

					/*
					if(node != null && !node.isEmpty())
						res="ServiceControl Result for Node : [" + node + "]: " + scresult.getMessage();
					else
						res="ServiceControl Result Installing in Local Node: " + scresult.getMessage();
					*/
					model.put("serviceResult", scresult.getMessage());

					if(scresult.getMessage().equals(ResultMessage.SUCCESS)){
						res = "My Services: ";
						returnPage = "servicediscoveryresult";
					} else{
						res = "Problem installing service!";
						returnPage = "servicecontrolresult";
						if(tmpFile.exists())
							if(!tmpFile.delete())
								tmpFile.deleteOnExit();
					}
					
				} else{
					res="Couldn't upload file!";
					returnPage = "servicecontrolresult";
				}

			}else if (method.equalsIgnoreCase("InstallServiceRemote")) {
				
				URL serviceUrl = new URL(url);
				
				if(logger.isDebugEnabled()) logger.debug("InstallService Remote Method on:" + serviceUrl +" on node " + node);
				
				asynchResult=this.getSCService().installService(serviceUrl, node);
				res="ServiceControl Result for Node : [" + node + "]";
				
				scresult = asynchResult.get();
				
				if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				model.put("serviceResult", scresult.getMessage());
					
			}else if (method.equalsIgnoreCase("StartService")){
				
				if(logger.isDebugEnabled()) logger.debug("StartService:" + serviceId);
				
				asynchResult=this.getSCService().startService(serviceId);
				scresult = asynchResult.get();
				
				if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				model.put("serviceResult", scresult.getMessage());
				
				Future<Service> asyncService =getSDService().getService(serviceId);
				Service myService = asyncService.get();
				
				res="Started service: " + myService.getServiceName();
				
				returnPage = "servicediscoveryresult";
				
			} else if (method.equalsIgnoreCase("UnshareService")){
				
				if(logger.isDebugEnabled()) logger.debug("UnshareService:" + serviceId);

				//GENERATE SERVICE OBJECT
				Future<Service> asyncService = this.getSDService().getService(serviceId);
				Service serviceToShare = asyncService.get();
				//SHARE SERVICE
				asynchResult = this.getSCService().unshareService(serviceToShare, node);
				scresult = asynchResult.get();
				
				if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				//GET REMOTE SERVICES
				Future<List<Service>> asynchServices = this.getSDService().getServices(node);
				List<Service> cisServices = asynchServices.get();
				if(logger.isDebugEnabled())
					logger.debug("CIS has " + cisServices.size() + " services shared.");
				model.put("cisservices", cisServices);

				//Get CIS Name
				ICis cis = this.getCisManager().getCis(node);
				model.put("cis", cis);
				
				res = "My Services to Share: ";//scresult.getMessage().toString();
				returnPage = "servicediscoveryresult";
	
			} else if (method.equalsIgnoreCase("StopService")){
				
				if(logger.isDebugEnabled()) logger.debug("StopService:" + serviceId);

				asynchResult=this.getSCService().stopService(serviceId);
				scresult = asynchResult.get();
				
				if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				model.put("serviceResult", scresult.getMessage());
				
				Future<Service> asyncService =getSDService().getService(serviceId);
				Service myService = asyncService.get();
				
				res="Stopped service: " + myService.getServiceName();
				returnPage = "servicediscoveryresult";
	
			} else if (method.equalsIgnoreCase("UninstallService")){
				
				if(logger.isDebugEnabled()) logger.debug("UninstallService:" + serviceId);

				Future<Service> asyncService =getSDService().getService(serviceId);
				Service myService = asyncService.get();
				
				asynchResult = this.getSCService().uninstallService(serviceId);
				scresult = asynchResult.get();
				
				if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				model.put("serviceResult", scresult.getMessage());
				
				if(scresult.getMessage().equals(ResultMessage.SUCCESS))
					res="Uninstalled service: " + myService.getServiceName();
				else
					res="My Services: ";
				
				returnPage = "servicediscoveryresult";
	
			//>>>>>>>>>>>> SHARE SERVICE <<<<<<<<<<<<<<<
			} else if (method.equalsIgnoreCase("ShareService")){
				
				if(logger.isDebugEnabled()) logger.debug("ShareService:" + serviceId);

				//GENERATE SERVICE OBJECT
				Future<Service> asyncService = this.getSDService().getService(serviceId);
				Service serviceToShare = asyncService.get();
				//SHARE SERVICE
				asynchResult = this.getSCService().shareService(serviceToShare, node);
				scresult = asynchResult.get();
				
				if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				//GET REMOTE SERVICES
				Future<List<Service>> asynchServices = this.getSDService().getServices(node);
				List<Service> cisServices = asynchServices.get();
				if(logger.isDebugEnabled())
					logger.debug("CIS has " + cisServices.size() + " services shared.");		
				model.put("cisservices", cisServices);

				//Get CIS Name
				ICis cis = this.getCisManager().getCis(node);
				model.put("cis", cis);
				
				res = "My Services to Share: ";//scresult.getMessage().toString();
				returnPage = "servicediscoveryresult";
	
			} else if (method.equalsIgnoreCase("Install3PService")){
				
				if(logger.isDebugEnabled()) logger.debug("Install3PService:" + serviceId);

				//GENERATE SERVICE OBJECT
				Future<Service> asyncService = this.getSDService().getService(serviceId);
				Service serviceToInstall = asyncService.get();

				//SHARE SERVICE
				asynchResult = this.getSCService().installService(serviceToInstall);
				
				if(logger.isDebugEnabled()) logger.debug("Called it, now should continue!");
				//scresult = asynchResult.get();
				
				//if(logger.isDebugEnabled()) logger.debug("Result of operation was " + scresult.getMessage());
				
				/*
				//GET REMOTE SERVICES
				Future<List<Service>> asynchServices = this.getSDService().getServices(node);
				List<Service> cisServices = asynchServices.get();
				model.put("cisservices", cisServices);
				 */
				//res = scresult.getMessage().toString();
				res = "Waiting...";
				returnPage = "servicecontrolresult";
	
			} else {
				res="error unknown method";
			}
		
			
			if (returnPage.equals("servicediscoveryresult")) {
				Future<List<Service>> asynchServices = this.getSDService().getLocalServices();
				List<Service> services = asynchServices.get();
				model.put("services", services);	
			}
			
		} catch (ServiceControlException e) {
			e.printStackTrace();
			res = "Oops!!!! Service Control Exception <br/>" + e.getMessage();
			returnPage = "servicecontrolresult";
		} catch (Exception ex) {
			ex.printStackTrace();
			res = "Oops!!!! " +ex.getMessage() +" <br/>";
			returnPage = "servicecontrolresult";
		};
		
		model.put("result", res);		
		return new ModelAndView(returnPage, model);
		

	}
}
