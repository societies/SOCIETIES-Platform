package org.societies.api.internal.servicelifecycle;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.eclipse.osgi.internal.signedcontent.Base64;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.services.ServiceMgmtEventType;


/**
 * Collection of utility methods.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceModelUtils extends org.societies.api.services.ServiceUtils{


	private ServiceModelUtils() {
		super();
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
	 * This method is used to obtain the Bundle Id that corresponds to a given a Service
	 * 
	 * @param The Service object whose bundle we wish to find
	 * @param The bundleContext
	 * @return The Bundle that exposes this service
	 */
	public static Bundle getBundleFromService(Service service, BundleContext bundleContext) {
		
		//Long bundleId = getBundleIdFromServiceIdentifier(service.getServiceIdentifier());
		return bundleContext.getBundle(service.getServiceLocation());
		
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
		filter.getServiceIdentifier().setServiceInstanceIdentifier(bundle.getSymbolicName());
		filter.setServiceLocation(bundle.getLocation());
		
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
			String bundleSymbolic = service.getServiceIdentifier().getServiceInstanceIdentifier();
			
			if(bundleSymbolic.equals(bundle.getSymbolicName())){
				result = service;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * This method is used to obtain the Service that is exposed by given Bundle
	 * 
	 * @param The ServiceInstance of this service
	 * @param A reference to IServiceDiscovery
	 * @return The Service object 
	 */
	public static Service getServiceFromServiceInstance(String serviceInstance, IServiceDiscovery serviceDiscovery) {
			
		// Preparing the search filter
		Service filter = generateEmptyFilter();
		filter.getServiceIdentifier().setServiceInstanceIdentifier(serviceInstance);
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
			
			if(service.getServiceIdentifier().getServiceInstanceIdentifier().equals(serviceInstance)){
				result = service;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * This method takes a Service Resource Identifier and returns the id of the bundle
	 * 
	 * @param serviceId
	 * @return the bundle Id
	 
	public static Long getBundleIdFromServiceIdentifier(ServiceResourceIdentifier serviceId){
		return Long.parseLong(serviceId.getServiceInstanceIdentifier());
	}
	*/
	
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
		ServiceReference[] registeredService = serviceBundle.getRegisteredServices();
		Service ourService = null;
		for(int i = 0; i < registeredService.length; i++){
			ServiceReference regiServ = registeredService[i];
			String property = (String) regiServ.getProperty("TargetPlatform");
			if(property != null && property.equals("SOCIETIES")){
				ourService = (Service) regiServ.getProperty("ServiceMetaModel");
				break;
			}
		}
		
		ServiceResourceIdentifier result = new ServiceResourceIdentifier();
		try {
			result.setIdentifier(new URI(identity.getJid()+'/'+ourService.getServiceName().replace(' ', '_')));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.setServiceInstanceIdentifier(serviceBundle.getSymbolicName());
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
			serResId.setIdentifier(new URI(service.getServiceInstance().getFullJid()+'/'+ service.getServiceName().replace(' ', '_')));
			//This next line is for solving https://redmine.ict-societies.eu/issues/619
			serResId.setServiceInstanceIdentifier(serBndl.getSymbolicName());
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
	 */
	public static boolean isServiceOurs(Service service, ICommManager commManager){
		
		try{
			IIdentity ourNode = commManager.getIdManager().getThisNetworkNode();
			IIdentity serviceNode = commManager.getIdManager().fromFullJid(getJidFromServiceIdentifier(service.getServiceIdentifier()));
			return ourNode.equals(serviceNode);
		} catch(Exception ex){
			return false;
		}
		

	}


	public static ServiceResourceIdentifier generateServiceResourceIdentifierForDevice(
			Service service, String deviceId) {
		
		ServiceResourceIdentifier serResId=new ServiceResourceIdentifier();		
		try {
			serResId.setIdentifier(new URI(service.getServiceInstance().getFullJid()+"/"+ service.getServiceName().replace(' ', '_')));
			//This next line is for solving https://redmine.ict-societies.eu/issues/619
			serResId.setServiceInstanceIdentifier(deviceId);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serResId;
	}
	
	public static boolean hasClient(ServiceResourceIdentifier serviceId, IServiceDiscovery serviceDiscovery){
		
		return !getClients(serviceId,serviceDiscovery).isEmpty();

	}

	public static List<Service> getClients(ServiceResourceIdentifier service, IServiceDiscovery serviceDiscovery){
		
		List<Service> clients = new ArrayList<Service>();
		
		Service filter = generateEmptyFilter();
		ServiceInstance serviceInstance = filter.getServiceInstance();
		serviceInstance.setParentIdentifier(service);
		filter.setServiceInstance(serviceInstance);
		filter.setServiceType(ServiceType.THIRD_PARTY_CLIENT);
		
		try {
			Future<List<Service>> clientAsync = serviceDiscovery.searchServices(filter);
			clients = clientAsync.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return clients;
		
	}

	/**
	 * This method converts a Service Resource Identifier into a Base64 encoded string.
	 * 
	 * @param serviceId
	 * @return
	 */
	public static String getServiceId64Encode(ServiceResourceIdentifier serviceId){

		return new String(Base64.encode(serviceResourceIdentifierToString(serviceId).getBytes()));
		
	}

	/**
	 * This method takes a Base64 Encoded string and converts it to a Service Resource Identifier
	 * @param encoded64
	 * @return
	 */
	public static ServiceResourceIdentifier getServiceId64Decode(String encoded64){
		
		return generateServiceResourceIdentifierFromString(new String(Base64.decode(encoded64.getBytes())));
	
	}
	
	public static void sendServiceMgmtEvent(ServiceMgmtEventType eventType, Service service,IIdentity node, Bundle bundle, IEventMgr eventManager){
		
		ServiceMgmtInternalEvent serviceEvent = new ServiceMgmtInternalEvent();
		serviceEvent.setEventType(eventType);
		serviceEvent.setSharedNode(node);
		
		if(service != null){
			serviceEvent.setServiceType(service.getServiceType());
			serviceEvent.setServiceId(service.getServiceIdentifier());
			serviceEvent.setServiceName(service.getServiceName());
			
			if(!service.getServiceType().equals(ServiceType.DEVICE)){
				serviceEvent.setBundleId(bundle.getBundleId());
				serviceEvent.setBundleSymbolName(bundle.getSymbolicName());
				serviceEvent.setInterfaceName(service.getServiceInstance().getServiceImpl().getServiceNameSpace());
			} else{
				serviceEvent.setBundleId(-1);
				serviceEvent.setBundleSymbolName(null);
			}
		}
		InternalEvent internalEvent = new InternalEvent(EventTypes.SERVICE_LIFECYCLE_EVENT, eventType.toString(), "org/societies/servicelifecycle", serviceEvent);
		
		try {
			eventManager.publishInternalEvent(internalEvent);
		} catch (EMSException e) {
			e.printStackTrace();
		}
	}

}
