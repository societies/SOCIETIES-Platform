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

package org.societies.slm.servicemgmt.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceMgmt.ServiceMgmtException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.scheduling.annotation.Async;

/**
 * 
 * This class implements the Service Management component of SOCIETIES
 * 
 * @author pkuppuud
 * @author sanchocsa
 */
public class ServiceManagement implements BundleContextAware{

	private IServiceRegistry serviceReg;
	private BundleContext bundleContext;

	static final Logger logger = LoggerFactory.getLogger(ServiceManagement.class);

	public IServiceRegistry getServiceReg() {
		return serviceReg;
	}

	public void setServiceReg(IServiceRegistry serviceReg) {
		this.serviceReg = serviceReg;
	}

	public boolean IsServiceRegistryActive() {

		if(logger.isDebugEnabled()) logger.debug("ServiceManagement: IsServiceRegistryActive()");
		
		// Return
		
		return false;
	}

	public void cleanServiceRegistry() {

		if(logger.isDebugEnabled()) logger.debug("Service Management: cleanServiceRegistry()");
		
		try{
			if(logger.isDebugEnabled()) logger.debug("Getting all services in the service registry");
			
			// We need some method in the service registry to clean it automatically
			//Toni//
			//Object filter = "*.*"; //placeholder for a filter to all
			Service filter=new Service();
			//Toni//
			List<Service> servicesToRemove= getServiceReg().findServices(filter);
			
			if(logger.isDebugEnabled()) logger.debug("Now unregistering all the services.");
		
			getServiceReg().unregisterServiceList(servicesToRemove);
			
			if(logger.isInfoEnabled()) logger.info("Service Registry Cleaned");
			
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception occured while cleaning service registry: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}
		

	}

	public void startService(ServiceResourceIdentifier serviceId) {

		if(logger.isDebugEnabled()) logger.debug("Service Management: startService method");

		try{
			// Get the service from the repository
			if(logger.isDebugEnabled()) logger.debug("Attempting to get service from registry");
			Service serviceToStart = getServiceReg().retrieveService(serviceId);
			
			// Does it exist?
			if(serviceToStart == null){
				logger.info("Service " + serviceId.getIdentifier() + " not found!");
				return;
			}
			
			// It exists, so we do whatever we need to do to start the service.
			
			
			// After it starts, we set the status to started
			//serviceToStart.setServiceStatus(ServiceStatus.STARTED);
			if(logger.isInfoEnabled()) logger.info("Service " + serviceToStart.getServiceName() + " has been started.");
			
			// And update it in the repository
			if(logger.isDebugEnabled()) logger.debug("Telling repository to update!");
			
		} catch(Exception ex){
			logger.error("Exception occured while starting Service: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}			
	}

	public void stopService(ServiceResourceIdentifier serviceId) {

		if(logger.isDebugEnabled()) logger.debug("Service Management: stopService method");

		try{
			// Get the service from the repository
			if(logger.isDebugEnabled()) logger.debug("Attempting to get service from registry");
			Service serviceToStop = getServiceReg().retrieveService(serviceId);
			
			// Does it exist?
			if(serviceToStop == null){
				logger.info("Service " + serviceId.getIdentifier() + " not found!");
				return;
			}
		
			// It exists, so we do whatever we need to do to stop the service.

			// After it starts, we set the status to started
			//serviceToStop.setServiceStatus(ServiceStatus.STARTED);
			if(logger.isInfoEnabled()) logger.info("Service " + serviceToStop.getServiceName() + " has been stopped.");
			
			// And update it in the repository
			if(logger.isDebugEnabled()) logger.debug("Telling repository to update!");
			
			
		} catch(Exception ex){
			logger.error("Exception occured while stopping Service: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}
	}

	public ServiceStatus getServiceStatus(ServiceResourceIdentifier serviceId) {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: startService method");
		
		try{
			// Get the service from the repository
			if(logger.isDebugEnabled()) logger.debug("Attempting to get service from registry");
			Service service = getServiceReg().retrieveService(serviceId);
			
			// Does it exist?
			if(service == null){
				logger.info("Service " + serviceId.getIdentifier() + " not found!");
				return ServiceStatus.UNAVAILABLE;
			} else
				return service.getServiceStatus();
			
		} catch(Exception ex){
			logger.error("Exception occured while getting Service Status: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
			return ServiceStatus.UNAVAILABLE; //TODO fix this.
		}
		
		
	}

	public void addServices() {

		if(logger.isDebugEnabled()) logger.debug("Service Management: addServices method");

		
	}

	public void removeServices() {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: removeServices method");

		List<Service> servicesToRemove = new ArrayList<Service>(); // Temporary, while we fix this.
		
		try{
			logger.info("Removing Services from Repository");
			
			if(logger.isDebugEnabled()){
				String debugMessage = "Attempting to remove a list of " + servicesToRemove.size() + " services from the registry:\n ";
				
				for(Service service : servicesToRemove ){
					debugMessage += service.getServiceName() + "_" + service.getServiceInstance().getServiceImpl().getServiceVersion() + '\n';
				}
				
				logger.debug(debugMessage);
			}

			// We remove them from the service repository
			getServiceReg().unregisterServiceList(servicesToRemove);
			
						
		} catch(Exception ex){
			logger.error("Exception occured while removing services: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}
		
	}

	public void updateServices() {
		// TODO Auto-generated method stub
		
	}

	public Collection<Service> findAllServices() {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: findAllServices method");
		
		Collection<Service> result = new ArrayList<Service>();

		try{
			if(logger.isDebugEnabled()) logger.debug("Getting All Services");

			// TODO this needs to be changed, so we have a filter for all the services
			//Toni//
			//Object filter = "*.*"; //placeholder for a filter to all
			Service filter=new Service();
			result = getServiceReg().findServices(filter);
			
			// Print out all the services that we've obtained
			if(logger.isDebugEnabled()){

				String debugMessage = "Obtained " + result.size() + " services from Registry:\n";
				for(Service service : result){
					debugMessage += service.getServiceName() + "_" + service.getServiceInstance().getServiceImpl().getServiceVersion() + '\n';
				}
				
				logger.debug(debugMessage);
			
			}
			
		} catch(Exception ex){
			logger.error("Exception occured: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}
		
		return result;
	}

	/**
	 * This method returns the Service  identified by a given service identifier
	 * @return the Service object that represents the Service, null if Service is not found
	 * @param the ServiceResourceIdentifier of the desired Service 
	 */
	public Service findService(ServiceResourceIdentifier serviceId) throws ServiceMgmtException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: findService method");

		try{
			if(logger.isDebugEnabled()) logger.debug("Service Management: Getting service from Repository");
			return getServiceReg().retrieveService(serviceId);
			
		} catch(Exception ex){
			logger.error("Exception while finding service: " + ex.getMessage());
			throw new ServiceMgmtException(ex.getMessage());
		}
		
	}

	@Async
	public void processServiceMetaData(List<URL> serviceMetaFileList, long bndlId, String bndlSymName) 
			throws ServiceMgmtException {
			
	}

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext=bundleContext;		
	}	
	
	public BundleContext setBundleContext(){
		return this.bundleContext;
	}
	
	public void registerListener(){
		
	}
	
	/*
	 * This function is a temporary placeholder for a second phase, when we have the service marketplace there
	 */
	public boolean installService(URL serviceUrl){
		
		// Get reference to the marketplace
		
		// Do whatever security and authorization is needed.
		
		// Download and install bundle...
		String serviceBundlelocation = null;
		try {
			bundleContext.installBundle(serviceBundlelocation);
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
		}
		
		return false;
	}


}
