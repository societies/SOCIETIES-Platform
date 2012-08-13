package org.societies.api.internal.servicelifecycle;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Collection of utility methods.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
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
		filter.setPrivacyPolicy(null);
		
		ServiceResourceIdentifier filterIdentifier = new ServiceResourceIdentifier();
		filterIdentifier.setIdentifier(null);
		filterIdentifier.setServiceInstanceIdentifier(null);
		filter.setServiceIdentifier(filterIdentifier);
		
		ServiceInstance filterInstance = new ServiceInstance();
		filterInstance.setFullJid(null);
		filterInstance.setXMPPNode(null);
		filterInstance.setCssJid(null);
		
		ServiceImplementation filterImplementation = new ServiceImplementation();
		filterImplementation.setServiceVersion(null);
		filterImplementation.setServiceNameSpace(null);
		filterImplementation.setServiceProvider(null);
		filterImplementation.setServiceClient(null);
		
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
		
		Long bundleId = getBundleIdFromServiceIdentifier(service.getServiceIdentifier());
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
		//filter.getServiceInstance().getServiceImpl().setServiceVersion(bundle.getVersion().toString());
		
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
			Long serBundleId = getBundleIdFromServiceIdentifier(service.getServiceIdentifier());
			
			if(serBundleId == bundle.getBundleId()){
				result = service;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * This method returns the Jid of the node where the service exists
	 * 
	 * @param serviceId
	 * @return the requested Jid
	 */
	public static String getJidFromServiceIdentifier(ServiceResourceIdentifier serviceId){
		
		return serviceId.getIdentifier().toString();
	}
	
	/**
	 * This method takes a Service Resource Identifier and returns the id of the bundle
	 * 
	 * @param serviceId
	 * @return the bundle Id
	 */
	public static Long getBundleIdFromServiceIdentifier(ServiceResourceIdentifier serviceId){
		return Long.parseLong(serviceId.getServiceInstanceIdentifier());
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
	
	/**
	 * This method generates a Service Resource Identifier; it is meant to be used by third-party services
	 * in order to determine their own SRI.
	 * 
	 * @param identity The IIdentity of the node where this service is running
	 * @param callingClass The service class
	 * @return The SRI of the associated service
	 */
	public static ServiceResourceIdentifier generateServiceResourceIdentifier(IIdentity identity, java.lang.Class<?> callingClass){
		
		// First we get the calling Bundle
		Bundle serviceBundle = FrameworkUtil.getBundle(callingClass);
		
		ServiceResourceIdentifier result = new ServiceResourceIdentifier();
		try {
			result.setIdentifier(new URI(identity.getJid()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.setServiceInstanceIdentifier(String.valueOf(serviceBundle.getBundleId()));
		//result.setServiceInstanceIdentifier(value);
		
		return result;
	}
	
	/**
	 * This method generates a ServiceResourceIdentifier given the ServiceBundle and the Service Model object
	 *
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
			serResId.setIdentifier(new URI(service.getServiceInstance().getFullJid()));
			//This next line is for solving https://redmine.ict-societies.eu/issues/619
			serResId.setServiceInstanceIdentifier(String.valueOf(serBndl.getBundleId()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serResId;
		
	}
	
	/**
	 * This method determines if a service belongs to the current node
	 * 
	 * @param service
	 * @param commManager
	 * @return true or false
	 * @throws InvalidFormatException
	 */
	public static boolean isServiceOurs(Service service, ICommManager commManager) throws InvalidFormatException{
		
		IIdentity ourNode = commManager.getIdManager().getThisNetworkNode();
		IIdentity serviceNode = commManager.getIdManager().fromFullJid(service.getServiceInstance().getFullJid());
			
		return ourNode.equals(serviceNode);

	}

}
