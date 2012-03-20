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
import java.util.List;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.servicelifecycle.IServiceControl;
import org.societies.api.servicelifecycle.IServiceControlRemote;
import org.societies.api.servicelifecycle.ServiceControlException;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * Describe your class here...
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
		
		try{
					
			// Our first task is to determine whether the service we're searching for is local or remote
			
			String nodeJid = serviceId.getIdentifier().getHost();
			String localNodeJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
						
			if(logger.isDebugEnabled())
				logger.debug("The JID of the node where the Service is: " + nodeJid + " and the local JID: " + localNodeJid);
				
			if(!nodeJid.equals(localNodeJid)){
				
				if(logger.isDebugEnabled())
					logger.debug("We're dealing with a different node! Need to do a remote call!");
				
				IIdentity node = getCommMngr().getIdManager().fromJid(nodeJid);
				ServiceControlRemoteClient callback = new ServiceControlRemoteClient();
				getServiceControlRemote().startService(serviceId, node, callback);
				
				if(logger.isDebugEnabled())
					logger.debug("Remote call complete, now we need to wait for the result...");
				
				ServiceControlResult result = callback.getResult();
				
				if(result == null){
					if(logger.isDebugEnabled())
						logger.debug("Error with communication to remote client");
					
					return new AsyncResult<ServiceControlResult>(ServiceControlResult.COMMUNICATION_ERROR);
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result);
					
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
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SERVICE_NOT_FOUND);
			}
			
			// Next step, we obtain the bundle that corresponds to this service			
			Bundle serviceBundle = getBundleFromService(service);
			
			// And we check if it isn't null!
			if(serviceBundle == null){
				if(logger.isDebugEnabled()) logger.debug("Service Bundle obtained from " + service.getServiceName() + " couldn't be found");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.BUNDLE_NOT_FOUND);			
			}
			
			// Now we need to start the bundle
			if(logger.isDebugEnabled())
				logger.debug("Attempting to start the bundle: " + serviceBundle.getSymbolicName());

			serviceBundle.start();
			
			if(logger.isDebugEnabled())
				logger.debug("Bundle " + serviceBundle.getSymbolicName() + " is now in state " + getStateName(serviceBundle.getState()));
			
			if(serviceBundle.getState() == Bundle.ACTIVE ){
				logger.info("Service " + service.getServiceName() + " has been started.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SUCCESS);
			}
			else{
				logger.info("Service " + service.getServiceName() + " has NOT been started successfully.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.OSGI_PROBLEM);
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
		
		try{
			
			// Our first task is to determine whether the service we're searching for is local or remote
			
			String nodeJid = serviceId.getIdentifier().getHost();
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
					
					return new AsyncResult<ServiceControlResult>(ServiceControlResult.COMMUNICATION_ERROR);
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result);
					
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
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SERVICE_NOT_FOUND);
			}
			
			// Next step, we obtain the bundle that corresponds to this service			
			Bundle serviceBundle = getBundleFromService(service);
			
			// And we check if it isn't null!
			if(serviceBundle == null){
				if(logger.isDebugEnabled()) logger.debug("Service Bundle obtained from " + service.getServiceName() + " couldn't be found");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.BUNDLE_NOT_FOUND);			
			}

			
			// Now we need to stop the bundle
			if(logger.isDebugEnabled())
				logger.debug("Attempting to stop the bundle: " + serviceBundle.getSymbolicName());

			serviceBundle.stop();
			
			if(logger.isDebugEnabled())
				logger.debug("Bundle " + serviceBundle.getSymbolicName() + " is now in state " + getStateName(serviceBundle.getState()));
			
			if(serviceBundle.getState() == Bundle.RESOLVED ){
				logger.info("Service " + service.getServiceName() + " has been stopped.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SUCCESS);
			}
			else{
				logger.info("Service " + service.getServiceName() + " has NOT been stopped successfully.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.OSGI_PROBLEM);
			}	
			
		} catch(Exception ex){
			logger.error("Exception occured while stopping Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while stopping Service.", ex);
		}

	}

	
	@Override
	public Future<ServiceControlResult> installService(URL bundleLocation)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method");
				
		try {
			logger.info("Installing service bundle from location: " + bundleLocation);
			Bundle newBundle = bundleContext.installBundle(bundleLocation.toString());
			
			if(logger.isDebugEnabled()){
				logger.debug("Service bundle "+newBundle.getSymbolicName() +" has been installed with id: " + newBundle.getBundleId());
				logger.debug("Service bundle "+newBundle.getSymbolicName() +" is in state: " + getStateName(newBundle.getState()));
			}
			
			//Now we need to start the bundle so that its services are registered with the OSGI Registry, and then SOCIETIES Registry
			if(logger.isDebugEnabled())
				logger.debug("Attempting to start bundle: " + newBundle.getSymbolicName() );
			
			newBundle.start();
			
			if(newBundle.getState() == Bundle.ACTIVE ){
				logger.info("Bundle " + newBundle.getSymbolicName() + " has been installed and activated.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SUCCESS);
			}
			else{
				logger.info("Bundle " + newBundle.getSymbolicName()  + " has been installed, but not activated.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.OSGI_PROBLEM);
			}
			
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}

	@Override
	public Future<ServiceControlResult> installService(URL bundleLocation, IIdentity node)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method, on another node");
				
		try {
			
			// Our first task is to verify if we're installing in the right node..

			String localNodeJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
			String nodeJid = node.getJid();
			
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
					
					return new AsyncResult<ServiceControlResult>(ServiceControlResult.COMMUNICATION_ERROR);
					
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result);
					
					return new AsyncResult<ServiceControlResult>(result);
				}
				
			}
			
			logger.info("Installing service bundle from location: " + bundleLocation);
			Bundle newBundle = bundleContext.installBundle(bundleLocation.toString());
			
			if(logger.isDebugEnabled()){
				logger.debug("Service bundle "+newBundle.getSymbolicName() +" has been installed with id: " + newBundle.getBundleId());
				logger.debug("Service bundle "+newBundle.getSymbolicName() +" is in state: " + getStateName(newBundle.getState()));
			}
			
			//Now we need to start the bundle so that its services are registered with the OSGI Registry, and then SOCIETIES Registry
			if(logger.isDebugEnabled())
				logger.debug("Attempting to start bundle: " + newBundle.getSymbolicName() );
			
			newBundle.start();
			
			if(newBundle.getState() == Bundle.ACTIVE ){
				logger.info("Bundle " + newBundle.getSymbolicName() + " has been installed and activated.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SUCCESS);
			}
			else{
				logger.info("Bundle " + newBundle.getSymbolicName()  + " has been installed, but not activated.");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.OSGI_PROBLEM);
			}
			
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}
	
	@Override
	public Future<ServiceControlResult> uninstallService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: uninstallService method");
		
		try{
			
			// Our first task is to determine whether the service we're searching for is local or remote
			
			String nodeJid = serviceId.getIdentifier().getHost();
			String localNodeJid = getCommMngr().getIdManager().getThisNetworkNode().getJid();
						
			if(logger.isDebugEnabled())
				logger.debug("The JID of the node where the Service is: " + nodeJid + " and the local JID: " + localNodeJid);
				
			if(!nodeJid.equals(localNodeJid)){
				
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
					
					return new AsyncResult<ServiceControlResult>(ServiceControlResult.COMMUNICATION_ERROR);
				} else{
					if(logger.isDebugEnabled())
						logger.debug("Result of operation was: " + result);
					
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
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SERVICE_NOT_FOUND);
			}
			
			// Next step, we obtain the bundle that corresponds to this service			
			Bundle serviceBundle = getBundleFromService(service);
			
			// And we check if it isn't null!
			if(serviceBundle == null){
				if(logger.isDebugEnabled()) logger.debug("Service Bundle obtained from " + service.getServiceName() + " couldn't be found");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.BUNDLE_NOT_FOUND);			
			}
			
			logger.info("Uninstalling service " + service.getServiceName());
			
			if(logger.isDebugEnabled()) logger.debug("Attempting to uninstall bundle: ");
			
			serviceBundle.uninstall();
			
			if(serviceBundle.getState() == Bundle.UNINSTALLED){
				if(logger.isDebugEnabled()) logger.debug("Bundle: " + serviceBundle.getSymbolicName() + " has been uninstalled.");

				//It's not enough to simply uninstall the service. We must also remove the service itself from the repository manually
				List<Service> servicesToRemove = new ArrayList<Service>();
				servicesToRemove.add(service);
				
				if(logger.isDebugEnabled()) logger.debug("Removing service: " + service.getServiceName() + " from SOCIETIES Registry");
				getServiceReg().unregisterServiceList(servicesToRemove);
	
				logger.info("Service " + service.getServiceName() + " has been uninstalled");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.SUCCESS);	
				
			} else{
				logger.info("Service " + service.getServiceName() + " has NOT been uninstalled");
				return new AsyncResult<ServiceControlResult>(ServiceControlResult.OSGI_PROBLEM);	
			}

		} catch(Exception ex){
			logger.error("Exception while uninstalling service: " + ex.getMessage());
			throw new ServiceControlException("Exception uninstalling service bundle.", ex);
		}

	}
	
	/**
	 * This method returns the textual description of a Bundle state
	 * 
	 * @param the state of the service
	 * @return The textual description of the bundle's state
	 */
	private String getStateName(int state){
		
		switch(state){
		
			case Bundle.ACTIVE: return "ACTIVE";
			case Bundle.INSTALLED: return "INSTALLED";
			case Bundle.RESOLVED: return "RESOLVED";
			case Bundle.STARTING: return "STARTING";
			case Bundle.STOPPING: return "STOPPING";
			case Bundle.UNINSTALLED: return "UNINSTALLED";
			default: return null;
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
		 long bundleId = Long.parseLong(service.getServiceIdentifier().getServiceInstanceIdentifier());
		
		 if(logger.isDebugEnabled())
			 logger.debug("The bundle Id is " + bundleId);
		 
		 // Now we get the bundle
		 Bundle result = bundleContext.getBundle(bundleId);

		 if(logger.isDebugEnabled()) 
				logger.debug("Bundle is " + result.getSymbolicName() + " with id: " + result.getBundleId() + " and state: " + getStateName(result.getState()));
			
		// Finally, we return
		 return result;
		 
	}

}
