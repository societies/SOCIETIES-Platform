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
package org.societies.personalisation.preference.api.UserPreferenceConditionMonitor;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.FeedbackEvent;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.common.api.model.ActionInformation;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;


/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 14:52:53
 */
public interface IUserPreferenceConditionMonitor {

	/**
	 * 
	 * @param ownerId
	 * @param attribute
	 * @param callback
	 */
	public Future<List<IPreferenceOutcome>> getOutcome(IIdentity ownerId, CtxAttribute attribute, String uuid);
	

	/**
	 * 
	 * @param ownerId
	 * @param action
	 * @param callback
	 */
	public Future<List<IPreferenceOutcome>> getOutcome(IIdentity ownerId, IAction action, String uuid);
	
	
	/**
	 * 
	 * @param ownerId
	 * @param serviceId
	 * @param preferenceName
	 * @return
	 */
	public Future<IOutcome> getOutcome(IIdentity ownerId, String serviceType, ServiceResourceIdentifier serviceId, String preferenceName);
	
	
	
	public void sendFeedback(FeedbackEvent fEvent, ActionInformation actionInformation);
	
	/**
	 * Returns the PreferenceManager service
	 * @return PreferenceManager service
	 */
	public IUserPreferenceManagement getPreferenceManager();
	
	
	
	/*
	 * this method is for testing only
	 */
	public void pushPreferencesToCommunities(Calendar calendar);
	
	
	public void downloadPreferencesFromCommunities(Calendar calendar);
}