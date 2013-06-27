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
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

/**
 * Interface exposed to Societies components in order to do remote actions relative
 * to a privacy policy
 * @author Olivier Maridat (Trialog)
 * @created 27 apr. 2012
 * @updated 16 jun. 2013
 */
public interface IPrivacyPolicyManagerRemote {
	/**
	 * Remote call to retrieve a privacy policy by the ID of the CIS or the 3P service
	 * 
	 * @param requestor Id of the CIS or the 3P service
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 * @throws PrivacyException 
	 */
	public void getPrivacyPolicy(RequestorBean requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;
	/**
	 * Will be removed in 1.2
	 * @see getPrivacyPolicy
	 */
	@Deprecated
	public void getPrivacyPolicy(Requestor requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;
	
	/**
	 * Remote call to store or update a (CIS or 3P Service) privacy policy
	 * 
	 * @param privacyPolicy The privacy policy
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 * @throws PrivacyException 
	 */
	public void updatePrivacyPolicy(RequestPolicy privacyPolicy, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;
	/**
	 * Will be removed in 1.2
	 * @see updatePrivacyPolicy
	 */
	@Deprecated
	public void updatePrivacyPolicy(org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;
	
	/**
	 * Remote call to delete a privacy policy by the ID of the Service or CIS
	 * 
	 * @param requestor Id of the CIS or the 3P service
	 * @param targetedNode CSS ID of the CSS which will receive this remote call.
	 * @param listener The callback object
	 * @throws PrivacyException 
	 */
	public void deletePrivacyPolicy(RequestorBean requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;
	/**
	 * Will be removed in 1.2
	 * @see deletePrivacyPolicy
	 */
	@Deprecated
	public void deletePrivacyPolicy(Requestor requestor, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;

	/**
	 * Will be removed in 1.2
	 * @see PrivacyPolicyUtils#inferPrivacyPolicy(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants, Map)
	 */
	@Deprecated
	public void inferPrivacyPolicy(int privacyPolicyType, Map configuration, IIdentity targetedNode, IPrivacyPolicyManagerListener listener) throws PrivacyException;
}