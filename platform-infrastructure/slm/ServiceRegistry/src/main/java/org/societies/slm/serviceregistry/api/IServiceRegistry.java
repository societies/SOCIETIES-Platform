package org.societies.slm.serviceregistry.api;

import java.util.List;

public interface IServiceRegistry {

	/*
	 * Description: This method provides the interface to add a new list of services.
	 * 				List services can be composed at least by only service at time
	 * Returning parameter: an Object representing ServiceID
	 */

	public Object RegisterServiceList (List<Object> servicesList);

	/*
	 * Description: This method permits you to unregister a services list 
	 */
	
	public boolean UnregisterServiceList (List<Object> servicesList);

	/* 
	 * Description: This method syncs local registry with societies 
	 */

	public List<Object> UpdateLocalRegistry (Object CSSID);

	/* 
	 * Description: Based on a Filter this method returns the services list inside the registry 
	 */

	public List<Object> SearchServices (Object filter);

	/* 
	 * Description: This method permits you to share a particular service with another CSS 
	 */
	
	public boolean ShareService (Object CSSID, Object serviceID);
}