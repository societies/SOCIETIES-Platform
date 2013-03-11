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
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

/**
 * @author Babak.Farshchian@sintef.no
 * Implemented by the CommunityManager APKLib service
 */
public interface ICisManager extends IServiceManager {
	//CIS MANAGER INTENTS
	public static final String INTENT_RETURN_VALUE = "org.societies.android.platform.community.ReturnValue";
	public static final String INTENT_RETURN_BOOLEAN = "org.societies.android.platform.community.ReturnBoolean"; // extra from True/False methods
	public static final String CREATE_CIS     		= "org.societies.android.platform.community.CREATE_CIS";
	public static final String DELETE_CIS    		= "org.societies.android.platform.community.DELETE_CIS";
	public static final String GET_CIS_LIST     	= "org.societies.android.platform.community.GET_CIS_LIST";
	public static final String SUBSCRIBE_TO_CIS 	= "org.societies.android.platform.community.SUBSCRIBE_TO_CIS";
	public static final String UNSUBSCRIBE_FROM_CIS = "org.societies.android.platform.community.UNSUBSCRIBE_FROM_CIS";
	public static final String REMOVE_MEMBER 		= "org.societies.android.platform.community.REMOVE_MEMBER";
	public static final String JOIN_CIS      		= "org.societies.android.platform.community.JOIN_CIS";
	public static final String LEAVE_CIS     		= "org.societies.android.platform.community.LEAVE_CIS";
	
	public String methodsArray[] = {"createCis(String client, String cisName, String cisType, String description, org.societies.api.schema.cis.community.MembershipCrit criteria, String privacyPolicy)",
							 		"deleteCis(String client, String cisId)",
							 		"getCisList(String client, String query)",
							 		"removeMember(String client, String cisId, String memberJid)",
							 		"Join(String client, org.societies.api.schema.cis.directory.CisAdvertisementRecord targetCis)",
							 		"Leave(String client, String cisId)",
									"startService()",
									"stopService()"
									};
	
	/**
	 * Create a new CIS. The Hosting CSS who creates the CIS will be the owner. Ownership can be changed
	 * later.
	 * 
	 * @param cisName is user given name for the CIS, e.g. "Footbal".
	 * @param cisType E.g. "disaster"
	 * @param description More detailed info
	 * @param cisCriteria hashtable of {@link membershipCriteria} objects
	 * @param privacyPolicy privacy policy info 
	 */
	public Community createCis(String client, String cisName, String cisType, String description, MembershipCrit criteria, String privacyPolicy);
	
	/**
	 * Delete a specific CIS represented by cisId. The cisId is available in the
	 * method of {@link ICisEditor} representing the CIS to be deleted. This method
	 * will delete only one CIS with the ID passed as cisId.
	 * 
	 * @param cisId The ID of the CIS to be deleted.
	 * @return true if deleted, false otherwise.
	 */
	public Boolean deleteCis(String client, String cisId);
	
	/**
	 * Return an array of all the CISs that match the query. 

	 * @param query Defines what to search for.
	 * @return Array of CIS Records that match the query.
	 */
	public Community[] getCisList(String client, String query);
	
	/**
	 * Remove a member from this CIS
	 * @param cisId
	 * @param memberJid
	 */
	public void removeMember(String client, String cisId, String memberJid);
	
	/**
	 * Join a community
	 * @param client
	 * @param targetCis
	 * @param qualifications
	 * @return
	 */
	public String Join(String client, CisAdvertisementRecord targetCis);
	
	/**
	 * Leave a Community
	 * @param client
	 * @param cisId
	 * @return
	 */
	public String Leave(String client, String cisId);
}
