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

/**
 * Is in charge of managing meta data about CISs.
 * 
 * @author Babak Farshchian
 * @version 0
 */
package org.societies.api.internal.cis.management;

import org.societies.cis.android.client.CisActivityFeed;

public interface ICisManager {
	//
	//
	/**
	 * Create a new CIS for the CSS represented by cssId.
	 * TODO: change the type from String to proper type when CSS ID datatype is defined.
	 *  
	 * @param cssId, cisId
	 * @return
	 */
	CisRecord createCis(String cssId, String cisId);
	/**
	 * Delete a specific CIS represented by cisId
	 * TODO: Need to give a more meaningful return.
	 * 
	 * @param cssId the ID of the owner CSS
	 * @param cisId The ID of the CIS to be deleted.
	 * @return true if deleted, false otherwise. 
	 */
	Boolean deleteCis(String cssId, String cisId);
	/**
	 * Updates an existing CIS with the data in the newCis. Update is done canonical. If it fails, the old CIS is
	 * not changed at all.
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param newCis the data to be updated is specified in this CISRecord.
	 * @param oldCisId The ID of the CIS that needs to be updated.
	 * @return true if update was successful, 
	 */
	Boolean updateCis(String cssId, CisRecord newCis, String oldCisId);
	
	/**
	 * Get a CIS Record with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID of the CIS to get.
	 * @return the CISRecord with the ID cisID, or null if no such CIS exists.
	 */
	CisRecord getCis(String cssId, String cisId);
	
	/**
	 * Return an array of all the CISs that match the query. 
	 * 
	 * TODO: need to refine this to something better. I am not sure how the query will be created.
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param query Defines what to search for.
	 * @return Array of CIS Records that match the query.
	 */
	CisRecord[] getCisList(CisRecord query);
	
	/**
	 * Returns the CISActivityFeed for a specific CIS.
	 * 
	 * @param cssId The ID of the owner CSS.
	 * @param cisId The ID of the CIS.
	 * @return The CISActivityFeed of the CIS.
	 */
	CisActivityFeed getActivityFeed(String cssId, String cisId);

}
