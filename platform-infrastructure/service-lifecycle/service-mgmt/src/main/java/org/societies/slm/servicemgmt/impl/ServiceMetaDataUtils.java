package org.societies.slm.servicemgmt.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


public class ServiceMetaDataUtils {

	public ServiceMetaDataUtils() {
		
	}

	public static ServiceResourceIdentifier generateServiceResourceIdentifier(Service service){
		// ***** To do ********
		// some logic to map available meta data and xmpp service identity 
		// and construct serviceResourceIdentity object
		// then pass return this object
		ServiceResourceIdentifier serResId=new ServiceResourceIdentifier();		
		try {
			serResId.setIdentifier(new URI("http://perumal@societies.org/pkcss/someservice"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serResId;
		
	}
}
