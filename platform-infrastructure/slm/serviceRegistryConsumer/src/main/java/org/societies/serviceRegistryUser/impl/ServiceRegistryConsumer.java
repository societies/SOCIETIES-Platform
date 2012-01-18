package org.societies.serviceRegistryUser.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

// import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
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
		/* Store a mock service */
		ServiceResourceIdentifier sri = null;
		try {
			sri = new ServiceResourceIdentifier(new URI("xmpp://test1"));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String cSSIDInstalled = "cSSIDInstalled";
		Service newlocalservice = new Service(sri, cSSIDInstalled, "0.0.1",
				"serviceName1", "serviceDescription1", "authorSignature1");

		List<Service> services = new ArrayList<Service>();
		services.add(newlocalservice);
		try {
			this.serReg.registerServiceList(services);
		} catch (ServiceRegistrationException e) {
			e.printStackTrace();
		}

		/* Retrieve a mock service
		Service newTestingService = this.serReg.retrieveService(sri);
		newTestingService.getServiceName(); */

		/* Delete a mock service */
		this.serReg.unregisterServiceList(services);
	}
}
