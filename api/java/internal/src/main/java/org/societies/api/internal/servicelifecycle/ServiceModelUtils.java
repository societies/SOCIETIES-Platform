package org.societies.api.internal.servicelifecycle;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


public class ServiceModelUtils {

	private ServiceModelUtils() {
		
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
	 * This method is used to obtain the Bundle Id that corresponds to a given a Service
	 * 
	 * @param The Service object whose bundle we wish to find
	 * @param The bundleContext
	 * @return The Bundle that exposes this service
	 */
	public static Bundle getBundleFromService(Service service, BundleContext bundleContext) {
		
		Long bundleId =Long.parseLong(service.getServiceIdentifier().getServiceInstanceIdentifier());
		return bundleContext.getBundle(bundleId);
		
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
		
		return serviceId.getIdentifier().getHost();
	}
	
	/**
	 * This method returns the textual description of a Bundle state
	 * 
	 * @param the state of the service
	 * @return The textual description of the bundle's state
	 */
	public static String getBundleStateName(int state){
		
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
	
	
	public static ServiceResourceIdentifier generateServiceResourceIdentifier(IIdentity identity, java.lang.Class<?> callingClass){
		
		// First we get the calling Bundle
		Bundle serviceBundle = FrameworkUtil.getBundle(callingClass);

		// Then we get the serviceReference
		ServiceReference<?>[] serviceReferenceList = serviceBundle.getRegisteredServices();
		ServiceReference ourService = null;
		for(ServiceReference<?>  serviceReference : serviceReferenceList){
			String targetPlatform = (String) serviceReference.getProperty("TargetPlatform");
			if(targetPlatform != null && targetPlatform.equals("SOCIETIES")){
				ourService = serviceReference;
				break;
			}
		}
		
		// We now have the right ServiceReference, the Bundle and the local IIdentity. Time to generate the ServiceResourceIdentifier
		
		identity.getJid();
		
		ServiceResourceIdentifier result = new ServiceResourceIdentifier();
		//result.setServiceInstanceIdentifier(value);
		
		return null;
	}
	
	/**
	 *  This method generates a ServiceResourceIdentifier given the ServiceBundle
	 * @param service
	 * @param serBndl
	 * @return the ServiceResourceIdentifier
	 */
	public static ServiceResourceIdentifier generateServiceResourceIdentifier(Service service, Bundle serBndl){
		// ***** To do ********
		// some logic to map available meta data and xmpp service identity 
		// and construct serviceResourceIdentity object
		// then pass return this object
		ServiceResourceIdentifier serResId=new ServiceResourceIdentifier();		
		try {
			serResId.setIdentifier(new URI("http://" + service.getServiceEndpoint()));
			//This next line is for solving https://redmine.ict-societies.eu/issues/619
			serResId.setServiceInstanceIdentifier(String.valueOf(serBndl.getBundleId()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serResId;
		
	}
}
