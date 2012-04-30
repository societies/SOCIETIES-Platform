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
package org.societies.api.internal.privacytrust.privacyprotection.remote;

import java.util.Map;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Interface exposed to Societies components in order to do remote actions relative
 * to a privacy policy
 * @author Olivier Maridat (Trialog)
 * @created 27 apr. 2012
 */
public interface IPrivacyPolicyManagerRemote {
	/**
	 * Remote call to retrieve a CIS privacy policy by the ID of the CIS
	 * 
	 * @param cisId Id of the CIS.
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 */
	public void getPrivacyPolicy(IIdentity cisId, IIdentity targetedNode, IPrivacyPolicyManagerListener listener);
	
	/**
	 * Remote call to retrieve a 3P Service privacy policy by the ID of the 3P Service
	 * 
	 * @param serviceId Id of the 3P Service
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 */
	public void getPrivacyPolicy(ServiceResourceIdentifier serviceId, IIdentity targetedNode, IPrivacyPolicyManagerListener listener);

	/**
	 * Remote call to store or update a (CIS or 3P Service) privacy policy
	 * 
	 * @param privacyPolicy The privacy policy
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 */
	public void updatePrivacyPolicy(RequestPolicy privacyPolicy, IIdentity targetedNode, IPrivacyPolicyManagerListener listener);
	
	/**
	 * Remote call to delete a CIS privacy policy by the ID of the Service
	 * 
	 * @param cisId Id of the CIS
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 */
	public void deletePrivacyPolicy(IIdentity cisId, IIdentity targetedNode, IPrivacyPolicyManagerListener listener);

	/**
	 * Remote call to delete a 3P Service privacy policy by the ID of the Service
	 * 
	 * @param serviceId Id of the 3P service
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 */
	public void deletePrivacyPolicy(ServiceResourceIdentifier serviceId, IIdentity targetedNode, IPrivacyPolicyManagerListener listener);

	/**
	 * Remote Call. Help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slighty completed but still need to be filled. E.g. if a CIS
	 * configuration contains information about geolocation data, the inference engine
	 * will add geolocation data line to the privacy policy.
	 * 
	 * @param privacyPolicyType 1 means CIS privacy policy, 0 means 3P Service privacy policy
	 * @param configuration Configuration of the CIS or the 3P service
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 */
	public void inferPrivacyPolicy(int privacyPolicyType, Map configuration, IIdentity targetedNode, IPrivacyPolicyManagerListener listener);
}