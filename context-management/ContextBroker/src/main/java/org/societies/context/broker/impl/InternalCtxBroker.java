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
package org.societies.context.broker.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.history.IUserCtxHistoryMgr;
import org.societies.context.broker.api.CtxBrokerException;
//import org.societies.context.user.db.impl.UserCtxDBMgr;
//import org.societies.context.userHistory.impl.UserContextHistoryManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 * Internal Context Broker Implementation
 * This class implements the internal context broker interfaces and orchestrates the db 
 * management 
 */
@Service
public class InternalCtxBroker implements ICtxBroker {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(InternalCtxBroker.class);

	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;

	/**
	 * The User Context DB Mgmt service reference.
	 * 
	 * @see {@link #setUserCtxDBMgr(IUserCtxDBMgr)}
	 */

	private IUserCtxDBMgr userCtxDBMgr = null;

	/**
	 * The User Context History Mgmt service reference. 
	 * 
	 * @see {@link #setUserCtxHistoryMgr(IUserCtxHistoryMgr)}
	 */
	@Autowired(required=true)
	private IUserCtxHistoryMgr userCtxHistoryMgr = null;


	public InternalCtxBroker(){
		//remove after testing
		//setUserCtxDBMgr(new UserCtxDBMgr());
		//setUserCtxHistoryMgr(new UserContextHistoryManagement());
	}


