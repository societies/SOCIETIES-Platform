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
package org.societies.android.api.internal.privacytrust;

import java.util.Map;

import org.societies.android.api.identity.ARequestorCis;
import org.societies.android.api.identity.ARequestorService;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.privacyprotection.model.privacypolicy.ARequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;


/**
 * Interface exposed to Societies components in order to do actions relative
 * to a privacy policy
 * 
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:29
 */
public interface IPrivacyPolicyManager {
	/**
     * IPrivacyPolicyManager intents
     * Used to create to create Intents to signal return values of a called method
     * If the method is locally bound it is possible to directly return a value but is discouraged
     * as called methods usually involve making asynchronous calls. 
     */
    public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.privacytrust.ReturnValue";
    public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.privacytrust.ReturnStatus";
    public static final String INTENT_RETURN_METHOD_KEY = "org.societies.android.privacytrust.ReturnMethod";
    
	/**
	 * Retrieve a CIS or 3P service privacy policy by the ID of the CIS or the 3P service
	 * @param client Client package name
	 * @param requestor Id of the CIS or the 3P service {@link ARequestorCis} and {@link ARequestorService}
	 * @post The response is available in an Intent: {@link MethodType}::GET_PRIVACY_POLICY. {@link INTENT_RETURN_VALUE_KEY} contains a {@link ARequestPolicy}
	 * @throws PrivacyException
	 */
	public void getPrivacyPolicy(String client, RequestorBean requestor) throws PrivacyException;
	
	/**
	 * Store or update a (CIS or 3P Service) privacy policy
	 * Example of use:
	 * - CIS Management, to create a policy for a CIS.
	 * - 3rd Service Creation, to attach a policy to a service
	 * - More generally: GUI, to edit a policy.
	 * 
	 * @param client Client package name
	 * @param privacyPolicy The privacy policy
	 * @return The stored privacy policy
	 */
	public RequestPolicy updatePrivacyPolicy(String client, RequestPolicy privacyPolicy) throws PrivacyException;
	
	/**
	 * Store or update a (CIS or 3P Service) privacy policy from an XML version of it.
	 * 
	 * @param client Client package name
	 * @param privacyPolicy XML formatted string containing the privacy policy
	 * @param requestor Identity of the owner of this privacy policy
	 * @return The privacy policy now stored by the PrivacyPolicyManager
	 * @throws PrivacyException
	 */
	public RequestPolicy updatePrivacyPolicy(String client, String privacyPolicy, RequestorBean requestor) throws PrivacyException;
	
	/**
	 * Delete a CIS or 3P service privacy policy by the ID of the CIS or the 3P Service
	 * 
	 * @param client Client package name
	 * @param requestor Id of the CIS or the 3P service
	 * @return True if the privacy policy is successfully deleted
	 */
	public boolean deletePrivacyPolicy(String client, RequestorBean requestor) throws PrivacyException;

	/**
	 * Help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slighty completed but still need to be filled. E.g. if a CIS
	 * configuration contains information about geolocation data, the inference engine
	 * will add geolocation data line to the privacy policy.
	 * @param client Client package name
	 * @param privacyPolicyType 1 means CIS privacy policy, 0 means 3P Service privacy policy
	 * @param configuration Configuration of the CIS or the 3P service
	 * @return A not complete privacy policy
	 */
	public RequestPolicy inferPrivacyPolicy(String client, int privacyPolicyType, Map configuration) throws PrivacyException;
	
	/**
	 * Create a Privacy Policy in an XML format from a Java format Privacy Policy
	 * @param privacyPolicy Privacy policy
	 * @return A string containing the XML version the privacy policy
	 */
	public String toXmlString(RequestPolicy privacyPolicy);
	
	/**
	 * Create a Privacy Policy in a Java format from a XML format Privacy Policy
	 * @param privacyPolicy Privacy policy
	 * @return A Java object containing the privacy policy
	 * @throws PrivacyException 
	 */
	public RequestPolicy fromXmlString(String privacyPolicy) throws PrivacyException;
}