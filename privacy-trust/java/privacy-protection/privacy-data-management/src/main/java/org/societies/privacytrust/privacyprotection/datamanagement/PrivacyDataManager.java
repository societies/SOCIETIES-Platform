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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestItemUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.datamanagement.util.PrivacyDataManagerUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManager extends PrivacyDataManagerUtility implements IPrivacyDataManager {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyDataManager.class);
	private static final Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); // to define a dedicated Logger for Performance Testing
	private static long performanceObfuscationCount = 0;

	/* Beans */
	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private IDataObfuscationManager dataObfuscationManager;
	private IPrivacyPolicyManager privacyPolicyManager;
	private ICommManager commManager;
	private ICisManager cisManager;
	/* Data */
	/**
	 * To choose between development and production mode.
	 * In development mode, it is possible to disable the
	 * privacy access control layer by a configuration
	 * parameter.
	 */
	private static final boolean IS_DEVELOPMENT_MODE = false;
	/**
	 * Flag to enable/disable access control and obfuscation
	 */
	private boolean enabled;


	/* *********************************
	 * ACCESS CONTROL
	 * ******************************* */


	@Override
	public List<ResponseItem> checkPermission(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException {
		List<ResponseItem> responseItemList = new ArrayList<ResponseItem>();
		for(DataIdentifier dataId : dataIds) {
			responseItemList.addAll(checkPermission(requestor, dataId, actions));
		}
		return responseItemList;
	}	

	@Override
	public List<ResponseItem> checkPermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new PrivacyException("[Parameters] Not enought information: requestor or owner id is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] Not enought information: data id is missing. At least the data type is expected.");
		}
		if (null == actions || actions.size() <= 0 || !ActionUtils.atLeast1MandatoryAction(actions)) {
			throw new PrivacyException("[Parameters] Actions are missing, at least one mandatory action is required, they can't be all optional.");
		}
		// Create useful values for default result
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		List<Condition> conditions = new ArrayList<Condition>();
		Resource resource = ResourceUtils.create(dataId);
		RequestItem requestItemNull = RequestItemUtils.create(resource, actions, conditions);
		// Access control disabled
		if (!isEnabled()) {
			permissions.add(ResponseItemUtils.create(Decision.PERMIT, requestItemNull));
			return permissions;
		}

		LOG.info("[checkPermission] Test");

		// -- Retrieve a stored permission
		try {
			permissions = privacyDataManagerInternal.getPermissions(requestor, dataId, actions);
			if (null != permissions && permissions.size() > 0) {
				LOG.info("[checkPermission] Not permissions retrieved ("+RequestorUtils.toUriString(requestor)+", "+DataIdentifierUtils.toUriString(dataId)+")");
				return permissions;
			}
		} catch (Exception e) {
			LOG.error("Error when retrieving stored decisions", e);
		}

		// -- Permission not available
		permissions = null;
		// - Access control for CSS data: ask to PrivacyPreferenceManager
		if (isCssAccessControl(dataId)) {
			permissions = checkPermissionCssData(requestor, dataId, actions);
		}
		// - Access control for CIS data: use the CIS privacy policy
		else {
			permissions = checkPermissionCisData(requestor, dataId, actions);
		}

		// -- Still no permission available: deny access
		if (null == permissions || permissions.size() <= 0) {
			permissions = new ArrayList<ResponseItem>();
			ResponseItem permission = ResponseItemUtils.create(Decision.DENY, requestItemNull);
			permissions.add(permission);
		}
		// Store new permission retrieved from PrivacyPreferenceManager
		try {
			privacyDataManagerInternal.updatePermissions(RequestorUtils.toRequestor(requestor, commManager.getIdManager()), permissions);
		} catch (Exception e) {
			LOG.error("Error during decisions storage", e);
		}
		return permissions;
	}

	public List<ResponseItem> checkPermissionCisData(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// -- Verify parameters
		if (!isDependencyInjectionDone(3)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}
		// -- Create useful values for default result
		List<Condition> conditions = new ArrayList<Condition>();
		Resource resource = ResourceUtils.create(dataId);
		RequestItem requestItem = RequestItemUtils.create(resource, actions, conditions);
		ResponseItem permissionDeny = ResponseItemUtils.create(Decision.DENY, requestItem);
		ResponseItem permissionPermit = ResponseItemUtils.create(Decision.PERMIT, requestItem);
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		// -- Internal call (requestor == current node)
		IIdentity currentCssId = null;
		try {
			currentCssId = commManager.getIdManager().getThisNetworkNode();
		}
		finally {
			// Error case
			if (null == currentCssId) {
				permissions.add(permissionDeny);
				return permissions;
			}
			if (null != currentCssId && requestor.getRequestorId().equals(currentCssId.getJid())) {
				LOG.debug("[CIS access control] Internal call: always PERMIT");
				permissions.add(permissionPermit);
				return permissions;
			}
		}


		// -- Retrieve the CIS Privacy Policy
		String cisId = dataId.getOwnerId();
		RequestorCisBean requestorCis = (RequestorCisBean) RequestorUtils.create(currentCssId.getJid(), cisId);
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
		}
		catch(Exception e) {
			LOG.error("[CIS access control] Error: The privacy policy can not be retrieved for this CIS: "+RequestorUtils.toString(requestorCis), e);
			privacyPolicy = null;
		}
		// Can't retrieve the privacy policy OR empty one: DENY all
		if (null == privacyPolicy || null == privacyPolicy.getRequestItems() || privacyPolicy.getRequestItems().size() <= 0) {
			permissions.add(permissionDeny);
			return permissions;
		}

		// -- Search data request in the privacy policy
		/*
		 * ALOGIRTHM
		 * If the requested data is not in the privacy policy: DENY
		 * The requested data is in the privacy policy:
		 * * If this data is private: DENY
		 * * If all requested actions are matching AND if this data is public: PERMIT
		 * * If all requested actions are matching AND if this data is members only: retrieve CIS member list
		 * ** If the requestor is in the CIS member list: PERMIT
		 * ** Otherwise: DENY
		 * * If partial requested actions are matching AND if this data is public: remove these actions and continue
		 * * If partial requested actions are matching AND if this data is members only: if PERMIT with the CIS member list, remove these actions and continue
		 */
		List<ICisParticipant> cisMemberList = null;
		List<Action> actionsDeepCopy = new ArrayList<Action>();
		for(int i = 0; i<actions.size(); i++) {
			actionsDeepCopy.add(ActionUtils.create(actions.get(i).getActionConstant(), actions.get(i).isOptional()));
		}
		try {
			LOG.debug("[CIS access control] Searching: "+dataId.getUri()+" in Cis Privacy Policy: "+RequestPolicyUtils.toXmlString(privacyPolicy));
			for(RequestItem request : privacyPolicy.getRequestItems()) {
				DataIdentifier requestItemId = ResourceUtils.getDataIdentifier(request.getResource());
				// - Match data id or data type
				if (DataIdentifierUtils.isParentOrSameType(requestItemId, dataId)) {
					//				if ((null != request.getResource().getDataId() && dataId.getUri().equals(request.getResource().getDataId().getUri()))
					//						|| (null != request.getResource().getScheme() && null != request.getResource().getDataType() && dataId.getScheme().value().equals(request.getResource().getScheme().value()) && dataId.getType().equals(request.getResource().getDataType()))) {
					List<Action> actionsThatMatch = new ArrayList<Action>();
					boolean allRequestedActionsMatch = ActionUtils.contains(actionsDeepCopy, request.getActions(), actionsThatMatch);
					boolean canBeSharedWith3pServices = ConditionUtils.contains(ConditionConstants.SHARE_WITH_3RD_PARTIES, request.getConditions());
					// All requested actions are matching AND if this data is public
					if (allRequestedActionsMatch && canBeSharedWith3pServices) {
						LOG.debug("[CIS access control] All requested items are matching (public): PERMIT");
						permissions.add(permissionPermit);
						return permissions;
					}
					boolean canBeSharedWithCisMembersOnly = ConditionUtils.contains(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, request.getConditions());
					// Retrieve Cis member list
					if (null == cisMemberList) {
						cisMemberList = retrieveCisMemberList(dataId.getOwnerId());
					}
					//  All requested actions are matching AND if this data is members only
					if (allRequestedActionsMatch && canBeSharedWithCisMembersOnly) {
						LOG.debug("[CIS access control] All requested items are matching (members only): PERMIT if necessary");
						// Is it a CIS member?
						if (isCisMember(cisMemberList, dataId.getOwnerId(), requestor.getRequestorId())) {
							permissions.add(permissionPermit);
							return permissions;
						}
						permissions.add(permissionDeny);
						return permissions;
					}
					// Requested actions are partially matching AND if this data is public
					if (actionsThatMatch.size() > 0 && canBeSharedWith3pServices) {
						LOG.debug("[CIS access control] Some requested items are matching (public)");
						actionsDeepCopy.removeAll(actionsThatMatch);
						continue;
					}
					// Requested actions are partially matching AND if this data is members only
					if (actionsThatMatch.size() > 0 && canBeSharedWithCisMembersOnly) {
						LOG.debug("[CIS access control] Some requested items are matching (members only)");
						// Is it a CIS member?
						if (isCisMember(cisMemberList, dataId.getOwnerId(), requestor.getRequestorId())) {
							actionsDeepCopy.removeAll(actionsThatMatch);
							continue;
						}
						permissions.add(permissionDeny);
						return permissions;
					}
				}
			}
		}
		catch(Exception e) {
			LOG.error("Exception during CIS Data Access control", e);
		}
		LOG.debug("[CIS access control] No requested items are matching, or an error appears, or anyway they are privates: always DENY");
		permissions.clear();
		permissions.add(permissionDeny);
		return permissions;
	}

	public List<ResponseItem> checkPermissionCssData(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// Dependency injection
		if (!isDependencyInjectionDone(4)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}
		// -- Retrieve a permission using the PrivacyPreferenceManager
		List<ResponseItem> permissions = null;
		try {
			permissions = privacyPreferenceManager.checkPermission(requestor, dataId, actions);
		} catch (Exception e) {
			LOG.error("Error when retrieving permission from PrivacyPreferenceManager", e);
		}
		return permissions;
	}

	/**
	 * @param cisMemberList
	 * @param ownerId
	 * @throws PrivacyException 
	 */
	public boolean isCisMember(List<ICisParticipant> cisMemberList, String cisId, String cssId) throws PrivacyException {
		if (null != cisMemberList) {
			for (ICisParticipant cisMember : cisMemberList) {
				if (cisMember.getMembersJid().equals(cssId)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<ICisParticipant> retrieveCisMemberList(String cisId) {
		ICisOwned cis = cisManager.getOwnedCis(cisId);
		if (null == cis) {
			return new ArrayList<ICisParticipant>();
		}
		Set<ICisParticipant> cisMemberListIncome = cis.getMemberList();
		return new ArrayList<ICisParticipant>(cisMemberListIncome);
	}

	/**
	 * 
	 * @param dataId
	 * @return true if this is a CSS access control, false if this is a CIS access control
	 */
	public boolean isCssAccessControl(DataIdentifier dataId) {
		if (null != dataId.getOwnerId()) {
			// Retrieve Owner IIdentity
			try {
				IIdentity ownerId = commManager.getIdManager().fromJid(dataId.getOwnerId());
				// Check if the owner IIdentity has a CIS type
				if (null != ownerId && IdentityType.CIS.equals(ownerId.getType())) {
					return false;
				}
			} catch (InvalidFormatException e) {
				LOG.error("[From JID Error] IIdentity can not be understand.", e);
			}
		}
		return true;
	}


	/* *********************************
	 * OBFUSCATION
	 * ******************************* */

	@Async
	@Override
	public Future<DataWrapper> obfuscateData(RequestorBean requestor, DataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new PrivacyException("[Parameters] Not enought information: requestor or owner id is missing");
		}
		if (null == dataWrapper || null == dataWrapper.getData() || null == dataWrapper.getDataType()) {
			throw new PrivacyException("[Parameters] Not enought information: data missing or data type is missing");
		}
		// Obfuscation disabled
		if (!isEnabled()) {
			return new AsyncResult<DataWrapper>(dataWrapper);
		}

		// -- Retrieve the obfuscation level
		DObfPreferenceDetailsBean dataObfuscationPrefDetails = new DObfPreferenceDetailsBean();
		dataObfuscationPrefDetails.setResource(ResourceUtils.create(dataWrapper.getScheme(), dataWrapper.getDataType()));
		dataObfuscationPrefDetails.setRequestor(requestor);
		double obfuscationLevel = 1.0;
		try {
			obfuscationLevel = privacyPreferenceManager.evaluateDObfPreference(dataObfuscationPrefDetails);
		}
		catch(Exception e) {
			LOG.error("[Obfuscation] Can't retrieve obfuscation level from privacy preferences manager", e);
			return new AsyncResult<DataWrapper>(dataWrapper);
		}
		// - Performance loggings
		doPerformanceLogging(obfuscationLevel);
		// - If no obfuscation is required: return directly the wrapped data
		if (obfuscationLevel >= 1) {
			return new AsyncResult<DataWrapper>(dataWrapper);
		}

		LOG.debug("Obfuscation level: "+obfuscationLevel);

		// -- Obfuscate the data
		DataWrapper obfuscatedDataWrapper = dataObfuscationManager.obfuscateData(dataWrapper, obfuscationLevel);
		return new AsyncResult<DataWrapper>(obfuscatedDataWrapper);
	}

	@Async
	@Override
	public Future<List<CtxModelObject>> obfuscateData(RequestorBean requestor, List<CtxModelObject> ctxDataList) throws PrivacyException {
		Map<String, List<CtxModelObject>> obfuscableGroups = DataWrapperFactory.sortByObfuscability(ctxDataList);
		List<CtxModelObject> obfuscatedCtxDataList = new ArrayList<CtxModelObject>();
		Map<String, Future<DataWrapper>> futureResults = new HashMap<String, Future<DataWrapper>>();
		// -- Launch obfuscations
		for (Entry<String, List<CtxModelObject>> group : obfuscableGroups.entrySet()) {
			// Retrieve relevant wrapper
			DataWrapper dataWrapper = DataWrapperFactory.getDataWrapper(group.getKey(), group.getValue());
			// No possible obfuscation: store CtxModelObject to send them back later
			if (null == dataWrapper) {
				obfuscatedCtxDataList.addAll(group.getValue());
				continue;
			}
			// Launch obfuscation
			futureResults.put(group.getKey(), obfuscateData(requestor, dataWrapper));
		}

		// -- Retrieve results
		for (Entry<String, Future<DataWrapper>> group : futureResults.entrySet()) {
			List<CtxModelObject> originalCtxDataList = obfuscableGroups.get(group.getKey());
			try {
				DataWrapper obfuscateDataWrapper = group.getValue().get();
				obfuscatedCtxDataList.addAll(DataWrapperFactory.retrieveData(obfuscateDataWrapper, originalCtxDataList));
			} catch (Exception e) {
				LOG.error("Can't retrieve some obfuscated data: "+group.getKey(), e);
				obfuscatedCtxDataList.addAll(originalCtxDataList);
			}
		}
		return new AsyncResult<List<CtxModelObject>>(obfuscatedCtxDataList);
	}

	@Async
	private Future<Double> doPerformanceLogging(double obfuscationLevel) {
		// Counter
		IPerformanceMessage m = new PerformanceMessage();
		m.setSourceComponent(this.getClass()+"");
		m.setD82TestTableName("S73");
		m.setTestContext("Privacyprotection.PrivacyDataManager.Obfuscation.Counter");
		m.setOperationType("NumberOfObfuscationDone");
		m.setPerformanceType(IPerformanceMessage.Quanitative);
		m.setPerformanceNameValue((++performanceObfuscationCount)+"");
		PERF_LOG.trace(m.toString());
		// Average obfuscation
		m.setD82TestTableName("S75");
		m.setTestContext("Privacyprotection.PrivacyDataManager.Obfuscation.AverageObfuscationLevel");
		m.setOperationType("LogEachObfuscationLevel");
		m.setPerformanceType(IPerformanceMessage.Quanitative);
		m.setPerformanceNameValue(obfuscationLevel+"");
		PERF_LOG.trace(m.toString());
		return new AsyncResult<Double>(obfuscationLevel);
	}


	/* *********************************
	 * Dependency Management
	 * ******************************* */
	public void setPrivacyPreferenceManager(IPrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
	}
	public void setPrivacyDataManagerInternal(IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
	}
	public void setDataObfuscationManager(IDataObfuscationManager dataObfuscationManager) {
		this.dataObfuscationManager = dataObfuscationManager;
	}
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public boolean isEnabled() {
		if (IS_DEVELOPMENT_MODE) 
			return enabled;
		return true;
	}
	/**
	 * To enable / disable the privacy access control layer.
	 * This is for development purpose, and will not be enforced in production mode.
	 * @param enabled
	 */
	@Value("${accesscontrol.privacy.enabled:1}")
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	/**
	 * Level 0: all
	 * Level 1: privacyPref, commManager
	 * Level 2: privacyPref, commManager, idManager
	 * Level 3: privacyPref, privacyPol, commManager, cisManager
	 * Level 4: privacyPref
	 */
	private boolean isDependencyInjectionDone(int level) {
		if (null == privacyPreferenceManager) {
			LOG.error("[Dependency Injection] Missing PrivacyPreferenceManager");
			return false;
		}
		if (level == 0 || level == 1) {
			if (null == commManager) {
				LOG.error("[Dependency Injection] Missing CommManager");
				return false;
			}
		}
		if (level == 0 || level == 2) {
			if (null == commManager) {
				LOG.error("[Dependency Injection] Missing CommManager");
				return false;
			}
			if (null == commManager.getIdManager()) {
				LOG.error("[Dependency Injection] Missing IdManager");
				return false;
			}
		}
		if (level == 0 || level == 3) {
			if (null == privacyPolicyManager) {
				LOG.error("[Dependency Injection] Missing PrivacyPolicyManager");
				return false;
			}
			if (null == commManager) {
				LOG.error("[Dependency Injection] Missing CommManager");
				return false;
			}
			if (null == cisManager) {
				LOG.error("[Dependency Injection] Missing CisManager");
				return false;
			}
		}
		return true;
	}
}