	public void createCSSOperator(){
		try {
			userCtxDBMgr.createIndividualCtxEntity(CtxEntityTypes.PERSON);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createAssociation(java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAssociation> createAssociation(String type) throws CtxException {

		CtxAssociation association = userCtxDBMgr.createAssociation(type);

		if (association!=null)
			return new AsyncResult<CtxAssociation>(association);
		else 
			return new AsyncResult<CtxAssociation>(null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAttribute> createAttribute(CtxEntityIdentifier scope,
			String type) throws CtxException {

		// TODO IUserCtxDBMgr should provide createAttribute(CtxEntityIdentifier scope, String type) 
		final CtxAttribute attribute = 
				this.userCtxDBMgr.createAttribute(scope, null, type);

		return new AsyncResult<CtxAttribute>(attribute);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#createEntity(java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxEntity> createEntity(String type) throws CtxException {

		final CtxEntity entity = 
				this.userCtxDBMgr.createEntity(type);

		return new AsyncResult<CtxEntity>(entity);
	}


	@Override
	public Future<IndividualCtxEntity> createIndividualEntity(String type)
			throws CtxException {

		IndividualCtxEntity individualCtxEnt = this.userCtxDBMgr.createIndividualCtxEntity(type);
		return new AsyncResult<IndividualCtxEntity>(individualCtxEnt);
	}



	@Override
	public void disableCtxMonitoring(CtxAttributeValueType type) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCtxMonitoring(CtxAttributeValueType type) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#lookupEntities(java.lang.String, java.lang.String, java.io.Serializable, java.io.Serializable)
	 */
	@Override
	@Async
	public Future<List<CtxEntityIdentifier>> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

		final List<CtxEntityIdentifier> results = new ArrayList<CtxEntityIdentifier>(); 

		results.addAll(
				this.userCtxDBMgr.lookupEntities(entityType, attribType, minAttribValue, maxAttribValue));

		return new AsyncResult<List<CtxEntityIdentifier>>(results);
	}


	@Override
	public Future<CtxModelObject> remove(CtxIdentifier identifier) throws CtxException {

		final CtxModelObject modelObj = this.userCtxDBMgr.remove(identifier);

		return new AsyncResult<CtxModelObject>(modelObj) ;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieve(org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	@Async
	public Future<CtxModelObject> retrieve(CtxIdentifier identifier) throws CtxException {

		final CtxModelObject modelObj = this.userCtxDBMgr.retrieve(identifier);

		return new AsyncResult<CtxModelObject>(modelObj);
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#retrieveCssOperator()
	 */
	@Override
	@Async
	public Future<IndividualCtxEntity> retrieveCssOperator()
			throws CtxException {

		IndividualCtxEntity operatorCss = null; // TODO this.userCtxDBMgr.retrieveOperatorCss();
		List<CtxIdentifier> entIds = this.userCtxDBMgr.lookup(CtxModelType.ENTITY, CtxEntityTypes.PERSON);
		for (CtxIdentifier entId : entIds) {
			if (entId.getObjectNumber() == 0) operatorCss = (IndividualCtxEntity) this.userCtxDBMgr.retrieve(entId);
		}
		return new AsyncResult<IndividualCtxEntity>(operatorCss);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#update(org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	@Async
	public Future<CtxModelObject> update(CtxModelObject identifier) throws CtxException {

		final CtxModelObject modelObject = this.userCtxDBMgr.update(identifier);

		// this part allows the storage of attribute updates to context history
		if (CtxModelType.ATTRIBUTE.equals(modelObject.getModelType())) {
			final CtxAttribute ctxAttr = (CtxAttribute) modelObject;
			if (ctxAttr.isHistoryRecorded() && this.userCtxHistoryMgr != null)
				this.userCtxHistoryMgr.storeHoCAttribute(ctxAttr);

			// check if ctxAttr is also registered to be stored in tuples			
			try {
				// the list of escorting atts is empty
				List<CtxAttributeIdentifier> escList = new ArrayList<CtxAttributeIdentifier>();
				List<CtxAttributeIdentifier> hocTuplesList = this.getHistoryTuples(ctxAttr.getId(),escList).get();
				if( hocTuplesList.size()>0 ) this.storeHoCAttributeTuples(ctxAttr);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return new AsyncResult<CtxModelObject>(modelObject);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#updateAttribute(org.societies.api.context.model.CtxAttributeIdentifier, java.io.Serializable)
	 */
	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value) throws CtxException {

		// Implies <code>null</code> valueMetric param
		return this.updateAttribute(attributeId, value, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#updateAttribute(org.societies.api.context.model.CtxAttributeIdentifier, java.io.Serializable, java.lang.String)
	 */
	@Override
	@Async
	public Future<CtxAttribute> updateAttribute(
			CtxAttributeIdentifier attributeId, Serializable value,
			String valueMetric) throws CtxException {

		if (attributeId == null)
			throw new NullPointerException("attributeId can't be null");
		// Will throw IllegalArgumentException if value type is not supported
		final CtxAttributeValueType valueType = this.findAttributeValueType(value);

		Future<CtxModelObject> futureModelObj = this.retrieve(attributeId);
		CtxAttribute attribute;
		try {
			attribute = (CtxAttribute) futureModelObj.get();

			if (attribute == null) {
				// Requested attribute not found
				return new AsyncResult<CtxAttribute>(null);
			} else {
				if (CtxAttributeValueType.EMPTY.equals(valueType))
					attribute.setStringValue(null);
				else if (CtxAttributeValueType.STRING.equals(valueType))
					attribute.setStringValue((String) value);
				else if (CtxAttributeValueType.INTEGER.equals(valueType))
					attribute.setIntegerValue((Integer) value);
				else if (CtxAttributeValueType.DOUBLE.equals(valueType))
					attribute.setDoubleValue((Double) value);
				else if (CtxAttributeValueType.BINARY.equals(valueType))
					attribute.setBinaryValue((byte[]) value);

				attribute.setValueType(valueType);
				futureModelObj = this.update(attribute);
				attribute = (CtxAttribute) futureModelObj.get();
				return new AsyncResult<CtxAttribute>(attribute);
			}
		} catch (InterruptedException ie) {
			throw new CtxBrokerException("Could not update attribute " 
					+ attributeId + ": " + ie.getMessage(), ie);
		} catch (ExecutionException ee) {
			throw new CtxBrokerException("Could not update attribute " 
					+ attributeId + ": " + ee.getMessage(), ee);
		}
	}

	@Override
	public Future<List<CtxIdentifier>> lookup(CtxModelType modelType,
			String type) throws CtxException {

		final List<CtxIdentifier> results = this.userCtxDBMgr.lookup(modelType, type);

		return new AsyncResult<List<CtxIdentifier>>(results) ;

	}

	//***********************************************
	//     Context Update Events Methods  
	//***********************************************
	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterForUpdates(CtxEntityIdentifier scope,
			String attributeType) throws CtxException {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerForUpdates(CtxEntityIdentifier scope,
			String attrType) throws CtxException {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerForUpdates(CtxAttributeIdentifier attrId) throws CtxException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final CtxIdentifier ctxId) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		final String[] topics = new String[] {
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Registering context change event listener for object '"
						+ ctxId + "' to topics '" + Arrays.toString(topics) + "'");
			this.ctxEventMgr.registerChangeListener(listener, topics, ctxId);
		} else {
			throw new CtxBrokerException("Could not register context change event listener for object '"
					+ ctxId + "' to topics '" + Arrays.toString(topics)
					+ "': ICtxEventMgr service is not available");
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final CtxIdentifier ctxId) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (ctxId == null)
			throw new NullPointerException("ctxId can't be null");

		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#registerForChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerForChanges(final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, final String attrType) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");

		final String[] topics = new String[] {
				CtxChangeEventTopic.UPDATED,
				CtxChangeEventTopic.MODIFIED,
				CtxChangeEventTopic.REMOVED,
		};
		if (this.ctxEventMgr != null) {
			if (LOG.isInfoEnabled())
				LOG.info("Registering context change event listener for attributes with scope '"
						+ scope + "' and type '" + attrType + "' to topics '" 
						+ Arrays.toString(topics) + "'");
			this.ctxEventMgr.registerChangeListener(listener, topics, scope, attrType );
		} else {
			throw new CtxBrokerException("Could not register context change event listener for attributes with scope '"
					+ scope + "' and type '" + attrType + "' to topics '" + Arrays.toString(topics)
					+ "': ICtxEventMgr service is not available");
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.context.broker.ICtxBroker#unregisterFromChanges(org.societies.api.context.event.CtxChangeEventListener, org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterFromChanges(final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, final String attrType) throws CtxException {

		if (listener == null)
			throw new NullPointerException("listener can't be null");
		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (attrType == null)
			throw new NullPointerException("attrType can't be null");

		// TODO Auto-generated method stub
	}


	//***********************************************
	//     Context Inference Methods  
	//***********************************************

	@Override
	public Future<List<Object>> evaluateSimilarity(
			Serializable objectUnderComparison,
			List<Serializable> referenceObjects) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, Date date) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxAttribute>> retrieveFuture(
			CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	//***********************************************
	//     Community Context Management Methods  
	//***********************************************
	@Override
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<IndividualCtxEntity> retrieveAdministratingCSS(
			CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<Set<CtxBond>> retrieveBonds(CtxEntityIdentifier community) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}


	//***********************************************
	//     Context History Management Methods  
	//***********************************************


	@Override
	@Async
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException {

		final List<CtxHistoryAttribute> result = new ArrayList<CtxHistoryAttribute>();

		result.addAll(this.userCtxHistoryMgr.retrieveHistory(attrId, startDate, endDate));

		return new AsyncResult<List<CtxHistoryAttribute>>(result);
	}

	@Override
	public Future<Integer> removeHistory(String type, Date startDate, Date endDate) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<List<CtxHistoryAttribute>> retrieveHistory(
			CtxAttributeIdentifier arg0, int arg1) throws CtxException {
		printHocDB();
		return null;
	}


	@Override
	public Future<List<CtxAttributeIdentifier>> getHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> arg1)
					throws CtxException {

		List<CtxAttributeIdentifier> tupleAttrIDs = new ArrayList<CtxAttributeIdentifier>(); 

		final String tupleAttrType = "tupleIds_" + primaryAttrIdentifier.getType();
		List<CtxIdentifier> ls;
		try {
			ls = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			if (ls.size() > 0) {
				CtxIdentifier id = ls.get(0);
				final CtxAttribute tupleIdsAttribute = (CtxAttribute) this.retrieve(id).get();

				//deserialise object
				tupleAttrIDs = (List<CtxAttributeIdentifier>) SerialisationHelper.deserialise(tupleIdsAttribute.getBinaryValue(), this.getClass().getClassLoader());
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CtxAttributeIdentifier>>(tupleAttrIDs);
	}

	@Override
	public Future<Boolean> removeHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.broker.ICtxBroker#setHistoryTuples(org.societies.api.context.model.CtxAttributeIdentifier, java.util.List)
	 */
	@Override
	@Async
	public Future<Boolean> setHistoryTuples(CtxAttributeIdentifier primaryAttrIdentifier,
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds) throws CtxException {

		boolean result = false;

		LOG.info("setting history tuples primaryAttrIdentifier: "+primaryAttrIdentifier );

		try {
			// set hoc recording flag for the attributes contained in tuple list
			final List<CtxAttributeIdentifier> allAttrIds = new ArrayList<CtxAttributeIdentifier>();
			// add the primary attr id
			allAttrIds.add(0,primaryAttrIdentifier);
			// add the escorting attr ids
			allAttrIds.addAll(listOfEscortingAttributeIds);

			for (CtxAttributeIdentifier escortingAttrID : allAttrIds) {

				// set history flag for all escorting attributes
				Future<CtxModelObject> attrFuture = this.retrieve(escortingAttrID);
				CtxAttribute attr = (CtxAttribute) attrFuture.get();
				attr.setHistoryRecorded(true);
				this.update(attr);
			}
			// set hoc recording flag for the attributes contained in tuple list --end

			//this attr will maintain the attr ids of all the (not only the escorting) hoc_attibutes in a blob
			final String tupleAttrType = "tupleIds_" + primaryAttrIdentifier.getType();
			final CtxAttribute tupleAttr = (CtxAttribute) this.createAttribute(primaryAttrIdentifier.getScope(), tupleAttrType).get();

			byte[] attrIdsBlob = SerialisationHelper.serialise((Serializable) allAttrIds);
			tupleAttr.setBinaryValue(attrIdsBlob);
			CtxModelObject updatedAttr = (CtxAttribute) this.update(tupleAttr).get();

			if(updatedAttr != null && updatedAttr.getType().contains("tupleIds_")) result = true;

			LOG.info("tuple Attr ids "+allAttrIds);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return new AsyncResult<Boolean>(result);
	}

	@Override
	public Future<List<CtxAttributeIdentifier>> updateHistoryTuples(
			CtxAttributeIdentifier primaryAttrIdentifier, List<CtxAttributeIdentifier> newAttrList)
					throws CtxException {

		List<CtxAttributeIdentifier> results = new ArrayList<CtxAttributeIdentifier>();
		try {
			for (CtxAttributeIdentifier escortingAttrID : newAttrList) {
				// set history flag for all escorting attributes
				Future<CtxModelObject> attrFuture = this.retrieve(escortingAttrID);
				CtxAttribute attr = (CtxAttribute) attrFuture.get();
				attr.setHistoryRecorded(true);
				this.update(attr);
			}
			//			List<CtxAttributeIdentifier> oldTupleAttrIDs = new ArrayList<CtxAttributeIdentifier>();
			final String tupleAttrType = "tupleIds_" + primaryAttrIdentifier.getType();
			List<CtxAttributeIdentifier> newTupleAttrIDs = new ArrayList<CtxAttributeIdentifier>(); 
			newTupleAttrIDs.add(0,primaryAttrIdentifier);
			newTupleAttrIDs.addAll(newAttrList);

			List<CtxIdentifier> ls;			
			ls = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			if (ls.size() > 0) {
				CtxIdentifier id = ls.get(0);
				final CtxAttribute tupleIdsAttribute = (CtxAttribute) this.retrieve(id).get();
				//deserialise object
				byte[] attrIdsBlob = SerialisationHelper.serialise((Serializable) newTupleAttrIDs);
				tupleIdsAttribute.setBinaryValue(attrIdsBlob);
				CtxAttribute updatedAttr = (CtxAttribute) this.update(tupleIdsAttribute).get();
				// set hoc recording flag for the attributes contained in tuple list --end
				if(updatedAttr != null && updatedAttr.getType().contains("tupleIds_")) {
					results = (List<CtxAttributeIdentifier>) SerialisationHelper.deserialise(updatedAttr.getBinaryValue(), this.getClass().getClassLoader());
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<List<CtxAttributeIdentifier>>(results);
	}

	@Override
	public Future<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>> retrieveHistoryTuples(
			CtxAttributeIdentifier primaryAttrId, List<CtxAttributeIdentifier> escortingAttrIds,
			Date arg2, Date arg3) throws CtxException {

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		LOG.info("retrieveHistoryTuples updating hocAttrs primaryAttr: "+primaryAttrId);

		if(primaryAttrId!= null){

			String tupleAttrType = "tuple_"+primaryAttrId.getType().toString();
			List<CtxIdentifier> listIds;

			try {
				listIds = this.lookup(CtxModelType.ATTRIBUTE,tupleAttrType).get();
				CtxAttributeIdentifier tupleAttrTypeID = (CtxAttributeIdentifier) listIds.get(0);

				// retrieve historic attrs of type "tuple_action"
				// each hoc attr contains a value (blob) list of historic attrs store together
				List<CtxHistoryAttribute> hocResults = retrieveHistory(tupleAttrTypeID,null,null).get();            

				// for each "tuple_status" hoc attr 
				for (CtxHistoryAttribute hocAttr : hocResults) {

					// get the list of hoc attrs stored as BlobValue
					List<CtxHistoryAttribute> tupleValueList = (List<CtxHistoryAttribute>) SerialisationHelper.deserialise(hocAttr.getBinaryValue(), this.getClass().getClassLoader());

					// list of historic attributes contained in "tuple_status" retrieved
					LOG.info("retrieveHistoryTuples tupleValueList: "+tupleValueList);
					//int ia = 0;
					//for each historic attr 
					for (CtxHistoryAttribute tupledHoCAttrTemp : tupleValueList){
						//the key , primary historic attribute
						CtxHistoryAttribute keyAttr = null;
						//the escorting historic attributes
						List<CtxHistoryAttribute> listEscHocAttrs = new ArrayList<CtxHistoryAttribute>();
						//for each historic attr in blob value check if the identifier equals the primary identifier
						if (tupledHoCAttrTemp.getId().toString().equals(primaryAttrId.toString())){
							//	ia++;
							keyAttr = tupledHoCAttrTemp;
							for (CtxHistoryAttribute tupledHoCAttrEscorting : tupleValueList){
								if (!(tupledHoCAttrEscorting.getId().toString().equals(primaryAttrId.toString()))){
									listEscHocAttrs.add(tupledHoCAttrEscorting);
								}  
							}
							results.put(keyAttr, listEscHocAttrs);    
						}
					}// end of for loop
				}	


			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(results == null){
			results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		}


		LOG.info("retrieveHistoryTuples results: "+results);

		return new AsyncResult<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>>(results);
	}


	public void storeHoCAttributeTuples(CtxAttribute primaryAttr){

		String tupleAttrType = "tuple_"+primaryAttr.getType().toString();

		// the attr that will maintain the tuples; 
		CtxAttribute tupleAttr = null;
		List<CtxHistoryAttribute> tupleValueList = new ArrayList<CtxHistoryAttribute>();
		try {
			List<CtxAttributeIdentifier> tempEscListIds = new ArrayList<CtxAttributeIdentifier>();
			List<CtxAttributeIdentifier> tupleListIds = this.getHistoryTuples(primaryAttr.getId(),tempEscListIds).get();

			List<CtxIdentifier> tupleAttrIDsList = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
			if(tupleAttrIDsList.size() != 0){
				//tuple_status retrieved
				tupleAttr = (CtxAttribute) this.retrieve(tupleAttrIDsList.get(0)).get();
			}
			if(tupleAttrIDsList.size() == 0){
				//tuple_status created
				tupleAttr = this.createAttribute(primaryAttr.getScope(), tupleAttrType).get();
			} 

			//prepare value of ctxAttribute
			for (CtxAttributeIdentifier tupleAttrID : tupleListIds) {
				//for one of the escorting attrIds retrieve all history and find the latest value
				List<CtxHistoryAttribute> allValues = this.retrieveHistory(tupleAttrID, null, null).get();
				if (allValues != null){
					//finding latest hoc value
					int size = allValues.size();
					int last = 0;
					if (size >= 1){
						last = size-1;    
						CtxHistoryAttribute latestHoCAttr2 = allValues.get(last);
						if (latestHoCAttr2 != null )tupleValueList.add(latestHoCAttr2);
					}
				}           
			}

			byte[] tupleValueListBlob = SerialisationHelper.serialise((Serializable) tupleValueList);
			if(tupleAttr != null) tupleAttr.setBinaryValue(tupleValueListBlob);

			CtxHistoryAttribute hocAttr = this.userCtxHistoryMgr.createHistoryAttribute(tupleAttr);

			LOG.info("storeHoCAttributeTuples updating hocAttrs: "+hocAttr);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}


	/*
	 * 


	public void createHistoryAttributeTuples(CtxAttribute primaryAttr, Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> hocTuples){

		String tupleAttrType = "tuple_"+primaryAttr.getType();
		CtxAttribute tupleAttr = null;

		List<CtxIdentifier> tupleAttrIDsList = this.lookup(CtxModelType.ATTRIBUTE, tupleAttrType).get();
		if(tupleAttrIDsList.size() != 0){
			//tuple_status retrieved
			tupleAttr = (CtxAttribute) this.retrieve(tupleAttrIDsList.get(0)).get();
		}
		if(tupleAttrIDsList.size() == 0){
			//tuple_status created
			tupleAttr = this.createAttribute(primaryAttr.getScope(), tupleAttrType).get();
		}


	}
	 */


	@Override
	public Future<CtxHistoryAttribute> createHistoryAttribute(CtxAttributeIdentifier attID, Date date, Serializable value, CtxAttributeValueType valueType){

		CtxHistoryAttribute hocAttr = this.userCtxHistoryMgr.createHistoryAttribute(attID,date,value,valueType);
		return new AsyncResult<CtxHistoryAttribute>(hocAttr);
	}




	void printHocDB(){
		this.userCtxHistoryMgr.printHocDB();
	}

	/*
	 * HoC tuples will be stored in an attribute of type "tuple_attibuteType" (tuple_status)
	 * the value will contain a list of ICtxHistoricAttribute 
	 * 
	 * tupleAttrIDs  the list of escorting attributes (also contains primary attribute id)
	 * ctxHocAttr  primary attribute to be stored 
	 */

	/*
	private void storeHoCTuples(ICtxHistoricAttribute ctxHocAttr ,List<ICtxAttributeIdentifier> tupleAttrIDs, IDigitalPersonalIdentifier dpi){

        String tupleAttrType = "tuple_"+ctxHocAttr.getType().toString();
        ICtxAttribute tupleAttr = null;
        ICtxIdentifier id;
        List<ICtxHistoricAttribute> tupleValueList = new ArrayList<ICtxHistoricAttribute>();
        // the attribute ids to be stored in same tuple.
        ICtxIdentifier tuple_type_attr_id;
        try {
            ICtxEntity operator = ctxDBManager.retrieveOperator();
            tuple_type_attr_id = this.identManager.getMappedCtxIdentifier(dpi, tupleAttrType);
            log.info(" storing tuples tuple_type_attr_id  :"+tuple_type_attr_id);
            if (tuple_type_attr_id != null) {
                // tuple_type_attr_id.setOperatorId(dpi.toUriString());
                tupleAttr = (ICtxAttribute) ctxDBManager.retrieve(tuple_type_attr_id); 
                tupleAttr.getCtxIdentifier().setOperatorId(dpi.toUriString());
            } 
            //create a new tuple_type attribute 
            if (tuple_type_attr_id == null) {
                tupleAttr = ctxDBManager.createAttribute(operator.getCtxIdentifier(), tupleAttrType);
                tuple_type_attr_id = this.identManager.addMappedCtxIdentifier(dpi, tupleAttr.getCtxIdentifier());
            }
            //            log.info("ATTRIBUTE exists and RETRIEVED ***** "+tupleAttr);

            for (ICtxAttributeIdentifier tupleAttrID : tupleAttrIDs) {

                //for one of the escorting attrIds retrieve all history and find the latest value
                List<ICtxHistoricAttribute> allValues = ctxHistoryDBManager.retrieveHistory(tupleAttrID);

                if (allValues != null){
                    //finding latest hoc value
                    int size = allValues.size();
                    int last = 0;
                    if (size >= 1){
                        last = size-1;    
                        ICtxHistoricAttribute latestHoCAttr2 = allValues.get(last);
                        if (latestHoCAttr2 != null )tupleValueList.add(latestHoCAttr2);
                    }
                }           
            }
            tupleAttr.setBlobValue((Serializable) tupleValueList);
            tupleAttr.getCtxIdentifier().setOperatorId(dpi.toUriString());
            ICtxHistoricAttribute hocAttr = ctxHistoryDBManager.createHistoricAttribute(tupleAttr);

            ctxHistoryDBManager.storeHistoricAttribute(hocAttr);
        }  catch (ContextDBException e) {
            this.log.error("Exception "+ e.getLocalizedMessage());
            //e.printStackTrace();
        } catch (ContextModelException e) {
            this.log.error("Exception "+ e);
            // e.printStackTrace();
        }
    }
	 */


	//********************************************************************
	//**************** end of hoc code  **********************************


	/**
	 * Sets the User Context DB Mgmt service reference.
	 * 
	 * @param userDB
	 *            the User Context DB Mgmt service reference to set.
	 */
	@Autowired(required=true)
	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {
		this.userCtxDBMgr = userDB;
		createCSSOperator();
	}

	/**
	 * Sets the User Context History Mgmt service reference.
	 * 
	 * @param userCtxHistoryMgr
	 *            the User Context History Mgmt service reference to set
	 */
	public void setUserCtxHistoryMgr(IUserCtxHistoryMgr userCtxHistoryMgr) {
		this.userCtxHistoryMgr = userCtxHistoryMgr;
	}

	private CtxAttributeValueType findAttributeValueType(Serializable value) {
		if (value == null)
			return CtxAttributeValueType.EMPTY;
		else if (value instanceof String)
			return CtxAttributeValueType.STRING;
		else if (value instanceof Integer)
			return CtxAttributeValueType.INTEGER;
		else if (value instanceof Double)
			return CtxAttributeValueType.DOUBLE;
		else if (value instanceof byte[])
			return CtxAttributeValueType.BINARY;
		else
			throw new IllegalArgumentException(value + ": Invalid value type");
	}

}
