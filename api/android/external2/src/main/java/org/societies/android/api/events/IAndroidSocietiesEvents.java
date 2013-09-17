/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.events;

import org.societies.android.api.css.manager.IServiceManager;

/**
 * Interface defines how 3rd party and optionally, platform components, can
 * subscribe/publish to Societies platform events. The base eventing system used in Societies
 * is based on XMPP Pubsub event nodes. These event nodes form the basis of inter-node
 * messaging.
 *
 * This interface allows local Android components to subscribe/publish XMPP events using a
 * an Android Intents/ XMPP Pubsub events translation.
 *
 * This interface should not be used for Android inter-node eventing - use Intents.
 *
 */
public interface IAndroidSocietiesEvents extends IServiceManager{

	/**
	 * Societies Events intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls.
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.events.ReturnValue";
	public static final String INTENT_EXCEPTION_VALUE_KEY = "org.societies.android.platform.events.ExceptionValue";

	public static final String PUBLISH_EVENT = "org.societies.android.platform.events.PUBLISH_EVENT";
	public static final String SUBSCRIBE_TO_ALL_EVENTS = "org.societies.android.platform.events.SUBSCRIBE_TO_ALL_EVENTS";
	public static final String SUBSCRIBE_TO_EVENT = "org.societies.android.platform.events.SUBSCRIBE_TO_EVENT";
	public static final String SUBSCRIBE_TO_EVENTS = "org.societies.android.platform.events.SUBSCRIBE_TO_EVENTS";
	public static final String UNSUBSCRIBE_FROM_ALL_EVENTS = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_ALL_EVENTS";
	public static final String UNSUBSCRIBE_FROM_EVENT = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_EVENT";
	public static final String UNSUBSCRIBE_FROM_EVENTS = "org.societies.android.platform.events.UNSUBSCRIBE_FROM_EVENTS";
	public static final String NUM_EVENT_LISTENERS = "org.societies.android.platform.events.NUM_EVENT_LISTENERS";
	public static final String CREATE_EVENT = "org.societies.android.platform.events.CREATE_EVENT";
	public static final String DELETE_EVENT = "org.societies.android.platform.events.DELETE_EVENT";
	/**
	 * Invalid values to signify that invalid parameters have been provided to the remote service
	 */
	public static final int INVALID_PUBSUB_NODE = 0;
	public static final int INVALID_SOCIETIES_INTENT = 1;
	public static final int INVALID_SOCIETIES_INTENT_ALREADY_EXISTS = 2;
	public static final int INVALID_PUBSUB_NODE_ALREADY_EXISTS = 3;

	/**
	 * Android intent Societies platform Pubsub related intents
	 */
	//Context Manager intents
	final static String CONTEXT_MANAGER_CREATED_INTENT = "org.societies.android.context.CREATED";
	final static String CONTEXT_MANAGER_UPDATED_INTENT = "org.societies.android.context.UPDATED";
	final static String CONTEXT_MANAGER_MODIFIED_INTENT = "org.societies.android.context.MODIFIED";
	final static String CONTEXT_MANAGER_REMOVED_INTENT = "org.societies.android.context.REMOVED";
	final static String CONTEXT_MODIFIED_LOCATION_SYMBOLIC_INTENT = "org.societies.android.context.MODIFIED.locationSymbolic";

	//CSS Manager intents
	final static String CSS_MANAGER_ADD_CSS_NODE_INTENT    = "org.societies.android.css.manager.addCSSNode";
	final static String CSS_MANAGER_DEPART_CSS_NODE_INTENT = "org.societies.android.css.manager.departCSSNode";
	final static String CSS_FRIEND_REQUEST_RECEIVED_INTENT = "org.societies.android.css.friends.RequestReceived";
	final static String CSS_FRIEND_REQUEST_ACCEPTED_INTENT = "org.societies.android.css.friends.RequestAccepted";
	final static String CSS_MANAGER_FILTER = "org.societies.android.css.manager";
	final static String CSS_FRIEND_FILTER = "org.societies.android.css.friends";

