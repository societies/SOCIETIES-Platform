/**
 * Copyright (c) 2011-2013, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
package org.societies.api.internal.css;

import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.css.FriendFilter;
import org.societies.api.css.ICSSManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.servicelifecycle.model.Service;

/**
 * This Interface defines the internal API methods to manage and retrieve CSS
 * related data on behalf of the user.
 * 
 * @author David McKitterick
 */
public interface ICSSInternalManager extends ICSSManager {

	// Original methods from ICSSLocalManager

	/**
	 * Register with chosen Domain Server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> registerXMPPServer(CssRecord profile);

	/**
	 * Unregister with chosen Domain Server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> unregisterXMPPServer(CssRecord profile);


	/**
	 * Login a user to a CSS.
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> loginCSS(CssRecord profile);

	/**
	 * Logout the user from a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> logoutCSS(CssRecord profile);

        /**
         * Synch the CSS profile
         * 
         * @param profile
         * @return Future<CssInterfaceResult>
         */
        public Future<CssInterfaceResult> synchProfile(CssRecord profile);


	/**
	 * Register a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> registerCSS(CssRecord profile);

	/**
	 * Unregister the CSS TODO Is a CSS deleted or made inactive
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> unregisterCSS(CssRecord profile);

	/**
	 * Register a device(s) with a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> registerCSSNode(CssRecord profile);

	/**
	 * Unregister a device(s) from a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> unregisterCSSNode(CssRecord profile);

	/**
	 * Get the CSS Profile. This operation will retrieve the local CSS Profile.
	 * If none exists or the local cache is deemed to have expired this will
	 * result in a synchronisation with the cloud node.
	 * 
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> getCssRecord();

	/**
	 * Modify the CSS Profile
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	public Future<CssInterfaceResult> modifyCssRecord(CssRecord profile);


	/**
	 * Return a list of people who I have asked to my friend
	 * 
	 * @return
	 */
	public Future<List<CssRequest>> findAllCssFriendRequests();

	/**
	 * Return list of requests to be my friend
	 * 
	 * @return
	 */
	public Future<List<CssRequest>> findAllCssRequests();


	/**
	 * send a request to remote css to join
	 */
	public void sendCssFriendRequest(String friendCssId);

	/**
	 * 
	 * @return
	 */
	public Future<List<CssAdvertisementRecordDetailed>> getCssAdvertisementRecordsFull();

	/**
	 * Returns the current Node Type i.e. "CSS_CLOUD", "CSS_RICH" or "CSS_LIGHT"
	 * 
	 * @return Future<String>
	 */
	public Future<String> getthisNodeType(String nodeId);

	/**
	 * 
	 * @param cssrecord
	 * @param nodeId
	 * @param nodestatus
	 * @param nodetype
	 * @param cssNodeMAC
	 * @param interactable
	 */
	public void setNodeType(CssRecord cssrecord, String nodeId, int nodestatus,
			int nodetype, String cssNodeMAC, String interactable);

	/**
	 * 
	 * @param cssrecord
	 * @param nodeId
	 */
	public void removeNode(CssRecord cssrecord, String nodeId);

	/**
	 * Returns a list of Css id's of people who we have accepted/have accepted
	 * friend requests
	 */
	public Future<List<CssAdvertisementRecord>> getCssFriends();

	/**
	 * 
	 * @return
	 */
	public Future<List<CssAdvertisementRecord>> getFriendRequests();

	// New methods merging functionality of old methods (see below)
	
	/**
	 * To be called from clients within the same CSS
	 */
	public void handleInternalFriendRequest(IIdentity externalCSS, CssRequestStatusType status);
	
	/**
	 * To be called from clients external to this CSS
	 */
	public void handleExternalFriendRequest(IIdentity externalCSS, CssRequestStatusType status);

	/**
	 * To be called from clients external to this CSS
	 */
	public void handleExternalUpdateRequest(IIdentity externalCSS, CssRequestStatusType status);
	
	// to be replaced by above
	
	/**
	 * @deprecated
	 * Add update a reuest sent to join your css
	 */
	public void updateCssRequest(CssRequest request);
	
