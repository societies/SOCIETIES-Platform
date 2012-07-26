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
package org.societies.api.cis.management;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;




/**
 * @author Babak.Farshchian@sintef.no
 *
 */

@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ICisManager {

	
	// API implementing server functionality
	
	/**
	 * Create a new CIS for the CSS whose JID is the one in cssId.
	 * 
	 * 
	 * The CSS who creates the CIS will be added as a member to the CIS and with the owner role.
	 *  Ownership should be possible to be changed later, but it is not right now. 
	 * 
	 * At the moment, the creation of the CIS triggers and advertisement record of it to be sent to
	 * the global cis directory 
	 * 
	 *  
	 * @param cisName is user given name for the CIS, e.g. "Footbal".
	 * @param cisType E.g. "disaster"
	 * @param mode membership type, e.g 1= read-only (will be defined in the future).
	 * @param privacyPolicy (for the second method) should follow the privacy policy schema.
	 * @param cisCriteria specifies the criterias for one to be a member of the CIS, specified as a Hashtable
	 *  of Context Attribute (String) and its criteria rule as a {@link MembershipCriteria} object
	 * @param description is the description of the CIS 
	 * @return a Future link to the {@link ICisOwned} representing the new CIS, or 
	 * null if the CIS was not created.
	 */


	Future<ICisOwned> createCis(String cisName, String cisType, Hashtable<String, MembershipCriteria> cisCriteria, String description);
	
	Future<ICisOwned> createCis(String cisName, String cisType, Hashtable<String, MembershipCriteria> cisCriteria, String description
			,String privacyPolicy);

	
	
	/**
	 * Delete a specific CIS represented by cisId. The cisId is available in the
	 * method of {@link ICisOwned} representing the CIS to be deleted. This method
	 * will delete only one CIS with the ID passed as cisId.
	 * 
	 * But it will trigger a delete notification to be sent to all the members of the CIS
	 * 
	 * 
	 * @param cisId The ID of the CIS to be deleted.
	 * @return true if deleted, false otherwise.
	 */
	boolean deleteCis(String cisId);
	
	
	/**
	 * Get a CIS Record with the ID cisId.
	 * The one calling the api must be aware that he will get a {@link ICis} which
	 * will not implement all the methods for the case of CIS that the user owns
	 * 
	 * @param cisId The ID (jabber ID) of the CIS to get.
	 * @return the {@link ICis} matching the input cisID, or null if no such CIS is owned or subscribed by the user.
	 * 
	 */
	ICis getCis(String cisId);
	
	/**
	 * Get a CIS Owned Interface with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID (jabber ID) of the CIS to get.
	 * @return the {@link ICisOwned} matching the input cisID, or null if no such CIS is owned by the user.
	 */
	ICisOwned getOwnedCis(String cisId);
	
	
	/**
	 * Return an array of all the CISs that the user own or participates. 
	 * 
	 * @return Array of {@link ICis} .
	 */
	List<ICis> getCisList();

	
	/**
	 * retrieves the list of CISs owned by that CIS Manager
	 * 
	 * @return list of {@link ICisOwned}
	 */
	public List<ICisOwned> getListOfOwnedCis();

	
	/**
	 * retrieves the list of CISs in which I joined 
	 * 
	 * @return list of {@link ICis}
	 */
	public List<ICis> getRemoteCis();

	
	
	/**
	 * Return the list of CISs (subscribed or owned) where the CIS name contains the input string
	 * if no CIS is found, it returns an empty list 
	 * 
	 * @param String name input to search for cis
	 * @return Array of {@link ICis} that contains the name string .
	 */
	public List<ICis> searchCisByName(String name);
	

	/**
	 * Return the list of owned CISs in which the CSS
	 * represented by {@link IIdentity}  is a member.
	 * 
	 * It will return an empty list if no CIS contains that CSS
	 * 
	 * @param {@link IIdentity} represensting the CSS
	 * @return Array of {@link ICisOwned} that contains the CSS .
	 */
	public List<ICisOwned> searchCisByMember(IIdentity css) throws InterruptedException, ExecutionException;

	
	// END OF API implementing server functionality
	
	// API implementing client functionality (to be called from webapp)

	
	/**
	 * Join a CIS (at the moment hosted remotely).
	 * The callback must be able to retrieve a community object
	 * containg a join response
	 * defined at org.societies.api.schema.cis.community 
	 * it has the result of the join plus some complimentary info from the CIS
	 *  
	 * @param adv advertisement Record of the cis (includes the membership criteria and jid)
	 * @param callback callback function
	 */
	public void joinRemoteCIS(CisAdvertisementRecord adv, ICisManagerCallback callback);
	
	/**
	 * Leave a CIS, most likely hosted remotely.
	 * The callback must be able to retrieve a community object
	 * contnaing a leave response
	 * defined at org.societies.api.schema.cis.community 
	 * it has the result of the leave plus some complimentary info from the CIS
	 *  
	 * @param cisId JID of the CIS to be joined
	 * @param callback callback function
	 */
	public void leaveRemoteCIS(String cisId, ICisManagerCallback callback);
	
	
	// END of API implementing client functionality

		
	
	// API which is not yet properly defined
	
	/*
	 * Return an array of all the CISs that match the query. 
	 * 
	 * TODO: DO NOT USE THIS METHOD YET
	 * We need to refine first what to be searched
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param query Defines what to search for.
	 * @return Array of CIS Records that match the query.
	 */
	//ICis[] getCisList(ICis query);

	
	
	
	
	/*
	 * Method not yet defined. 
	 * 
	 * TODO: DO NOT USE THIS METHOD YET - not yet defined
	 * 
	 * 
	 * @param currentOwnerCssId The ID of the owner CSS
	 * @param currentOwnerCssPassword passwod of the owner of the CIS
	 * @param newOwnerCssId JID of the new owner
	 * @param cisId JID of the CIS which will have its owner changed
	 * @return boolean stating if the operation worked or failed
	 */
	//boolean requestNewCisOwner(String currentOwnerCssId, String currentOwnerCssPassword,
	//	String newOwnerCssId, String cisId);

}