	//Device Manager intents
	final static String DEVICE_MANAGER_DEVICE_REGISTERED_INTENT   = "org.societies.android.device.manager.DEVICE_CONNECTED";
	final static String DEVICE_MANAGER_DEVICE_DISCONNECTED_INTENT = "org.societies.android.device.manager.DEVICE_DISCONNECTED";
	final static String DEVICE_MANAGER_EVENTING_NODE_NAME_INTENT  = "org.societies.android.device.manager.EVENTING_NODE_NAME";

	//UserFeedback intents
	final static String UF_PRIVACY_NEGOTIATION_REQUEST_INTENT  = "org.societies.useragent.feedback.privacyNegotiation";
	final static String UF_PRIVACY_NEGOTIATION_RESPONSE_INTENT = "org.societies.useragent.feedback.privacyNegotiationResponse";
	final static String UF_ACCESS_CONTROL_REQUEST_INTENT       = "org.societies.useragent.feedback.accessControl.REQUEST";
	final static String UF_ACCESS_CONTROL_RESPONSE_INTENT      = "org.societies.useragent.feedback.accessControl.RESPONSE";
	final static String UF_REQUEST_INTENT 		   			   = "org.societies.useragent.feedback.event.REQUEST";
	final static String UF_EXPLICIT_RESPONSE_INTENT  	       = "org.societies.useragent.feedback.event.EXPLICIT_RESPONSE";
	final static String UF_IMPLICIT_RESPONSE_INTENT  	       = "org.societies.useragent.feedback.event.IMPLICIT_RESPONSE";

	//Array of Societies Android Pubsub Intents
	//N.B. Must be in same order as societiesAndroidEvents array to allow successful translation
	final static String societiesAndroidIntents [] = {
										 CONTEXT_MANAGER_CREATED_INTENT,
										 CONTEXT_MANAGER_UPDATED_INTENT,
										 CONTEXT_MANAGER_MODIFIED_INTENT,
										 CONTEXT_MANAGER_REMOVED_INTENT,
										 CSS_MANAGER_ADD_CSS_NODE_INTENT,
										 CSS_MANAGER_DEPART_CSS_NODE_INTENT,
										 CSS_FRIEND_REQUEST_RECEIVED_INTENT,
										 CSS_FRIEND_REQUEST_ACCEPTED_INTENT,
										 DEVICE_MANAGER_DEVICE_REGISTERED_INTENT,
										 DEVICE_MANAGER_DEVICE_DISCONNECTED_INTENT,
										 DEVICE_MANAGER_EVENTING_NODE_NAME_INTENT,
										 UF_PRIVACY_NEGOTIATION_REQUEST_INTENT,
										 UF_PRIVACY_NEGOTIATION_RESPONSE_INTENT,
										 UF_ACCESS_CONTROL_REQUEST_INTENT,
										 UF_ACCESS_CONTROL_RESPONSE_INTENT,
										 UF_REQUEST_INTENT,
                                         UF_EXPLICIT_RESPONSE_INTENT,
                                         UF_IMPLICIT_RESPONSE_INTENT,
                                         CONTEXT_MODIFIED_LOCATION_SYMBOLIC_INTENT
										 };
	/**
	 * Android intent Societies platform Pubsub related events
	 */
	//Context Manager pubsub nodes
	final static String CONTEXT_MANAGER_CREATED_EVENT  = "org/societies/context/change/event/CREATED";
	final static String CONTEXT_MANAGER_UPDATED_EVENT  = "org/societies/context/change/event/UPDATED";
	final static String CONTEXT_MANAGER_MODIFIED_EVENT = "org/societies/context/change/event/MODIFIED";
	final static String CONTEXT_MANAGER_REMOVED_EVENT  = "org/societies/context/change/event/REMOVED";
	final static String CONTEXT_MODIFIED_LOCATION_SYMBOLIC_EVENT = "org/societies/context/change/event/MODIFIED/locationSymbolic";
	
	//CSS Manager pubsub nodes
	final static String CSS_MANAGER_ADD_CSS_NODE_EVENT    = "addCSSNode";
	final static String CSS_MANAGER_DEPART_CSS_NODE_EVENT = "departCSSNode";
	final static String CSS_FRIEND_REQUEST_RECEIVED_EVENT = "friendRequestReceived";
	final static String CSS_FRIEND_REQUEST_ACCEPTED_EVENT = "friendRequestAccepted";

