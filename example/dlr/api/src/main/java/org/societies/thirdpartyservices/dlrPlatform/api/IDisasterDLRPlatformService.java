package org.societies.thirdpartyservices.dlrPlatform.api;


public interface IDisasterDLRPlatformService {
	
	public String createUser(String email, String password, String lastName, String firstName, String institute);
	
	public String createTicket(String title, String description, String date); // dateformat:09/28/2012 12:00 am

}