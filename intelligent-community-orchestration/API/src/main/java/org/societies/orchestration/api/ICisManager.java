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
package org.societies.orchestration.api;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;




/**
 * @author Babak.Farshchian@sintef.no
 *
 */

public class ICisManager {

	
	// API implementing server functionality
	
	
	/**
	 * Create a new CIS for the CSS whose JID is the one in cssId. Password is needed and is the
	 * same as the CSS password (at the moment this is not checked).
	 * 
	 * 
	 * The CSS who creates the CIS will be the owner. Ownership should be possible to be changed
	 * later, but it is not right now.
	 * 
	 * TODO: define what values mode can have and what each means.
	 * TODO: change the type from String to proper type when CSS ID datatype is defined.
	 *  
	 * @param cssId and cssPassword are to recognise the user
	 * @param cisName is user given name for the CIS, e.g. "Footbal".
	 * @param cisType E.g. "disaster"
	 * @param mode membership type, e.g 1= read-only (will be defined in the future).
	 * @return a Future link to the {@link ICisOwned} representing the new CIS, or 
	 * null if the CIS was not created.
	 */
	public Future<ICisOwned> createCis(String cssId, String cssPassword, String cisName, String cisType, int mode) {
		return null;
	}
	
	/**
	 * Delete a specific CIS represented by cisId. The cisId is available in the
	 * method of {@link ICisOwned} representing the CIS to be deleted. This method
	 * will delete only one CIS with the ID passed as cisId.
	 * 
	 * 
	 * @param cssId and cssPassword of the owner of the CIS.
	 * @param cisId The ID of the CIS to be deleted.
	 * @return true if deleted, false otherwise.
	 */
	public boolean deleteCis(String cssId, String cssPassword, String cisId) {
		return true;
	}
	
	/**
	 * Get a CIS Record with the ID cisId.
	 * The one calling the api must be aware that he will get a {@link ICis} which
	 * will not implement all the methods for the case of CIS that the user owns
	 * 
	 * @param cssId The ID (jabber ID) of the CSS triggering the command (TODO: do we really need it?).
	 * @param cisId The ID (jabber ID) of the CIS to get.
	 * @return the {@link ICis} matching the input cisID, or null if no such CIS is owned or subscribed by the user.
	 * 
	 */
	public ICis getCis(String cssId, String cisId) {
		return null;
	}
	
	/**
	 * Get a CIS Owned Interface with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID (jabber ID) of the CIS to get.
	 * @return the {@link ICisOwned} matching the input cisID, or null if no such CIS is owned by the user.
	 */
	public ICisOwned getOwnedCis(String cisId) {
		return null;
	}
	
	
	/**
	 * Return an array of all the CISs that the user own or participates. 
	 * 
	 * @return Array of {@linkICisRecord} .
	 */
	public List<ICis> getCisList() {
		return null;
	}

	
	/**
	 * retrieves the list of CISs owned by that CIS Manager
	 * 
	 * @return list of {@link ICisOwned}
	 */
	public List<ICisOwned> getListOfOwnedCis() {
		return null;
	}

	
	// END OF API implementing server functionality
	
	// API implementing client functionality (to be called from webapp)

	
	
	// END of API implementing client functionality

	
	
	// API which is not yet properly defined
	
	/**
	 * Return an array of all the CISs that match the query. 
	 * 
	 * TODO: DO NOT USE THIS METHOD YET
	 * We need to refine first what to be searched
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param query Defines what to search for.
	 * @return Array of CIS Records that match the query.
	 */
	public ICis[] getCisList(ICis query) {
		return null;
	}
	
	public ICisOwned getBlankCisOwned() {
		return new ICisOwned();
	}
	
	public ICis getBlankCis() {
		return new ICis();
	}
	
	
	
	
	/**
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
	public boolean requestNewCisOwner(String currentOwnerCssId, String currentOwnerCssPassword,
		String newOwnerCssId, String cisId) {
		return true;
	}

}
