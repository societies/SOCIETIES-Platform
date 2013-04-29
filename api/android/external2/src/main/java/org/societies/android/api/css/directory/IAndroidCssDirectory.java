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
package org.societies.android.api.css.directory;

import org.societies.android.api.comms.xmpp.VCardParcel;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public interface IAndroidCssDirectory extends IServiceManager {

	/**
	 * IAndroidCssDirectory intents
	 * Used to create to create Intents to signal return values of a called method
	 * If the method is locally bound it is possible to directly return a value but is discouraged
	 * as called methods usually involve making asynchronous calls. 
	 */
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.cssdirectory.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.cssdirectory.ReturnStatus";
	public static final String FIND_ALL_CSS_ADVERTISEMENT_RECORDS = "org.societies.android.platform.cssdirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS";
	public static final String FIND_FOR_ALL_CSS = "org.societies.android.platform.cssdirectory.FIND_FOR_ALL_CSS";
	public static final String GET_USER_VCARD   = "org.societies.android.platform.cssdirectory.GET_USER_VCARD";
	public static final String GET_MY_VCARD   = "org.societies.android.platform.cssdirectory.GET_MY_VCARD";
	
	String methodsArray [] = {"findForAllCss(String client, String searchTerm)",	//0
							  "findAllCssAdvertisementRecords(String client)",		//1
							  "getUserVCard(String client, String userId)",			//2
							  "getMyVCard(String client)"							//3
							};
	
	/**
	 * Retreives a filtered array of  CSS Adverts based on search term
	 * @param client
	 * @param searchTerm
	 * @return
	 */
	public CssAdvertisementRecord[] findForAllCss(String client, String searchTerm);
	
	/**
	 * Retrieves all CSS Adverts - no filter
	 * @param client
	 * @return
	 */
	public CssAdvertisementRecord[] findAllCssAdvertisementRecords(String client);
	
	/**
	 * Get the VCard for a specified user
	 * @param client
	 * @param userId
	 * @return
	 */
	public VCardParcel getUserVCard(String client, String userId);
	
	/**
	 * Retrieves my VCard
	 * @param client
	 * @return
	 */
	public VCardParcel getMyVCard(String client);
}
