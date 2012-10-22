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
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.SimpleDataIdentifier;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal;
import org.societies.privacytrust.privacyprotection.model.PrivacyPermission;

/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerInternal implements IPrivacyDataManagerInternal {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerInternal.class.getSimpleName());

	private SessionFactory sessionFactory;


	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#getPermissions(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier)
	 */
	@Override
	public List<ResponseItem> getPermissions(Requestor requestor, DataIdentifier dataId) throws PrivacyException {
		// Check Dependency injection
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] Data Storage Manager not ready");
		}
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] DataId is missing");
		}
		//		if (null == dataId.getOwnerId()) {
		//			throw new PrivacyException("[Parameters] OwnerId is missing");
		//		}

		Session session = sessionFactory.openSession();
		List<ResponseItem> permissions = new ArrayList<ResponseItem>();
		session = sessionFactory.openSession();
		try {
			// -- Retrieve the privacy permission
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId);
			List<PrivacyPermission> privacyPermissions = (List<PrivacyPermission>) criteria.list();


			// -- Generate the response item
			// - Privacy Permissions don't exist
			if (null == privacyPermissions || privacyPermissions.size() <= 0) {
				LOG.debug("PrivacyPermission not available");
				return null;
			}
			// - Privacy permissions retrieved
			for(PrivacyPermission privacyPermission : privacyPermissions) {
				permissions.add(privacyPermission.createResponseItem());
				LOG.debug("PrivacyPermission retrieved: "+privacyPermission.toString());
			}
		} catch (Exception e) {
			throw new PrivacyException("Error during the retrieving of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return permissions;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#getPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	@Override
	public ResponseItem getPermission(Requestor requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// Check Dependency injection
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] Data Storage Manager not ready");
		}
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] DataId is missing");
		}
		//		if (null == dataId.getOwnerId()) {
		//			throw new PrivacyException("[Parameters] OwnerId is missing");
		//		}
		if (null == actions || actions.size() <= 0) {
			throw new PrivacyException("[Parameters] Actions are missing");
		}

		Session session = sessionFactory.openSession();
		ResponseItem permission = null;
		session = sessionFactory.openSession();
		try {
			// -- Retrieve the privacy permission
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId, actions);
			List<PrivacyPermission> privacyPermissions = (List<PrivacyPermission>) criteria.list();


			// -- Generate the response item
			// - Privacy Permissions don't exist
			if (null == privacyPermissions || privacyPermissions.size() <= 0) {
				LOG.debug("PrivacyPermission not available");
				return null;
			}
			// - Privacy permissions retrieved
			PrivacyPermission relevantPrivacyPermission = null;
			// Find the most relevant PERMIT (even if we need to avoid some optional actions)
			boolean found = false;
			for(PrivacyPermission privacyPermission : privacyPermissions) {
				// If it matches to PERMIT, this is the most relevant
				if (privacyPermission.getPermission().equals(Decision.PERMIT)) {
					relevantPrivacyPermission = privacyPermission;
					found = true;
					break;
				}
			}
			// If no PERMIT has been found: take the most relevant (i.e. the first one)
			if (!found) {
				relevantPrivacyPermission = privacyPermissions.get(0);
			}
			// - We could also try (in a second loop) to deduce a result by enlarging the research
			// Not at the moment
			// - Return the most relevant privacy permission
			permission = relevantPrivacyPermission.createResponseItem();
			LOG.debug("PrivacyPermission retrieved: "+relevantPrivacyPermission.toString());
		} catch (Exception e) {
			throw new PrivacyException("Error during the retrieving of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return permission;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision)
	 */
	@Override
	public boolean updatePermission(Requestor requestor, DataIdentifier dataId, List<Action> actions, Decision permission) throws PrivacyException {
		// Check Dependency injection
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] Data Storage Manager not ready");
		}
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

		Session session = sessionFactory.openSession();
		boolean result = false;
		Transaction t = session.beginTransaction();
		try {
			// -- Retrieve the privacy permission (that matches all actions)
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId, actions, true);
			PrivacyPermission privacyPermission = (PrivacyPermission) criteria.uniqueResult();

			// -- Update this privacy permission
			// - Privacy Permission doesn't exist: create a new one
			if (null == privacyPermission) {
				LOG.debug("PrivacyPermission doesn not already exist: create it");
				privacyPermission = new PrivacyPermission(requestor, dataId, actions, permission);
			}
			// - Privacy permission already exists: update it
			else {
				privacyPermission.setRequestor(requestor);
				privacyPermission.setDataId(dataId);
				privacyPermission.setActions(actions);
				privacyPermission.setPermission(permission);
			}
			// - Update
			session.saveOrUpdate(privacyPermission);
			t.commit();
			LOG.debug("PrivacyPermission saved: "+privacyPermission.toString());
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

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)
	 */
	@Override
	public boolean updatePermission(Requestor requestor, ResponseItem permission) throws PrivacyException {
		DataIdentifier dataId;
		// Data id
		if (null != permission.getRequestItem().getResource().getDataId()) {
			dataId = permission.getRequestItem().getResource().getDataId();
		}
		// Data type only
		else if (null != permission.getRequestItem().getResource().getDataType() && !"".equals(permission.getRequestItem().getResource().getDataType())) {
			dataId = new SimpleDataIdentifier();
			dataId.setType(permission.getRequestItem().getResource().getDataType());
			dataId.setScheme(permission.getRequestItem().getResource().getScheme());
		}
		else {
			throw new PrivacyException("[Parameters] DataId or DataType is missing");
		}
		return updatePermission(requestor, dataId, permission.getRequestItem().getActions(), permission.getDecision());
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#deletePermissions(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier)
	 */
	@Override
	public boolean deletePermissions(Requestor requestor, DataIdentifier dataId) throws PrivacyException {
		// Check Dependency injection
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] Data Storage Manager not ready");
		}
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] DataId is missing");
		}

		Session session = sessionFactory.openSession();
		boolean result = false;
		Transaction t = session.beginTransaction();
		try {
			// -- Retrieve the privacy permission
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId);
			List<PrivacyPermission> privacyPermissions = (List<PrivacyPermission>) criteria.list();

			// -- Delete the privacy permission
			// - Privacy Permission doesn't exist
			if (null == privacyPermissions || privacyPermissions.size() <= 0) {
				LOG.debug("PrivacyPermissions not available: no need to delete");
			}
			// - Privacy permission retrieved: delete it
			else {
				for(Iterator<PrivacyPermission> it = privacyPermissions.iterator(); it.hasNext();) {
					session.delete(it.next());
				}
				t.commit();
				LOG.debug("PrivacyPermissions deleted.");
			}
			result = true;
		} catch (Exception e) {
			if (null != t) {
				t.rollback();
			}
			throw new PrivacyException("Error during the removal of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#deletePermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	@Override
	public boolean deletePermission(Requestor requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		// Check Dependency injection
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] Data Storage Manager not ready");
		}
		// Verifications
		if (null == requestor) {
			throw new PrivacyException("[Parameters] RequestorId is missing");
		}
		if (null == dataId) {
			throw new PrivacyException("[Parameters] DataId is missing");
		}

		Session session = sessionFactory.openSession();
		boolean result = false;
		Transaction t = session.beginTransaction();
		try {
			// -- Retrieve the privacy permission
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId, actions, true);
			PrivacyPermission privacyPermission = (PrivacyPermission) criteria.list();

			// -- Delete the privacy permission
			// - Privacy Permission doesn't exist
			if (null == privacyPermission) {
				LOG.debug("PrivacyPermission not available: no need to delete");
			}
			// - Privacy permission retrieved: delete it
			else {
				session.delete(privacyPermission);
				t.commit();
				LOG.debug("PrivacyPermissions deleted.");
			}
			result = true;
		} catch (Exception e) {
			if (null != t) {
				t.rollback();
			}
			throw new PrivacyException("Error during the removal of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return result;
	}


	// -- Private methods

	private Criteria findPrivacyPermissions(Session session, Requestor requestor, DataIdentifier dataId) {
		return findPrivacyPermissions(session, requestor, dataId, null);
	}
	private Criteria findPrivacyPermissions(Session session, Requestor requestor, DataIdentifier dataId, List<Action> actions) {
		return findPrivacyPermissions(session, requestor, dataId, actions, false);
	}

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
	 */
	private Criteria findPrivacyPermissions(Session session, Requestor requestor, DataIdentifier dataId, List<Action> actions, boolean mustMatchAllActions) {
		// -- Criteria about data id
		Criteria criteria = session
				.createCriteria(PrivacyPermission.class)
				.add(Restrictions.like("dataId", dataId.getUri()));
		// -- Criteria about requestor
		criteria.add(Restrictions.like("requestorId", requestor.getRequestorId().getJid()));
		if (requestor instanceof RequestorCis) {
			criteria.add(Restrictions.like("cisId", ((RequestorCis) requestor).getCisRequestorId().getJid()));
			criteria.add(Restrictions.isNull("serviceId"));
		}
		else if (requestor instanceof RequestorService) {
			criteria.add(Restrictions.isNull("cisId"));
			criteria.add(Restrictions.like("serviceId", ((RequestorService) requestor).getRequestorServiceId().getIdentifier().toString()));
		}
		else {
			criteria.add(Restrictions.isNull("cisId"));
			criteria.add(Restrictions.isNull("serviceId"));
		}
		// -- Criteria about action list
		if (null != actions && actions.size() > 0) {
			// - Create strings
			Criterion criterionAllActions = null;
			Criterion criterionMandatoryActions = null;
			StringBuilder strAllActions = new StringBuilder();
			StringBuilder strMandatoryActions = new StringBuilder();
			for(Action action : actions) {
				strAllActions.append(action.getActionType().name()+"/");
				if (!action.isOptional()) {
					strMandatoryActions.append(action.getActionType().name()+"/");
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
		return criteria;
	}

	@Deprecated
	private boolean containsAction(List<Action> actions, Action action) {
		if (null == actions || actions.size() <= 0 || null == action) {
			return false;
		}
		for(Action actionTmp : actions) {
			if (actionTmp.toXMLString().equals(action.toXMLString())) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	private boolean containsActions(List<Action> actions, List<Action> subActions) {
		if (null == actions || actions.size() <= 0 || null == subActions || subActions.size() <= 0 || actions.size() < subActions.size()) {
			return false;
		}
		for(Action subActionTmp : subActions) {
			if (!containsAction(actions, subActionTmp)) {
				return false;
			}
		}
		return true;
	}


	// --- Dependency Injection
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		LOG.info("[Dependency Injection] sessionFactory injected");
	}

	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		boolean result = true;
		if (null == sessionFactory) {
			result = false;
		}
		return result;
	}

}
