package org.societies.slm.servicemgmt.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.osgi.framework.Bundle;
import org.societies.api.schema.servicelifecycle.model.Service;
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
			serResId.setIdentifier(new URI("http://" + service.getServiceEndpoint() + "/" + service.getServiceName().replaceAll(" ", "")));
			//This next line is for solving https://redmine.ict-societies.eu/issues/619
			serResId.setServiceInstanceIdentifier(String.valueOf(serBndl.getBundleId()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serResId;
		
	}
}
