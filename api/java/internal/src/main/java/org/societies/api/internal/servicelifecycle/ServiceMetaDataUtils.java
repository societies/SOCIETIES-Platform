package org.societies.api.internal.servicelifecycle;

import java.util.List;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


public class ServiceMetaDataUtils {

	private ServiceMetaDataUtils() {
		
	}


	/**
	 *  This method generates a Service object that can be used as a filter for the Service Discovery search methods.
	 * 
	 * @return The Service filter object
	 */
	public static Service generateEmptyFilter(){
		
		Service filter = new Service();

		// Preparing the search filter
		filter.setAuthorSignature(null);
		filter.setServiceDescription(null);
		filter.setServiceEndpoint(null);
		filter.setServiceName(null);
		filter.setServiceStatus(null);
		filter.setServiceType(null);
		
		ServiceResourceIdentifier filterIdentifier = new ServiceResourceIdentifier();
		filterIdentifier.setIdentifier(null);
		filterIdentifier.setServiceInstanceIdentifier(null);
		filter.setServiceIdentifier(filterIdentifier);
		
		ServiceInstance filterInstance = new ServiceInstance();
		filterInstance.setFullJid(null);
		filterInstance.setXMPPNode(null);
		
		ServiceImplementation filterImplementation = new ServiceImplementation();
		filterImplementation.setServiceVersion(null);
		filterImplementation.setServiceNameSpace(null);
		filterImplementation.setServiceProvider(null);
		
		filterInstance.setServiceImpl(filterImplementation);
		filter.setServiceInstance(filterInstance);
		
		return filter;
	}
	
	/**
	 *  This method compares to ServiceResourceIdentifiers to determine if they are equal. It is, effectively, an "equals" method
	 *  as the ServiceResourceIdentifier does not have one, since it is a bean.
	 *  
	 * @param serviceId
	 * @param otherServiceId
	 * @return true if they are equal, false otherwise.
	 */
	public static boolean compare(ServiceResourceIdentifier serviceId, ServiceResourceIdentifier otherServiceId){
		
		if(serviceId == null || otherServiceId == null)
			return false;
		
		if(serviceId.getIdentifier().equals(otherServiceId.getIdentifier()) && serviceId.getServiceInstanceIdentifier().equals(otherServiceId.getServiceInstanceIdentifier()))
			return true;
		else
			return false;
			
	}
	
	/**
	 * This method returns the textual description of a Bundle state
	 * 
	 * @param the state of the service
	 * @return The textual description of the bundle's state
	 */
	public static String getStateName(int state){
		
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
	 * This method is used to obtain the Bundle Id that corresponds to a given a Service
	 * 
	 * @param The Service object whose bundle we wish to find
	 * @return The BundleId that exposes this service
	 */
	public static Long getBundleIdFromService(Service service) {
		
		return Long.parseLong(service.getServiceIdentifier().getServiceInstanceIdentifier());
	}

	/**
	 * This method is used to obtain the Service that is exposed by given Bundle
	 * 
	 * @param The Bundle that exposes this service
	 * @param A reference to IServiceDiscovery
	 * @return The Service object whose bundle we wish to find
	 */
	public static Service getServiceFromBundle(Bundle bundle, IServiceDiscovery serviceDiscovery) {
			
		// Preparing the search filter
		Service filter = generateEmptyFilter();
		filter.getServiceIdentifier().setServiceInstanceIdentifier(String.valueOf(bundle.getBundleId()));
		filter.getServiceInstance().getServiceImpl().setServiceVersion(bundle.getVersion().toString());
		
		List<Service> listServices = null;
		
		try {
			Future<List<Service>> asyncListServices = serviceDiscovery.searchServices(filter);
			listServices = asyncListServices.get();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(listServices == null || listServices.isEmpty())
			return null; 
		
		Service result = null;

		for(Service service: listServices){
			Long serBundleId = Long.parseLong(service.getServiceIdentifier().getServiceInstanceIdentifier());
			
			if(serBundleId == bundle.getBundleId()){
				result = service;
				break;
			}
		}
		
		return result;
	}
	
	public static String getJidFromServiceIdentifier(ServiceResourceIdentifier serviceId){
		
		serviceId.getIdentifier();
	}
}
