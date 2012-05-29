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
package org.societies.android.privacytrust.datamanagement;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.LocationCoordinates;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.Name;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.PostalLocation;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.Status;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.Temperature;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.obfuscator.IDataObfuscator;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.privacytrust.api.IPrivacyDataManagerInternal;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.LocationCoordinatesObfuscator;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.NameObfuscator;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.PostalLocationObfuscator;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.StatusObfuscator;
import org.societies.android.privacytrust.dataobfuscation.obfuscator.TemperatureObfuscator;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ActionConstants;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManager implements IPrivacyDataManager {
	private final static String TAG = PrivacyDataManager.class.getSimpleName();

	private IPrivacyDataManagerInternal privacyDataManagerInternal;

	
	public PrivacyDataManager()  {
		privacyDataManagerInternal = new PrivacyDataManagerInternal();
	}


	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.internal.mock.String, org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public ResponseItem checkPermission(RequestorBean requestor, String ownerId, String dataId, Action action) throws PrivacyException {
		// -- Verify parameters
		verifyParemeters(requestor, ownerId, null, dataId);
		ResponseItem permission = null;
		
		
		// -- Create Useful Values for NULL Result
		// List of actions
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		// RequestItem
		Resource resource = new Resource();
		resource.setCtxUriIdentifier(dataId);
		List<Condition> conditions = new ArrayList<Condition>();
		RequestItem requestItemNull = new RequestItem();
		// TODO: set list
		requestItemNull.setResource(resource);
		requestItemNull.setOptional(false);

		// -- Retrieve a stored permission
		permission = privacyDataManagerInternal.getPermission(requestor, ownerId, dataId);
		// - Permission available: check actions
		if (null != permission) {
			// Actions available
			if (null != permission.getRequestItem() && containsAction(permission.getRequestItem().getActions(), action)) {
				Log.i(TAG, "RequestItem NOT NULL and action match");
				// Return only used actions for this request
				// TODO: set list
//				permission.getRequestItem().setActions(actions);
			}
			// Actions not available
			else if(null != permission.getRequestItem()) {
				Log.i(TAG, "RequestItem NOT but action doesn't match NULL");
				permission = new ResponseItem();
				permission.setRequestItem(requestItemNull);
				permission.setDecision(Decision.DENY);
			}
		}


		// -- Permission not available: remote call
		if (null == permission) {
			Log.e(TAG, "No Permission retrieved: remote call");
			try {
				// TODO: remote call
//				permission = privacyPreferenceManager.checkPermission(requestor, dataAttributeId, actions);
			} catch (Exception e) {
				Log.e(TAG, "Error when retrieving permission from PrivacyDataManagerRemote", e);
			}
			
			// Permission still not available: deny access
			if (null == permission) {
				permission = new ResponseItem();
				permission.setRequestItem(requestItemNull);
				permission.setDecision(Decision.DENY);
			}
			// Store new permission retrieved from PrivacyPreferenceManager
			privacyDataManagerInternal.updatePermission(requestor, ownerId, permission);
		}
		return permission;
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public IDataWrapper obfuscateData(String requestor, String ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		RequestorBean requestorBean = new RequestorServiceBean();
		requestorBean.setRequestorId(requestor);
		verifyParemeters(requestorBean, ownerId, dataWrapper, null);

//		// -- Retrieve the obfuscation level
		// TODO: remote call
//		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, ownerId, dataWrapper.getDataId());
//		double obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		double obfuscationLevel = 1;
		// If no obfuscation is required: return directly the wrapped data
		if (1 == obfuscationLevel) {
			return dataWrapper;
		}

		// -- Verify params
		// Wrapper ready for obfuscation
		if (!dataWrapper.isReadyForObfuscation()) {
			throw new PrivacyException("This data wrapper is not ready for obfuscation. Data are needed.");
		}
		// Obfuscation level in [0, 1]
		if (obfuscationLevel > 1) {
			obfuscationLevel = 1;
		}
		if (obfuscationLevel < 0) {
			obfuscationLevel = 0.000001;
		}
		// Return directly if obfuscation level is 1
		if (1 == obfuscationLevel) {
			return dataWrapper;
		}

		// -- Mapping: retrieve the relevant obfuscator
		IDataObfuscator obfuscator = getDataObfuscator(dataWrapper);

		// -- Obfuscate
		IDataWrapper obfuscatedDataWrapper = null;
		try {
			// - Obfuscation
			// Local obfuscation
			if (obfuscator.isAvailable()) {
				Log.d(TAG, "Local obfuscation");
				obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
			}
			// Remote obfuscation needed
			else {
				Log.d(TAG, "Remote obfuscation needed");
				// TODO: remote call
			}
			
			// - Persistence
			//			if (dataWrapper.isPersistenceEnabled()) {
			// TODO: persiste the obfuscated data using a data broker
			//				System.out.println("Persist the data "+dataWrapper.getDataId());
			//			}
		}
		catch(Exception e) {
			throw new PrivacyException("Obfuscation aborted", e);
		}
		return obfuscatedDataWrapper;
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public String hasObfuscatedVersion(String requestor, String ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		RequestorBean requestorBean = new RequestorServiceBean();
		requestorBean.setRequestorId(requestor);
		verifyParemeters(requestorBean, ownerId, dataWrapper, null);

		// -- Retrieve the obfuscation level
		// TODO: remote call
		//		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, owner, dataWrapper.getDataId());
		//		double obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		double obfuscationLevel = 1;
		// If no obfuscation is required: return directly the wrapped data
		if (1 == obfuscationLevel) {
			return dataWrapper.getDataId();
		}

		// -- Search obfuscated version
		//		if (dataWrapper.isPersistenceEnabled()) {
		// TODO: retrieve obfsucated data ID using data broker
		// An obfuscated version exist
		//			if (false) {
		//				System.out.println("Retrieve the persisted data id of data id "+dataWrapper.getDataId());
		//			}
		//		}
		return dataWrapper.getDataId();
	}


	// -- Private methods
	private void verifyParemeters(RequestorBean requestor, String ownerId, IDataWrapper dataWrapper, String dataId) throws PrivacyException {
		if (null == requestor || null == ownerId) {
			Log.e(TAG, "verifyParemeters(): Not enought information: requestor or owner id is missing");
			throw new NullPointerException("Not enought information: requestor or owner id is missing");
		}
		if (null == dataId && (null == dataWrapper || null == dataWrapper.getData())) {
			Log.e(TAG, "verifyParemeters(): Not enought information: data id is missing");
			throw new PrivacyException("Not enought information: data id is missing");
		}
	}

	
	private boolean containsAction(List<Action> actions, Action action) {
		if (null == actions || actions.size() <= 0 || null == action) {
			return false;
		}
		for(Action actionTmp : actions) {
			if (actionTmp.equals(action)) {
				return true;
			}
		}
		return false;
	}
	
	private IDataObfuscator getDataObfuscator(IDataWrapper dataWrapper) throws PrivacyException {
		IDataObfuscator obfuscator = null;
		if (dataWrapper.getData() instanceof LocationCoordinates) {
			obfuscator = new LocationCoordinatesObfuscator((IDataWrapper<LocationCoordinates>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Name) {
			obfuscator = new NameObfuscator((IDataWrapper<Name>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Temperature) {
			obfuscator = new TemperatureObfuscator((IDataWrapper<Temperature>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof Status) {
			obfuscator = new StatusObfuscator((IDataWrapper<Status>) dataWrapper);
		}
		else if (dataWrapper.getData() instanceof PostalLocation) {
			obfuscator = new PostalLocationObfuscator((IDataWrapper<PostalLocation>) dataWrapper);
		}
		else {
			throw new PrivacyException("Obfuscation aborted: no known obfuscator for this type of data");
		}
		return obfuscator;
	}
}
