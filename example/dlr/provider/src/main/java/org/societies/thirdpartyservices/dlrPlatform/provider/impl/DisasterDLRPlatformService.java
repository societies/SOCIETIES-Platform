package org.societies.thirdpartyservices.dlrPlatform.provider.impl;

import org.societies.thirdpartyservices.dlrPlatform.api.IDisasterDLRPlatformService;


public class DisasterDLRPlatformService implements IDisasterDLRPlatformService {
	
	public String createUser(String email, String password, String lastName,
			String firstName, String institute) {
		// TODO Auto-generated method stub
		return "SUCCESS: Created user";
	}

	public String createTicket(String title, String description, String date) {
		// TODO Auto-generated method stub
		return "SUCCESS: Created Ticket";
	}
	
}
