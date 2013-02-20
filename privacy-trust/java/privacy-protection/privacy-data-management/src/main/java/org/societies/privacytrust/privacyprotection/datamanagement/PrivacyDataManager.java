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
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.DataIdentifierUtil;
import org.societies.api.identity.DataTypeFactory;
import org.societies.api.identity.DataTypeUtil;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.logging.IPerformanceMessage;
import org.societies.api.internal.logging.PerformanceMessage;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ConditionUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.DObfOutcome;
import org.societies.privacytrust.privacyprotection.dataobfuscation.DataObfuscationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManager implements IPrivacyDataManager {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManager.class.getName());
	private static Logger PERF_LOG = LoggerFactory.getLogger("PerformanceMessage"); // to define a dedicated Logger for Performance Testing
	private static long performanceObfuscationCount = 0;

	private IPrivacyDataManagerInternal privacyDataManagerInternal;
	private IPrivacyPreferenceManager privacyPreferenceManager;
	private IDataObfuscationManager dataObfuscationManager;
	private IPrivacyPolicyManager privacyPolicyManager;
	private ICommManager commManager;
	private ICisManager cisManager;
	private IIdentity currentCssId;

	public PrivacyDataManager()  {
		dataObfuscationManager = new DataObfuscationManager();
		currentCssId = null;
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new NullPointerException("[Parameters] Not enought information: requestor or owner id is missing");
		}
		if (null == dataId) {
			throw new NullPointerException("[Parameters] Not enought information: data id is missing. At least the data type is expected.");
		}
		if (null == actions || actions.size() <= 0) {
			throw new NullPointerException("[Parameters] Actions are missing");
		}
		if (!atLeast1MandatoryAction(actions)) {
			throw new PrivacyException("[Parameters] At least one mandatory action is required, they can't be all optional.");
		}
		// Uncomment when data hiearchy will be ready :-)
