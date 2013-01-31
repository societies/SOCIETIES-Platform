package org.societies.android.api.internal.servicelifecycle;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.util.Base64;


/**
 * Collection of utility methods.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 * @author Modified for Android by Olivier (Trialog)
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
	 * This method returns the Jid of the node where the service exists
	 * 
	 * @param serviceId
	 * @return the requested Jid
	 */
	public static String getJidFromServiceIdentifier(ServiceResourceIdentifier serviceId){

		String identifier = serviceId.getIdentifier().toString();
		int lastIndex = identifier.lastIndexOf('/');
		return identifier.substring(0, lastIndex);
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

	public static ServiceResourceIdentifier generateServiceResourceIdentifierFromString(String serviceId){

		ServiceResourceIdentifier result = new ServiceResourceIdentifier();

		int index = serviceId.indexOf(' ');	
		String instanceExtract = serviceId.substring(0, index);
		String identifierExtract = serviceId.substring(index+1);

		result.setServiceInstanceIdentifier(instanceExtract);
		try {
			result.setIdentifier(new URI(identifierExtract));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return null;
		}

		return result;
	}

	public static String serviceResourceIdentifierToString(ServiceResourceIdentifier serviceId){

		return serviceId.getServiceInstanceIdentifier() + " " + serviceId.getIdentifier().toString();
	}


	public static String getServiceId64Encode(ServiceResourceIdentifier serviceId){

		return new String(Base64.encode(serviceResourceIdentifierToString(serviceId).getBytes(), Base64.DEFAULT));
	}

	public static ServiceResourceIdentifier getServiceId64Decode(String encoded64){
		return generateServiceResourceIdentifierFromString(new String(Base64.decode(encoded64.getBytes(), Base64.DEFAULT)));
	}
}
