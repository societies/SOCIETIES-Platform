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
package org.societies.platform.servicelifecycle.servicecontrol;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.services.ServiceMgmtEventType;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceMgmtInternalEvent;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
//import org.eclipse.virgo.nano.deployer.api.core.ApplicationDeployer;
import org.societies.platform.servicelifecycle.servicecontrol.ServiceNegotiationCallback.ServiceNegotiationResult;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Implementation of Service Control
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceControl implements IServiceControl, BundleContextAware {

	private BundleContext bundleContext;
	
	static final Logger logger = LoggerFactory.getLogger(ServiceControl.class);

	private IServiceRegistry serviceReg;
	private ICommManager commMngr;
	private IServiceControlRemote serviceControlRemote;
	private INegotiation policyNegotiation;
	private ICisManager cisManager;
	private IDeviceManager deviceMngr;
	private IUserFeedback userFeedback;
	private IEventMgr eventMgr;
	private String serviceDir;

	protected static boolean restart;
	private static HashMap<Long,BlockingQueue<Service>> installServiceMap = new HashMap<Long,BlockingQueue<Service>>();
	private static HashMap<Long,BlockingQueue<Service>> uninstallServiceMap = new HashMap<Long,BlockingQueue<Service>>();
	private final long TIMEOUT = 70;

	private SocietiesEventListener eventListener;

	private INetworkNode thisNode;

	private IIdentity myId;

	private static ExecutorService executor;
	
	public String getServiceDir() {
		return serviceDir;
	}

	public void setServiceDir(String serviceDir) {
		this.serviceDir = serviceDir;
	}
	
	public IEventMgr getEventMgr(){
		return eventMgr;
	}
	
	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr=eventMgr;
	}
	
	public IDeviceManager getDeviceMngr(){
		return deviceMngr;
	}
	
	public void setDeviceMngr(IDeviceManager deviceMngr){
		this.deviceMngr = deviceMngr;
	}
	
	public ICisManager getCisManager(){
		return cisManager;
	}
	
	public void setCisManager(ICisManager cisManager){
		this.cisManager = cisManager;
	}
	
	public IServiceRegistry getServiceReg() {
		return serviceReg;
	}

	public void setServiceReg(IServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}
	
	public ICommManager getCommMngr() {
		return commMngr;
	}

	
	public void setPolicyNegotiation(INegotiation policyNegotiation){
		this.policyNegotiation = policyNegotiation;
	}
	
	public INegotiation getPolicyNegotiation(){
		return policyNegotiation;
	}
	
	public void setServiceControlRemote(IServiceControlRemote serviceControlRemote){
		this.serviceControlRemote = serviceControlRemote;
	}
	
	public IServiceControlRemote getServiceControlRemote(){
		return serviceControlRemote;
	}
	
	public IUserFeedback getUserFeedback(){
		return userFeedback;
	}
	
	public void setUserFeedback(IUserFeedback userFeedback){
		this.userFeedback = userFeedback;
	}
	/*
	public ApplicationDeployer getVirgoDeployer(){
		return virgoDeployer;
	}
	
	public void setVirgoDeployer(AppplicationDeployer virgoDeployer){
		this.virgoDeployer = virgoDeployer;
	}
	*/
	@Override
	public void setBundleContext(BundleContext bundleContext) {	
		this.bundleContext = bundleContext;

	}

	public ServiceControl(){
		executor = Executors.newCachedThreadPool();
	}
	
	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {

			if(logger.isDebugEnabled())
				logger.debug("Now creating the listener for events!");

			this.eventListener = new SocietiesEventListener(this);
			
			thisNode = getCommMngr().getIdManager().getThisNetworkNode();
			myId = getCommMngr().getIdManager().fromJid(thisNode.getJid());

		} catch (Exception e) {
			logger.error("Exception registering for CIS events");
			e.printStackTrace();
		}
	}
	
	public void killService(){
		// UnregisterStuff
		try{
			
			this.eventListener.unregister();
			this.eventListener = null;
			
		} catch(Exception e){
			logger.error("Exception removing Bean! :" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Async
	@Override
	public Future<ServiceControlResult> startService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: startService method");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(serviceId);
		
		try{
					
			// Our first task is to determine whether the service we're searching for is local or remote
			
			String nodeJid = ServiceModelUtils.getJidFromServiceIdentifier(serviceId);
			INetworkNode myNode = getCommMngr().getIdManager().getThisNetworkNode();
			String localNodeJid = myNode.getJid();
						
			logger.debug("The JID of the node where the Service is: {} and the local JID is {}", nodeJid,localNodeJid);
				
			if(!nodeJid.equals(localNodeJid)){
				
				if(logger.isDebugEnabled())
					logger.debug("We're dealing with a different node! Need to do a remote call!");
				
				IIdentity node = getCommMngr().getIdManager().fromJid(nodeJid);
				
				// Does this node belong to our CSS?
				if(!node.equals((IIdentity) myNode)){
					if(logger.isDebugEnabled()) logger.debug("This is not our CSS!");
				}
				
				ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
				getServiceControlRemote().startService(serviceId, node, callback);
				
				if(logger.isDebugEnabled())
					logger.debug("Remote call complete, now we need to wait for the result...");
				
				ServiceControlResult result = callback.getResult();
				
				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with communication to remote client");
					
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					return new AsyncResult<ServiceControlResult>(returnResult);
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result.getMessage());
					
					return new AsyncResult<ServiceControlResult>(result);
				}
				
			}
				
			//Local node
			if(logger.isDebugEnabled())
				logger.debug("We're dealing with our current, local node...");
					
			// Our first task is to obtain the Service object from the identifier, for this we got to the registry
			if(logger.isDebugEnabled()) logger.debug("Obtaining Service from SOCIETIES Registry");

			Service service = getServiceReg().retrieveService(serviceId);
			
			// Check to see if we actually got a service
			if(service == null){
				if(logger.isDebugEnabled()) logger.debug("Service represented by " + serviceId + " does not exist in SOCIETIES Registry");
				
				returnResult.setMessage(ResultMessage.SERVICE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			//Next, we need to determine if we should continue
			if(service.getServiceType().equals(ServiceType.DEVICE)){
				if(logger.isDebugEnabled()) logger.debug("It's a device, so shouldn't proceed.");
				returnResult.setMessage(ResultMessage.SERVICE_TYPE_NOT_SUPPORTED);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			// Next step, we obtain the bundle that corresponds to this service			
			Bundle serviceBundle = getBundleFromService(service);
			
			// And we check if it isn't null!
			if(serviceBundle == null){
				if(logger.isDebugEnabled()) logger.debug("Service Bundle obtained from " + service.getServiceName() + " couldn't be found");
				
				returnResult.setMessage(ResultMessage.BUNDLE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			if(serviceBundle.getState() == Bundle.ACTIVE){
				if(logger.isDebugEnabled())
					logger.debug("Service is already started, no need to act!");
				
				returnResult.setMessage(ResultMessage.SUCCESS);
				return new AsyncResult<ServiceControlResult>(returnResult);

			}
			
			//Before we start the bundle we prepare the entry on the hashmap
			BlockingQueue<Service> idList = new ArrayBlockingQueue<Service>(1);
			Long bundleId = new Long(serviceBundle.getBundleId());
			
			synchronized(this){		
				installServiceMap.put(bundleId, idList);
			}
			
			// Now we need to start the bundle
			if(logger.isDebugEnabled())
				logger.debug("Attempting to start the bundle: " + serviceBundle.getSymbolicName());

			serviceBundle.start();
			
			if(logger.isDebugEnabled())
				logger.debug("Bundle " + serviceBundle.getSymbolicName() + " is now in state " + ServiceModelUtils.getBundleStateName(serviceBundle.getState()));
			
			if(serviceBundle.getState() == Bundle.ACTIVE ){
				
				service = idList.take();
				
				logger.info("Service {} has been started.", service.getServiceName());				
				returnResult.setMessage(ResultMessage.SUCCESS);
				
				synchronized(this){
					installServiceMap.remove(bundleId);
				}
				
			}
			else{
				logger.info("Service {} has NOT been started successfully.",service.getServiceName() );	
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
			}

			return new AsyncResult<ServiceControlResult>(returnResult);
			
		} catch(Exception ex){
			logger.error("Exception occured while starting Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while starting Service.", ex);
		}

	}


	@Async
	@Override
	public Future<ServiceControlResult> stopService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: stopService method");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(serviceId);
		
		try{
			
			// Our first task is to determine whether the service we're searching for is local or remote
			
			String nodeJid = ServiceModelUtils.getJidFromServiceIdentifier(serviceId);
			String localNodeJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
						
			if(logger.isDebugEnabled())
				logger.debug("The JID of the node where the Service is: " + nodeJid + " and the local JID: " + localNodeJid);
				
			if(!nodeJid.equals(localNodeJid)){
				
				if(logger.isDebugEnabled())
					logger.debug("We're dealing with a different node! Need to do a remote call!");
				
				IIdentity node = getCommMngr().getIdManager().fromJid(nodeJid);
				ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
				getServiceControlRemote().stopService(serviceId, node, callback);
				
				if(logger.isDebugEnabled())
					logger.debug("Remote call complete, now we need to wait for the result...");
				
				ServiceControlResult result = callback.getResult();
				
				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with communication to remote client");
					
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					return new AsyncResult<ServiceControlResult>(returnResult);

				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result.getMessage());
					
					return new AsyncResult<ServiceControlResult>(result);
				}
				
			}
			
			//Local node
			if(logger.isDebugEnabled())
				logger.debug("We're dealing with our current, local node...");
					
			// Our first task is to obtain the Service object from the identifier, for this we got to the registry
			if(logger.isDebugEnabled()) logger.debug("Obtaining Service from SOCIETIES Registry");

			Service service = getServiceReg().retrieveService(serviceId);
			
			// Check to see if we actually got a service
			if(service == null){
				if(logger.isDebugEnabled()) logger.debug("Service represented by " + serviceId + " does not exist in SOCIETIES Registry");
				returnResult.setMessage(ResultMessage.SERVICE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);			}
			
			//Next, we need to determine if we should continue
			if(service.getServiceType().equals(ServiceType.DEVICE)){
				if(logger.isDebugEnabled()) logger.debug("It's a device, so shouldn't proceed.");
				returnResult.setMessage(ResultMessage.SERVICE_TYPE_NOT_SUPPORTED);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			// Next step, we obtain the bundle that corresponds to this service			
			Bundle serviceBundle = getBundleFromService(service);
			
			// And we check if it isn't null!
			if(serviceBundle == null){
				if(logger.isDebugEnabled()) logger.debug("Service Bundle obtained from " + service.getServiceName() + " couldn't be found");
				returnResult.setMessage(ResultMessage.BUNDLE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}

			if(serviceBundle.getState() != Bundle.ACTIVE){
				if(logger.isDebugEnabled())
					logger.debug("Service is not ACTIVE, so no need to stop it.");
				
				returnResult.setMessage(ResultMessage.SUCCESS);
				return new AsyncResult<ServiceControlResult>(returnResult);

			}
			
			// Now we need to stop the bundle
			if(logger.isDebugEnabled())
				logger.debug("Attempting to stop the bundle: " + serviceBundle.getSymbolicName());
			
			//Before we start the bundle we prepare the entry on the hashmap
			BlockingQueue<Service> idList = new ArrayBlockingQueue<Service>(1);
			Long bundleId = new Long(serviceBundle.getBundleId());
			
			synchronized(this){		
				installServiceMap.put(bundleId, idList);
			}
			
			serviceBundle.stop();
			
			if(logger.isDebugEnabled())
				logger.debug("Bundle " + serviceBundle.getSymbolicName() + " is now in state " + ServiceModelUtils.getBundleStateName(serviceBundle.getState()));
			
			if(serviceBundle.getState() == Bundle.RESOLVED ){
				logger.info("Service {} has been stopped.", service.getServiceName());
				returnResult.setMessage(ResultMessage.SUCCESS);
				
				Service serviceStopped = idList.take();
				
				synchronized(this){
					installServiceMap.remove(bundleId);
				}
				
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			else{
				logger.info("Service {} has NOT been stopped successfully.",service.getServiceName());
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}	
			
		} catch(Exception ex){
			logger.error("Exception occured while stopping Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while stopping Service.", ex);
		}

	}

	@Async
	@Override
	public Future<ServiceControlResult> installService(Service serviceToInstall) 
			throws ServiceControlException {
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(serviceToInstall.getServiceIdentifier());
	
		try{
		
			if(logger.isDebugEnabled()) 
				logger.debug("Service Management: installService method, trying to install a remote service!");
		
			if(serviceToInstall.getServiceType().equals(ServiceType.DEVICE)){
				
				if(logger.isDebugEnabled()) 
					logger.debug("This is a device, calling Device Manager");
				
				IIdentity deviceNodeId = getCommMngr().getIdManager().fromFullJid(serviceToInstall.getServiceInstance().getFullJid());
				DeviceCommonInfo deviceCommonInfo = new DeviceCommonInfo();
				deviceCommonInfo.setDeviceDescription(serviceToInstall.getServiceDescription());
				deviceCommonInfo.setDeviceID(serviceToInstall.getServiceIdentifier().getServiceInstanceIdentifier());
				deviceCommonInfo.setDeviceLocation(serviceToInstall.getServiceLocation());
				deviceCommonInfo.setDeviceName(serviceToInstall.getServiceName());
				deviceCommonInfo.setDeviceType(serviceToInstall.getServiceCategory());
				deviceCommonInfo.setDevicePhysicalAddress(null);
				deviceCommonInfo.setDeviceFamilyIdentity(null);
				deviceCommonInfo.setDeviceProvider(serviceToInstall.getServiceInstance().getServiceImpl().getServiceProvider());
				deviceCommonInfo.setDeviceConnectionType(null);
				
				if(serviceToInstall.getContextSource().equals("isContextSource"))
					deviceCommonInfo.setContextSource(true);
				else
					deviceCommonInfo.setContextSource(false);
				
				logger.debug("About to install!");

				String deviceId = getDeviceMngr().fireNewSharedDevice(deviceCommonInfo, deviceNodeId);
				
				if(deviceId == null){
					if(logger.isDebugEnabled()) 
						logger.debug("Problem installing device!");
					returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
					sendUserNotification("Device '"+serviceToInstall.getServiceName()+"' not installed: " + returnResult.getMessage());
					sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);
					
					return new AsyncResult<ServiceControlResult>(returnResult);	

				} else{
					if(logger.isDebugEnabled()) 
						logger.debug("Device installed with id: " + deviceId);
					returnResult.setServiceId(ServiceModelUtils.generateServiceResourceIdentifierForDevice(serviceToInstall, deviceId));
					returnResult.setMessage(ResultMessage.SUCCESS);
					sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' installed!");

					return new AsyncResult<ServiceControlResult>(returnResult);	
				}
				
			}
			
			// First up, we need to do the negotiation check
			
			if(logger.isDebugEnabled())
				logger.debug("Trying to do policy negotiation!");
			
			IIdentity providerNode = getCommMngr().getIdManager().fromJid(serviceToInstall.getServiceInstance().getFullJid());
			INetworkNode myNode = getCommMngr().getIdManager().getThisNetworkNode();
			

			logger.debug("Got the provider IIdentity {}, now creating the Requestor",providerNode.getJid());
		
			RequestorService provider = new RequestorService(providerNode, serviceToInstall.getServiceIdentifier());
			
			
			ServiceNegotiationCallback negotiationCallback = new ServiceNegotiationCallback();
			getPolicyNegotiation().startNegotiation(provider, negotiationCallback);
			ServiceNegotiationResult negotiationResult = negotiationCallback.getResult();
		
			if(negotiationResult == null){
				if(logger.isDebugEnabled()) logger.debug("Problem doing negotiation!");
				returnResult.setMessage(ResultMessage.NEGOTIATION_ERROR);
				sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' not installed: " + returnResult.getMessage());
				sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);

				return new AsyncResult<ServiceControlResult>(returnResult);
			} 
			
			if(!negotiationResult.getSuccess()){
	
				if(logger.isDebugEnabled())
					logger.debug("Negotiation was not successful!");
				returnResult.setMessage(ResultMessage.NEGOTIATION_FAILED);
				sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' not installed: " + returnResult.getMessage());
				sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);

				return new AsyncResult<ServiceControlResult>(returnResult);
			}	
			
			if(logger.isDebugEnabled())
					logger.debug("Negotiation was successful! URI returned is: " + negotiationResult.getServiceUri());
			
			//sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' : negotiation success!");

			// Now install the client!
			if(serviceToInstall.getServiceType().equals(ServiceType.THIRD_PARTY_WEB)){
				if(logger.isDebugEnabled()) logger.debug("This is a web-type service, no client to install!");
				//serviceToInstall.setServiceEndpoint(negotiationResult.getServiceUri().toString());				

				ServiceInstance si = serviceToInstall.getServiceInstance();
				si.setParentIdentifier(serviceToInstall.getServiceIdentifier());
				si.setParentJid(serviceToInstall.getServiceInstance().getFullJid());
				si.setFullJid(myNode.getJid());
				si.setCssJid(myNode.getBareJid());
				serviceToInstall.setServiceInstance(si);
				
				List<Service> addServices = new ArrayList<Service>();
				addServices.add(serviceToInstall);
				getServiceReg().registerServiceList(addServices);

				logger.info("Installed web-type third-party service.");
				returnResult.setMessage(ResultMessage.SUCCESS);
				sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' installed!");
				sendEvent(ServiceMgmtEventType.NEW_SERVICE,serviceToInstall,null);
				sendEvent(ServiceMgmtEventType.SERVICE_STARTED,serviceToInstall,null);
				
			} else{
			
				// CLIENT BASED SERVICE 
				if(logger.isDebugEnabled()) logger.debug("This is a client-based service, we need to install it");
					
				Future<ServiceControlResult> asyncResult = null;
				
				URL bundleLocation = null;
				List<URI> urlList = negotiationResult.getServiceUri();
				if(!urlList.isEmpty()){
					logger.debug("More than one service client detected, finding the the virgo one!");
					for(URI uri: urlList){
						logger.debug("Service Client URI: {}", uri);
						if(uri.toString().contains(".war") || uri.toString().contains(".jar")){
							bundleLocation = uri.toURL();
						}
					}
					
				}
				else
				{
					if(logger.isDebugEnabled())
						logger.debug("No client url returned from negotiation!");
					returnResult.setMessage(ResultMessage.NEGOTIATION_ERROR);
					sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);
					sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' not installed: No file to install returned!");
					return new AsyncResult<ServiceControlResult>(returnResult);	

				}

				if(logger.isDebugEnabled())
					logger.debug("Now trying to download the jar...");
				
				URI jarLocation = ServiceDownloader.downloadClientJar(bundleLocation, serviceToInstall,myNode.getIdentifier(),getServiceDir());
				
				if(jarLocation == null){
					if(logger.isDebugEnabled())
						logger.debug("Problem with downloading jar, no file available!");
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' not installed: Failure to download jar!");
					sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);
					return new AsyncResult<ServiceControlResult>(returnResult);	
				}
				
				ServiceControlResult result = installService(jarLocation.toURL()).get();

				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with installation! ");
						
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					logger.warn("Couldn't install the service, deleting the file then!");
					ServiceDownloader.deleteFile(jarLocation);
					
					sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' not installed: " + returnResult.getMessage());
					sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);

					return new AsyncResult<ServiceControlResult>(returnResult);	
				} 
					
				if(result.getMessage() == ResultMessage.SUCCESS){
						
					// We get the service from the registry
					Service newService = getServiceReg().retrieveService(result.getServiceId());
						
					ServiceInstance newServiceInstance = newService.getServiceInstance();
					newServiceInstance.setParentJid(serviceToInstall.getServiceInstance().getFullJid());
					newServiceInstance.setParentIdentifier(serviceToInstall.getServiceIdentifier());

					ServiceImplementation newServImpl = newServiceInstance.getServiceImpl();
					//newServImpl.setServiceClient(jarLocation.toString());
					newServiceInstance.setServiceImpl(newServImpl);
					newService.setServiceInstance(newServiceInstance);
					
					boolean test = getServiceReg().updateRegisteredService(newService);
					
					//
					logger.info("Installed shared third-party service client! : " + test);
					returnResult.setServiceId(result.getServiceId());
					returnResult.setMessage(result.getMessage());
					sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' installed!");
					
					sendEvent(ServiceMgmtEventType.NEW_SERVICE,newService,null);
					sendEvent(ServiceMgmtEventType.SERVICE_STARTED,newService,null);
					
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Installation of client was not successful");
					returnResult.setMessage(result.getMessage());
					logger.warn("Couldn't install the service, deleting the file then!");
					ServiceDownloader.deleteFile(jarLocation);
					
					sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' not installed: " + result.getMessage());
					sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);
				}
	
			}
						
			return new AsyncResult<ServiceControlResult>(returnResult);	
			
		
		} catch (Exception ex) {
			sendUserNotification("Service '"+serviceToInstall.getServiceName()+"' : Problems!");
			sendEvent(ServiceMgmtEventType.PROBLEM_OCURRED,serviceToInstall,null);
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			ex.printStackTrace();
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);

		}
		
	}
	
	@Async
	@Override
	public Future<ServiceControlResult> installService(URL bundleLocation) {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method, local node");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(null);
		Bundle newBundle = null;
		
		try {
			logger.info("Installing service bundle from location: {} ", bundleLocation);

			try{
				newBundle = bundleContext.installBundle(bundleLocation.toString());		
			} catch(BundleException ex){
				logger.error("Exception installing the bundle itself! {}", ex.getMessage());
				ex.printStackTrace();				
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
				
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("Service bundle {} has been installed with id: {}",newBundle.getSymbolicName(),newBundle.getBundleId());
				logger.debug("Service bundle {} is in state: {}",newBundle.getSymbolicName(),ServiceModelUtils.getBundleStateName(newBundle.getState()));
				logger.debug("Service bundle {} is in location: {}",newBundle.getSymbolicName(),newBundle.getLocation());
			}
			
			//Before we start the bundle we prepare the entry on the hashmap
			BlockingQueue<Service> idList = new ArrayBlockingQueue<Service>(1);
			Long bundleId = new Long(newBundle.getBundleId());
			
			synchronized(this){		
				installServiceMap.put(bundleId, idList);
			}
						
			//Now we need to start the bundle so that its services are registered with the OSGI Registry, and then SOCIETIES Registry
			if(logger.isDebugEnabled())
				logger.debug("Attempting to start bundle: " + newBundle.getSymbolicName() );
			
			try{
			
				newBundle.start();
				
			} catch(BundleException ex){
				logger.error("Exception while trying to start bundle: {}",ex.getMessage());
				ex.printStackTrace();
			}
			
			if(newBundle.getState() == Bundle.ACTIVE ){
				logger.info("Bundle " + newBundle.getSymbolicName() + " has been installed and activated.");
				
				// Now we need to search the service registry for the list of services and find the correct one!
				if(logger.isDebugEnabled()) logger.debug("Now searching for the service installed by the new bundle");
				
				//TODO Something to assure the other function is called first...
				Service service = idList.poll(TIMEOUT,TimeUnit.SECONDS);

				if(service != null){
					if(logger.isDebugEnabled()) logger.debug("Found service: " + service.getServiceName() + " so install was success!");
					returnResult.setServiceId(service.getServiceIdentifier());
					returnResult.setMessage(ResultMessage.SUCCESS);
				} else{
					if(logger.isDebugEnabled()) logger.debug("Couldn't find the service!");
					returnResult.setMessage(ResultMessage.SERVICE_NOT_FOUND);
					newBundle.stop();
					newBundle.uninstall();
				}
				
				synchronized(this){
					installServiceMap.remove(bundleId);
				}
			}
			else{
				logger.info("Bundle " + newBundle.getSymbolicName()  + " has been installed, but not activated.");
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				newBundle.uninstall();
			}
			
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
		} 
			
		return new AsyncResult<ServiceControlResult>(returnResult);


	}

	@Async
	@Override
	public Future<ServiceControlResult> installService(InputStream inputStream, String fileName)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled())
			logger.debug("Installing service from a received input stream!");
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(null);
		
		URI jarLocation = ServiceDownloader.downloadServerJar(inputStream, fileName,myId.getIdentifier(),getServiceDir());
		
		if(jarLocation == null){
			if(logger.isDebugEnabled())
				logger.debug("Problem with downloading jar, no file available!");
			
			returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
			sendUserNotification("Service not installed: Failure to download jar: "+fileName);
			return new AsyncResult<ServiceControlResult>(returnResult);	
		}
		
		try {
			Future<ServiceControlResult> asyncResult = installService(jarLocation.toURL());
			ServiceControlResult result = asyncResult.get();
			if(!result.getMessage().equals(ResultMessage.SUCCESS)){
				logger.warn("Couldn't install the service, deleting the file then!");
				ServiceDownloader.deleteFile(jarLocation);
			}
			return new AsyncResult<ServiceControlResult>(result);
		} catch (Exception e) {
			logger.error("Exception while installing bundle! {}", e.getMessage());
			logger.warn("Couldn't install the service, deleting the file then!");
			ServiceDownloader.deleteFile(jarLocation);
			e.printStackTrace();
			throw new ServiceControlException("Exception while attempting to install a bundle.", e);
		}
		
	}

	@Async
	@Override
	public Future<ServiceControlResult> installService(URL bundleLocation, IIdentity node)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method, on a given node, Identity input");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(null);
		
		try {
			// First we verify if we are installing in our own CSS
			
			// Our first task is to verify if we're installing in the right node..

			String localNodeJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
			String nodeJid = localNodeJid;
			
			if(node != null)
				nodeJid = node.getJid();
			
			if(logger.isDebugEnabled())
				logger.debug("The JID of the node where the Service is: " + nodeJid + " and the local JID: " + localNodeJid);
				
			if(!nodeJid.equals(localNodeJid)){
				
				if(logger.isDebugEnabled())
					logger.debug("We're dealing with a different node! Need to do a remote call!");
				
				ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
				getServiceControlRemote().installService(bundleLocation, node, callback);
				
				if(logger.isDebugEnabled())
					logger.debug("Remote call complete, now we need to wait for the result...");
				
				ServiceControlResult result = callback.getResult();
				
				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with communication to remote client");
					
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					return new AsyncResult<ServiceControlResult>(returnResult);
					
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result.getMessage());
					
					return new AsyncResult<ServiceControlResult>(result);
				}
				
			} else
			{
				if(logger.isDebugEnabled())
					logger.debug("It's the local node, installing...");
				
				URI jarLocation = ServiceDownloader.downloadServerJar(bundleLocation,myId.getIdentifier(),getServiceDir());
				
				if(jarLocation == null){
					if(logger.isDebugEnabled())
						logger.debug("Problem with downloading jar, no file available!");
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					sendUserNotification("Service not installed: Failure to download jar!");
					return new AsyncResult<ServiceControlResult>(returnResult);	
				}
				
				Future<ServiceControlResult> asyncResult = installService(jarLocation.toURL());
				ServiceControlResult result = asyncResult.get();

				return new AsyncResult<ServiceControlResult>(result);
			}		
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}

	@Async
	@Override
	public Future<ServiceControlResult> installService(URL bundleLocation, String nodeJid)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method, on given node, String input");
				
		try {
			
			// We convert to a node, then call the other method...
			IIdentity node = null;
			
			if(nodeJid != null && !nodeJid.isEmpty())
				node = getCommMngr().getIdManager().fromJid(nodeJid);
			
			Future<ServiceControlResult> asyncResult = null;
			
			asyncResult = installService(bundleLocation,node);
			ServiceControlResult result = asyncResult.get();
			
			return new AsyncResult<ServiceControlResult>(result);
					
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}
	
	
	@Async
	@Override
	public Future<ServiceControlResult> uninstallService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: uninstallService method");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(serviceId);
		
		try{
			
			// Our first task is to obtain the Service object from the identifier, for this we got to the registry
			if(logger.isDebugEnabled()) logger.debug("Obtaining Service from SOCIETIES Registry");
			Service service = getServiceReg().retrieveService(serviceId);
			
			String nodeJid = ServiceModelUtils.getJidFromServiceIdentifier(serviceId);
			String localNodeJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
						
			if(logger.isDebugEnabled())
				logger.debug("The JID of the node where the Service is: " + nodeJid + " and the local JID: " + localNodeJid);
			
			if(!nodeJid.equals(localNodeJid) && service == null){

				if(logger.isDebugEnabled())
					logger.debug("We're dealing with a different node! Need to do a remote call!");
				
				IIdentity node = getCommMngr().getIdManager().fromJid(nodeJid);
				ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
				getServiceControlRemote().uninstallService(serviceId, node, callback);
				
				if(logger.isDebugEnabled())
					logger.debug("Remote call complete, now we need to wait for the result...");
				
				ServiceControlResult result = callback.getResult();
				
				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with communication to remote client");
					
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					return new AsyncResult<ServiceControlResult>(returnResult);
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result.getMessage());
					
					return new AsyncResult<ServiceControlResult>(result);
				}
				
			}
			
			//Local node
			if(logger.isDebugEnabled())
				logger.debug("We're dealing with our current, local node...");
			
			// Check to see if we actually got a service
			if(service == null){
				if(logger.isDebugEnabled()) logger.debug("Service represented by " + serviceId + " does not exist in SOCIETIES Registry");
				
				returnResult.setMessage(ResultMessage.SERVICE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			if(service.getServiceType().equals(ServiceType.DEVICE) && !nodeJid.equals(localNodeJid)){
				if(logger.isDebugEnabled()) logger.debug("It's a remote device...");
				getDeviceMngr().fireDisconnectedSharedDevice(service.getServiceIdentifier().getServiceInstanceIdentifier());

				returnResult.setMessage(ResultMessage.SUCCESS);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			//Next, we need to determine if we should continue
			if(service.getServiceType().equals(ServiceType.DEVICE) || service.getServiceType().equals(ServiceType.THIRD_PARTY_ANDROID)){
				if(logger.isDebugEnabled()) logger.debug("It's a device, so shouldn't proceed.");
				returnResult.setMessage(ResultMessage.SERVICE_TYPE_NOT_SUPPORTED);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			//Next, we need to determine if we should continue
			if(service.getServiceType().equals(ServiceType.THIRD_PARTY_WEB) && !nodeJid.equals(localNodeJid)){
				if(logger.isDebugEnabled())
					logger.debug("It's a web-app, we need to remove it");
				
				List<Service> servicesToRemove = new ArrayList<Service>();
				servicesToRemove.add(service);
				
				if(logger.isDebugEnabled()) logger.debug("Removing service: " + service.getServiceName() + " from SOCIETIES Registry");

				getServiceReg().unregisterServiceList(servicesToRemove);
				logger.info("Service {} has been uninstalled", service.getServiceName());
				
				returnResult.setMessage(ResultMessage.SUCCESS);
				
				if(service.getServiceStatus().equals(ServiceStatus.STARTED))
					sendEvent(ServiceMgmtEventType.SERVICE_STOPPED,service,null);
				
				sendEvent(ServiceMgmtEventType.SERVICE_REMOVED,service,null);
				
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			// Next step, we obtain the bundle that corresponds to this service			
			Bundle serviceBundle = getBundleFromService(service);
			
			// And we check if it isn't null!
			if(serviceBundle == null){
				if(logger.isDebugEnabled()) logger.debug("Service Bundle obtained from " + service.getServiceName() + " couldn't be found");
				
				returnResult.setMessage(ResultMessage.BUNDLE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
			logger.info("Uninstalling service {}", service.getServiceName());
			
			
			//Before we uninstall the bundle we prepare the entry on the hashmap
			BlockingQueue<Service> idList = new ArrayBlockingQueue<Service>(1);
			Long bundleId = new Long(serviceBundle.getBundleId());
			
			synchronized(this){
				uninstallServiceMap.put(bundleId, idList);
			}
				
			
			if(logger.isDebugEnabled()) logger.debug("Attempting to uninstall bundle: " + serviceBundle.getSymbolicName());
			
			serviceBundle.uninstall();
			
			if(serviceBundle.getState() == Bundle.UNINSTALLED){
				if(logger.isDebugEnabled()) logger.debug("Bundle: " + serviceBundle.getSymbolicName() + " has been uninstalled.");

				returnResult.setMessage(ResultMessage.SUCCESS);

				Service serviceUninstalled = idList.take();
				
				synchronized(this){
					uninstallServiceMap.remove(bundleId);
				}
				
				if(logger.isDebugEnabled())
					logger.debug("Now we need to delete the file: " + service.getServiceLocation());
				
				String serviceLocation = service.getServiceLocation();
				int index = serviceLocation.indexOf('@');	
				String newServiceLocation = serviceLocation.substring(index+1);

				ServiceDownloader.deleteFile(new URI(newServiceLocation));
				
				return new AsyncResult<ServiceControlResult>(returnResult);
				
			} else{
				logger.info("Service {} has NOT been uninstalled", service.getServiceName());
				
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}

		} catch(Exception ex){
			logger.error("Exception while uninstalling service: " + ex.getMessage());
			ex.printStackTrace();
			throw new ServiceControlException("Exception uninstalling service bundle.", ex);
		}

	}
	
	
	/**
	 * This method is used to obtain the Bundle that corresponds to a given a Service
	 * 
	 * @param The Service object whose bundle we wish to find
	 * @return The Bundle that exposes this service
	 */
	private Bundle getBundleFromService(Service service) {
		
		if(logger.isDebugEnabled()) logger.debug("Obtaining Bundle that corresponds to a service...");
			 
		 // Now we get the bundle
		 Bundle result = bundleContext.getBundle(service.getServiceLocation());

		 if(logger.isDebugEnabled()) 
				logger.debug("Bundle is " + result.getSymbolicName() + " with id: " + result.getBundleId() + " and state: " + ServiceModelUtils.getBundleStateName(result.getState()));
			
		// Finally, we return
		 return result;
		 
	}

	protected static boolean installingBundle(long bundleId){
		if(logger.isDebugEnabled()) logger.debug("installingBundle Called for bundleId: " + bundleId );
		return installServiceMap.containsKey(new Long(bundleId));
	}
	
	protected static boolean uninstallingBundle(long bundleId){
		if(logger.isDebugEnabled()) logger.debug("uninstallingBundle Called for bundleId: " + bundleId );
		return uninstallServiceMap.containsKey(new Long(bundleId));
	}	
	
	protected static void serviceInstalled(long bundleIdentifier, Service newService){
		Long bundleId = new Long(bundleIdentifier);
		if(logger.isDebugEnabled()) logger.debug("serviceInstalled Called for bundleId: " + bundleId );
		BlockingQueue<Service> queue = installServiceMap.get(bundleId);
		queue.add(newService);
	}
	
	protected static void serviceUninstalled(long bundleIdentifier, Service newService){
		Long bundleId = new Long(bundleIdentifier);
		if(logger.isDebugEnabled()) logger.debug("serviceUninstalled Called for bundleId: " + bundleId );
		BlockingQueue<Service> queue = uninstallServiceMap.get(bundleId);
		queue.add(newService);
	}
	
	@Async
	@Override
	public Future<ServiceControlResult> shareService(Service service, String nodeJid)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: shareShared method, String input");
		
		try {
			
			// We convert to a node, then call the other method...
			IIdentity node = null;
			
			if(nodeJid != null && !nodeJid.isEmpty())
				node = getCommMngr().getIdManager().fromJid(nodeJid);
			
			Future<ServiceControlResult> asyncResult = null;
			
			asyncResult = shareService(service,node);
			ServiceControlResult result = asyncResult.get();
			
			return new AsyncResult<ServiceControlResult>(result);
					
		} catch (Exception ex) {
			logger.error("Exception while attempting to share a service: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to share a service:", ex);
		}

	}

	@Async
	@Override
	public Future<ServiceControlResult> shareService(Service service, IIdentity node) throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: shareShared method, node input");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(service.getServiceIdentifier());
		
		if(!service.getServiceType().equals(ServiceType.THIRD_PARTY_SERVER)){
			logger.debug("{} is not a server, so we can't share it!",service.getServiceName());
			returnResult.setMessage(ResultMessage.SERVICE_TYPE_NOT_SUPPORTED);
			return new AsyncResult<ServiceControlResult>(returnResult);
		}
		
		try {
			
			// First we check if the service is already in the repository
			//TODO a check to see if the service is "ours"? And then only add it if it's not?
			
			Service ourService = getServiceReg().retrieveService(service.getServiceIdentifier());
			if(ourService==null){
				if(logger.isDebugEnabled())
					logger.debug("Service is not in the repository yet, adding it");
				
				List<Service> servicesList = new ArrayList<Service>();
				servicesList.add(service);
				getServiceReg().registerServiceList(servicesList );
			}
					
			// Now that we've added it, we can share it!
			
			switch (node.getType())
			{
			case CSS:
			case CSS_RICH:
			case CSS_LIGHT:
				if(logger.isDebugEnabled()) logger.debug("For now, sharing to specific CSS is not supported");
				break;
			case CIS:
				if(logger.isDebugEnabled()) logger.debug("Sharing with a CIS: " + node.getJid());
				
				// First we need to check if we own the CIS. If so, then we can add it, if not then we need to tell the respective CIS
				ICisOwned myCIS = getCisManager().getOwnedCis(node.getJid());
				
				if(myCIS!= null) {
					if(logger.isDebugEnabled())
						logger.debug("We are dealing with a CIS that we own: " + myCIS.getName());
					
					//Adding service to repository
					if(logger.isDebugEnabled())
						logger.debug("Adding service-cis association to repository");
					getServiceReg().notifyServiceIsSharedInCIS(service.getServiceIdentifier(), node.getJid());

					returnResult.setMessage(ResultMessage.SUCCESS);
					
					if(logger.isDebugEnabled())
						logger.debug("Updating ActivityFeed for " + myCIS.getCisId());
					
					updateActivityFeed(node,"Shared",service);
					sendEvent(ServiceMgmtEventType.SERVICE_SHARED,service,node);
					sendUserNotification("Shared service '"+ service.getServiceName()+"' with CIS: " + myCIS.getName());
					logger.info("Shared service '"+ service.getServiceName()+"' with CIS: " + myCIS.getName());

				} else {
					
					if(logger.isDebugEnabled())
						logger.debug("We need to send the message to the remote CIS!");
					
					ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
					getServiceControlRemote().shareService(service, node, callback);
					
					if(logger.isDebugEnabled())
						logger.debug("Remote call complete, now we need to wait for the result...");
					
					ServiceControlResult result = callback.getResult();
					
					if(result == null){
						if(logger.isDebugEnabled())
							logger.debug("Error with communication to remote client");
						
						returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					} else{
						if(logger.isDebugEnabled())
							logger.debug("Result of operation was: " + result.getMessage());
						
						returnResult.setMessage(result.getMessage());
						returnResult.setServiceId(result.getServiceId());
						
						if(result.getMessage() == ResultMessage.SUCCESS && ServiceModelUtils.isServiceOurs(service,getCommMngr())){
								
							// And we add it to the table
							getServiceReg().notifyServiceIsSharedInCIS(service.getServiceIdentifier(), node.getJid());
							ICis remoteCis = getCisManager().getCis(node.getJid());
							
							if(logger.isDebugEnabled())
								logger.debug("Updating ActivityFeed for " + remoteCis.getCisId());
							
							updateActivityFeed(node,"Shared",service);
							sendUserNotification("Shared service '"+ service.getServiceName()+"' with CIS: " + remoteCis.getName());
							sendEvent(ServiceMgmtEventType.SERVICE_SHARED,service,node);
							logger.info("Shared service '"+ service.getServiceName()+"' with CIS: " + remoteCis.getName());
						}	
					}				
				}					
				break;
				
			default: 
				if(logger.isDebugEnabled()) logger.debug("Unknown kind of node!");
				returnResult.setMessage(ResultMessage.UNKNOWN_NODE);
				break;
			} 
			
						
		} catch (Exception ex) {
			logger.error("Exception while attempting to share a service: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to share a service:", ex);
		}
		
		return new AsyncResult<ServiceControlResult>(returnResult);
			
	}

	@Async
	@Override
	public Future<ServiceControlResult> unshareService(Service service, String nodeJid) throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: unshareShared method, String input");
		
		try {
			
			// We convert to a node, then call the other method...
			IIdentity node = null;
			
			if(nodeJid != null && !nodeJid.isEmpty())
				node = getCommMngr().getIdManager().fromJid(nodeJid);
			
			Future<ServiceControlResult> asyncResult = null;
			
			asyncResult = unshareService(service,node);
			ServiceControlResult result = asyncResult.get();
			
			return new AsyncResult<ServiceControlResult>(result);
					
		} catch (Exception ex) {
			logger.error("Exception while attempting to unshare a service: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to unshare a service:", ex);
		}
	}

	@Async
	@Override
	public Future<ServiceControlResult> unshareService(Service service, IIdentity node) throws ServiceControlException {

		if(logger.isDebugEnabled()) logger.debug("Service Management: shareShared method, node input");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(service.getServiceIdentifier());
		
		try {
			
			// First we check if the service is already in the repository. If it's not, then there's something wrong!
			//TODO a check to see if the service is "ours"? And then only add it if it's not?
			
			Service ourService = getServiceReg().retrieveService(service.getServiceIdentifier());
			if(ourService==null){
				if(logger.isDebugEnabled())
					logger.debug("Service is not in the repository yet, then how can we unshare it?");
				
				returnResult.setMessage(ResultMessage.SERVICE_NOT_FOUND);
				return new AsyncResult<ServiceControlResult>(returnResult);

			}
			
			// Now we unshare it!
			
			switch (node.getType())
			{
			case CSS:
			case CSS_RICH:
			case CSS_LIGHT:
				if(logger.isDebugEnabled()) logger.debug("For now, sharing to specific CSS is not supported");
				break;
			case CIS:
				if(logger.isDebugEnabled()) logger.debug("Removing sharing with a CIS: " + node.getJid());
				
				// First we need to check if we own the CIS. If so, then we can remove it, if not then we need to tell the respective CIS
				ICisOwned myCIS = getCisManager().getOwnedCis(node.getJid());
				
				if(myCIS!= null) {
					if(logger.isDebugEnabled())
						logger.debug("We are dealing with a CIS that we own: " + myCIS.getName());
					
					//Removing service from repository
					if(logger.isDebugEnabled())
						logger.debug("Removing service from sharing");
					getServiceReg().removeServiceSharingInCIS(service.getServiceIdentifier(), node.getJid());
					List<String> sharedCis = getServiceReg().retrieveCISSharedService(service.getServiceIdentifier());
					
					//Checking if the service is ours, if not then we remove it from the repository IF it's no longer shared
					if(!ServiceModelUtils.isServiceOurs(service,getCommMngr()) && sharedCis.isEmpty()){
						if(logger.isDebugEnabled())
							logger.debug("Service isn't ours, removing it from the service repository!");
						
						List<Service> servicesList = new ArrayList<Service>();
						servicesList.add(service);
						getServiceReg().unregisterServiceList(servicesList);
						
						//And if it's a device...
						if(service.getServiceType().equals(ServiceType.DEVICE)){
							if(logger.isDebugEnabled())
								logger.debug("Service is a device, alerting device management");
							
							getDeviceMngr().fireDisconnectedSharedDevice(service.getServiceIdentifier().getServiceInstanceIdentifier());
						}
					} else{
						updateActivityFeed(node,"Unshared",service);
						sendEvent(ServiceMgmtEventType.SERVICE_UNSHARED,service,node);
						sendUserNotification("No longer sharing "+ service.getServiceName() + " with " + myCIS.getName());
						logger.info("No longer sharing "+ service.getServiceName() + " with " + getCisManager().getCis(node.getJid()).getName());

					}
		
					returnResult.setMessage(ResultMessage.SUCCESS);
				} else {
					
					if(logger.isDebugEnabled())
						logger.debug("We need to send the message to the remote CIS!");
					
					ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
					getServiceControlRemote().unshareService(service, node, callback);
					
					if(logger.isDebugEnabled())
						logger.debug("Remote call complete, now we need to wait for the result...");
					
					ServiceControlResult result = callback.getResult();
					
					if(result == null){
						if(logger.isDebugEnabled())
							logger.debug("Error with communication to remote client");
						
						returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					} else{
						if(logger.isDebugEnabled())
							logger.debug("Result of operation was: " + result.getMessage());
						
						returnResult.setMessage(result.getMessage());
						
						if(result.getMessage() == ResultMessage.SUCCESS){

							// Don't think this makes sense or is needed... should you be able to remove a service that isn't yours from a remote CIS?!
							if(!ServiceModelUtils.isServiceOurs(ourService, getCommMngr())){
								if(logger.isDebugEnabled())
									logger.debug("Service isn't ours, removing it from the service repository!");
								
								List<Service> servicesList = new ArrayList<Service>();
								servicesList.add(service);
								getServiceReg().unregisterServiceList(servicesList);
							} else{
								getServiceReg().removeServiceSharingInCIS(ourService.getServiceIdentifier(), node.getJid());
								updateActivityFeed(node,"Unshared",service);
								sendEvent(ServiceMgmtEventType.SERVICE_UNSHARED,service,node);
								sendUserNotification("No longer sharing "+ service.getServiceName() + " with " + getCisManager().getCis(node.getJid()).getName());
								logger.info("No longer sharing "+ service.getServiceName() + " with " + getCisManager().getCis(node.getJid()).getName());
							}
						}
					}
				}
					
				break;
			default: 
				if(logger.isDebugEnabled()) logger.debug("Unknown kind of node!");
				returnResult.setMessage(ResultMessage.UNKNOWN_NODE);
				break;
			} 
			
						
		} catch (Exception ex) {
			logger.error("Exception while attempting to share a service: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to share a service:", ex);
		}
		
		return new AsyncResult<ServiceControlResult>(returnResult);
	}
	
	@Override
	@Async
	public Future<List<ICis>> getCisServiceIsSharedWith(ServiceResourceIdentifier serviceId){
		if(logger.isDebugEnabled())
			logger.debug("Getting all CIS the service is shared with...");
		
		List<ICis> finalResult = new ArrayList<ICis>();
		try{
				
			String myLocalJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
			String serviceJid = ServiceModelUtils.getJidFromServiceIdentifier(serviceId);
				
			// Is it supposed to be local?
			if(!myLocalJid.equals(serviceJid)){
					
				//TODO
				
					
			} else{
					
				List<String> cisList = getServiceReg().retrieveCISSharedService(serviceId);
				for(String cisId : cisList){
					ICis myCis = getCisManager().getCis(cisId);
					if(myCis != null){
						finalResult.add(myCis);
					}
							
				}
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("getService():: Exception getting Service: " + ex);
		}
			
		return new AsyncResult<List<ICis>>(finalResult);

	}
	
	private void sendEvent(ServiceMgmtEventType eventType, Service service,IIdentity node){	
		ServiceControlAsync servAsync = new ServiceControlAsync(eventType,service,node);
		executor.execute(servAsync);
	}
	
	private void sendUserNotification(String message){
		ServiceControlAsync servAsync = new ServiceControlAsync(message);
		executor.execute(servAsync);
	}
	
	private void updateActivityFeed(IIdentity target, String verb, Service service){
		ServiceControlAsync servAsync = new ServiceControlAsync(target,verb,service);
		executor.execute(servAsync);
	}
	
	@Override
	public void cleanAfterRestart(){
		
		if(logger.isDebugEnabled())
			logger.debug("Cleaning and Restarting. First we try to restore already installed servers!");
		
		String fullJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
		List<Service> deleteServices = new ArrayList<Service>();

		logger.debug("The JID of this node is: {}",fullJid);
		
		try{

			List<Service> oldServices = getServiceReg().retrieveServicesInCSSNode(fullJid);
			
			for(Service oldService : oldServices){

				if(oldService.getServiceType().equals(ServiceType.DEVICE) || oldService.getServiceType().equals(ServiceType.THIRD_PARTY_ANDROID)){
					if(logger.isDebugEnabled()) 
						logger.debug("Service is a device or Android, so we don't reinstall...");
					continue;
				}

				String serviceLocation = oldService.getServiceLocation();
				
				logger.debug("Checking if Bundle with location: {} exists in OSGI...",serviceLocation);
				
				Bundle thisBundle = this.bundleContext.getBundle(serviceLocation);
				
				if(thisBundle == null || (thisBundle != null && !(thisBundle.getSymbolicName().equals(oldService.getServiceInstance().getServiceImpl().getServiceNameSpace())))){
					if(logger.isDebugEnabled()){
						logger.debug("Bundle doesn't exist, or isn't our service! We need to install!");
						logger.debug("Attempting to reinstall service: " + oldService.getServiceName() + " from " + oldService.getServiceLocation());
						//logger.debug("Parent is: " + ServiceModelUtils.serviceResourceIdentifierToString(oldService.getServiceInstance().getParentIdentifier()));
					}
					
					int index = serviceLocation.indexOf('@');	
					String newServiceLocation = serviceLocation.substring(index+1);
	
					logger.debug("ServiceLocation: {}", serviceLocation);
					
					URI bundleLocation = new URI(newServiceLocation);
		
					File localBundle = new File(bundleLocation);
					if(localBundle.isFile()){
						Future<ServiceControlResult> asyncResult = installService(bundleLocation.toURL());
						ServiceControlResult result = asyncResult.get();
									
						if(result.getMessage() == ResultMessage.SUCCESS){
								
							Service newService = getServiceReg().retrieveService(result.getServiceId());

							if(newService.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT)){
								// We get the service from the registry
								ServiceInstance newServiceInstance = newService.getServiceInstance();
								newServiceInstance.setParentJid(oldService.getServiceInstance().getParentJid());
								newServiceInstance.setParentIdentifier(oldService.getServiceInstance().getParentIdentifier());
								
								ServiceImplementation newServImpl = newServiceInstance.getServiceImpl();
								newServiceInstance.setServiceImpl(newServImpl);
								newService.setServiceInstance(newServiceInstance);
								
								getServiceReg().updateRegisteredService(newService);
							}
							
							sendEvent(ServiceMgmtEventType.SERVICE_RESTORED,newService,null);
							sendEvent(ServiceMgmtEventType.SERVICE_STARTED,newService,null);

							logger.debug("Installed service {}", newService.getServiceName());
							
						} else{
							if(logger.isDebugEnabled()){
								logger.debug("Installation of "+ ServiceModelUtils.serviceResourceIdentifierToString(oldService.getServiceIdentifier()) +" was not successful");
								logger.debug("Deleting the service from database: {}", oldService);
							}
								deleteServices.add(oldService);
								List<String> cisShared = getServiceReg().retrieveCISSharedService(oldService.getServiceIdentifier());
								for(String cisJid: cisShared){
									this.unshareService(oldService, cisJid);
								}
						}
					} else{
						if(logger.isDebugEnabled()){
							logger.debug("Bundle for {} can't be found at: {}",ServiceModelUtils.serviceResourceIdentifierToString(oldService.getServiceIdentifier()), bundleLocation);
							//logger.debug("Deleting the service from database: {}", oldService);
						}
						/*
							deleteServices.add(oldService);
							List<String> cisShared = getServiceReg().retrieveCISSharedService(oldService.getServiceIdentifier());
							for(String cisJid: cisShared){
								this.unshareService(oldService, cisJid);
							}*/
					}
					
				} else{
					if(logger.isDebugEnabled()){
						logger.debug("Bundle for " + ServiceModelUtils.serviceResourceIdentifierToString(oldService.getServiceIdentifier()) + " is installed!");
					}
				}
				 	
			}
			
		} catch(Exception ex){
			logger.error("Error on cleanAfterRestart, not able to restart installed 3rd party service clients");
			ex.printStackTrace();
		}
		
		if(logger.isDebugEnabled()) 
			logger.debug("Cleaning services that are no longer installed");
				
		try{
		 	
			getServiceReg().unregisterServiceList(deleteServices);
						
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception while cleaning database: " + ex);
		}
		
		restart = false;
		
		InternalEvent internalEvent = new InternalEvent(EventTypes.SERVICE_LIFECYCLE_EVENT, "SLM_START", "org/societies/servicelifecycle", "SLM_START");
		
		try {
			getEventMgr().publishInternalEvent(internalEvent);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error sending event!");
		}
		
	}
	
	private class ServiceControlAsync implements Runnable{

		String message;
		IIdentity target;
		String verb;
		Service service;
		ServiceMgmtEventType eventType;
		boolean activityUpdate;
		boolean internalEvent;
		
		public ServiceControlAsync(String message){
			this.message = message;
			activityUpdate = false;
			internalEvent = false;
		}
		
		public ServiceControlAsync(IIdentity target, String verb, Service service){
			this.target = target;
			this.verb = verb;
			this.service = service;
			activityUpdate = true;
			internalEvent = false;

		}
		
		public ServiceControlAsync(ServiceMgmtEventType eventType, Service service,IIdentity node){
			internalEvent = true;
			this.service = service;
			this.target = node;
			this.eventType = eventType;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			if(internalEvent){			
				logger.debug("Sending event of type: {} for service {}", eventType,ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()));
				ServiceModelUtils.sendServiceMgmtEvent(eventType,service,target,ServiceModelUtils.getBundleFromService(service, bundleContext),getEventMgr());
			} else{
				if(activityUpdate)
					this.updateActivityFeed(target,verb,service);
				else
					this.sendUserNotification(message);
			}
		}
		
		private void sendUserNotification(String message){
			if(logger.isDebugEnabled())
				logger.debug("Sending notification: " + message);
			getUserFeedback().showNotification(message);
		}
		

		
		private void updateActivityFeed(IIdentity target, String verb, Service service){
			
			ICis remoteCis = getCisManager().getCis(target.getJid());
			
			IActivity activity = remoteCis.getActivityFeed().getEmptyIActivity();
			activity.setActor(getCommMngr().getIdManager().getThisNetworkNode().getJid());
			activity.setObject(service.getServiceName());
			activity.setTarget(target.getJid());
			activity.setVerb(verb);
			IActivityFeedCallback cisCallback = new ServiceActivityFeedbackCallback();
			remoteCis.getActivityFeed().addActivity(activity, cisCallback );
			
			if(logger.isDebugEnabled())
				logger.debug("Updated ActivityFeed for " + remoteCis.getCisId());
		}
		
	}


}
