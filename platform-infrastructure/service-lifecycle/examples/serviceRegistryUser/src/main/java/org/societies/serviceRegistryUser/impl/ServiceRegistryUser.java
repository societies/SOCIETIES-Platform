package org.societies.serviceRegistryUser.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceRegistryUser {

	private IServiceRegistry serRegistry;
	private RegistryEntry registryEntry;
	
	@Autowired
	public ServiceRegistryUser(IServiceRegistry serRegistry) {
		System.out.print("ServiceRegistryUser constructor called");
		this.serRegistry = serRegistry;
		System.out.print("IServiceRegistry reference set");		
	}

	@PostConstruct
	public void init(){
		System.out.print("Post construct called");		
		registryEntry=new RegistryEntry();
		registryEntry.setServiceDescription("some service desc");
		registryEntry.setServiceName("some service name");
		registryEntry.setAuthorSignature("some signature");
		registryEntry.setId(57245623);		
		List<RegistryEntry> servicelist=new ArrayList<RegistryEntry>();
		servicelist.add(registryEntry);
		System.out.print("Registering service");
		this.serRegistry.registerServiceList(servicelist);
		
		List<RegistryEntry> getServiceList=new ArrayList<RegistryEntry>();
		serRegistry.registerServiceList(getServiceList);
		String serDesc=getServiceList.get(0).getServiceDescription();
		System.out.print(serDesc);
	}
	

}
