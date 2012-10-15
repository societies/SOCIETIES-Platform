/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.internal.cssmanager;

import java.util.List;

import org.societies.android.api.css.directory.ACssAdvertisementRecord;


/**
 * 
 * This interface forms the basis of the CSSManager, a component that will exist on all nodes. Its main tasks are:
 * 
 * 1. Allow a user to register or unregister a CSS
 * 2. Allow a user to login into a CSS
 * 3. Allow a user to logout out of a CSS
 * 3. Add and remove nodes that make up a CSS
 * 4. Get the current CSS profile
 * 5. Allow the status of a device to be changed
 * 6. Synchronise profile data
 * 7. Modify the CSS profile 
 * 
 * This interface will be implemented for android nodes. 
 *
 */

public interface IAndroidCSSManager {

	/**
	 * CSS Manager intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.cssmanager.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.cssmanager.ReturnStatus";

	public static final String CHANGE_CSS_NODE_STATUS = "org.societies.android.platform.cssmanager.CHANGE_CSS_NODE_STATUS";
	public static final String GET_ANDROID_CSS_RECORD = "org.societies.android.platform.cssmanager.GET_ANDROID_CSS_RECORD";
	public static final String LOGIN_CSS = "org.societies.android.platform.cssmanager.LOGIN_CSS";
	public static final String LOGIN_XMPP_SERVER = "org.societies.android.platform.cssmanager.LOGIN_XMPP_SERVER";
	public static final String LOGOUT_CSS = "org.societies.android.platform.cssmanager.LOGOUT_CSS";
	public static final String LOGOUT_XMPP_SERVER = "org.societies.android.platform.cssmanager.LOGOUT_XMPP_SERVER";
	public static final String MODIFY_ANDROID_CSS_RECORD = "org.societies.android.platform.cssmanager.MODIFY_ANDROID_CSS_RECORD";
	public static final String REGISTER_CSS = "org.societies.android.platform.cssmanager.REGISTER_CSS";
	public static final String REGISTER_CSS_DEVICE = "org.societies.android.platform.cssmanager.REGISTER_CSS_DEVICE";
	public static final String REGISTER_XMPP_SERVER = "org.societies.android.platform.cssmanager.REGISTER_XMPP_SERVER";
	public static final String SET_PRESENCE_STATUS = "org.societies.android.platform.cssmanager.SET_PRESENCE_STATUS";
	public static final String SYNCH_PROFILE = "org.societies.android.platform.cssmanager.SYNCH_PROFILE";
	public static final String UNREGISTER_CSS = "org.societies.android.platform.cssmanager.UNREGISTER_CSS";
	public static final String UNREGISTER_CSS_DEVICE = "org.societies.android.platform.cssmanager.UNREGISTER_CSS_DEVICE";
	public static final String UNREGISTER_XMPP_SERVER = "org.societies.android.platform.cssmanager.UNREGISTER_XMPP_SERVER";

	public static final String SUGGESTED_FRIENDS = "org.societies.android.platform.cssmanager.SUGGESTED_FRIENDS";
	public static final String GET_CSS_FRIENDS = "org.societies.android.platform.cssmanager.GET_CSS_FRIENDS";
	public static final String READ_PROFILE_REMOTE = "org.societies.android.platform.cssmanager.READ_PROFILE_REMOTE";
	public static final String SEND_FRIEND_REQUEST = "org.societies.android.platform.cssmanager.SEND_FRIEND_REQUEST";
	public static final String GET_FRIEND_REQUESTS = "org.societies.android.platform.cssmanager.GET_FRIEND_REQUESTS";
	public static final String ACCEPT_FRIEND_REQUEST = "org.societies.android.platform.cssmanager.ACCEPT_FRIEND_REQUEST";

	String methodsArray [] = {"registerXMPPServer(String client, AndroidCSSRecord profile)", //0
			"unregisterXMPPServer(String client, AndroidCSSRecord profile)",//1
			"loginXMPPServer(String client, AndroidCSSRecord profile)",		//2
			"logoutXMPPServer(String client)",								//3
			"loginCSS(String client, AndroidCSSRecord profile)", 			//4
			"logoutCSS(String client, AndroidCSSRecord profile)",			//5
			"registerCSS(String client, AndroidCSSRecord profile)",			//6
			"unregisterCSS(String client, AndroidCSSRecord profile)",		//7
			"registerCSSDevice(String client, AndroidCSSRecord profile)",	//8
			"unregisterCSSDevice(String client, AndroidCSSRecord profile)",	//9
			"getAndroidCSSRecord(String client)",							//10
			"modifyAndroidCSSRecord(String client, AndroidCSSRecord profile)",//11
			"changeCSSNodeStatus(String client, AndroidCSSRecord profile)", //12
			"synchProfile(String client, AndroidCSSRecord profile)",		//13
			"setPresenceStatus(String client, AndroidCSSRecord profile)",	//14
			"getCssFriends(String client)",									//15
			"getSuggestedFriends(String client)",							//16
			"readProfileRemote(String client, String cssId)",				//17
			"sendFriendRequest(String client, String cssId)",				//18
			"getFriendRequests(String client)",								//19
			"acceptFriendRequest(String client, String cssId)"				//20
	};
	/**
	 * Register with chosen Domain Server
	 * 
	 * @param client component package calling method
	 * @param profile
	 */
	void registerXMPPServer(String client, AndroidCSSRecord profile);
	/**
	 * Unregister with chosen Domain Server
	 * 
	 * @param client component package calling method
	 * @param profile
	 */
	void unregisterXMPPServer(String client, AndroidCSSRecord profile);
	/**
	 * Login with chosen Domain server
	 * 
	 * @param client component package calling method
	 * @param profile
	 */
	void loginXMPPServer(String client, AndroidCSSRecord profile);
	/**
	 * Logout from chosen Domain server
	 * 
	 * @param client component package calling method
	 * @param profile
	 */
	void logoutXMPPServer(String client);

	
	/**
	 * Login a user to a CSS. The registration of devices included in the profile
	 * is implied.
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord loginCSS(String client, AndroidCSSRecord profile);	

	/**
	 * Logout the user from a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord logoutCSS(String client, AndroidCSSRecord profile);	
	
	/**
	 * Register a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord registerCSS(String client, AndroidCSSRecord profile);

	/**
	 * Unregister the CSS
	 * TODO Is a CSS deleted or made inactive
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord unregisterCSS(String client, AndroidCSSRecord profile);

	/**
	 * Register a device(s) with a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord registerCSSDevice(String client, AndroidCSSRecord profile);
	/**
	 * Unregister a device(s) from a CSS
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord unregisterCSSDevice(String client, AndroidCSSRecord profile);
	
	/**
	 * Get the CSS Profile. This operation will retrieve the local CSS Profile. 
	 * If none exists or the local cache is deemed to have expired this will 
	 * result in a synchronisation with the cloud node. 
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord getAndroidCSSRecord(String client);

	/**
	 * Modify the CSS Profile
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord modifyAndroidCSSRecord(String client, AndroidCSSRecord profile);
	/**
	 * Change the status a CSS device
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord changeCSSNodeStatus(String client, AndroidCSSRecord profile);
	
	/**
	 * Synchronise the CSS profile. The CSS cloud node's current profile is synchronised
	 * with the local device's cached version
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord synchProfile(String client, AndroidCSSRecord profile);
	
	/**
	 * Set the presence status of the user
	 * 
	 * @param client component package calling method
	 * @param profile
	 * @return AndroidCSSRecord
	 */
	AndroidCSSRecord setPresenceStatus(String client, AndroidCSSRecord profile);
	
	/**
	 * Get friend CSS(s) advertisements
	 * @param client
	 * @return
	 */
	List<ACssAdvertisementRecord> getCssFriends(String client);

	/**
	 * Get friend CSS(s) advertisements
	 * @param client
	 * @return
	 */
	List<ACssAdvertisementRecord> getSuggestedFriends(String client);
	/**
	 * Get a friend's CSS profile
	 * 
	 * @param client
	 * @param cssId
	 * @return AndroidCSSRecord profile
	 */
	AndroidCSSRecord readProfileRemote(String client, String cssId);

	/**
	 * TODO: what is return type ?
	 * @param client
	 * @param cssId
	 */
	void sendFriendRequest(String client, String cssId);
	
	/**
	 * Returns a list of Friend requests
	 * @param client
	 * @return
	 */
	ACssAdvertisementRecord[] getFriendRequests(String client);
	
	/**
	 * Accepts Friend request
	 * @param client
	 * @param cssId
	 * @return
	 */
	void acceptFriendRequest(String client, String cssId);
}
