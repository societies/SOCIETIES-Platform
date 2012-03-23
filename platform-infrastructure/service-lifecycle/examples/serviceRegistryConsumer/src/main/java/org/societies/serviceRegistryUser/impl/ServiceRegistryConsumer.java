package org.societies.serviceRegistryUser.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

// import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;


import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceLocation;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.springframework.stereotype.Component;

@Component
public class ServiceRegistryConsumer {
	private IServiceRegistry serReg;

	@Autowired
	public ServiceRegistryConsumer(IServiceRegistry serReg) {
		System.out.print("ServiceRegistryUser constructor called");
		this.serReg = serReg;
		System.out.print("IServiceRegistry reference set");
	}

	public ServiceRegistryConsumer() {
	}

	public IServiceRegistry getSerReg() {
		return serReg;
	}

	public void setSerReg(IServiceRegistry serReg) {
		this.serReg = serReg;
	}

	public void init() throws Exception {
		serReg.registerServiceList(generateServiceList(12));
		Service tmpServiceFilter=new Service();
	    tmpServiceFilter.setServiceName("%");
		List<Service> returnedList=serReg.findServices(tmpServiceFilter);
		System.out.println("Returned list: "+returnedList.size());
	}
	/* Utilities methods */
	private List<Service> generateServiceList(int numberOfService) {
		List<Service> returnedServiceList = new ArrayList<Service>();
		Service result = null;
		ServiceInstance si = null;
		ServiceImplementation servImpl = null;
		for (int i = 0; i < numberOfService; i++) {
			try {
				result = new Service();
				ServiceResourceIdentifier sid = new ServiceResourceIdentifier();
				sid.setIdentifier(new URI("societies","the/path/of/the/service/v"+i,null));
				sid.setServiceInstanceIdentifier("instance_"+i);
				result.setServiceIdentifier(sid);
				result.setAuthorSignature("authorSignaturexx");
				result.setServiceDescription("serviceDescription" + i);
				result.setServiceEndpoint("serviceEndPoint");
				result.setServiceName("serviceName" + i);
				result.setServiceType(ServiceType.CORE_SERVICE);
				result.setServiceLocation(ServiceLocation.LOCAL);
				result.setServiceStatus(ServiceStatus.STARTED);
				si = new ServiceInstance();
				si.setFullJid("fullJid"+i);
				si.setXMPPNode("XMPPNode"+i);
				servImpl = new ServiceImplementation();
				servImpl.setServiceNameSpace("net.calendar");
				servImpl.setServiceProvider("net.soluta");
				servImpl.setServiceVersion("1.0");
				si.setServiceImpl(servImpl);
				result.setServiceInstance(si);
				returnedServiceList.add(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnedServiceList;
	}
}
