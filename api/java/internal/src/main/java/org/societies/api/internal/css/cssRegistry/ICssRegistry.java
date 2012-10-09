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
package org.societies.api.internal.css.cssRegistry;

import java.util.List;

import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;



/**
 * @author Maria Mannion - Intel
 */
public interface ICssRegistry {

	/**
	 * Description: This method provides the interface to add a new Css.
	 * 				
	 * @param cssDetails
	 * @throws CssRegistrationException
	 */
	public CssInterfaceResult registerCss (CssRecord cssDetails) throws CssRegistrationException;
	
	/**
	 * Description: This method permits you to unregister a css
	 * @param cssDetails
	 * @throws CssRegistrationException
	 */
	public void unregisterCss (CssRecord cssDetails) throws CssRegistrationException;

	/**
	 * Description: This method permits you to update the css Record
	 * @param cssDetails
	 * @throws CssRegistrationException
	 */
	public void updateCssRecord(CssRecord cssDetails) throws CssRegistrationException; 
		

	/**
	 * Description: Returns the css record details
	 * @param none
	 * @throws CssRegistrationException
	 */
	public CssRecord getCssRecord() throws CssRegistrationException; 

	/**
	 * Description: Test if a CssRecord exists
	 * 
	 * @return boolean true id record exists
	 * @throws CssRegistrationException
	 */
	public boolean cssRecordExists() throws CssRegistrationException;
	
	
	List<CssRequest>  getCssFriendRequests()
			throws CssRegistrationException;
	
	void updateCssFriendRequestRecord(CssRequest cssRequest)
			throws CssRegistrationException ;
	
	List<CssRequest>  getCssRequests()
			throws CssRegistrationException;
	
	void updateCssRequestRecord(CssRequest cssRequest)
			throws CssRegistrationException;
	
	CssRequest  getCssFriendRequest(String friendId)
			throws CssRegistrationException;
	
	List<String>  getCssFriends()
			throws CssRegistrationException;
			
}