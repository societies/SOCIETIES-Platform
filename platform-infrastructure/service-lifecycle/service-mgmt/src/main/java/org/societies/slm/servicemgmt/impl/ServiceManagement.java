package org.societies.slm.servicemgmt.impl;

import java.net.URL;
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
	private BundleContext bctx;

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

		if(logger.isDebugEnabled()) logger.debug("ServiceManagement: cleanServiceRegistry()");
		

	}

	public void startService(ServiceResourceIdentifier serviceId) {

		if(logger.isDebugEnabled()) logger.debug("ServiceManagement: startService()");
		
	}

	public void stopService(ServiceResourceIdentifier serviceId) {

		if(logger.isDebugEnabled()) logger.debug("ServiceManagement: stopService()");

	}

	public ServiceStatus getServiceStatus(ServiceResourceIdentifier serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addServices() {
		// TODO Auto-generated method stub
		
	}

	public void removeServices() {
		// TODO Auto-generated method stub
		
	}

	public void updateServices() {
		// TODO Auto-generated method stub
		
	}

	public Collection<Service> findAllServices() {
		// TODO Auto-generated method stub
		return null;
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
	public void setBundleContext(BundleContext bctx) {
		this.bctx=bctx;		
	}	
	
	public BundleContext setBundleContext(){
		return this.bctx;
	}
	
	public void registerListener(){
		
	}
}