	//Device Manager pubsub nodes
	final static String DEVICE_MANAGER_DEVICE_REGISTERED_EVENT   = "DEVICE_CONNECTED";
	final static String DEVICE_MANAGER_DEVICE_DISCONNECTED_EVENT = "DEVICE_DISCONNECTED";
	final static String DEVICE_MANAGER_EVENTING_NODE_NAME_EVENT  = "EVENTING_NODE_NAME";

	//UserFeedback pubsub nodes
	final static String UF_PRIVACY_NEGOTIATION_REQUEST_EVENT  = "org/societies/useragent/feedback/privacyNegotiation";
	final static String UF_PRIVACY_NEGOTIATION_RESPONSE_EVENT = "org/societies/useragent/feedback/privacyNegotiationResponse";
	final static String UF_ACCESS_CONTROL_REQUEST_EVENT       = "org/societies/useragent/feedback/privacyAccessControl";
	final static String UF_ACCESS_CONTROL_RESPONSE_EVENT      = "org/societies/useragent/feedback/privacyAccessControlResponse";
	final static String UF_REQUEST_EVENT 					  = "org/societies/useragent/feedback/event/REQUEST";
	final static String UF_EXPLICIT_RESPONSE_EVENT 			  = "org/societies/useragent/feedback/event/EXPLICIT_RESPONSE";
	final static String UF_IMPLICIT_RESPONSE_EVENT 			  = "org/societies/useragent/feedback/event/IMPLICIT_RESPONSE";

	//N.B. Must be in same order as societiesAndroidIntents array to allow successful translation
	//N.B. These events must be created at Virgo container start-up
	final static String societiesAndroidEvents [] = {
										 CONTEXT_MANAGER_CREATED_EVENT,
										 CONTEXT_MANAGER_UPDATED_EVENT,
										 CONTEXT_MANAGER_MODIFIED_EVENT,
										 CONTEXT_MANAGER_REMOVED_EVENT,
										 CSS_MANAGER_ADD_CSS_NODE_EVENT,
										 CSS_MANAGER_DEPART_CSS_NODE_EVENT,
										 CSS_FRIEND_REQUEST_RECEIVED_EVENT,
										 CSS_FRIEND_REQUEST_ACCEPTED_EVENT,
										 DEVICE_MANAGER_DEVICE_REGISTERED_EVENT,
										 DEVICE_MANAGER_DEVICE_DISCONNECTED_EVENT,
										 DEVICE_MANAGER_EVENTING_NODE_NAME_EVENT,
										 UF_PRIVACY_NEGOTIATION_REQUEST_EVENT,
										 UF_PRIVACY_NEGOTIATION_RESPONSE_EVENT,
                                         UF_ACCESS_CONTROL_REQUEST_EVENT,
                                         UF_ACCESS_CONTROL_RESPONSE_EVENT,
										 UF_REQUEST_EVENT,
										 UF_EXPLICIT_RESPONSE_EVENT,
                                         UF_IMPLICIT_RESPONSE_EVENT,
                                         CONTEXT_MODIFIED_LOCATION_SYMBOLIC_EVENT
										 };

	//Array of interface method signatures
	//N.B. Must include any extended interface(s) method arrays
	final static String methodsArray [] = {"subscribeToEvent(String client, String societiesIntent)",
			"subscribeToEvents(String client, String intentFilter)",
			"subscribeToAllEvents(String client)",
			"unSubscribeFromEvent(String client, String societiesIntent)",
			"unSubscribeFromEvents(String client, String intentFilter)",
			"unSubscribeFromAllEvents(String client)",
			"publishEvent(String client, String societiesIntent, Object payload)",
			"getNumSubscribedNodes(String client)",
			"startService()",
			"stopService()",
			"createEvent(String client, String pubsubNode, String societiesIntent)",
			"deleteEvent(String client, String pubsubNode)"
	};

