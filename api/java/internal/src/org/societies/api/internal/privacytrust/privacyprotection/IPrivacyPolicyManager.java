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
package org.societies.api.internal.privacytrust.privacyprotection;

import java.util.List;
import java.util.Map;

import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;

/**
 * External Interface to do actions relative to a privacy policy or a privacy
 * agreement.
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:29
 */
public interface IPrivacyPolicyManager {
	/**
	 * Retrieve a (CIS or Service) privacy policy by its ID.
	 * Example of use:
	 * - CIS Management, when it sends the CIS data (URI, description and privacy
	 * policy) to let a user join it.
	 * - CIS Management, to edit a policy (GUI call)
	 * 
	 * @param id
	 */
	public RequestPolicy getPrivacyPolicy(String id);

	/**
	 * Retrieve (CIS or Service)  privacy policy using criteria
	 * Example of use:
	 * - CIS Management, when it sends CIS data (URI, description and privacy
	 * policy) to let a user join it.
	 * - CIS Management, to edit a policy (GUI call)
	 * - CIS Management, to list policies (to choose one to edit for example)
	 * 
	 * @param criteria
	 */
	public List<RequestPolicy> getPrivacyPolicies(Map criteria);

	/**
	 * Store or update a (CIS or Service) privacy policy
	 * Example of use:
	 * - CIS Management, to create a policy for a CIS.
	 * - 3rd Service Creation, to attach a policy to a service
	 * - More generally: GUI, to edit a policy.
	 * 
	 * @param privacyPolicy
	 */
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy);

	/**
	 * Delete a (CIS or Service) privacy policy by its ID.
	 * 
	 * @param id
	 */
	public boolean deletePrivacyPolicy(String id);

	/**
	 * Delete one or more (CIS or Service) privacy policies.
	 * 
	 * @param criteria
	 */
	public boolean deletePrivacyPolicies(Map criteria);

	/**
	 * Help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slighty completed but still need to be filled. E.g. if a CIS
	 * configuration contains information about geolocation data, the inference engine
	 * will add geolocation data line to the privacy policy.
	 * Example of use:
	 * - CIS Management, or 3rd Service Creation, to create a policy
	 * 
	 * @param configuration
	 * @param privacyPolicyType CIS or Service
	 */
	public RequestPolicy inferPrivacyPolicy(Map configuration, Object privacyPolicyType);
}