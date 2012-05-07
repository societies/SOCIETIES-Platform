package org.societies.slm.servicecontrol;

import java.net.URI;
import java.net.URISyntaxException;

import org.osgi.framework.Bundle;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


public class ServiceMetaDataUtils {

	public ServiceMetaDataUtils() {
		
	}

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
	
}
