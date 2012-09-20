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
package org.societies.slm.servicecontrol;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.slm.servicecontrol.ServiceNegotiationCallback.ServiceNegotiationResult;
import org.springframework.osgi.context.BundleContextAware;
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
	
	private static HashMap<Long,BlockingQueue<Service>> installServiceMap = new HashMap<Long,BlockingQueue<Service>>();
	private static HashMap<Long,BlockingQueue<Service>> uninstallServiceMap = new HashMap<Long,BlockingQueue<Service>>();
	
	private final long TIMEOUT = 5;

	
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
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		
		this.bundleContext = bundleContext;

		if(logger.isDebugEnabled()) logger.debug("BundleContextSet");
	}

	
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
						
			if(logger.isDebugEnabled())
				logger.debug("The JID of the node where the Service is: " + nodeJid + " and the local JID: " + localNodeJid);
				
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
			
			// Now we need to start the bundle
			if(logger.isDebugEnabled())
				logger.debug("Attempting to start the bundle: " + serviceBundle.getSymbolicName());

			serviceBundle.start();
			
			if(logger.isDebugEnabled())
				logger.debug("Bundle " + serviceBundle.getSymbolicName() + " is now in state " + ServiceModelUtils.getBundleStateName(serviceBundle.getState()));
			
			if(serviceBundle.getState() == Bundle.ACTIVE ){
				logger.info("Service " + service.getServiceName() + " has been started.");
				
				returnResult.setMessage(ResultMessage.SUCCESS);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			else{
				logger.info("Service " + service.getServiceName() + " has NOT been started successfully.");
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}						
		} catch(Exception ex){
			logger.error("Exception occured while starting Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while starting Service.", ex);
		}

	}


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

			
			// Now we need to stop the bundle
			if(logger.isDebugEnabled())
				logger.debug("Attempting to stop the bundle: " + serviceBundle.getSymbolicName());

			serviceBundle.stop();
			
			if(logger.isDebugEnabled())
				logger.debug("Bundle " + serviceBundle.getSymbolicName() + " is now in state " + ServiceModelUtils.getBundleStateName(serviceBundle.getState()));
			
			if(serviceBundle.getState() == Bundle.RESOLVED ){
				logger.info("Service " + service.getServiceName() + " has been stopped.");
				returnResult.setMessage(ResultMessage.SUCCESS);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			else{
				logger.info("Service " + service.getServiceName() + " has NOT been stopped successfully.");
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}	
			
		} catch(Exception ex){
			logger.error("Exception occured while stopping Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while stopping Service.", ex);
		}

	}

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
					return new AsyncResult<ServiceControlResult>(returnResult);	

				} else{
					if(logger.isDebugEnabled()) 
						logger.debug("Device installed with id: " + deviceId);
					returnResult.setServiceId(ServiceModelUtils.generateServiceResourceIdentifierForDevice(serviceToInstall, deviceId));
					returnResult.setMessage(ResultMessage.SUCCESS);
					return new AsyncResult<ServiceControlResult>(returnResult);	
				}
				
			}
			
			// First up, we need to do the negotiation check
			
			if(logger.isDebugEnabled())
				logger.debug("Trying to do policy negotiation!");
			
			IIdentity providerNode = getCommMngr().getIdManager().fromJid(serviceToInstall.getServiceInstance().getFullJid());
			INetworkNode myNode = getCommMngr().getIdManager().getThisNetworkNode();
			
			if(logger.isDebugEnabled())
				logger.debug("Got the provider IIdentity, now creating the Requestor");
		
			RequestorService provider = new RequestorService(providerNode, serviceToInstall.getServiceIdentifier());
			
			
			ServiceNegotiationCallback negotiationCallback = new ServiceNegotiationCallback();
			getPolicyNegotiation().startNegotiation(provider, negotiationCallback);
			ServiceNegotiationResult negotiationResult = negotiationCallback.getResult();
		
			if(negotiationResult == null){
				if(logger.isDebugEnabled()) logger.debug("Problem doing negotiation!");
				returnResult.setMessage(ResultMessage.NEGOTIATION_ERROR);
				return new AsyncResult<ServiceControlResult>(returnResult);
			} 
			
			if(!negotiationResult.getSuccess()){
	
				if(logger.isDebugEnabled())
					logger.debug("Negotiation was not successful!");
				returnResult.setMessage(ResultMessage.NEGOTIATION_FAILED);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}	
			
			if(logger.isDebugEnabled())
					logger.debug("Negotiation was successful! URI returned is: " + negotiationResult.getServiceUri());
			
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
			
			} else{
									
				if(logger.isDebugEnabled()) logger.debug("This is a client-based service, we need to install it");
					
				Future<ServiceControlResult> asyncResult = null;
				URL bundleLocation = negotiationResult.getServiceUri().toURL();
				//URL bundleLocation = new URL(serviceToInstall.getServiceInstance().getServiceImpl().getServiceClient());

				asyncResult = installService(bundleLocation);
				ServiceControlResult result = asyncResult.get();

				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with installation! ");
						
					returnResult.setMessage(ResultMessage.COMMUNICATION_ERROR);
					return new AsyncResult<ServiceControlResult>(returnResult);	
				} 
					
				if(result.getMessage() == ResultMessage.SUCCESS){
						
					// We get the service from the registry
					Service newService = getServiceReg().retrieveService(result.getServiceId());
						
					ServiceInstance newServiceInstance = newService.getServiceInstance();
					newServiceInstance.setParentJid(serviceToInstall.getServiceInstance().getFullJid());
					newServiceInstance.setParentIdentifier(serviceToInstall.getServiceIdentifier());
					newService.setServiceInstance(newServiceInstance);
					getServiceReg().updateRegisteredService(newService);
						
					//
					logger.info("Installed shared third-party service client!");
					returnResult.setServiceId(result.getServiceId());
					returnResult.setMessage(result.getMessage());
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Installation of client was not successful");
					returnResult.setMessage(result.getMessage());
				}
	
			}
			

			return new AsyncResult<ServiceControlResult>(returnResult);	
			
		
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			ex.printStackTrace();
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}
		
	}

	public Future<ServiceControlResult> installService(Service serviceToInstall, IIdentity node) 
			throws ServiceControlException {
		
		try{
		
			if(logger.isDebugEnabled()) 
				logger.debug("Service Management: installService method, on another node: jid");
		
			// Now install the client!
			Future<ServiceControlResult> asyncResult = null;
			URL bundleLocation = null;
			
			asyncResult = installService(bundleLocation,node);
			ServiceControlResult result = asyncResult.get();
			
			return new AsyncResult<ServiceControlResult>(result);
		
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}
		
	}
	
	public Future<ServiceControlResult> installService(Service serviceToInstall, String jid) 
			throws ServiceControlException {
		
		try{
		
			//
			if(logger.isDebugEnabled()) 
				logger.debug("Service Management: install Remote Service method, on another node: jid");
		
			// Now install the client!
			Future<ServiceControlResult> asyncResult = null;
			URL bundleLocation = null;
			
			asyncResult = installService(bundleLocation,jid);
			ServiceControlResult result = asyncResult.get();
			
			return new AsyncResult<ServiceControlResult>(result);
		
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}
		
	}
	
	@Override
	public Future<ServiceControlResult> installService(URL bundleLocation)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method, local node");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(null);
		
		try {
			logger.info("Installing service bundle from location: " + bundleLocation);
			Bundle newBundle = bundleContext.installBundle(bundleLocation.toString());
			
			if(logger.isDebugEnabled()){
				logger.debug("Service bundle "+newBundle.getSymbolicName() +" has been installed with id: " + newBundle.getBundleId());
				logger.debug("Service bundle "+newBundle.getSymbolicName() +" is in state: " + ServiceModelUtils.getBundleStateName(newBundle.getState()));
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
			
			newBundle.start();
			
			if(newBundle.getState() == Bundle.ACTIVE ){
				logger.info("Bundle " + newBundle.getSymbolicName() + " has been installed and activated.");
				
				// Now we need to search the service registry for the list of services and find the correct one!
				if(logger.isDebugEnabled()) logger.debug("Now searching for the service installed by the new bundle");
				
				//TODO Something to assure the other function is called first...
				Service service = idList.poll(TIMEOUT, TimeUnit.SECONDS);
				
				synchronized(this){
					installServiceMap.remove(bundleId);
				}
				
				//Service service = getServiceFromBundle(newBundle);
				
				if(service != null){
					if(logger.isDebugEnabled()) logger.debug("Found service: " + service.getServiceName() + " so install was success!");
					returnResult.setServiceId(service.getServiceIdentifier());
					returnResult.setMessage(ResultMessage.SUCCESS);
					return new AsyncResult<ServiceControlResult>(returnResult);
				} else{
					if(logger.isDebugEnabled()) logger.debug("Couldn't find the service!");
					returnResult.setMessage(ResultMessage.SERVICE_NOT_FOUND);
					return new AsyncResult<ServiceControlResult>(returnResult);
				}
				
			}
			else{
				logger.info("Bundle " + newBundle.getSymbolicName()  + " has been installed, but not activated.");
				
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}
			
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}

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
				
				Future<ServiceControlResult> asyncResult = null;
				
				asyncResult = installService(bundleLocation);
				ServiceControlResult result = asyncResult.get();
				
				return new AsyncResult<ServiceControlResult>(result);
			}
					
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}

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
				logger.info("Service " + service.getServiceName() + " has been uninstalled");
				
				returnResult.setMessage(ResultMessage.SUCCESS);
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
			
			logger.info("Uninstalling service " + service.getServiceName());
			
			/*
			//Before we uninstall the bundle we prepare the entry on the hashmap
			BlockingQueue<Service> idList = new ArrayBlockingQueue<Service>(1);
			Long bundleId = new Long(serviceBundle.getBundleId());
			
			synchronized(this){		
				uninstallServiceMap.put(bundleId, idList);
			}
				*/
			
			if(logger.isDebugEnabled()) logger.debug("Attempting to uninstall bundle: " + serviceBundle.getSymbolicName());
			
			serviceBundle.uninstall();
			
			if(serviceBundle.getState() == Bundle.UNINSTALLED){
				if(logger.isDebugEnabled()) logger.debug("Bundle: " + serviceBundle.getSymbolicName() + " has been uninstalled.");

				returnResult.setMessage(ResultMessage.SUCCESS);
				return new AsyncResult<ServiceControlResult>(returnResult);
				
			} else{
				logger.info("Service " + service.getServiceName() + " has NOT been uninstalled");
				
				returnResult.setMessage(ResultMessage.OSGI_PROBLEM);
				return new AsyncResult<ServiceControlResult>(returnResult);
			}

		} catch(Exception ex){
			logger.error("Exception while uninstalling service: " + ex.getMessage());
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
		
		// First we get the bundleId
		 long bundleId = ServiceModelUtils.getBundleIdFromServiceIdentifier(service.getServiceIdentifier());
				 
		 if(logger.isDebugEnabled())
			 logger.debug("The bundle Id is " + bundleId);
		 
		 // Now we get the bundle
		 Bundle result = bundleContext.getBundle(bundleId);

		 if(logger.isDebugEnabled()) 
				logger.debug("Bundle is " + result.getSymbolicName() + " with id: " + result.getBundleId() + " and state: " + ServiceModelUtils.getBundleStateName(result.getState()));
			
		// Finally, we return
		 return result;
		 
	}

	protected static boolean installingBundle(long bundleId){
		if(logger.isDebugEnabled()) logger.debug("installingBundle Called");
		return installServiceMap.containsKey(new Long(bundleId));
	}
	
	protected static boolean uninstallingBundle(long bundleId){
		if(logger.isDebugEnabled()) logger.debug("uninstallingBundle Called");
		return installServiceMap.containsKey(new Long(bundleId));
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

	@Override
	public Future<ServiceControlResult> shareService(Service service, IIdentity node) throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: shareShared method, node input");
		
		ServiceControlResult returnResult = new ServiceControlResult();
		returnResult.setServiceId(service.getServiceIdentifier());
		
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
					
					
					//Checking if the service is ours, if not then we remove it from the repository
					if(!ServiceModelUtils.isServiceOurs(service,getCommMngr())){
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
}
