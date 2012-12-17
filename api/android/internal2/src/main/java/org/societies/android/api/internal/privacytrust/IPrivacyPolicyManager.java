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

import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.PrivacyPolicyBehaviourConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;


/**
 * Interface exposed to Societies components in order to do actions relative
 * to a privacy policy
 * 
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:29
 */
public interface IPrivacyPolicyManager {
	/**
	 * Intent default action: If there is an error, the action name can't be retrieve and this one is used instead.
	 * Must be used in the listening IntentFilter
	 */
	public static final String INTENT_DEFAULT_ACTION = "org.societies.android.privacytrust.DefaultAction";
	/**
	 * Intent field: Return value of the request
	 */
    public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.privacytrust.ReturnValue";
    /**
     * Intent field: Status of the request
     */
    public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.privacytrust.ReturnStatus";
    /**
	 * Intent field: Error description if the request status is failure
	 */
    public static final String INTENT_RETURN_STATUS_MSG_KEY = "org.societies.android.privacytrust.ReturnStatusMsg";
    
	/**
	 * Retrieve a CIS or 3P service privacy policy by the ID of the CIS or the 3P service
	 * @param client Client package name
	 * @param owner Id of the CIS {@link RequestorCisBean} or the 3P service {@link RequestorServiceBean} owner of the privacy policy
	 * @post The response is available in an Intent: {@link MethodType}::GET_PRIVACY_POLICY. {@link IPrivacyPolicyManager}INTENT_RETURN_STATUS_KEY contains the status of the request and the meaning of an eventual failure is available in {@link IPrivacyPolicyManager}::INTENT_RETURN_STATUS_MSG_KEY. {@link IPrivacyPolicyManager}::INTENT_RETURN_VALUE_KEY contains a {@link RequestPolicy}
	 * @throws PrivacyException
	 */
	public void getPrivacyPolicy(String client, RequestorBean owner) throws PrivacyException;
	
	/**
	 * Store or update a (CIS or 3P Service) privacy policy
	 * 
	 * @param client Client package name
	 * @param privacyPolicy The privacy policy to store or update
	 * @post The response is available in an Intent: {@link MethodType}::UPDATE_PRIVACY_POLICY. {@link IPrivacyPolicyManager}INTENT_RETURN_STATUS_KEY contains the status of the request and the meaning of an eventual failure is available in {@link IPrivacyPolicyManager}::INTENT_RETURN_STATUS_MSG_KEY.
	 */
	public void updatePrivacyPolicy(String client, RequestPolicy privacyPolicy) throws PrivacyException;
	
	/**
	 * Store or update a (CIS or 3P Service) privacy policy from an XML version of it.
	 * 
	 * @param client Client package name
	 * @param privacyPolicy XML formatted string containing the privacy policy to store or update
	 * @param owner Id of the CIS {@link RequestorCisBean} or the 3P service {@link RequestorServiceBean} owner of the privacy policy
	 * @post The response is available in an Intent: {@link MethodType}::UPDATE_PRIVACY_POLICY. {@link IPrivacyPolicyManager}INTENT_RETURN_STATUS_KEY contains the status of the request to know if the operation succeed or not, and the meaning of an eventual failure is available in {@link IPrivacyPolicyManager}::INTENT_RETURN_STATUS_MSG_KEY.
	 * @throws PrivacyException
	 */
	public void updatePrivacyPolicy(String client, String privacyPolicy, RequestorBean owner) throws PrivacyException;
	
	/**
	 * Delete a CIS or 3P service privacy policy by the ID of the CIS or the 3P Service
	 * 
	 * @param client Client package name
	 * @param owner Id of the CIS {@link RequestorCisBean} or the 3P service {@link RequestorServiceBean} owner of the privacy policy
	 * @post The response is available in an Intent: {@link MethodType}::DELETE_PRIVACY_POLICY. {@link IPrivacyPolicyManager}INTENT_RETURN_STATUS_KEY contains the status of the request to know if the operation succeed or not,  and the meaning of an eventual failure is available in {@link IPrivacyPolicyManager}::INTENT_RETURN_STATUS_MSG_KEY.
	 * @throws PrivacyException
	 */
	public void deletePrivacyPolicy(String client, RequestorBean owner) throws PrivacyException;

	/**
	 * General function to help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param privacyPolicyType Type of the privacy policy: for a CIS or a 3P service
	 * @param configuration Configuration of the CIS or the 3P service
	 * @return A slightly completed privacy policy
	 */
	@SuppressWarnings("rawtypes")
	public RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType, Map configuration) throws PrivacyException;
	
	/**
	 * Help a developer or a user to create a CIS privacy policy by inferring a default
	 * one using information about the CIS. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param globalBehaviour Global behavior of the privacy policy: private (default), members only, public or custom
	 * @param membershipCriteria Membership criteria of the CIS (optional)
	 * @param configuration Other optional configuration
	 * @return A slightly completed privacy policy
	 */
	public RequestPolicy inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants globalBehaviour, MembershipCriteria membershipCriteria, Map<String, String> configuration) throws PrivacyException;
	
	/**
	 * Help a developer or a user to create a CIS privacy policy by inferring a default
	 * one using information about the CIS. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param globalBehaviour Global behavior of the privacy policy: private (default), members only, public or custom
	 * @param membershipCriteria Membership criteria of the CIS
	 * @return A slightly completed privacy policy
	 */
	public RequestPolicy inferCisPrivacyPolicy(PrivacyPolicyBehaviourConstants globalBehaviour, MembershipCriteria membershipCriteria) throws PrivacyException;
	
	/**
	 * Help a developer or a user to create a 3P-service privacy policy by inferring a default
	 * one using information about the 3P-service. The privacy policy in
	 * result will be slightly completed but still need to be filled.
	 * E.g. if a CIS membership criteria engine requires access to geolocation data,
	 * the inference engine will add geolocation data line to the privacy policy.
	 * @param configuration Configuration of the 3P service
	 * @return A slightly completed privacy policy
	 */
	public RequestPolicy infer3pServicePrivacyPolicy(Map<String, String> configuration) throws PrivacyException;
	
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