	//Pubsub event payload classes
    //TODO: Insert all known event classes. Class must be an <xs:element> in the XSD!!! Not a <xs:complexType>. Check!!!
	static final String CSS_MANAGER_CLASS 	= "org.societies.api.schema.cssmanagement.CssEvent";
	static final String CONTEXT_CLASS 		= "org.societies.api.schema.context.model.CtxIdentifierBean";
	static final String FRIEND_EVENT_CLASS 	= "org.societies.api.schema.css.directory.CssFriendEvent";
	static final String UF_PRIVACY_CLASS 	= "org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent";
	static final String UF_ACCESS_CLASS 	= "org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent";
	static final String UF_REQUEST_CLASS	= "org.societies.api.schema.useragent.feedback.UserFeedbackBean";
	static final String UF_EXPLICIT_RESPONSE_CLASS	= "org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean";
	static final String UF_IMPLICIT_RESPONSE_CLASS	= "org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean";
	//N.B. Add any new event payload classes to this array - order is unimportant
	final static String pubsubPayloadClasses [] = {
                                                CSS_MANAGER_CLASS,
                                                CONTEXT_CLASS,
                                                FRIEND_EVENT_CLASS,
                                                UF_PRIVACY_CLASS,
                                                UF_ACCESS_CLASS,
                                                UF_REQUEST_CLASS,
                                                UF_EXPLICIT_RESPONSE_CLASS,
                                                UF_IMPLICIT_RESPONSE_CLASS
    };

	final static String GENERIC_INTENT_PAYLOAD_KEY = "Pubsub_Payload_Key";

	/**
	 * Subscribe to a specified Societies platform event (Android Intent)
	 *
	 * @param client app package
	 * @param societiesIntent specific event intent
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToEvent(String client, String societiesIntent);

	/**
	 * Subscribe to Societies platform events (Android Intent), specified with a filter. All platform events
	 * that start with the filter will be subscribed to.
	 *
	 * @param client app package
	 * @param intentFilter event filter
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToEvents(String client, String intentFilter);

	/**
	 * Subscribe to all platform events. This should only be used if really required.
	 *
	 * @param client app package
	 * @return boolean true if subscription takes place
	 */
	boolean subscribeToAllEvents(String client);

	/**
	 * Un-subscribe from a specified Societies platform event (Android Intent)
	 *
	 * @param client app package
	 * @param societiesIntent specific event intent
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromEvent(String client, String societiesIntent);

	/**
	 * Un-subscribe from Societies platform events (Android Intent), specified with a filter. All platform events
	 * that start with the filter will be un-subscribed from.
	 *
	 * @param client app package
	 * @param intentFilter event filter
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromEvents(String client, String intentFilter);

	/**
	 * Un-subscribe from all current platform event subscriptions.
	 *
	 * @param client app package
	 * @return boolean true if subscription takes place
	 */
	boolean unSubscribeFromAllEvents(String client);

	/**
	 * Publish an event to the Societies platform for consumption by other CSS nodes
	 *
	 * @param client app package
	 * @param societiesIntent specific event intent
	 * @param eventPayload event object
	 * @param eventClass class of event object
	 * @return boolean - returned via Android intent
	 */
	/**
	 * Publish an event to the Societies platform for consumption by other CSS nodes
	 *
	 * @param client app package
	 * @param societiesIntent specific event intent
	 * @param payload
	 * @return boolean- returned via Android intent
	 */
	boolean publishEvent(String client, String societiesIntent, Object payload);

	/**
	 * Obtain the current number of subscribed to events
	 *
	 * @param client
	 * @return int number of subscribed to events - returned via Android intent
	 */
	int getNumSubscribedNodes(String client);

	/**
	 * Create a new Societies Pubsub node
	 * @param client
	 * @param pubsubNode Pubsub node to be used in Societies, e.g. org.3rdpartyservice.sampleevent
	 * @param societiesIntent should use Societies format , i.e. org.societies.3rdpartyservice.sampleevent
	 * @return boolean - returned via Android intent
	 */
	boolean createEvent(String client, String pubsubNode, String societiesIntent);
	/**
	 * Delete a Societies Pubsub node. Can only be used to delete Pubsub nodes previously created by the same client.
	 *
	 * @param client
	 * @param pubsubNode Pubsub node to be used in Societies, e.g. org.3rdpartyservice.sampleevent
	 * @return boolean - returned via Android intent
	 */
	boolean deleteEvent(String client, String pubsubNode);
}
