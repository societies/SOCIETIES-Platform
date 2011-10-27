package org.societies.cssserdiscovery.servicebrowser.api;

import java.util.Collection;
import java.util.List;

/**
 * This is the initial interface for the Service Browser. I think we still need methods to tell
 * it to share, stop and start the service.
 * 
 */
public interface IServiceBrowser {
	
	public void selectService(Object service);
	
	public void displayServiceList();

}
