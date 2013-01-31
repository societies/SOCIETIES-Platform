package org.societies.android.api.pubsub;

public interface IPubsubService {
	String methodsArray [] = {	"discoItems(String client, String pubsubService, String node, long remoteCallID)",
								"ownerCreate(String client, String pubsubService, String node, long remoteCallID)",
								"ownerDelete(String client, String pubsubService, String node, long remoteCallID)",
								"ownerPurgeItems(String client, String pubsubServiceJid, String node, long remoteCallID)",
								"publisherPublish(String client, String pubsubService, String node, String itemId, String item, long remoteCallID)",
								"publisherDelete(String client, String pubsubServiceJid, String node, String itemId, long remoteCallID)",
								"subscriberSubscribe(String client, String pubsubService, String node, long remoteCallID)",
								"subscriberUnsubscribe(String client, String pubsubService, String node, long remoteCallID)",
								"bindToAndroidComms(String client, long remoteCallID)",
								"unBindFromAndroidComms(String client, long remoteCallID)"
	};

	/**
	* Android Pubsub intents
	* Used to create to create Intents to signal return values of a called method
	* If the method is locally bound it is possible to directly return a value but is discouraged
	* as called methods usually involve making asynchronous calls. 
	*/
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.comms.ReturnValue";
	public static final String INTENT_RETURN_EXCEPTION_KEY = "org.societies.android.platform.comms.ReturnException";
	public static final String INTENT_RETURN_EXCEPTION_TRACE_KEY = "org.societies.android.platform.comms.ReturnExceptionTrace";
	public static final String INTENT_RETURN_CALL_ID_KEY = "org.societies.android.platform.comms.ReturnCallId";
	
	public static final String DISCO_ITEMS = "org.societies.android.api.pubsub.DISCO_ITEMS";
	public static final String OWNER_CREATE = "org.societies.android.api.pubsub.OWNER_CREATE";
	public static final String OWNER_DELETE = "org.societies.android.api.pubsub.OWNER_DELETE";
	public static final String OWNER_PURGE_ITEMS = "org.societies.android.api.pubsub.OWNER_PURGE_ITEMS";
	public static final String PUBLISHER_PUBLISH = "org.societies.android.api.pubsub.PUBLISHER_PUBLISH";
	public static final String PUBLISHER_DELETE = "org.societies.android.api.pubsub.PUBLISHER_DELETE";
	public static final String SUBSCRIBER_SUBSCRIBE = "org.societies.android.api.pubsub.SUBSCRIBER_SUBSCRIBE";
	public static final String SUBSCRIBER_UNSUBSCRIBE = "org.societies.android.api.pubsub.SUBSCRIBER_UNSUBSCRIBE";
	public static final String BIND_TO_ANDROID_COMMS = "org.societies.android.api.pubsub.BIND_TO_ANDROID_COMMS";
	public static final String UNBIND_FROM_ANDROID_COMMS = "org.societies.android.api.pubsub.UNBIND_FROM_ANDROID_COMMS";

	
	public boolean bindToAndroidComms(String client, long remoteCallID);

	public boolean unBindFromAndroidComms(String client, long remoteCallID);
	
	public String [] discoItems(String client, String pubsubService, String node, long remoteCallID);
	
	public boolean ownerCreate(String client, String pubsubService, String node, long remoteCallID);
	
	public boolean ownerDelete(String client, String pubsubService, String node, long remoteCallID);
	
	public boolean ownerPurgeItems(String client, String pubsubServiceJid, String node, long remoteCallID);
	
	public String publisherPublish(String client, String pubsubService, String node, String itemId, String item, long remoteCallID);
	
	public boolean publisherDelete(String client, String pubsubServiceJid, String node, String itemId, long remoteCallID);
	
	public boolean subscriberSubscribe(String client, String pubsubService,	String node, long remoteCallID);
	
	public boolean subscriberUnsubscribe(String client, String pubsubService, String node, long remoteCallID);
}
