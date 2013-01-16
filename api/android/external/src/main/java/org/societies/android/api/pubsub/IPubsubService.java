package org.societies.android.api.pubsub;


public interface IPubsubService {
	String methodsArray [] = {	"discoItems(String pubsubService, String node, long remoteCallID)",
								"ownerCreate(String pubsubService, String node, long remoteCallID)",
								"ownerDelete(String pubsubService, String node, long remoteCallID)",
								"ownerPurgeItems(String pubsubServiceJid, String node, long remoteCallID)",
								"publisherPublish(String pubsubService, String node, String itemId, String item, long remoteCallID)",
								"publisherDelete(String pubsubServiceJid, String node, String itemId, long remoteCallID)",
								"subscriberSubscribe(String pubsubService, String node, long remoteCallID)",
								"subscriberUnsubscribe(String pubsubService, String node, long remoteCallID)"
	};

	/**
	* Android Comms intents
	* Used to create to create Intents to signal return values of a called method
	* If the method is locally bound it is possible to directly return a value but is discouraged
	* as called methods usually involve making asynchronous calls. 
	*/
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.comms.ReturnValue";
	public static final String INTENT_RETURN_EXCEPTION_KEY = "org.societies.android.platform.comms.ReturnException";
	public static final String INTENT_RETURN_EXCEPTION_TRACE_KEY = "org.societies.android.platform.comms.ReturnExceptionTrace";
	public static final String INTENT_RETURN_CALL_ID_KEY = "org.societies.android.platform.comms.ReturnCallId";
	
	public static final String UN_REGISTER_COMM_MANAGER_RESULT = "org.societies.android.platform.comms.UN_REGISTER_COMM_MANAGER_RESULT";

	
	public String [] discoItems(String pubsubService, String node, long remoteCallID);
	
	public boolean ownerCreate(String pubsubService, String node, long remoteCallID);
	
	public boolean ownerDelete(String pubsubService, String node, long remoteCallID);
	
	public boolean ownerPurgeItems(String pubsubServiceJid, String node, long remoteCallID);
	
	public String publisherPublish(String pubsubService, String node, String itemId, String item, long remoteCallID);
	
	public boolean publisherDelete(String pubsubServiceJid, String node, String itemId, long remoteCallID);
	
	public SubscriptionParcelable subscriberSubscribe(String pubsubService,	String node, long remoteCallID);
	
	public boolean subscriberUnsubscribe(String pubsubService,	String node, long remoteCallID);
}
