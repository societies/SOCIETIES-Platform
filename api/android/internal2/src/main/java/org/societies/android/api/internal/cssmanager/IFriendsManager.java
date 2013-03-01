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
package org.societies.android.api.internal.cssmanager;

import java.util.List;

import org.societies.android.api.css.manager.IServiceManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssRecord;

/**
 * Methods for handling friend requests
 *
 * @author aleckey
 *
 */
public interface IFriendsManager extends IServiceManager {

	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.cssmanager.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.cssmanager.ReturnStatus";

	public static final String SUGGESTED_FRIENDS 	 = "org.societies.android.platform.cssmanager.SUGGESTED_FRIENDS";
	public static final String GET_CSS_FRIENDS 	 	 = "org.societies.android.platform.cssmanager.GET_CSS_FRIENDS";
	public static final String READ_PROFILE_REMOTE 	 = "org.societies.android.platform.cssmanager.READ_PROFILE_REMOTE";
	public static final String SEND_FRIEND_REQUEST   = "org.societies.android.platform.cssmanager.SEND_FRIEND_REQUEST";
	public static final String GET_FRIEND_REQUESTS 	 = "org.societies.android.platform.cssmanager.GET_FRIEND_REQUESTS";
	public static final String ACCEPT_FRIEND_REQUEST = "org.societies.android.platform.cssmanager.ACCEPT_FRIEND_REQUEST";

	String methodsArray [] = {
			"getCssFriends(String client)",						//0
			"getSuggestedFriends(String client)",				//1
			"readProfileRemote(String client, String cssId)",	//2
			"sendFriendRequest(String client, String cssId)",	//3
			"getFriendRequests(String client)",					//4
			"acceptFriendRequest(String client, String cssId)",	//5
			"startService()",									//6
			"stopService()"										//7
	};
	
	/**
	 * Get friend CSS(s) advertisements
	 * @param client
	 * @return
	 */
	List<CssAdvertisementRecord> getCssFriends(String client);

	/**
	 * Get friend CSS(s) advertisements
	 * @param client
	 * @return
	 */
	List<CssAdvertisementRecord> getSuggestedFriends(String client);
	/**
	 * Get a friend's CSS profile
	 * 
	 * @param client
	 * @param cssId
	 * @return AndroidCSSRecord profile
	 */
	CssRecord readProfileRemote(String client, String cssId);

	/**
	 * TODO: CssRecord
	 * @param client
	 * @param cssId
	 */
	void sendFriendRequest(String client, String cssId);
	
	/**
	 * Returns a list of Friend requests
	 * @param client
	 * @return
	 */
	CssAdvertisementRecord[] getFriendRequests(String client);
	
	/**
	 * Accepts Friend request
	 * @param client
	 * @param cssId
	 * @return
	 */
	void acceptFriendRequest(String client, String cssId);	
}
