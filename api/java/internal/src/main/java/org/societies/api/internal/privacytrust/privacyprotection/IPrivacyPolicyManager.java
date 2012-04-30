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

import java.util.Map;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Interface exposed to Societies components in order to do actions relative
 * to a privacy policy
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:29
 */
public interface IPrivacyPolicyManager {
	/**
	 * Retrieve a CIS privacy policy by the ID of the CIS
	 * Example of use:
	 * - CIS Management, when it sends the CIS data (URI, description and privacy
	 * policy) to let a user join it.
	 * - CIS Management, to edit a policy (GUI call)
	 * 
	 * @param cisId Id of the CIS
	 * @return the CIS privacy policy
	 * @throws PrivacyException
	 */
	public RequestPolicy getPrivacyPolicy(IIdentity cisId) throws PrivacyException;
	
	/**
	 * Retrieve a 3P Service privacy policy by the ID of the 3P Service
	 * 
	 * @param serviceId Id of the 3P Service
	 * @return the CIS privacy policy
	 * @throws PrivacyException
	 */
	public RequestPolicy getPrivacyPolicy(ServiceResourceIdentifier serviceId) throws PrivacyException;

	/**
	 * Store or update a (CIS or 3P Service) privacy policy
	 * Example of use:
	 * - CIS Management, to create a policy for a CIS.
	 * - 3rd Service Creation, to attach a policy to a service
	 * - More generally: GUI, to edit a policy.
	 * 
	 * @param privacyPolicy The privacy policy
	 * @return The stored privacy policy
	 */
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException;
	
	/**
	 * Delete a CIS privacy policy by the ID of the Service
	 * 
	 * @param cisId Id of the CIS
	 * @return True if the privacy policy is successfully deleted
	 */
	public boolean deletePrivacyPolicy(IIdentity cisId) throws PrivacyException;

	/**
	 * Delete a 3P Service privacy policy by the ID of the Service
	 * 
	 * @param serviceId Id of the 3P service
	 * @return True if the privacy policy is successfully deleted
	 */
	public boolean deletePrivacyPolicy(ServiceResourceIdentifier serviceId) throws PrivacyException;

	/**
	 * Help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slighty completed but still need to be filled. E.g. if a CIS
	 * configuration contains information about geolocation data, the inference engine
	 * will add geolocation data line to the privacy policy.
	 * Example of use:
	 * - CIS Management, or 3rd Service Creation, to create a policy
	 * 
	 * @param privacyPolicyType 1 means CIS privacy policy, 0 means 3P Service privacy policy
	 * @param configuration Configuration of the CIS or the 3P service
	 * @return A not complete privacy policy
	 */
	public RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType, Map configuration) throws PrivacyException;
}