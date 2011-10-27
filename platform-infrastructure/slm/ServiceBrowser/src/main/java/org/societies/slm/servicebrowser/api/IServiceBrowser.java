package org.societies.slm.servicebrowser.api;

import java.util.List;

public interface IServiceBrowser {
	/*
	 * Description: Based on a Filter this method returns the services list 
	 * 				matching that particular filter
	 */
	public List<Object> GetServices (Object filter);

	/* 
	 * Description: This method invokes the selected Services giving access to 
	 * 				the Service RTE to make the service runs 
	 */

	public void InvokeSelectedService (Object serviceID);
	
	/* 
	 * Description: This method gives out more details on a particular selected service 
	 * Returning parameter: an Object representing ServiceDetails
	 */

	public Object GetServiceDetail (Object serviceID);
}