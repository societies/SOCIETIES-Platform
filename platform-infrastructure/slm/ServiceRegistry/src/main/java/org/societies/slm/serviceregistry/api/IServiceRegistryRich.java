package org.societies.slm.serviceregistry.api;

import java.util.List;

public interface IServiceRegistryRich {

	/*
	 * Description: This Method provides the interface to add a new list of services.
	 * 				List services can be composed at least by only service at time
	 * @return An Object representing ServiceID
	 */

	public Object RegisterServiceList (List<Object> servicesList);

	/*
	 * Description: This method permits you to unregister a services list 
	 * @return true if the services were successfully removed
	 */
	
	public boolean UnregisterServiceList (List<Object> servicesList);

	/* 
	 * Description: This method syncs local registry with societies 
	 * @return true if the local registry was successfully synced
	 */

	public boolean SyncLocalRegistry ());

	/* 
	 * Description: Based on a Filter this method returns the services list inside the registry 
	 * @return a list of services retrieved
	 */

	public List<Object> SearchServices (Object filter);

	/* 
	 * Description: This method permits you to share a particular service with another CSS 
	 * @return true if the system was able to share the service
	 */
	
	public boolean ShareService (Object CSSID, Object serviceID);
}