//		if (!(new DataTypeUtil().isLeaf(DataTypeFactory.getType(dataId)))) {
//			throw new PrivacyException("[Parameters] Can't manage access control on data type "+DataTypeFactory.getType(dataId)+" (it is not a leaf in the data type hiearchy)");
//		}
		if (!isDepencyInjectionDone(1)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}
		

		// -- Create useful values for default result
		List<Condition> conditions = new ArrayList<Condition>();
		Resource resource = new Resource(dataId);
		RequestItem requestItemNull = new RequestItem(resource, actions, conditions);

		// -- Retrieve a stored permission
		ResponseItem permission = privacyDataManagerInternal.getPermission(requestor, dataId, actions);

		// -- Permission not available
		if (null == permission || null == permission.getRequestItem()) {
			LOG.info("No Permission retrieved");
			permission = null;

			// - Check access control type: CSS or CIS
			boolean cssAccessControl = true;
			IIdentity ownerId = null;
			if (null != dataId.getOwnerId()) {
				// Retrieve Owner IIdentity
				try {
					ownerId = commManager.getIdManager().fromJid(dataId.getOwnerId());
				} catch (InvalidFormatException e) {
					LOG.error("[From JID Error] IIdentity can not be understand.", e);
				}
				LOG.info("Check CSS/CIS Data Owner for using identity: "+ownerId);
				// Check if the owner IIdentity has a CIS type
				if (null != ownerId && IdentityType.CIS.equals(ownerId.getType())) {
					cssAccessControl = false;
				}
			}

			// - Access control for CIS data: use the CIS privacy policy
			if (!cssAccessControl) {
				LOG.info("CIS Data Access Control");
				permission = checkPermissionCisData(requestor, dataId, actions, ownerId);
			}
			// - Access control for CSS data: ask to PrivacyPreferenceManager
			else {
				LOG.info("CSS Data Access Control");
				permission = checkPermissionCssData(requestor, dataId, actions);
			}

			// - Still no permission available: deny access
			if (null == permission || null == permission.getRequestItem()) {
				permission = new ResponseItem(requestItemNull, Decision.DENY);
			}
			// Store new permission retrieved from PrivacyPreferenceManager
			privacyDataManagerInternal.updatePermission(requestor, permission);
		}
		return permission;
	}

	private ResponseItem checkPermissionCisData(Requestor requestor, DataIdentifier dataId, List<Action> actions, IIdentity cisId) throws PrivacyException {
		// -- Create useful values for default result
		List<Condition> conditions = new ArrayList<Condition>();
		Resource resource = new Resource(dataId);
		RequestItem requestItemNull = new RequestItem(resource, actions, conditions);
		ResponseItem permission = new ResponseItem(requestItemNull, Decision.DENY);
		// -- Internal call (requestor == current node)
		if (null == currentCssId) {
			currentCssId = commManager.getIdManager().getThisNetworkNode();
			LOG.info("[checkPermissionCisData] CurrentCssId: "+currentCssId.getJid());
			LOG.info("[checkPermissionCisData] Requestor Id: "+requestor.getRequestorId().getJid());
		}
		if (currentCssId.getJid().equals(requestor.getRequestorId().getJid())) {
			LOG.info("[checkPermissionCisData] Internal call: always PERMIT");
			return new ResponseItem(requestItemNull, Decision.PERMIT);
		}
		// -- Verify parameters
		if (!isDepencyInjectionDone(3)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}

		// -- Retrieve the CIS Privacy Policy
		RequestorCis requestorCis = new RequestorCis(currentCssId, cisId);
		RequestPolicy privacyPolicy = null;
		try {
			LOG.info("[checkPermissionCisData] Retrieve the privacy policy of: "+requestorCis);
			privacyPolicy = privacyPolicyManager.getPrivacyPolicy(requestorCis);
		}
		catch(Exception e) {
			LOG.error("[checkPermissionCisData] Error: The privacy policy can not be retrieved for this CIS: "+requestorCis.toString(), e);
			return permission;
		}
		// Can't retrieve the privacy policy OR empty one: DENY all
		if (null == privacyPolicy || null == privacyPolicy.getRequests() || privacyPolicy.getRequests().size() <= 0) {
			LOG.error("[checkPermissionCisData] The privacy policy can not be retrieved, or is empty, for this CIS: "+requestorCis.toString());
			return permission;
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
			actionsDeepCopy.add(new Action(actions.get(i)));
		}
		try {
			LOG.info("Cis Privacy Policy: "+privacyPolicy);
			for(RequestItem request : privacyPolicy.getRequests()) {
				LOG.info("[checkPermissionCisData] Searching: "+dataId.getUri()+" in ");
				LOG.info("Resource: "+request.getResource().toXMLString());
				if (null != request.getResource().getScheme() && null != request.getResource().getDataType()) {
					LOG.info("Resource Data ID scheme: "+request.getResource().getScheme());
					LOG.info("Resource Data ID type: "+request.getResource().getDataType());
				}
				if (null != request.getResource().getDataId()) {
					LOG.info("Resource Data ID uri: "+request.getResource().getDataId().getUri());
				}
				// - Match data id or data type
				if ((null != request.getResource().getDataId() && dataId.getUri().equals(request.getResource().getDataId().getUri()))
						|| (null != request.getResource().getScheme() && null != request.getResource().getDataType() && dataId.getScheme().value().equals(request.getResource().getScheme().value()) && dataId.getType().equals(request.getResource().getDataType()))) {
					if (null != request.getResource().getScheme() && null != request.getResource().getDataType()) {
						LOG.info("[checkPermissionCisData] One data is matching on the privacy policy: "+request.getResource().getScheme()+"//"+request.getResource().getDataType());
					}
					if (null != request.getResource().getDataId()) {
						LOG.info("[checkPermissionCisData] One data is matching on the privacy policy: "+request.getResource().getDataId().getUri());
					}
					List<Action> actionsThatMatch = new ArrayList<Action>();
					boolean allRequestedActionsMatch = ActionUtils.contains(actionsDeepCopy, request.getActions(), actionsThatMatch);
					boolean canBeSharedWith3pServices = ConditionUtils.contains(ConditionConstants.SHARE_WITH_3RD_PARTIES, request.getConditions());
					// All requested actions are matching AND if this data is public
					if (allRequestedActionsMatch && canBeSharedWith3pServices) {
						LOG.info("[checkPermissionCisData] All requested items are matching (public): PERMIT");
						return new ResponseItem(requestItemNull, Decision.PERMIT);
					}
					boolean canBeSharedWithCisMembersOnly = ConditionUtils.contains(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, request.getConditions());
					// Retrieve Cis member list
					if (null == cisMemberList) {
						cisMemberList = retrieveCisMemberList(dataId.getOwnerId());
					}
					//  All requested actions are matching AND if this data is members only
					if (allRequestedActionsMatch && canBeSharedWithCisMembersOnly) {
						LOG.info("[checkPermissionCisData] All requested items are matching (members only): PERMIT if necessary");
						// Is it a CIS member?
						if (isCisMember(cisMemberList, dataId.getOwnerId(), requestor.getRequestorId().getJid())) {
							return new ResponseItem(requestItemNull, Decision.PERMIT);
						}
						return new ResponseItem(requestItemNull, Decision.DENY);
					}
					// Requested actions are partially matching AND if this data is public
					if (actionsThatMatch.size() > 0 && canBeSharedWith3pServices) {
						LOG.info("[checkPermissionCisData] Some requested items are matching (public)");
						actionsDeepCopy.removeAll(actionsThatMatch);
						continue;
					}
					// Requested actions are partially matching AND if this data is members only
					if (actionsThatMatch.size() > 0 && canBeSharedWithCisMembersOnly) {
						LOG.info("[checkPermissionCisData] Some requested items are matching (members only)");
						// Is it a CIS member?
						if (isCisMember(cisMemberList, dataId.getOwnerId(), requestor.getRequestorId().getJid())) {
							actionsDeepCopy.removeAll(actionsThatMatch);
							continue;
						}
						return new ResponseItem(requestItemNull, Decision.DENY);
					}
				}
			}
		}
		catch(Exception e) {
			LOG.error("Exception during CIS Data Access control", e);
			return new ResponseItem(requestItemNull, Decision.DENY);
		}
		LOG.info("[checkPermissionCisData] No requested items are matching, or anyway, they are private: always DENY");
		return permission;
	}

	/**
	 * @param cisMemberList
	 * @param ownerId
	 * @throws PrivacyException 
	 */
	private boolean isCisMember(List<ICisParticipant> cisMemberList, String cisId, String cssId) throws PrivacyException {
		if (null != cisMemberList) {
			for (ICisParticipant cisMember : cisMemberList) {
				if (cisMember.getMembersJid().equals(cssId)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * @return
	 * @throws PrivacyException 
	 */
	private List<ICisParticipant> retrieveCisMemberList(String cisId){
		Set<ICisParticipant> ciMemberListIncome = cisManager.getOwnedCis(cisId).getMemberList();
		return new ArrayList<ICisParticipant>(ciMemberListIncome);
	}


	private ResponseItem checkPermissionCssData(Requestor requestor, DataIdentifier dataId, List<Action> actions) {
		// -- Retrieve a permission using the PrivacyPreferenceManager
		ResponseItem permission = null;
		try {
			permission = privacyPreferenceManager.checkPermission(requestor, dataId, actions);
		} catch (Exception e) {
			LOG.error("Error when retrieving permission from PrivacyPreferenceManager", e);
		}
		return permission;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, java.util.List, java.util.List)
	 */
	@Override
	public List<ResponseItem> checkPermission(Requestor requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException {
		List<ResponseItem> responseItemList = new ArrayList<ResponseItem>();
		for(DataIdentifier dataId : dataIds) {
			responseItemList.add(checkPermission(requestor, dataId, actions));
		}
		return responseItemList;
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, java.util.List, org.societies.api.privacytrust.privacy.model.privacypolicy.Action)
	 */
	@Override
	public List<ResponseItem> checkPermission(Requestor requestor, List<DataIdentifier> dataIds, Action action) throws PrivacyException {
		// List of actions
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		return checkPermission(requestor, dataIds, actions);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, DataIdentifier dataId, Action action) throws PrivacyException {
		// List of actions
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		return checkPermission(requestor, dataId, actions);
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#checkPermission(org.societies.api.internal.mock.CtxIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.mock.EntityIdentifier, org.societies.api.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public ResponseItem checkPermission(Requestor requestor, IIdentity ownerId, CtxIdentifier dataId, Action action) throws PrivacyException {
		// List of actions
		List<Action> actions = new ArrayList<Action>();
		actions.add(action);
		return checkPermission(requestor, dataId, actions);
	}

	
	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Async
	@Override
	public Future<IDataWrapper> obfuscateData(Requestor requestor, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new NullPointerException("Not enought information: requestor or owner id is missing");
		}
		if (null == dataWrapper || null == dataWrapper.getData()) {
			throw new PrivacyException("Not enought information: data missing");
		}
		if (null == dataWrapper.getDataId()) {
			throw new PrivacyException("Not enought information: data id missing. At least the data type is expected");
		}
		if (!isDepencyInjectionDone(2)) {
			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		}

		// -- Retrieve the obfuscation level
		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, dataWrapper.getDataId().getType());
		double obfuscationLevel = 1;
		if (null != dataObfuscationPreferences) {
			obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		}
		// - Performance loggings
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
		// - If no obfuscation is required: return directly the wrapped data
		if (obfuscationLevel >= 1) {
			return new AsyncResult<IDataWrapper>(dataWrapper);
		}

		// -- Obfuscate the data
		IDataWrapper obfuscatedDataWrapper = dataObfuscationManager.obfuscateData(dataWrapper, obfuscationLevel);
		return new AsyncResult<IDataWrapper>(obfuscatedDataWrapper);
	}
	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#obfuscateData(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Async
	@Override
	public Future<IDataWrapper> obfuscateData(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		return obfuscateData(requestor, dataWrapper);
	}

	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public IDataWrapper hasObfuscatedVersion(Requestor requestor, IDataWrapper dataWrapper) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			throw new NullPointerException("Not enought information: requestor or owner id is missing");
		}
		if (null == dataWrapper || null == dataWrapper.getDataId()) {
			throw new PrivacyException("Not enought information: data id is missing. At least the data type is expected.");
		}
		return dataWrapper;
		// Not use at the moment
		//		if (!isDepencyInjectionDone(2)) {
		//			throw new PrivacyException("[Dependency Injection] PrivacyDataManager not ready");
		//		}
		//		
		//		// -- Retrieve the obfuscation level
		//		DObfOutcome dataObfuscationPreferences = privacyPreferenceManager.evaluateDObfPreference(requestor, dataWrapper.getDataId().getType());
		//		double obfuscationLevel = 1;
		//		if (null != dataObfuscationPreferences) {
		//			obfuscationLevel = dataObfuscationPreferences.getObfuscationLevel();
		//		}
		//		
		//		// -- Check if an obfuscated version is available
		//		return dataObfuscationManager.hasObfuscatedVersion(dataWrapper, obfuscationLevel);
	}
	/*
	 * 
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager#hasObfuscatedVersion(org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper, double, org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.listener.IDataObfuscationListener)
	 */
	@Override
	public String hasObfuscatedVersion(Requestor requestor, IIdentity ownerId, IDataWrapper dataWrapper) throws PrivacyException {
		return hasObfuscatedVersion(requestor, dataWrapper).getDataId().getUri();
	}


	// -- Private methods

	/**
	 * Simple method to check if a list of actions has, at least, one action
	 * which is not optional
	 * @param actions List of action
	 * @return True if the list is ok
	 */
	private boolean atLeast1MandatoryAction(List<Action> actions) {
		boolean oneMandatory = false;
		for(Action action : actions) {
			if (!action.isOptional()) {
				oneMandatory = true;
				break;
			}
		}
		return oneMandatory;
	}

	// --- Dependency Injection
	public void setPrivacyPreferenceManager(
			IPrivacyPreferenceManager privacyPreferenceManager) {
		this.privacyPreferenceManager = privacyPreferenceManager;
		LOG.info("[Dependency Injection] privacyPreferenceManager injected");
	}
	public void setPrivacyDataManagerInternal(
			IPrivacyDataManagerInternal privacyDataManagerInternal) {
		this.privacyDataManagerInternal = privacyDataManagerInternal;
		LOG.info("[Dependency Injection] PrivacyDataManagerInternal injected");
	}
	public void setPrivacyPolicyManager(
			IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
		LOG.info("[Dependency Injection] PrivacyPolicyManager injected");
	}
	public void setCommManager(
			ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[Dependency Injection] CommManager injected");
	}
	public void setCisManager(
			ICisManager cisManager) {
		this.cisManager = cisManager;
		LOG.info("[Dependency Injection] ICisManager injected");
	}


	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == privacyPreferenceManager) {
			LOG.info("[Dependency Injection] Missing PrivacyPreferenceManager");
			return false;
		}
		if (level == 0 || level == 1) {
			if (null == privacyDataManagerInternal) {
				LOG.info("[Dependency Injection] Missing PrivacyDataManagerInternal");
				return false;
			}
			if (null == commManager) {
				LOG.info("[Dependency Injection] Missing CommManager");
				return false;
			}
		}
		if (level == 0 || level == 2) {
			if (null == dataObfuscationManager) {
				LOG.info("[Dependency Injection] Missing DataObfuscationManager");
				return false;
			}
		}
		if (level == 0 || level == 3) {
			if (null == privacyPolicyManager) {
				LOG.info("[Dependency Injection] Missing PrivacyPolicyManager");
				return false;
			}
			if (null == commManager) {
				LOG.info("[Dependency Injection] Missing CommManager");
				return false;
			}
			if (null == cisManager) {
				LOG.info("[Dependency Injection] Missing CisManager");
				return false;
			}
		}
		return true;
	}
}
