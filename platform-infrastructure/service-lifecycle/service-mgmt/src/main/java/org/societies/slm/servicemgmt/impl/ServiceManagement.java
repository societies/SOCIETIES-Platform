package org.societies.slm.servicemgmt.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.serviceMgmt.IServiceManagement;
import org.societies.api.internal.servicelifecycle.serviceMgmt.ServiceMgmtException;
import org.societies.api.internal.servicelifecycle.serviceMgmt.ServiceStatus;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.scheduling.annotation.Async;


public class ServiceManagement implements IServiceManagement, BundleContextAware{

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
			Object filter = "*.*"; //placeholder for a filter to all
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
				//return service.getServiceStatus();
				return ServiceStatus.STOPPED; // TODO fix this.

			
		} catch(Exception ex){
			logger.error("Exception occured while getting Service Status: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}
		
		return ServiceStatus.UNAVAILABLE;
		
	}

	public void addServices() {
		// TODO Auto-generated method stub
		
	}

	public void removeServices() {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: removeServices method");

		try{
			// Get the service from the repository
			if(logger.isDebugEnabled()) logger.debug("Attempting to remove a list of services from the registry");

			//getServiceReg().unregisterServiceList();
			
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
			Object filter = "*.*";
			result = getServiceReg().findServices(filter);
			
			// Print out all the services that we've obtained
			if(logger.isDebugEnabled()){

				String debugMessage = "Obtained " + result.size() + " services from Registry:\n";
				for(Service service : result){
					debugMessage += service.getServiceName() + "_" + service.getVersion() + '\n';
				}
				
				logger.debug(debugMessage);
			
			}
			
		} catch(Exception ex){
			logger.error("Exception occured: " + ex.getMessage());
			//throw new ServiceMgmtException(ex.getMessage());
		}
		
		return result;
	}

	@Override
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

	@Override
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
