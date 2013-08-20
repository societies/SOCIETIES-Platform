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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.context.api.user.db.IUserCtxDBMgr;
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

	@Autowired(required=false)
	private IUserCtxDBMgr userCtxDBMgr;

	private boolean ctxRecording = true;

	public UserContextHistoryManagement() {

		LOG.info(this.getClass().getName() + " instantiated");
	}

	@Override
	public CtxHistoryAttribute createHistoryAttribute(
			final CtxAttribute attribute) throws CtxException{

		if (attribute == null)
			throw new NullPointerException("attribute can't be null");

		if (ctxRecording == false)
			throw new UserCtxHistoryMgrException("context history recording is disabled");

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

	@Override
	public CtxHistoryAttribute createHistoryAttribute (
			final CtxAttributeIdentifier attrId, final Date date, 
			final Serializable value, final CtxAttributeValueType valueType) throws CtxException{

		if (attrId == null)
			throw new NullPointerException("attrId can't be null");

		if (date == null)
			throw new NullPointerException("date can't be null");

		if (value == null)
			throw new NullPointerException("value can't be null");

		if (valueType == null)
			throw new NullPointerException("valueType can't be null");

		if (ctxRecording == false)
			throw new UserCtxHistoryMgrException("context history recording is disabled");

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



	@Override
	public void storeHoCAttribute(CtxAttribute ctxAttribute) throws CtxException{

		try {
			this.createHistoryAttribute(ctxAttribute);

			List<CtxAttributeIdentifier> escList = new ArrayList<CtxAttributeIdentifier>();

			List<CtxAttributeIdentifier> hocTuplesList = this.getCtxHistoryTuples(ctxAttribute.getId(),escList);
			//System.out.println("hocTuplesList size:"+hocTuplesList.size());
			if( hocTuplesList != null && hocTuplesList.size()>0 ) this.storeHoCAttributeTuples(ctxAttribute);

		} catch (CtxException e) {

			throw new UserCtxHistoryMgrException("context attribute not stored in context DB"
					+ ctxAttribute.getId() + ": " + e.getLocalizedMessage(), e);
		}	
	}



	public void storeHoCAttributeTuples(CtxAttribute ctxAttribute){

		//String tupleAttrType = "tuple_"+primaryAttr.getType().toString();
		if (LOG.isDebugEnabled())
			LOG.debug("storing hoc tuples for " +ctxAttribute.getId());

		String tupleAttrType = "tuple_"+ctxAttribute.getId().getType().toString()+"_"+ctxAttribute.getId().getObjectNumber().toString();
		if (LOG.isDebugEnabled())
			LOG.debug("store: tuple attr type "+ tupleAttrType);
		// the attr that will maintain the tuples; 
		CtxAttribute tupleAttr = null;
		List<CtxHistoryAttribute> tupleValueList = new ArrayList<CtxHistoryAttribute>();
		try {

			List<CtxAttributeIdentifier> tempEscListIds = new ArrayList<CtxAttributeIdentifier>();
			List<CtxAttributeIdentifier> tupleListIds = this.getCtxHistoryTuples(ctxAttribute.getId(),tempEscListIds);

			final Set<String> types = new HashSet<String>();
			types.add(tupleAttrType);
			List<CtxIdentifier> tupleAttrIDsList = new ArrayList<CtxIdentifier>(); 
			tupleAttrIDsList.addAll(this.userCtxDBMgr.lookup(
					ctxAttribute.getOwnerId(), CtxModelType.ATTRIBUTE, types));
			if(tupleAttrIDsList.size() > 0){
				if (LOG.isDebugEnabled())
					LOG.debug("retrieved: "+ tupleAttrType);
				//tuple_status retrieved

				CtxAttributeIdentifier ctxAttrId = (CtxAttributeIdentifier) tupleAttrIDsList.get(0);

				tupleAttr = (CtxAttribute) this.userCtxDBMgr.retrieve(ctxAttrId);
			} else {
				if (LOG.isDebugEnabled())
					LOG.debug("created: "+ tupleAttrType);
				//tuple_status created, dead code, the attribute is created by setHocTuples
				tupleAttr = this.userCtxDBMgr.createAttribute(ctxAttribute.getScope(), tupleAttrType);
			} 

			//prepare value of ctxAttribute
			for (CtxAttributeIdentifier tupleAttrID : tupleListIds) {
				//for one of the escorting attrIds retrieve all history and find the latest value
				if (LOG.isDebugEnabled())
					LOG.debug("Retrieving history for escorting attribute " + tupleAttrID);
				
				List<CtxHistoryAttribute> allValues = this.retrieveHistory(tupleAttrID, null, null);
				//if (LOG.isDebugEnabled())
					//LOG.debug("Retrieved history " + allValues);
				if (allValues != null){
					//finding latest hoc value
					int size = allValues.size();
					//if (LOG.isDebugEnabled())
						//LOG.debug("Retrieved history size " + size);
					int last = 0;
					if (size >= 1){
						last = size-1;
						//if (LOG.isDebugEnabled())
						//	LOG.debug("Retrieved history last " + last);
						CtxHistoryAttribute latestHoCAttr2 = allValues.get(last);
						if (latestHoCAttr2 != null )tupleValueList.add(latestHoCAttr2);
					}
				}           

			}
			byte[] tupleValueListBlob = SerialisationHelper.serialise((Serializable) tupleValueList);
			if(tupleAttr != null) tupleAttr.setBinaryValue(tupleValueListBlob);

			//LOG.info("ready to store tupleAttr: "+tupleAttr);

			CtxHistoryAttribute hocAttr = this.createHistoryAttribute(tupleAttr);


		} catch (Exception e) {
			LOG.error("Exception while storing tuples for ctxAttribute id:"+ctxAttribute.getId()+" ."+e.getLocalizedMessage());
			e.printStackTrace();
		}		

	}


	@Override
	public void disableCtxRecording() {

		ctxRecording =  false;

	}

	@Override
	public void enableCtxRecording() {

		ctxRecording =  true;

	}


	@Override
	public List<CtxHistoryAttribute> retrieveHistory(final CtxAttributeIdentifier attrId) throws CtxException{

		if (attrId == null)
			throw new UserCtxHistoryMgrException("attrId can't be null");

		final List<CtxHistoryAttribute> results = new LinkedList<CtxHistoryAttribute>();
		try {
			final List<UserCtxHistoryAttributeDAO> historyDAOs = this.retrieve(attrId, null, null);
			for (final UserCtxHistoryAttributeDAO historyDAO : historyDAOs)
				results.add(UserCtxHistoryDAOTranslator.getInstance()
						.fromUserCtxHistoryAttributeDAO(historyDAO));
		} catch (Exception e) {

			throw new UserCtxHistoryMgrException("Could not retrieve history of context attribute "
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
			
			throw new IllegalStateException("Could not retrieve history of context attribute "
					+ attrId + ": " + e.getLocalizedMessage(), e);
		}
		return results;
	}

	@Override
	public int removeCtxHistory(CtxAttribute arg0, Date arg1, Date arg2)
			throws CtxException {

		return 0;
	}

	@Override
	public int removeHistory(String arg0, Date arg1, Date arg2)
			throws CtxException {

		return 0;
	}

	@Override
	public List<CtxHistoryAttribute> retrieveHistory(
			CtxAttributeIdentifier arg0, int arg1) throws CtxException {

		return null;
	}


	//*************************************
	//  Tuple management
	//*************************************

	@Override
	public List<CtxAttributeIdentifier> getCtxHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> arg1)
					throws CtxException {

		if(this.userCtxDBMgr == null) return null;
		
		List<CtxAttributeIdentifier> tupleAttrIDs = new ArrayList<CtxAttributeIdentifier>(); 

		final String tupleAttrType = "tuple_"+primaryAttrIdentifier.getType().toString()+"_"+primaryAttrIdentifier.getObjectNumber().toString();

		List<CtxIdentifier> ls = new ArrayList<CtxIdentifier>();
		try {
			final Set<String> types = new HashSet<String>();
			types.add(tupleAttrType);
			ls.addAll(this.userCtxDBMgr.lookup(primaryAttrIdentifier.getOwnerId(), 
					CtxModelType.ATTRIBUTE, types));
			if (ls.size() > 0) {
				CtxIdentifier id = ls.get(0);
				final CtxAttribute tupleIdsAttribute = (CtxAttribute) this.userCtxDBMgr.retrieve(id);

				//deserialise object
				tupleAttrIDs = (List<CtxAttributeIdentifier>) SerialisationHelper.deserialise(tupleIdsAttribute.getBinaryValue(), this.getClass().getClassLoader());
			}

		} catch (Exception e) {
			LOG.error("Exception while getting ctx history tuples for id:"+primaryAttrIdentifier+". "+e.getLocalizedMessage());
			e.printStackTrace();
		}

		return tupleAttrIDs;
	}

	@Override
	public Boolean removeCtxHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		
		return null;
	}

	@Override
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1,
			Date arg2, Date arg3) throws CtxException {

		return null;
	}

	@Override
	public Boolean setCtxHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {

		boolean result = false;

		try {
			// set hoc recording flag for the attributes contained in tuple list
			final List<CtxAttributeIdentifier> allAttrIds = new ArrayList<CtxAttributeIdentifier>();
			// add the primary attr id
			allAttrIds.add(0,primaryAttrIdentifier);
			// add the escorting attr ids
			allAttrIds.addAll(listOfEscortingAttributeIds);

			
			// set history flag for all escorting attributes
						
			for (CtxAttributeIdentifier escortingAttrID : allAttrIds) {
				
				if (escortingAttrID != null ){
					CtxAttribute attr = (CtxAttribute) this.userCtxDBMgr.retrieve(escortingAttrID);
					if(attr != null){
						attr.setHistoryRecorded(true);
						this.userCtxDBMgr.update(attr);	
						this.storeHoCAttribute(attr);
					}	
				}
				
			}

			//this attr will maintain the attr ids of all the (not only the escorting) hoc_attibutes in a blob
			final String tupleAttrType = "tuple_"+primaryAttrIdentifier.getType().toString()+"_"+primaryAttrIdentifier.getObjectNumber().toString();
			final CtxAttribute tupleAttr = (CtxAttribute) this.userCtxDBMgr.createAttribute(primaryAttrIdentifier.getScope(), tupleAttrType);

			byte[] attrIdsBlob = SerialisationHelper.serialise((Serializable) allAttrIds);
			tupleAttr.setBinaryValue(attrIdsBlob);
			CtxAttribute updatedTupleAttr = (CtxAttribute) this.userCtxDBMgr.update(tupleAttr);

			if(updatedTupleAttr != null && updatedTupleAttr.getType().contains("tuple_")) result = true;

			//LOG.info("tuple Attr ids "+allAttrIds);

		} catch (IOException e) {
			LOG.error("Exception while setting ctx history tuples for id:"+primaryAttrIdentifier+". "+e.getLocalizedMessage());
			
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<CtxAttributeIdentifier> updateCtxHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
					throws CtxException {
		
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
		try {
			final Criteria criteria = session.createCriteria(UserCtxHistoryAttributeDAO.class);

			if (ctxId != null)
				criteria.add(Restrictions.eq("ctxId", ctxId));

			if (startDate != null) 
				criteria.add(Restrictions.ge("lastUpdated", startDate));

			if (endDate != null)
				criteria.add(Restrictions.le("lastUpdated", endDate));

			criteria.addOrder(Order.asc("lastUpdated"));


			result.addAll(criteria.list());
		} finally {
			if (session != null)
				session.close();
		}

		return result;
	}

}