/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.platform.servicelifecycle.servicecontrol;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.model.DeviceMgmtConstants;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderSLMCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceMgmtInternalEvent;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceUpdateException;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.services.ServiceMgmtEventType;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.OsgiListenerUtils;

/**
 * 
 * This class implements the Service Registry Listener, that registers and unregisters services
 * 
 * @author pkuppuud
 * @author mmanniox
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 * 
 */

public class OsgiRegistryListener implements BundleContextAware,
		ServiceListener, BundleListener {

	private BundleContext bctx;
	private static Logger log = LoggerFactory.getLogger(OsgiRegistryListener.class);
	private IServiceRegistry serviceReg;
	private ICommManager commMngr;
	private INegotiationProviderServiceMgmt negotiationProvider;
	private IServiceControl serviceControl;
	private IPrivacyPolicyManager privacyManager;
	private IEventMgr eventMgr;
	private String clientRepository;
	private String serviceDir;
	private IIdentity myId;
	private	INetworkNode thisNode;

	public String getServiceDir() {
		return serviceDir;
	}

	public void setServiceDir(String serviceDir) {
		this.serviceDir = serviceDir;
	}
	
	public String getClientRepository() {
		return clientRepository;
	}

	public void setClientRepository(String clientRepository) {
		this.clientRepository = clientRepository;
	}

	public IEventMgr getEventMgr(){
		return eventMgr;
	}
	
	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr=eventMgr;
	}
	
	public IPrivacyPolicyManager getPrivacyManager(){
		return privacyManager;
	}
	
	public void setPrivacyManager(IPrivacyPolicyManager privacyManager){
		this.privacyManager = privacyManager;
	}
	
	public IServiceControl getServiceControl(){
		return serviceControl;
	}
	
	public void setServiceControl(IServiceControl serviceControl){
		this.serviceControl = serviceControl;
	}
	
	  public INegotiationProviderServiceMgmt getNegotiationProvider(){
		return negotiationProvider;
	}
	
	public void setNegotiationProvider(INegotiationProviderServiceMgmt negotiationProvider){
		this.negotiationProvider = negotiationProvider;
	}
	
	public IServiceRegistry getServiceReg() {
		return serviceReg;
	}

	public void setServiceReg(IServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	public ICommManager getCommMngr() {
		return commMngr;
	}
	
	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}
	
	public OsgiRegistryListener() {
		log.info("Service RegistryListener Bean Instantiated");
	}


	
	public void registerListener() {
		
		Filter fltr = null;
		
		if(log.isDebugEnabled()) 
			log.debug("Registering Listener!");
		
		try{
			thisNode = getCommMngr().getIdManager().getThisNetworkNode();
			myId = getCommMngr().getIdManager().fromJid(thisNode.getJid());
		} catch(Exception ex){
			log.error("Exception getting current node!");
			ex.printStackTrace();
		}
		
		try {
			fltr = this.bctx.createFilter("(TargetPlatform=SOCIETIES)");		
			log.info("Service Filter Registered");
			
		} catch (InvalidSyntaxException e) {
			log.error("Error creating Service Listener Filter");
			e.printStackTrace();
		}
		
		OsgiListenerUtils.addServiceListener(this.bctx, this, fltr);
		
		log.info("Bundle Listener Registered");
		this.bctx.addBundleListener(this);
		
		getServiceControl().cleanAfterRestart();
		
	}

	public void unRegisterListener() {
		log.info("Service Management unregistering service listener");
		OsgiListenerUtils.removeServiceListener(this.bctx, this);
		log.info("Service Management unregistering bundle listener");
		this.bctx.removeBundleListener(this);
	}

	@Override
	public void setBundleContext(BundleContext ctx) {
		this.bctx = ctx;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {

		log.debug("ServiceEvent from OSGI received for a SOCIETIES service!");

		Bundle serviceBundle = event.getServiceReference().getBundle();
		Service serviceReference = (Service) event.getServiceReference().getProperty("ServiceMetaModel");
		
		if(serviceReference == null){
			log.debug("Bean {} is not a valid SOCIETIES service, so we can ignore the event!", event.getServiceReference().getProperty("org.springframework.osgi.bean.name"));
			return;
		}
		
		boolean isDevice = serviceReference.getServiceType().equals(ServiceType.DEVICE);
		log.debug("SOCIETIES service {} is a {}",serviceReference.getServiceName(),serviceReference.getServiceType());
		if(isDevice) {
			log.debug("For now we are ignoring devices!");
			return;
		}
		// Now we actually get the service!
		Service service = null;
		Service existService = null;
		
		if(isDevice){
			service = getServiceFromOSGIService(event.getServiceReference());
				
			try{
				if(service != null)
					existService = getServiceReg().retrieveService(service.getServiceIdentifier());
				
			} catch(Exception ex){
				log.error("Exception while trying to start/register device! {}",ex.getMessage());
				ex.printStackTrace();
				return;
			}
			
		} else{
			existService = getServiceFromBundle(serviceBundle);
			if(existService == null && event.getType() == ServiceEvent.REGISTERED){
				log.debug("{} might be a new service, we need the information from the metamodel.",serviceReference.getServiceName());
				service = getServiceFromOSGIService(event.getServiceReference());
			} else
				service = existService;
		}
		
		if(service == null){
			log.warn("Couldn't get information from service!");
			return;
		}
		
		switch (event.getType()) {

		case ServiceEvent.MODIFIED:
			
			if(log.isDebugEnabled())
				log.debug("Service Modification: we do nothing!");
			break;
			
		case ServiceEvent.REGISTERED:
							
			log.debug("We must check if the service that was registered for {} is new or just starting.",serviceReference.getServiceName());
			if(existService == null)
				installService(service,serviceBundle);
			else
				startService(service,serviceBundle);

			break;
			
		case ServiceEvent.UNREGISTERING:
			
			if(isDevice){
				sendEvent(ServiceMgmtEventType.SERVICE_STOPPED,service,serviceBundle);
				removeService(service,serviceBundle);
			} else{
				stopService(service,serviceBundle);
			}
			break;
			
		default:
			log.warn("Unknown ServiceEvent Type!");
		}
		
		
	}

	@Override
	public void bundleChanged(BundleEvent event) {
		
		if(event.getType() != BundleEvent.UNINSTALLED){
			return;
		} 
		
		Bundle bundle = event.getBundle();
		log.debug("Bundle {} has been uninstalled, we must check if it corresponds to a SOCIETIES service.",bundle.getSymbolicName());
		
		Service bundleService = getServiceFromBundle(bundle);
		
		if(bundleService != null){
			log.debug("Bundle {} corresponds to SOCIETIES service {}. Uninstalling!", bundle.getSymbolicName(),bundleService.getServiceName());
			removeService(bundleService,bundle);
		}
		
	}
	
	
	/**
	 * @param newService
	 * @param newBundle
	 */


	/**
	 * @param serviceReference
	 * @return 
	 */
	private Service getServiceFromOSGIService(ServiceReference serviceReference) {
		
		log.debug("Processing new Service Reference, in order to obtain a SOCIETIES service!");
		Bundle serviceBundle = serviceReference.getBundle();
		
		try{
			if(log.isDebugEnabled()){
				
				log.debug("Checking Service Reference's properties:");
				for (String key : serviceReference.getPropertyKeys() ) {
					log.debug("{} : {}",key,serviceReference.getProperty(key));
				}
			
				log.debug("Bundle Id: {} with Symbolic Name: {}", serviceBundle.getBundleId(), serviceBundle.getSymbolicName());
			}
			
			Service service = (Service) serviceReference.getProperty("ServiceMetaModel");
			
			if(service==null || (!(service instanceof Service) )){
				if(log.isDebugEnabled()) 
					log.debug("**Service MetadataModel object is null**");
				return null;
			}
			
			INetworkNode myNode = commMngr.getIdManager().getThisNetworkNode();
			
			//TODO DEAL WITH THIS
			//service.setServiceEndpoint(myNode.getJid()  + "/" +  service.getServiceName().replaceAll(" ", ""));
			if(service.getServiceEndpoint() != null && !service.getServiceEndpoint().startsWith("/")){
				log.debug("Service Endpoint was not started with '/', adding it: {}",service.getServiceEndpoint());
				service.setServiceEndpoint("/"+service.getServiceEndpoint());
			}
				
			//TODO: Do this properly!
			ServiceInstance si = new ServiceInstance();

			if(service.getServiceType().equals(ServiceType.DEVICE)){
				String nodeId = (String) serviceReference.getProperty(DeviceMgmtConstants.DEVICE_NODE_ID);
				
				log.debug("This is device with nodeId: {} ", nodeId);
					
				try{
					INetworkNode serviceNode = getCommMngr().getIdManager().fromFullJid(nodeId);
					
					si.setFullJid(serviceNode.getJid());
					si.setCssJid(serviceNode.getBareJid());
					si.setParentJid(serviceNode.getJid()); //This is later changed!
					si.setXMPPNode(serviceNode.getNodeIdentifier());
				
				}catch(Exception ex){
					ex.printStackTrace();
					log.warn("Exception in IdManager, doing alternate solution!");
					si.setFullJid(nodeId);
					si.setCssJid(nodeId);
					si.setParentJid(nodeId); //This is later changed!
					si.setXMPPNode(myNode.getNodeIdentifier());
				}
				
			} else{
				log.debug("This is a normal service, not a device!");
				
				si.setFullJid(myNode.getJid());
				si.setCssJid(myNode.getBareJid());
				si.setParentJid(myNode.getJid()); //This is later changed!
				si.setXMPPNode(myNode.getNodeIdentifier());
				service.setServiceLocation(serviceBundle.getLocation());
			}
			
			ServiceImplementation servImpl = new ServiceImplementation();
			servImpl.setServiceVersion((String) serviceReference.getProperty("Bundle-Version"));
			
			if(service.getServiceType().equals(ServiceType.DEVICE))
				servImpl.setServiceNameSpace("device."+service.getServiceName()+"."+myNode.getBareJid());
			else
				servImpl.setServiceNameSpace(((String[]) serviceReference.getProperty("objectClass"))[0]);
			
			servImpl.setServiceProvider((String) serviceReference.getProperty("ServiceProvider"));
			servImpl.setServiceClient((String) serviceReference.getProperty("ServiceClient"));
			
			si.setServiceImpl(servImpl);
			service.setServiceInstance(si);
			
			// By default, the status is started...
			service.setServiceStatus(ServiceStatus.STARTED);
			
			
			if(log.isDebugEnabled()){
				log.debug("**Service MetadataModel Data Read**");
				log.debug("Service Name: {}",service.getServiceName());
				log.debug("Service Description: {}",service.getServiceDescription());
				log.debug("Service type: {}",service.getServiceType().toString());
				log.debug("Service Location: {}",service.getServiceLocation());
				log.debug("Service Endpoint: {}",service.getServiceEndpoint());
				log.debug("Service PrivacyPolicy: {}",service.getPrivacyPolicy());
				log.debug("Service SecurityPolicy: {}",service.getSecurityPolicy());
				log.debug("Service isContextSource: {}",service.getContextSource());
				log.debug("Service Provider: {}",service.getServiceInstance().getServiceImpl().getServiceProvider());
				log.debug("Service Namespace: {}",service.getServiceInstance().getServiceImpl().getServiceNameSpace());
				log.debug("Service ServiceClient: {}",service.getServiceInstance().getServiceImpl().getServiceClient());
				log.debug("Service Version: {}",service.getServiceInstance().getServiceImpl().getServiceVersion());
				log.debug("Service XMPPNode: {}",service.getServiceInstance().getXMPPNode());
				log.debug("Service FullJid: {}",service.getServiceInstance().getFullJid());
				log.debug("Service CssJid: {}",service.getServiceInstance().getCssJid());
			}
			
			if(!service.getServiceType().equals(ServiceType.DEVICE)){
				service.setServiceIdentifier(ServiceModelUtils.generateServiceResourceIdentifier(service, serviceBundle));
			}
			else{
				String deviceId = (String) serviceReference.getProperty("DeviceId");
				if(service.getServiceType().equals(ServiceType.DEVICE) && deviceId == null){
					log.warn("Service Type is DEVICE but no device Id. Aborting");
					return null;
				}
				
				service.setServiceIdentifier(ServiceModelUtils.generateServiceResourceIdentifierForDevice(service, deviceId));
			}
			
			si.setParentIdentifier(service.getServiceIdentifier());
			service.setServiceInstance(si);
			
			log.debug("Generated ServiceResourceIdentifier for new service: {}", ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));
			
			return service;
			
		} catch(Exception ex){
			log.error("Exception occured while trying to process ServiceReference and turn it into a SOCIETIES event! {}",ex.getMessage());
			ex.printStackTrace();
		}
		
		return null;
		
		
		
	}

	/**
	 * This method is used to obtain the Service that is exposed by given Bundle
	 * 
	 * @param The Bundle that exposes this service
	 * @return The Service object whose bundle we wish to find
	 */
	private Service getServiceFromBundle(Bundle bundle) {
		
		log.debug("Obtaining Service that corresponds to a bundle: {}", bundle.getSymbolicName());
		
		// Preparing the search filter		
		Service filter = ServiceModelUtils.generateEmptyFilter();
		filter.getServiceIdentifier().setServiceInstanceIdentifier(bundle.getSymbolicName());
		//filter.getServiceInstance().setCssJid(thisNode.getBareJid());
		
		//filter.setServiceLocation(bundle.getLocation());
		
		List<Service> listServices;
		try {
			listServices = getServiceReg().findServices(filter);
		} catch (ServiceRetrieveException e) {
			log.error("Exception while searching for services: {}",e.getMessage());
			e.printStackTrace();
			return null;
		}
		

		if(listServices == null || listServices.isEmpty()){
			if(log.isDebugEnabled()) log.debug("Couldn't find any services that fulfill the criteria");
			return null;
		} 
		
		Service result = null;

		for(Service service: listServices){
			String bundleSymbolic = service.getServiceIdentifier().getServiceInstanceIdentifier();
			
			log.debug("Service CSS: {}", service.getServiceInstance().getCssJid());
			log.debug("Is service mine? {}",ServiceModelUtils.isServiceOurs(service, getCommMngr()));

			if(bundleSymbolic.equals(bundle.getSymbolicName())){
				result = service;
				break;
			}
				
		}
		
		 if(log.isDebugEnabled() && result != null) 
			 log.debug("The service corresponding to bundle {} is {}",bundle.getSymbolicName(),result.getServiceName() );
			
		// Finally, we return
		 return result;
		 
	}
	
	private boolean updateSecurityPolicy(Service service, Bundle bundle){
		
		try{
			
			if(service.getServiceType() != ServiceType.THIRD_PARTY_SERVER){
				log.debug("{} does not need to process the security stuff!", service.getServiceName());
				return true;
			}
			
			log.debug("Processing Security and Service Clients for {}",service.getServiceName());
				
			String slaXml = null;
			List<URL> clientList = getServiceClients(service,bundle);
			
			log.debug("Obtained {} service clients for {}",clientList.size(),service.getServiceName());
			INegotiationProviderSLMCallback callback = new ServiceNegotiationCallback();
			
			if(clientList.isEmpty()){
				log.debug("There are no clients to register, so we don't supply the fileServer!");
				getNegotiationProvider().addService(service.getServiceIdentifier(), slaXml, null, clientList.toArray(new URL[0]), callback);
			} else{
				log.debug("There are clients to register, so we supply the fileServer {}", clientRepository);
				getNegotiationProvider().addService(service.getServiceIdentifier(), slaXml, new URI(clientRepository), clientList.toArray(new URL[clientList.size()]), callback);
			}
			
		} catch(Exception ex){
			log.error("Exception Registring in Security Provider: {}",ex.getMessage());
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
		
	private boolean updatePrivacyPolicy(Service service, Bundle bundle){
			
		try{
			
			if(service.getServiceType() != ServiceType.THIRD_PARTY_SERVER){
				log.debug("{} does not need to process the privacy policy!", service.getServiceName());
				return true;
			}
			
			log.debug("Adding privacy policy for {} . Need to obtain it...",service.getServiceName());
			
			//First we check if we have a privacy policy online
			String privacyPolicy = null;
				
			String privacyLocation = service.getPrivacyPolicy();
			if(privacyLocation != null){
				log.debug("Attempting to retrieve Privacy Policy from supplied URL: {}",privacyLocation);
				try{	
					privacyPolicy = new Scanner( new URL(privacyLocation).openStream(), "UTF-8").useDelimiter("\\A").next();
				} catch(Exception ex){
					log.warn("Privacy policy was supplied but couldn't be retrieved, we'll try from inside bundle!");
				}
			} 
			
			if(privacyPolicy== null){
	
				log.debug("Privacy Policy URL not supplied, so we check inside the .jar/war, at: {} ", bundle.getLocation());

				String privacyPath = bundle.getLocation();
				int index = privacyPath.indexOf('@');	
				privacyLocation = privacyPath.substring(index+1);
				
				File bundleFile = new File(new URI(privacyLocation));
					
				if(bundleFile.isDirectory()){
					
					if(log.isDebugEnabled())
						log.debug("OSGI expanded .jar, getting privacy-policy directly");
				
					privacyLocation = privacyLocation + "/privacy-policy.xml";
					privacyPolicy = new Scanner( new URL(privacyLocation).openStream(), "UTF-8").useDelimiter("\\A").next();
					
				} else {
					if(bundleFile.isFile()) {
						
						if(log.isDebugEnabled())
							log.debug("OSGI didn't expand .jar/.war, getting privacy-policy from inside.");
						
						JarFile jarFile = new JarFile(bundleFile);
						privacyPolicy = new Scanner( jarFile.getInputStream(jarFile.getEntry("privacy-policy.xml")) ).useDelimiter("\\A").next();
					
					} else{
						if(log.isDebugEnabled())
							log.debug("Couldn't get privacy-policy from jar!");
					}
				}
			}

			if(privacyPolicy == null){	
				log.debug("Failed to obtain the Privacy Policy for {}!",service.getServiceName());
				return false;
			}
				
			IIdentity myNode = getCommMngr().getIdManager().getThisNetworkNode();
			RequestPolicy policyResult = getPrivacyManager().updatePrivacyPolicy(privacyPolicy, RequestorUtils.create(myNode.getBareJid(), service.getServiceIdentifier()));
			
			if(policyResult == null){	
				log.debug("Error adding privacyPolicy for {} to Privacy Manager!",service.getServiceName());
				return true;
			} else{
				log.debug("Added privacyPolicy for {} to Privacy Manager!",service.getServiceName());
				return true;
			}
			
						
		} catch(Exception ex){
			log.error("Exception while trying to update Privacy Policy for {}!",service.getServiceName());
			ex.printStackTrace();
			return false;
		}

	}
	
	private void installService(Service service, Bundle serviceBundle) {
		log.info("{} has been installed in OSGI",service.getServiceName());
		log.debug("{} has been installed in OSGI.",ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));
		
		try{
			
			if(!updateSecurityPolicy(service,serviceBundle) || !updatePrivacyPolicy(service,serviceBundle)){
				log.warn("Adding security and privacy failed!");
				return;
			}
			
			log.debug("Adding {} to the database!", service.getServiceName());
	
			List<Service> serviceList = new ArrayList<Service>();		
			serviceList.add(service);
			this.getServiceReg().registerServiceList(serviceList);
			
			if(!service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT)){
				sendEvent(ServiceMgmtEventType.NEW_SERVICE,service,serviceBundle);
				sendEvent(ServiceMgmtEventType.SERVICE_STARTED,service,serviceBundle);
			}
			
			//The service is now registered, so we update the hashmap
			if(ServiceControl.installingBundle(serviceBundle.getBundleId())){
				log.debug("ServiceControl is installing the bundle {}, so we need to tell it it's done", serviceBundle.getBundleId());
				ServiceControl.serviceInstalled(serviceBundle.getBundleId(), service);
			}
			
		} catch(Exception ex){
			log.error("Exception occurred while trying to install a service: {}", ex.getMessage());
			ex.printStackTrace();
		}
		
	}
		
	private void startService(Service service, Bundle serviceBundle){
		log.info("{} has been started in OSGI",service.getServiceName());
		log.debug("{} has been started in OSGI.",ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));
		
		try{
			
			// First we check if the service was already started
			if(service.getServiceStatus()==ServiceStatus.STARTED){
				
				log.debug("{} is already started in the database, so this is a SOCIETIES restart.",service.getServiceName());
				
				if(ServiceModelUtils.isServiceOurs(service, getCommMngr()) && !service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) && !service.getServiceType().equals(ServiceType.DEVICE)){
					if(!updateSecurityPolicy(service,serviceBundle) || !updatePrivacyPolicy(service,serviceBundle)){
						log.warn("{} Adding security and privacy failed!",service.getServiceName());
						return;
					}
					
					sendEvent(ServiceMgmtEventType.SERVICE_RESTORED,service,serviceBundle);
					sendEvent(ServiceMgmtEventType.SERVICE_STARTED,service,serviceBundle);
				} else{
					if(log.isDebugEnabled())
						log.debug("It's a restart, but this service doesn't need security & privacy updates");
				}
					
			} else{
				log.debug("{} is stopped in the database, so this is a normal service start. We update the database.", service.getServiceName()); 
				this.getServiceReg().changeStatusOfService(service.getServiceIdentifier(), ServiceStatus.STARTED);
				sendEvent(ServiceMgmtEventType.SERVICE_STARTED,service,serviceBundle);
			}
			
			//The service is now registered, so we update the hashmap
			if(ServiceControl.installingBundle(serviceBundle.getBundleId())){
				log.debug("ServiceControl is installing the bundle {}, so we need to tell it it's done", serviceBundle.getBundleId());
				ServiceControl.serviceInstalled(serviceBundle.getBundleId(), service);
			}
			
		} catch(Exception ex){
			log.error("Exception occurred while trying to start a service: {}", ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	private void stopService(Service service, Bundle serviceBundle){
		log.info("{} has been stopped in OSGI",service.getServiceName());
		log.debug("{} has been stopped in OSGI.",ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));
		
		try{
			
			// Double check if it's not a device!
			if(!service.getServiceType().equals(ServiceType.DEVICE)){
				
				log.debug("{} must be stopped in the database as well!", service.getServiceName()); 
				this.getServiceReg().changeStatusOfService(service.getServiceIdentifier(), ServiceStatus.STOPPED);
				sendEvent(ServiceMgmtEventType.SERVICE_STOPPED,service,serviceBundle);
							
				//The service is now unregistered, so we update the hashmap
				if(ServiceControl.installingBundle(serviceBundle.getBundleId())){			
					log.debug("ServiceControl is stopping the bundle {}, so we need to tell it it's done", serviceBundle.getBundleId());
					ServiceControl.serviceInstalled(serviceBundle.getBundleId(), service);
				}
				
			} else{
				log.debug("{} is a Device, so we handle it a bit differently!");
				sendEvent(ServiceMgmtEventType.SERVICE_STOPPED,service,serviceBundle);
				removeService(service,serviceBundle);
			}
			
			
		} catch(Exception ex){
			log.error("Exception occurred while trying to stop a service: {}", ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	private void removeService(Service service, Bundle serviceBundle){
		
		log.debug("{} has been removed from OSGI.",ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));;
				
		List<Service> servicesToRemove = new ArrayList<Service>();
		servicesToRemove.add(service);
					
		try {
			
			log.debug("Checking if service {} is shared with any CIS, and removing that association.", service.getServiceName());
			
			List<String> cisSharedList = getServiceReg().retrieveCISSharedService(service.getServiceIdentifier());
			
			if(!cisSharedList.isEmpty()){
				for(String cisShared: cisSharedList){
					log.debug("Removing {} sharing to CIS: {}", service.getServiceName(), cisShared);
					try {
						getServiceControl().unshareService(service, cisShared);
					} catch (ServiceControlException e) {
						log.error("Couldn't unshare {} from that CIS {}", service.getServiceName(), cisShared);
						e.printStackTrace();
					}
				}
			} else{
				log.debug("Service {} not shared with any CIS.", service.getServiceName());
			}
			
			if(service.getServiceType().equals(ServiceType.THIRD_PARTY_SERVER)){
				
				// We notify the Negotiation/Policy Provider		
				log.debug("Removing the service {} from the Security Manager!", service.getServiceName());
				getNegotiationProvider().removeService(service.getServiceIdentifier());
				
				log.debug("Removing the service {} from Privacy Manager",service.getServiceName());
				
				try{
					getPrivacyManager().deletePrivacyPolicy(RequestorUtils.create(myId.getBareJid(), service.getServiceIdentifier()));
				} catch (PrivacyException e) {
					log.error("Exception while removing privacy policy: " + e.getMessage());
					e.printStackTrace();
				}
				
				if(service.getServiceInstance().getServiceImpl().getServiceClient() != null){
					log.debug("{} has service clients we might need to clean!",service.getServiceName());
					ServiceDownloader.cleanClients(serviceBundle.getSymbolicName(), myId.getIdentifier(), serviceDir);
				}
			}

			
			log.debug("Removing service {} from SOCIETIES Registry",service.getServiceName());

			getServiceReg().unregisterServiceList(servicesToRemove);
			log.info("Service {} has been removed!",service.getServiceName());
			sendEvent(ServiceMgmtEventType.SERVICE_REMOVED,service,serviceBundle);
			
			//The service is now registered, so we update the hashmap
			if(ServiceControl.uninstallingBundle(serviceBundle.getBundleId())){
				if(log.isDebugEnabled())
					log.debug("ServiceControl is uninstalling the bundle, so we need to tell it it's done");
				ServiceControl.serviceUninstalled(serviceBundle.getBundleId(), service);
			}
				
			
		} catch(Exception ex){
			log.error("Exception occurred while trying to uninstall a service: {}", ex.getMessage());
			ex.printStackTrace();
		}
						

	}
	
	private void sendEvent(ServiceMgmtEventType eventType, Service service, Bundle bundle){
		log.debug("Sending event of type: {} for service {}", eventType, service.getServiceName());
		ServiceModelUtils.sendServiceMgmtEvent(eventType, service, null, bundle, eventMgr);
	}
	
	
	private List<URL> getServiceClients(Service service, Bundle bundle){
		log.debug("Trying to see if the service {} has serviceClients", service.getServiceName());
		
		String serviceClient = service.getServiceInstance().getServiceImpl().getServiceClient();
		Set<URL> clientList = new HashSet<URL>();
		
		if(serviceClient != null){
			log.debug("Supplied metadata says Service Client is: {}", serviceClient);
			
			String[] clients = serviceClient.split(" ");
			for(int k=0; k < clients.length; k++){
							
				try {
					URL clientUrl = new URL(clients[k]);
					// We should verify if the URL is valid before passing it on... maybe later!
					clientList.add(clientUrl);
				} catch (MalformedURLException e) {
					log.error("Exception getting URL for metadata client!");
					e.printStackTrace();
				}
				
			}
			
		} else{
			log.debug("Supplied metadata includes no clients");
		}
		
		// Now we check inside the .jar or .war for clients!
		try{
			
			String bundlePath = bundle.getLocation();
			int index = bundlePath.indexOf('@');	
			bundlePath = bundlePath.substring(index+1);
			
			log.debug("Now we check for serviceClients inside: {}",bundlePath);
			
			File bundleFile = new File(new URI(bundlePath));
			
			if(bundleFile.isDirectory()){
				if(log.isDebugEnabled())
					log.debug("OSGI expanded .jar, searching for clients directly...");
			
				String clientDirPath = bundlePath + "/societies-client/";
				File clientDir = new File(new URI(clientDirPath));
				if(clientDir.exists() && clientDir.isDirectory()){
					log.debug("Searching inside {}",clientDirPath);
					File[] clientFiles = clientDir.listFiles();
					for(int i = 0; i < clientFiles.length ; i++){
						String clientName = clientFiles[i].getName().toLowerCase();
						if(clientFiles[i].isFile() && 
						( clientName.endsWith(".jar") || clientName.endsWith(".jar") || clientName.endsWith(".jar") )){
							log.debug("Found a client: {}",clientName);
							clientList.add(clientFiles[i].toURI().toURL());
						}
					}
				}
				
			} else {
				if(bundleFile.isFile()) {
					if(log.isDebugEnabled())
						log.debug("OSGI didn't expand .jar, searching for clients inside...");
					
					JarFile jarFile = new JarFile(bundleFile);
					Enumeration<JarEntry> jarEntries = jarFile.entries();
					
					while(jarEntries.hasMoreElements()){
						JarEntry jarEntry = jarEntries.nextElement();
						String entryName = jarEntry.getName().toLowerCase();
						
						if(entryName.contains("societies-client/") && !jarEntry.isDirectory()
						&& (entryName.endsWith(".jar") || entryName.endsWith(".war") ||  entryName.endsWith(".apk"))){
							
							String fileName = entryName.substring(entryName.lastIndexOf('/')+1);
							
							log.debug("Found a client inside jar: {} so trying to download!",fileName);
							
							URI storedClient = ServiceDownloader.storeClient(jarFile.getInputStream(jarEntry), fileName, bundle.getSymbolicName(), myId.getIdentifier(), getServiceDir());
							
							if(storedClient == null){
								log.debug("There was a problem storing this client locally, so we don't do anything...");
							} else{
								log.debug("Stored client {} at {}",fileName,storedClient.toString());
								clientList.add(storedClient.toURL());
							}
						}
					}
								
				} else{
					if(log.isDebugEnabled())
						log.debug("Couldn't get access service clients from inside bundle");
				}
			}
			
		} catch(Exception ex){
			log.error("Exception trying to get clients from inside jar: {}", ex.getMessage());
			ex.printStackTrace();
		}
		
		List<URL> result = new ArrayList<URL>();
		if(!clientList.isEmpty()){
			StringBuilder serviceClientList = new StringBuilder();
			for(URL client : clientList){
				result.add(client);
				serviceClientList.append(client.toString()).append(' ');
			}
			service.getServiceInstance().getServiceImpl().setServiceClient(serviceClientList.toString());
			log.debug("Service Client List is now {}",service.getServiceInstance().getServiceImpl().getServiceClient());
		}
		
		return result;
	}
	

}
