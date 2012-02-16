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
package org.societies.personalisation.preference.api.UserPreferenceManagement;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;
import org.societies.personalisation.preference.api.callback.IPreferenceOutcomeCallback;
import org.societies.personalisation.preference.api.model.IPreference;


/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:51:53
 */
public interface IUserPreferenceManagement {

	/**
	 * 
	 * @param ownerID
	 * @param details
	 */
	public void deletePreference(Identity ownerID, PreferenceDetails details);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public void deletePreference(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public void getPreference(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName, IPreferenceOutcomeCallback callback);

	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 * @param preference
	 */
	public void updatePreference(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName, IPreference preference);

	/**
	 * 
	 * @param ownerID
	 * @param details
	 * @param preference
	 */
	public void updatePreference(Identity ownerID, PreferenceDetails details, IPreference preference);

	
	
	/**
	 * THE FOLLOWING METHODS ARE CONSIDERED OBSOLETE AFTER MERGING UPM WITH PCM. 
	 */
	
	
	
	/**
	 * 
	 * @param ownerID
	 * @param outcome
	 *//*
	public List<CtxAttributeIdentifier> getConditions(Identity ownerID, IOutcome outcome);*/


	/**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 *//*
	public IPreferenceTreeModel getModel(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName);

	*//**
	 * 
	 * @param ownerID
	 * @param details
	 *//*
	public IPreferenceTreeModel getModel(Identity ownerID, PreferenceDetails details);*/
	
	
	/*	*//**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 *//*
	public List<IPreferenceConditionIOutcomeName> getPreferenceConditions(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID);

	*//**
	 * 
	 * @param ownerID
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 *//*
	public List<CtxAttributeIdentifier> getPreferenceConditions(Identity ownerID, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName);*/

	/**
	 * 
	 * @param userId
	 * @param attr
	 * @param preferenceIdentifier
	 *//*
	public List<IOutcome> reEvaluatePreferences(Identity userId, CtxAttribute attr, List<PreferenceDetails> preferenceIdentifier);

	*/
	
	/**
	 * 
	 * @param ownerID
	 * @param attr
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 *//*
	public IOutcome reEvaluatePreferences(Identity ownerID, CtxAttribute attr, String serviceType, IServiceResourceIdentifier serviceID, String preferenceName);*/
}