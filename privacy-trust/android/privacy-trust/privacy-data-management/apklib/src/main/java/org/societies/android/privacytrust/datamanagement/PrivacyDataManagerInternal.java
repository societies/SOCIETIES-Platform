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

import org.societies.android.privacytrust.api.IPrivacyDataManagerInternal;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.android.api.identity.util.DataIdentifierFactory;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerInternal implements IPrivacyDataManagerInternal {
	private final static String TAG = PrivacyDataManagerInternal.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.privacytrust.api.IPrivacyDataManagerInternal#getPermission(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	public List<PrivacyPermission> getPermissions(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		List<PrivacyPermission> permissions = null;
		//		Session session = sessionFactory.openSession();
		//		Transaction t = session.beginTransaction();
		//		try {
		//			// -- Retrieve the privacy permission
		//			Criteria criteria = session
		//					.createCriteria(PrivacyPermission.class)
		//					.add(Restrictions.eq("requestorId", requestor.getRequestorBeanId().getJid()))
		//					.add(Restrictions.eq("ownerId", ownerId.getJid()))
		//					.add(Restrictions.eq("dataId", dataId.toUriString()));
		//			if (requestor instanceof RequestorBeanCis) {
		//				criteria.add(Restrictions.eq("cisId", ((RequestorBeanCis) requestor).getCisRequestorBeanId().getJid()));
		//			}
		//			else if (requestor instanceof RequestorBeanService) {
		//				criteria.add(Restrictions.eq("serviceId", ((RequestorBeanService) requestor).getRequestorBeanServiceId().getIdentifier().toString()));
		//			}
		//			PrivacyPermission privacyPermission = (PrivacyPermission) criteria.uniqueResult();
		//
		//
		//			// -- Generate the response item
		//			// - Privacy Permission doesn't exist
		//			if (null == privacyPermission) {
		//				LOG.info("PrivacyPermission not available");
		//				return null;
		//			}
		//			// - Privacy permission retrieved
		//			LOG.info(privacyPermission.toString());
		//			permission = privacyPermission.createResponseItem();
		//			LOG.info("PrivacyPermission retrieved.");
		//		} catch (Exception e) {
		//			t.rollback();
		//			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		//		} finally {
		//			if (session != null) {
		//				session.close();
		//			}
		//		}
		return permissions;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.privacytrust.api.IPrivacyDataManagerInternal#getObfuscationLevel(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	public double getObfuscationLevel(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException {
		// Create list of actions: READ
		List<Action> actions = new ArrayList<Action>();
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		actions.add(action);
		// Retrieve privacy permission
		List<PrivacyPermission> privacyPermissions = getPermissions(requestor, dataId, actions);
		// Retrieve obfuscation level
		if (null != privacyPermissions && privacyPermissions.size() > 0) {
			return privacyPermissions.get(0).getObfuscationLevel();
		}
		// No obfuscation level: default value
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.privacytrust.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.identity.DataIdentifier, java.util.List, org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Decision, double)
	 */
	public boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision permission, double obfuscationLevel) throws PrivacyException {
		boolean result = false;
		//		Session session = sessionFactory.openSession();
		//		Transaction t = session.beginTransaction();
		//		try {
		//			// -- Retrieve the privacy permission
		//			Criteria criteria = session
		//					.createCriteria(PrivacyPermission.class)
		//					.add(Restrictions.eq("requestorId", requestor.getRequestorBeanId().getJid()))
		//					.add(Restrictions.eq("ownerId", ownerId.getJid()))
		//					.add(Restrictions.eq("dataId", dataId.toUriString()));
		//			if (requestor instanceof RequestorBeanCis) {
		//				criteria.add(Restrictions.eq("cisId", ((RequestorBeanCis) requestor).getCisRequestorBeanId().getJid()));
		//			}
		//			else if (requestor instanceof RequestorBeanService) {
		//				criteria.add(Restrictions.eq("serviceId", ((RequestorBeanService) requestor).getRequestorBeanServiceId().getIdentifier().toString()));
		//			}
		//			PrivacyPermission privacyPermission = (PrivacyPermission) criteria.uniqueResult();
		//
		//
		//			// -- Update this privacy permission
		//			// - Privacy Permission doesn't exist: create a new one
		//			if (null == privacyPermission) {
		//				LOG.info("PrivacyPermission not available: create it");
		//				privacyPermission = new PrivacyPermission(requestor, ownerId, dataId, actions, permission);
		//			}
		//			// - Privacy permission already exists: update it
		//			else {
		//				privacyPermission.setRequestorBean(requestor);
		//				privacyPermission.setOwnerId(ownerId);
		//				privacyPermission.setDataId(dataId);
		//				privacyPermission.setActions(actions);
		//				privacyPermission.setPermission(permission);
		//			}
		//			// - Update
		//			session.save(privacyPermission);
		//			t.commit();
		//			LOG.info("PrivacyPermission saved.");
		//			result = true;
		//		} catch (Exception e) {
		//			t.rollback();
		//			throw new PrivacyException("Error during the persistance of the privacy permission", e);
		//		} finally {
		//			if (session != null) {
		//				session.close();
		//			}
		//		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.privacytrust.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.schema.identity.RequestorBean, org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem)
	 */
	public boolean updatePermission(RequestorBean requestor, ResponseItem permission) throws PrivacyException {
		DataIdentifier dataId = null;
		try {
			dataId = DataIdentifierFactory.fromUri(permission.getRequestItem().getResource().getDataIdUri());
		}
		catch(MalformedCtxIdentifierException e) {
			throw new PrivacyException("Can't update the permission. DataIdentifier in the resource is badly formatted.", e);
		}
		return updatePermission(requestor, dataId, permission.getRequestItem().getActions(), permission.getDecision());
	}
	/*
	 * (non-Javadoc)
	 * @see org.societies.android.privacytrust.api.IPrivacyDataManagerInternal#updatePermission(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.identity.DataIdentifier, java.util.List, org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Decision)
	 */
	public boolean updatePermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions, Decision permission) throws PrivacyException {
		return updatePermission(requestor, dataId, actions, permission, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.android.privacytrust.api.IPrivacyDataManagerInternal#deletePermission(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.identity.DataIdentifier)
	 */
	public boolean deletePermission(RequestorBean requestor, DataIdentifier dataId) throws PrivacyException {
		boolean result = false;
		//		Session session = sessionFactory.openSession();
		//		Transaction t = session.beginTransaction();
		//		try {
		//			// -- Retrieve the privacy permission
		//			Criteria criteria = session
		//					.createCriteria(PrivacyPermission.class)
		//					.add(Restrictions.eq("requestorId", requestor.getRequestorBeanId().getJid()))
		//					.add(Restrictions.eq("ownerId", ownerId.getJid()))
		//					.add(Restrictions.eq("dataId", dataId.toUriString()));
		//			if (requestor instanceof RequestorBeanCis) {
		//				criteria.add(Restrictions.eq("cisId", ((RequestorBeanCis) requestor).getCisRequestorBeanId().getJid()));
		//			}
		//			else if (requestor instanceof RequestorBeanService) {
		//				criteria.add(Restrictions.eq("serviceId", ((RequestorBeanService) requestor).getRequestorBeanServiceId().getIdentifier().toString()));
		//			}
		//			PrivacyPermission privacyPermission = (PrivacyPermission) criteria.uniqueResult();
		//
		//			// -- Delete the privacy permission
		//			// - Privacy Permission doesn't exist
		//			if (null == privacyPermission) {
		//				LOG.debug("PrivacyPermission not available: no need to delete");
		//			}
		//			// - Privacy permission retrieved: delete it
		//			else {
		//				LOG.info(privacyPermission.toString());
		//				session.delete(privacyPermission);
		//				t.commit();
		//				LOG.debug("PrivacyPermission deleted.");
		//			}
		//			result = true;
		//		} catch (Exception e) {
		//			t.rollback();
		//			throw new PrivacyException("Error during the removal of the privacy permission", e);
		//		} finally {
		//			if (session != null) {
		//				session.close();
		//			}
		//		}
		return result;
	}
}
