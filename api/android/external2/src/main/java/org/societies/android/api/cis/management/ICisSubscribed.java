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
package org.societies.android.api.cis.management;

import org.societies.android.api.css.manager.IServiceManager;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse; 
import org.societies.api.schema.cis.community.Community;

/**
 * This interface represents CISs that are not owned by this CSS but that this
 * CIS subscribes to (is a member of).
 * 
 * @author Babak.Farshchian@sintef.no
 * Implemented by the CommunityManager APKLib service
 */
public interface ICisSubscribed extends IServiceManager {
	//CIS SUBSCRIBER INTENTS
	public static final String INTENT_RETURN_VALUE = "org.societies.android.platform.community.ReturnValue";
	public static final String INTENT_RETURN_BOOLEAN = "org.societies.android.platform.community.ReturnBoolean"; // extra from True/False methods
	public static final String GET_MEMBERS     	= "org.societies.android.platform.community.GET_MEMBERS";
	public static final String GET_ACTIVITY_FEED= "org.societies.android.platform.community.GET_ACTIVITY_FEED";
	public static final String ADD_ACTIVITY 	= "org.societies.android.platform.community.ADD_ACTIVITY";
	public static final String DELETE_ACTIVITY 	= "org.societies.android.platform.community.DELETE_ACTIVITY";
	public static final String CLEAN_ACTIVITIES = "org.societies.android.platform.community.CLEAN_ACTIVITIES";
	public static final String GET_CIS_INFO 	= "org.societies.android.platform.community.GET_CIS_INFO";
	
	public String methodsArray[] = {"getMembers(String client, String cisId)",
							 		"getCisInformation(String client, String cisId)",
							 		"getActivityFeed(String client, String cisId)",
							 		"addActivity(String client, String cisId, org.societies.api.schema.activity.MarshaledActivity activity)",
							 		"deleteActivity(String client, String cisId, org.societies.api.schema.activity.MarshaledActivity activity)",
							 		"cleanActivityFeed(String client, String cisId)",
									"startService()",
									"stopService()"
								};
	
	/**
	 * Get the list of members for this community
	 * @param client
	 * @param cisId
	 * @return
	 */
	public String[] getMembers(String client, String cisId);
	
	/**
	 * Get info for this community
	 * @param client
	 * @param cisId
	 * @return
	 */
	public Community getCisInformation(String client, String cisId);

	/**
	 * Get the list of activities for this community
	 * @param client
	 * @param cisId
	 * @return
	 */
	public MarshaledActivity[] getActivityFeed(String client, String cisId);
	
	/**
	 * Add an activity to this community
	 * @param activity
	 * @return
	 */
	public Boolean addActivity(String client, String cisId, MarshaledActivity activity);
	
	/**
	 * Deletes the relevant activty from the feed
	 */
	public Boolean deleteActivity(String client, String cisId, MarshaledActivity  activity);
	/**
	 * Clean up the activity feed for this community
	 * @return
	 */
	public CleanUpActivityFeedResponse cleanActivityFeed(String client, String cisId);
}
