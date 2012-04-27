package org.societies.thirdpartyservices.dlrPlatform.consumer.impl;

import org.societies.thirdpartyservices.dlrPlatform.api.IDisasterDLRPlatformService;

public class DisasterDLRPlatformConsumer {

	private IDisasterDLRPlatformService service;
	//private Logger LOG =
	
	public DisasterDLRPlatformConsumer(IDisasterDLRPlatformService service)
	{
		this.service = service;
	}
	
	public void startConsumer()
	{
		System.out.println("start consumer");
		System.out.println("output:" + service.createUser(null, null, null, null, null));
	}
	
	public void stopConsumer()
	{
		System.out.println("stop consumer");
	}
}
