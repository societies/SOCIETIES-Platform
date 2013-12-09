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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.DataIdentifierUtils;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.datamanagement.util.PrivacyDataManagerInternalUtility;
import org.societies.privacytrust.privacyprotection.model.PrivacyPermission;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerInternal extends PrivacyDataManagerInternalUtility implements IPrivacyDataManagerInternal {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerInternal.class);

	private SessionFactory sessionFactory;


	@Override
	public List<ResponseItem> getPermissions(RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException {
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataIds || dataIds.size() <= 0) {
			LOG.debug("[Parameters] Data id list is missing, return null");
			return null;
		}

		List<ResponseItem> permissions = null;
		Session session = null;
		try {
			session = sessionFactory.openSession();

			// Search all matching permissions
			List<PrivacyPermission> privacyPermissions = findPrivacyPermissions(session, requestor, dataIds, actions, false);
			LOG.debug("Get: {} permissions retrieved", (null == privacyPermissions ? "0" : privacyPermissions.size()));
			if (null == privacyPermissions) {
				return null;
			}

			// Keep the most relevants for these actions
			permissions = new ArrayList<ResponseItem>();
			if (actions != null && actions.size() > 0) {
				for(DataIdentifier dataId : dataIds) {
					PrivacyPermission permission = selectRelevantPermission(dataId, privacyPermissions);
					if (null != permission) {
						LOG.trace("Get: on of the retrieved and selected permission. {}", permission);
						permissions.add(permission.createResponseItem());
					}
				}
			}
			else {
				LOG.trace("Get: don't check actions and keep them all");
				permissions.addAll(PrivacyPermission.createResponseItems(privacyPermissions));
			}
			// Robustness
			if (permissions.size() <= 0) {
				permissions = null;
			}
		} catch (Exception e) {
			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		return permissions;
	}

	@Override
	public boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision decision) throws PrivacyException {
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] DataId is missing");
		}
		if (null == actions || actions.size() <= 0) {
			throw new PrivacyException("[Parameters] Actions are missing");
		}
		if (null == decision) {
			throw new PrivacyException("[Parameters] Decision is missing");
		}

		Session session = null;
		Transaction t = null;
		boolean result = false;
		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();

			// -- Retrieve the privacy permission (that matches all actions)
			PrivacyPermission privacyPermission = findPrivacyPermissions(session, requestor, dataId, actions);

			// -- Update this privacy permission
			// - Privacy Permission doesn't exist: create a new one
			if (null == privacyPermission) {
				LOG.trace("Update: no permission retrieved, create a new one");
				privacyPermission = new PrivacyPermission(requestor, dataId, actions, decision);
			}
			// - Privacy permission already exists: update it
			else {
				LOG.trace("Update: permission retrieved, update it. Before: {}", privacyPermission);
				privacyPermission.setRequestor(requestor);
				privacyPermission.setDataId(dataId);
				privacyPermission.setActionsToData(actions);
				privacyPermission.setPermission(decision);
			}
			// - Update
			session.saveOrUpdate(privacyPermission);
			t.commit();
			result = true;
		} catch (Exception e) {
			if (null != t) {
				t.rollback();
			}
			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		} finally {
			if (null != session) {
				session.close();
			}
		}
		return result;
	}

	@Override
	public boolean deletePermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] DataId is missing");
		}

		Session session = null;
		Transaction t = null;
		boolean result = false;
		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();

			// -- Retrieve the privacy permissions
			List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
			dataIds.add(dataId);
			List<PrivacyPermission> privacyPermissions = findPrivacyPermissions(session, requestor, dataIds, actions, false);

			// -- Delete the privacy permission
			// - Privacy permission retrieved: delete it
			if (null != privacyPermissions && privacyPermissions.size() > 0) {
				for(Iterator<PrivacyPermission> it = privacyPermissions.iterator(); it.hasNext();) {
					session.delete(it.next());
				}
				t.commit();
			}
			result = true;
		} catch (Exception e) {
			if (null != t) {
				t.rollback();
			}
			result = false;
			throw new PrivacyException("Error during the removal of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return result;
	}


	/* --- Private methods --- */
	/**
	 * Retrieve a list of privacy permission
	 * 
	 * @param session
	 * @param requestor
	 * @param dataId
	 * @param actions
	 * @param mustMatchAllActions True to retrieve only if it matches all actions
	 * @return  If all actions are mandatory: only one result
	 * @return If some actions are optional: several results, ordered by relevance
	 * @throws PrivacyException 
	 */
	private List<PrivacyPermission> findPrivacyPermissions(Session session, RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, boolean mustMatchAllActions) throws PrivacyException {
		List<PrivacyPermission> privacyPermissions = null;
		try {
			// Retrieve the privacy permissions
			Criteria criteria = createPrivacyPermissionsCriteria(session, requestor, dataIds, actions, mustMatchAllActions);
			privacyPermissions = (List<PrivacyPermission>) criteria.list();
			if (null == privacyPermissions || privacyPermissions.size() <= 0) {
				privacyPermissions = null;
			}
		}
		catch (Exception e) {
			throw new PrivacyException("Error during the retrieving of the privacy permissions", e);
		}
		return privacyPermissions;
	}

	/**
	 * Duplication of {@link #findPrivacyPermissions(RequestorBean, List, List, boolean)} for one data id
	 * All actions must match
	 */
	private PrivacyPermission findPrivacyPermissions(Session session, RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		List<DataIdentifier> dataIds = new ArrayList<DataIdentifier>();
		dataIds.add(dataId);
		List<PrivacyPermission> privacyPermissions = findPrivacyPermissions(session, requestor, dataIds, actions, true);
		if (null == privacyPermissions || privacyPermissions.size() <= 0) {
			return null;
		}
		return privacyPermissions.get(0);
	}


	private Criteria createPrivacyPermissionsCriteria(Session session, RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions, boolean mustMatchAllActions) {
		Criteria criteria = session.createCriteria(PrivacyPermission.class);
		// -- Criteria about data id
		criteria = criteria.add(Restrictions.in("dataId", DataIdentifierUtils.toUriString(dataIds).toArray()));
		// -- Criteria about requestor
		criteria.add(Restrictions.like("requestorId", RequestorUtils.toUriString(requestor)));
		// -- Criteria about action list
		if (null != actions && actions.size() > 0) {
			// - Create strings
			Criterion criterionAllActions = null;
			Criterion criterionMandatoryActions = null;
			StringBuilder strAllActions = new StringBuilder();
			StringBuilder strMandatoryActions = new StringBuilder();
			Collections.sort(actions, new ActionUtils.ActionComparator());
			for(Action action : actions) {
				strAllActions.append(action.getActionConstant().value()+"/");
				if (!action.isOptional()) {
					strMandatoryActions.append(action.getActionConstant().value()+"/");
				}
			}
			// - Create the query
			criterionAllActions = Restrictions.like("actions", strAllActions.toString());
			// If mode: match only mandatory actions OR no optional actions requested
			if (mustMatchAllActions || strAllActions.toString().equals(strMandatoryActions.toString())) {
				criteria.add(criterionAllActions);
			}
			// Otherwise: match all actions OR only mandatory actions
			else {
				criterionMandatoryActions = Restrictions.like("actions", strMandatoryActions.toString());
				criteria.add(Restrictions.or(criterionAllActions, criterionMandatoryActions));
			}
		}
		// - Order by data id, then the number of mandatory actions, and then by decision
		criteria.addOrder(Order.desc("dataId"));
		criteria.addOrder(Order.desc("nbOfActions"));
		criteria.addOrder(Order.desc("permission"));
		return criteria;
	}

	/**
	 * Select the most relevant permission for this data id
	 * @pre The list of permissions is order by the number of actions available and their decisions. For a faster algorithm.
	 * @param dataId
	 * @param privacyPermissions
	 * @return The most relevant permission
	 */
	private PrivacyPermission selectRelevantPermission(DataIdentifier dataId, List<PrivacyPermission> privacyPermissions) {
		if (null == privacyPermissions || privacyPermissions.size() <= 0) {
			return null;
		}

		PrivacyPermission mostRelevantPrivacyPermission = null;
		PrivacyPermission aPrivacyPermission = null;
		// Find the most relevant PERMIT (even if we need to avoid some optional actions)
		boolean found = false;
		for(PrivacyPermission privacyPermission : privacyPermissions) {
			// Interesting permission
			if (DataIdentifierUtils.toUriString(dataId).equals(privacyPermission.getDataId())) {
				// Store one matching privacy permission
				aPrivacyPermission = privacyPermission;

				// If it matches to PERMIT, this is the most relevant
				if(privacyPermission.getPermission().equals(Decision.PERMIT)) {
					mostRelevantPrivacyPermission = privacyPermission;
					privacyPermissions.remove(privacyPermission);
					found = true;
					break;
				}
			}
		}
		// If no PERMIT has been found: take a matching permission (if any)
		if (!found) {
			mostRelevantPrivacyPermission = aPrivacyPermission;
		}
		// - Return the most relevant privacy permission
		if (null == mostRelevantPrivacyPermission) {
			return null;
		}
		return mostRelevantPrivacyPermission;
	}


	/* --- Dependency Injection --- */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
