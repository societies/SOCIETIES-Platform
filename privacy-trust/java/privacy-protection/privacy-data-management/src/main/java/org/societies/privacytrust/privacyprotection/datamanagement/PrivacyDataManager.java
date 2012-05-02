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
package org.societies.privacytrust.privacyprotection.datamanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.dataobfuscation.DataObfuscationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManager implements IPrivacyDataManager {
	IPrivacyDataManagerInternal privacyDataManager;
//	IPrivacyPreferenceManager privacyPreferenceManager;
	IDataObfuscationManager dataObfuscationManager;

	public PrivacyDataManager()  {
		dataObfuscationManager = new DataObfuscationManager();
//		privacyPreferenceManager = new PrivacyPreferenceManager();
		privacyDataManager = new PrivacyDataManagerInternal();
	}


	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.internal.mock.CtxIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor,IIdentity ownerId, CtxIdentifier dataId, Action action) throws PrivacyException {
		// -- Verify parameters
		verifyParemeters(requestor, ownerId, null, dataId);

		// -- Retrieve a stored permission
		ResponseItem permission = privacyDataManager.getPermission(requestor, ownerId, dataId);
		// - Permission available: check actions
		if (null != permission) {
			// Actions available
			if (null != permission.getRequestItem() && permission.getRequestItem().getActions().contains(action)) {
				// Return only used actions for this request
				List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				permission.getRequestItem().setActions(actions);
			}
			// Actions not available
			else if(null != permission.getRequestItem()) {
				permission = new ResponseItem(null, Decision.DENY);
			}

		}

		// -- Permission not available: ask to PrivacyPreferenceManager
		if (null == permission) {
			//			permission = privacyPreferenceManager.checkPermission(ctxId, action, requestorIIdentity);
			// Permission still not available: deny access
			if (null == permission) {
				permission = new ResponseItem(null, Decision.DENY);
			}
		}
		return permission;
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Async
	@Override
	public Future<IDataWrapper> obfuscateData(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		verifyParemeters(requestor, ownerId, dataWrapper, null);

		// -- Retrieve the obfuscation level
		//		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, owner, dataWrapper.getDataId());
		//		double obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		double obfuscationLevel = 1;
		// If no obfuscation is required: return directly the wrapped data
		if (1 == obfuscationLevel) {
			return new AsyncResult<IDataWrapper>(dataWrapper);
		}

		// -- Obfuscate the data
		IDataWrapper obfuscatedDataWrapper = dataObfuscationManager.obfuscateData(dataWrapper, obfuscationLevel);
		return new AsyncResult<IDataWrapper>(obfuscatedDataWrapper);
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public CtxIdentifier hasObfuscatedVersion(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		verifyParemeters(requestor, ownerId, dataWrapper, null);

		// -- Retrieve the obfuscation level
		//		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, owner, dataWrapper.getDataId());
		//		double obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		double obfuscationLevel = 1;

		// -- Check if an obfuscated version is available
		return dataObfuscationManager.hasObfuscatedVersion(dataWrapper, obfuscationLevel);
	}


	private void verifyParemeters(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper, CtxIdentifier dataId) throws PrivacyException {
		if (null == requestor || null == ownerId) {
			throw new NullPointerException("Not enought information to knwo how to obfuscate this data");
		}
		if (null == dataId && (null == dataWrapper || null == dataWrapper.getDataId())) {
			throw new PrivacyException("Not enought information in the wrapper to obfuscate this data");
		}
	}
}
