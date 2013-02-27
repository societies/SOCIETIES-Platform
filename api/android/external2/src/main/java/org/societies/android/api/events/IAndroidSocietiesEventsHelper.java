package org.societies.android.api.events;

import org.societies.android.api.events.IPlatformEventsCallback;

import org.societies.android.api.services.ICoreSocietiesServices;
/**
 * Companion interface to the <{@link IAndroidSocietiesEvents} interface 
 * Provides a more user-friendly interface to Societies Android Platform Events service.
 * Extends the base {@link ICoreSocietiesServices} interface
 *
 */
public interface IAndroidSocietiesEventsHelper extends ICoreSocietiesServices {
	
	/**
	 * Subscribe to a specified Societies platform event (Android Intent)
	 * 
	 * @param societiesIntent specific event intent
	 * @param callback callback to return the value
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToEvent(String societiesIntent, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Subscribe to Societies platform events (Android Intent), specified with a filter. All platform events 
	 * that start with the filter will be subscribed to.
	 * 
	 * @param intentFilter event filter
	 * @param callback callback to return the value
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToEvents(String intentFilter, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Subscribe to all platform events. This should only be used if really required.
	 * 
	 * @param callback callback to return the value
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToAllEvents(IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Un-subscribe from a specified Societies platform event (Android Intent)
	 * 
	 * @param societiesIntent specific event intent
	 * @param callback callback to return the value
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromEvent(String societiesIntent, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Un-subscribe from Societies platform events (Android Intent), specified with a filter. All platform events 
	 * that start with the filter will be un-subscribed from.
	 * 
	 * @param intentFilter event filter
	 * @param callback callback to return the value
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromEvents(String intentFilter, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Un-subscribe from all current platform event subscriptions.
	 * 
	 * @param callback callback to return the value
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromAllEvents(IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Publish an event to the Societies platform for consumption by other CSS nodes
	 * 
	 * @param societiesIntent specific event intent
	 * @param eventPayload event object
	 * @param callback callback to return the value
	 * @return boolean - returned via Android intent
	 */
	boolean publishEvent(String societiesIntent, Object eventPayload, IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Obtain the current number of subscribed to events
	 * 
	 * @param callback callback to return the value
	 * @return int number of subscribed to events (returned in String form)
	 */
	int getNumSubscribedNodes(IPlatformEventsCallback callback) throws PlatformEventsHelperNotConnectedException;

}
