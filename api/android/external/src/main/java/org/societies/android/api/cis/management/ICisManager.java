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

import java.util.List;

/**
 * @author Babak.Farshchian@sintef.no
 * Implemented by the CommunityManager APKLib service
 */
public interface ICisManager {
	public String methodsArray[] = {"createCis(String client, String cisName, String cisType, String description, List<ACriteria> criteria, String privacyPolicy)",
							 		"deleteCis(String client, String cisId)",
							 		"Community[] getCisList(String client, String query)",
		//					 		"subscribeToCommunity(String client, String name, String cisId)",
		//					 		"unsubscribeFromCommunity(String client, String cisId)",
							 		"removeMember(String client, String cisId, String memberJid)"
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
	public ACommunity createCis(String client, String cisName, String cisType, String description, List<ACriteria> criteria, String privacyPolicy);
	
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
	public ACommunity[] getCisList(String client, String query);
	
	/**Notify cloud that we've joined a new CIS
	 *  
	 * @param name CIS Name
	 * @param cisId CIS id
	 */
	//public void subscribeToCommunity(String client, String name, String cisId);

	/**
	 * Notify cloud we've unsubscribed from a CIS
	 * @param cisId
	 */
	//public void unsubscribeFromCommunity(String client, String cisId);

	/**
	 * Remove a member from this CIS
	 * @param cisId
	 * @param memberJid
	 */
	public void removeMember(String client, String cisId, String memberJid);
}
