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
package org.societies.context.userHistory.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
import org.societies.context.userHistory.impl.model.UserCtxHistoryAttributeDAO;
import org.societies.context.userHistory.impl.model.UserCtxHistoryDAOTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserContextHistoryManagement implements IUserCtxHistoryMgr {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserContextHistoryManagement.class);

	@Autowired
	private SessionFactory sessionFactory;

	boolean ctxRecording = true;
	
	public UserContextHistoryManagement() {
		
		LOG.info(this.getClass().getName() + " instantiated");
	}

	@Override
	public CtxHistoryAttribute createHistoryAttribute(
			final CtxAttribute attribute) {

		if (attribute == null)
			throw new NullPointerException("attribute can't be null");
		
		//if (ctxRecording == false)
		//	throw new UserCtxHistoryMgrException("context history recording is disabled");
		
		CtxHistoryAttribute result = null;
		
		UserCtxHistoryAttributeDAO dao = UserCtxHistoryDAOTranslator.getInstance()
				.fromCtxAttribute(attribute);
		
		final Session session = this.sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			final Serializable historyRecordId = session.save(dao);
			tx.commit();
			dao = (UserCtxHistoryAttributeDAO) session.load(
					UserCtxHistoryAttributeDAO.class, historyRecordId);
			result = UserCtxHistoryDAOTranslator.getInstance()
					.fromUserCtxHistoryAttributeDAO(dao);
			
		} catch (Exception e) {
			tx.rollback();
			throw new IllegalStateException("Could not create history for attribute "
					+ attribute + ": " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}

	// TODO throw UserCtxHistoryMgrException
	@Override
	public CtxHistoryAttribute createHistoryAttribute(
			final CtxAttributeIdentifier attrId, final Date date, 
			final Serializable value, final CtxAttributeValueType valueType) {
		
		// TODO null checks??
		
		CtxHistoryAttribute result = null;
		
		final String stringValue;
		final Integer integerValue;
		final Double doubleValue;
		final byte[] binaryValue;
		
		if (valueType.equals(CtxAttributeValueType.STRING)) {
			
			stringValue = (String) value;
			integerValue = null;
			doubleValue = null;
			binaryValue = null;
		} else if (valueType.equals(CtxAttributeValueType.INTEGER)) {
			
			stringValue = null;
			integerValue = (Integer) value;
			doubleValue = null;
			binaryValue = null;
		} else if (valueType.equals(CtxAttributeValueType.DOUBLE)) {
			
			stringValue = null;
			integerValue = null;
			doubleValue = (Double) value;
			binaryValue = null;
		} else if (valueType.equals(CtxAttributeValueType.BINARY)) {
			
			stringValue = null;
			integerValue = null;
			doubleValue = null;
			binaryValue = (byte[]) value;
		} else {
			
			stringValue = null;
			integerValue = null;
			doubleValue = null;
			binaryValue = null;
		}
		
		UserCtxHistoryAttributeDAO dao = UserCtxHistoryDAOTranslator.getInstance()
				.fromCtxAttributeProperties(attrId, date, date, stringValue, 
						integerValue, doubleValue, binaryValue, valueType, null);
		
		final Session session = this.sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			final Serializable historyRecordId = session.save(dao);
			tx.commit();
			dao = (UserCtxHistoryAttributeDAO) session.load(
					UserCtxHistoryAttributeDAO.class, historyRecordId);
			result = UserCtxHistoryDAOTranslator.getInstance()
					.fromUserCtxHistoryAttributeDAO(dao);
			
		} catch (Exception e) {
			tx.rollback();
			throw new IllegalStateException("Could not create history for attribute "
					+ attrId + ": " + e.getLocalizedMessage(), e);
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}

	// TODO deprecate
	@Override
	public void storeHoCAttribute(CtxAttribute ctxAttribute){

		this.createHistoryAttribute(ctxAttribute);	
	}


	public void storeHoCAttributeTuples(CtxAttribute ctxAttribute){
		//TODO
	}


	@Override
	public void disableCtxRecording() {
		
		ctxRecording =  false;

	}

	@Override
	public void enableCtxRecording() {

		ctxRecording =  true;

	}

	// TODO throws UserCtxHistoryException
	@Override
	public List<CtxHistoryAttribute> retrieveHistory(final CtxAttributeIdentifier attrId) {
		
		if (attrId == null)
			throw new NullPointerException("attrId can't be null");

		final List<CtxHistoryAttribute> results = new LinkedList<CtxHistoryAttribute>();
		try {
			final List<UserCtxHistoryAttributeDAO> historyDAOs = this.retrieve(attrId, null, null);
			for (final UserCtxHistoryAttributeDAO historyDAO : historyDAOs)
				results.add(UserCtxHistoryDAOTranslator.getInstance()
						.fromUserCtxHistoryAttributeDAO(historyDAO));
		} catch (Exception e) {
			// TODO throw new UserCtxHistoryMgrException
			throw new IllegalStateException("Could not retrieve history of context attribute "
					+ attrId + ": " + e.getLocalizedMessage(), e);
		}
		return results;
	}

	@Override
	public List<CtxHistoryAttribute> retrieveHistory(CtxAttributeIdentifier attrId,
			final Date startDate, final Date endDate) {
		
		final List<CtxHistoryAttribute> results = new LinkedList<CtxHistoryAttribute>();
		
		try {
			final List<UserCtxHistoryAttributeDAO> historyDAOs = this.retrieve(attrId, startDate, endDate);
			for (final UserCtxHistoryAttributeDAO historyDAO : historyDAOs)
				results.add(UserCtxHistoryDAOTranslator.getInstance()
						.fromUserCtxHistoryAttributeDAO(historyDAO));
		} catch (Exception e) {
			// TODO throw new UserCtxHistoryMgrException
			throw new IllegalStateException("Could not retrieve history of context attribute "
					+ attrId + ": " + e.getLocalizedMessage(), e);
		}
		return results;
	}

	@Override
	public int removeCtxHistory(CtxAttribute arg0, Date arg1, Date arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeHistory(String arg0, Date arg1, Date arg2)
			throws CtxException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<CtxHistoryAttribute> retrieveHistory(
			CtxAttributeIdentifier arg0, int arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	//*************************************
	//  Tuple management
	//*************************************

	@Override
	public List<CtxAttributeIdentifier> getCtxHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
					throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean removeCtxHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1,
			Date arg2, Date arg3) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setCtxHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxAttributeIdentifier> updateCtxHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
					throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	//******************** helper methods *****************

	@Override
	public void printHocDB(){

		try {
			final List<UserCtxHistoryAttributeDAO> daos = this.retrieve(null, null, null);
			System.out.println("key |      attr.getId                                                 |  valueSt     | Time ");
			for (final UserCtxHistoryAttributeDAO dao : daos) {

				final String valueSt =  dao.getStringValue();
				final Date date = (Date) dao.getLastUpdated();
				System.out.println(dao.getHistoryRecordId()+" | "+dao.getId()+" | "+valueSt+" | "+date.getTime());
			}
		} catch (Exception e) {
			
			throw new IllegalStateException("Could not print history of context: "
					+ e.getLocalizedMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<UserCtxHistoryAttributeDAO> retrieve(
			final CtxAttributeIdentifier ctxId,	final Date startDate,
			final Date endDate) throws Exception {
		
		final List<UserCtxHistoryAttributeDAO> result= new LinkedList<UserCtxHistoryAttributeDAO>();
		
		final Session session = sessionFactory.openSession();
		final Criteria criteria = session.createCriteria(UserCtxHistoryAttributeDAO.class);
		
		if (ctxId != null)
			criteria.add(Restrictions.eq("ctxId", ctxId));
		
		if (startDate != null) 
			criteria.add(Restrictions.ge("lastUpdated", startDate));
		
		if (endDate != null)
			criteria.add(Restrictions.le("lastUpdated", endDate));
		
		criteria.addOrder(Order.asc("lastUpdated"));
		
		try {
			result.addAll(criteria.list());
		} finally {
			if (session != null)
				session.close();
		}
			
		return result;
	}
}