	/**
	 * @deprecated
	 * Called by remote css is response t your request to join
	 */
	public void updateCssFriendRequest(CssRequest request);
	
	/**
	 * @deprecated
	 * Accept
	 * 
	 * @param request
	 */
	public void acceptCssFriendRequest(CssRequest request);

	/**
	 * @deprecated
	 * Decline
	 * 
	 * @param request
	 */
	public void declineCssFriendRequest(CssRequest request);
	

	
	// Proxy methods to CSS Directory and Service Discovery - to be removed
		
	/**
	 * @deprecated
	 * adds advertisement records to CssDirectory
	 * 
	 * @param record
	 */
	public void addAdvertisementRecord(CssAdvertisementRecord record);

	/**
	 * @deprecated
	 * deletes advertisement record from CssDirectory
	 * 
	 * @param record
	 */
	public void deleteAdvertisementRecord(CssAdvertisementRecord record);

	/**
	 * @deprecated
	 * updates advertisement records to CssDirectory
	 * 
	 * @param currentRecord
	 *            , newRecord
	 */
	public void updateAdvertisementRecord(CssAdvertisementRecord currentRecord,
			CssAdvertisementRecord updatedRecord);

	/**
	 * @deprecated
	 * returns all advertisement records in CssDirectory
	 * 
	 * @return Future<List<CssAdvertisementRecord>>
	 */
	public Future<List<CssAdvertisementRecord>> findAllCssAdvertisementRecords();

	/**
	 * @deprecated
	 * finds all services for advertised Css's via service discovery
	 * 
	 * @param listCssAds
	 * @return Future<List<Service>>
	 */
	public Future<List<Service>> findAllCssServiceDetails(
			List<CssAdvertisementRecord> listCssAds);
	
	public void setFriendfilter(FriendFilter filter);
	
	/**
	 * Returns the current Node Type i.e. "CSS_CLOUD", "CSS_RICH" or "CSS_LIGHT"
	 * 
	 * @return Future<String>
	 */
	public FriendFilter getFriendfilter();

	/**
	 * This method will parse a timeperiod and return a subset of the actitvies
     *  in this activityfeed that is within the given timeperiod
     *
     * @param {@link String} timeperiod can be: "millisecondssinceepoch millisecondssinceepoch+n"
     * @return a @List of {@link IActivity}
     * or a empty list if the parameters are wrong or the  timeperiod did not match any activties
	 * @param timePeriod
	 * @return
	 */
	public Future<List<MarshaledActivity>> getActivities(String timePeriod, int limitResults);
	
	public void pushtoContext(CssRecord cssrecord);
	
	// No implementation of the following methods
	
//	/**
//	 * Change the status a CSS device
//	 * 
//	 * @param profile
//	 * @return Future<CssInterfaceResult>
//	 */
//	public Future<CssInterfaceResult> changeCSSNodeStatus(CssRecord profile);
//	
//	/**
//	 * Login with chosen Domain server
//	 * 
//	 * @param profile
//	 * @return Future<CssInterfaceResult>
//	 */
//	public Future<CssInterfaceResult> loginXMPPServer(CssRecord profile);
//	
//	/**
//	 * Logout from chosen Domain server
//	 * 
//	 * @param profile
//	 * @return Future<CssInterfaceResult>
//	 */
//	public Future<CssInterfaceResult> logoutXMPPServer(CssRecord profile);
//	
//	/**
//	 * Set the presence status of the user
//	 * 
//	 * @param profile
//	 * @return Future<CssInterfaceResult>
//	 */
//	public Future<CssInterfaceResult> setPresenceStatus(CssRecord profile);
//
//	/**
//	 * Synchronise the CSS profile. The CSS cloud node's current profile is
//	 * synchronised with the local device's cached version
//	 * 
//	 * @param profile
//	 * @return Future<CssInterfaceResult>
//	 */
//	public Future<CssInterfaceResult> synchProfile(CssRecord profile);
	
	// moved to external Interface
	
//	/**
//	 * Return a list of SNS inspired friends
//	 * 
//	 * @return
//	 */
//	public Future<List<CssAdvertisementRecord>> suggestedFriends();

}
