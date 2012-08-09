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
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#getPermission(org.societies.api.identity.Requestor, org.societies.api.schema.identity.DataIdentifier)
	 */
	@Override
	public ResponseItem getPermission(Requestor requestor, DataIdentifier dataId) throws PrivacyException {
		return getPermission(requestor, dataId, null);
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
		if (null == dataId.getOwnerId()) {
			throw new PrivacyException("[Parameters] OwnerId is missing");
		}

		Session session = sessionFactory.openSession();
		ResponseItem permission = null;
		Transaction t = session.beginTransaction();
		try {
			// -- Retrieve the privacy permission
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId);
			List<PrivacyPermission> privacyPermissions = (List<PrivacyPermission>) criteria.list();


			// -- Generate the response item
			// - Privacy Permissions don't exist
			if (null == privacyPermissions && privacyPermissions.size() <= 0) {
				LOG.debug("PrivacyPermission not available");
				return null;
			}
			// - Privacy permissions retrieved
			PrivacyPermission relevantPrivacyPermission = null;
			// Check action list
			if (null != actions) {
				boolean found = false;
				for(PrivacyPermission privacyPermission : privacyPermissions) {
					// Action list is matching: return this one
					if (containsActions(privacyPermission.getActionsFromString(), actions)) {
						relevantPrivacyPermission = privacyPermission;
						found = true;
						break;
					}
				}
				if (!found) {
					LOG.debug("PrivacyPermission (matching ation list) not available");
					return null;
				}
			}
			else {
				relevantPrivacyPermission = privacyPermissions.get(0);
			}
			// Return the relevant privacy permission
			permission = relevantPrivacyPermission.createResponseItem();
			LOG.debug("PrivacyPermission retrieved: "+relevantPrivacyPermission.toString());
		} catch (Exception e) {
			t.rollback();
			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return permission;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxIdentifier, java.util.List, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyOutcomeConstants)
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
		LOG.info("############updatePermission"+dataId.getUri());

		Session session = sessionFactory.openSession();
		boolean result = false;
		Transaction t = session.beginTransaction();
		try {
			// -- Retrieve the privacy permission
			Criteria criteria = findPrivacyPermissions(session, requestor, dataId);
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
			t.rollback();
			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return result;
	}

	/* (non-Javadoc)
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
		}
		else {
			throw new PrivacyException("[Parameters] DataId or DataType is missing");
		}
		return updatePermission(requestor, dataId, permission.getRequestItem().getActions(), permission.getDecision());
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyDataManagerInternal#deletePermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public boolean deletePermission(Requestor requestor, DataIdentifier dataId) throws PrivacyException {
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
			PrivacyPermission privacyPermission = (PrivacyPermission) criteria.uniqueResult();

			// -- Delete the privacy permission
			// - Privacy Permission doesn't exist
			if (null == privacyPermission) {
				LOG.debug("PrivacyPermission not available: no need to delete");
			}
			// - Privacy permission retrieved: delete it
			else {
				LOG.info(privacyPermission.toString());
				session.delete(privacyPermission);
				t.commit();
				LOG.debug("PrivacyPermission deleted.");
			}
			result = true;
		} catch (Exception e) {
			t.rollback();
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
		Criteria criteria = session
				.createCriteria(PrivacyPermission.class)
				.add(Restrictions.like("requestorId", requestor.getRequestorId().getJid()))
				.add(Restrictions.like("dataId", dataId.getUri()));
		if (null != actions && actions.size() > 0) {
			// All actions
			Criterion criterionAllActions = null;
			for(Action action : actions) {
				criterionAllActions = Restrictions.and(criterionAllActions, Restrictions.like("actions", "%"+action.getActionType().name()+":"+action.isOptional()+"%"));
			}
			// Actions without optional ones
			// TODO
		}
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
		return criteria;
	}

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
