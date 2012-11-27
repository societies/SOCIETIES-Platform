package org.societies.api.services;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * Collection of utility methods.
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceModelUtils {


	
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
	
}
