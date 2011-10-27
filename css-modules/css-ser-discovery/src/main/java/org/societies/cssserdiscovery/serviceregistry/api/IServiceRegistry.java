package org.societies.cssserdiscovery.serviceregistry.api;

import java.util.Collection;
import java.util.List;

/**
 *  Initial interface for the Service Registry component
 *
 */
public interface IServiceRegistry {

	/**
	 * Registers a service in the Service Registry
	 */
	public void registerService(Object callback, Object service);
	
	/**
	 * Unregisters a service from the Service Registry
	 */
	public void unregisterService(Object callback, Object service);
	
	/**
	 * Registers a list of services in the Service Registry
	 */
	public void addServiceList(Object callback, List<Object> services);

	/**
	 * Unregisters a list of services from the Service Registry
	 */
	public void removeServiceList(Object callback, List<Object> services);
	
	/**
	 * Used to sync the list of services from the registry syncronizer
	 */
	public void syncServiceList(Object callback, List<Object> services);
	
	/**
	 *  Gets a service
	 */
	public void getService(Object callback, Object serviceIdentifier);
	
	/**
	 *  Gets the list of available services
	 */
	public void getServiceList(Object callback);
	
	/***
	 * Used to set the shared service of a service
	 */
	
	public void shareService(Object callback, Object Service, boolean shared);
	
	/***
	 * Used to set the active service of a service => this was a status defined in the initial lifecycle definition.
	 * It is up for debate if this this information should be in the service registry.
	 */
	
	public void activateService(Object callback, Object Service, boolean activate);
	
}
