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
	  * @param societiesIntent
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean subscribeToEvent(String societiesIntent, IPlatformEventsCallback callback) 
			throws PlatformEventsHelperNotConnectedException;
	
	 /**
	  * Subscribe to Societies platform events (Android Intent), specified with a filter. All platform events 
	  * that start with the filter will be subscribed to.
	  * 
	  * @param intentFilter
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean subscribeToEvents(String intentFilter, IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException;
	

	 /**
	  * Subscribe to all platform events. This should only be used if really required.
	  * 
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean subscribeToAllEvents(IPlatformEventsCallback callback) 
			throws PlatformEventsHelperNotConnectedException;
	
	 /**
	  * Un-subscribe from a specified Societies platform event (Android Intent)
	  * 
	  * @param societiesIntent
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean unSubscribeFromEvent(String societiesIntent, IPlatformEventsCallback callback) 
			throws PlatformEventsHelperNotConnectedException;

	 /**
	  * Un-subscribe from Societies platform events (Android Intent), specified with a filter. All platform events 
	  * that start with the filter will be un-subscribed from.
	  * 
	  * @param intentFilter
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean unSubscribeFromEvents(String intentFilter, IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException;
	
	 /**
	  * Un-subscribe from all current platform event subscriptions.
	  * 
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean unSubscribeFromAllEvents(IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException;
	
	 /**
	  * Publish an event to the Societies platform for consumption by other CSS nodes
	  * 
	  * @param societiesIntent
	  * @param eventPayload
	  * @param callback
	  * @return
	  * @throws PlatformEventsHelperNotConnectedException
	  */
	 boolean publishEvent(String societiesIntent, Object eventPayload, IPlatformEventsCallback callback) 
			throws PlatformEventsHelperNotConnectedException;
	/**
	 * Obtain the current number of subscribed to events
	 * 
	 * @param callback callback to return the value
	 * @return int number of subscribed to events (returned in String form)
	 */
	int getNumSubscribedNodes(IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException;
	
	/**
	 * Create a new Societies Pubsub node
	 * 
	 * @param pubsubNode Pubsub node to be used in Societies, e.g. org.3rdpartyservice.sampleevent
	 * @param societiesIntent should use Societies format , i.e. org.societies.3rdpartyservice.sampleevent
	 * @return boolean - returned via Android intent
	 */
	boolean createEvent(String pubsubNode, String societiesIntent, IPlatformEventsCallback callback) 
			throws PlatformEventsHelperNotConnectedException;
	/**
	 * Delete a Societies Pubsub node. Can only be used to delete Pubsub nodes previously created by the same client.
	 * 
	 * @param pubsubNode Pubsub node to be used in Societies, e.g. org.3rdpartyservice.sampleevent
	 * @return boolean - returned via Android intent
	 */
	boolean deleteEvent(String pubsubNode, IPlatformEventsCallback callback)
			throws PlatformEventsHelperNotConnectedException;

}
