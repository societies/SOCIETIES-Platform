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
package org.societies.context.user.db.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.societies.context.user.db.impl.bo.UserCtxEntityMgrBo;
import org.societies.context.user.db.impl.bo.UserCtxAttributeMgrBo;
import org.societies.context.user.db.impl.model.UserCtxEntityMgr;
import org.societies.context.user.db.impl.model.UserCtxAttributeMgr;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.mock.EntityIdentifier;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.societies.context.api.user.db.IUserCtxDBMgrCallback;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;


//@Component
public class UserCtxDBMgr implements IUserCtxDBMgr{

	ApplicationContext appContext = 
	    	  new ClassPathXmlApplicationContext("spring/config/BeanLocations.xml");
	 
   	UserCtxEntityMgrBo entityBo = (UserCtxEntityMgrBo)appContext.getBean("entityBo");
   	UserCtxAttributeMgrBo attributeBo = (UserCtxAttributeMgrBo)appContext.getBean("attributeBo");

	private final Map<CtxIdentifier, CtxModelObject> modelObjects;

	private final EntityIdentifier privateId;

//	@Autowired
	public UserCtxDBMgr() {
		this.modelObjects =  new HashMap<CtxIdentifier, CtxModelObject>();
		this.privateId = new EntityIdentifier();
	}

	@Override
	public CtxAssociation createAssociation(String arg0) {
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgr#createAttribute(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxAttributeValueType, java.lang.String, org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback)
	 */
	@Override
	public CtxAttribute createAttribute(CtxEntityIdentifier scope,
			CtxAttributeValueType valueType, String type) {

		if (scope == null)
			throw new NullPointerException("scope can't be null");

		final CtxEntity entity = (CtxEntity) modelObjects.get(scope);

		/**************************/
		if (entity == null)
			// F A I L (callback should throw an exception!!)
			System.err.println("No such context entity: " + scope); // TEMP SOLUTION
		/**************************/

		CtxAttributeIdentifier attrIdentifier = new CtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final CtxAttribute attribute = new CtxAttribute(attrIdentifier);

		this.modelObjects.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		// AGAIN?? modelObjects.put(entity.getId(), entity);
		
		Date date = new Date();
    	/** insert **/
    	UserCtxAttributeMgr attributeDB = new UserCtxAttributeMgr();
    	attributeDB.setOperatorId(attribute.getScope().toString());
    	attributeDB.setType(attribute.getType());
    	attributeDB.setObjectNumber(attribute.getObjectNumber());
    	attributeDB.setLastModified(date);
    	attributeBo.save(attributeDB);
    	
//		callback.ctxAttributeCreated(attribute);
    	return attribute;
	}


	@Override
	public CtxEntity createEntity(String type) {

		final CtxEntityIdentifier identifier = new CtxEntityIdentifier(this.privateId, 
				type, CtxModelObjectNumberGenerator.getNextValue());
		final CtxEntity entity = new  CtxEntity(identifier);
		this.modelObjects.put(entity.getId(), entity);

		Date date = new Date();
    	/** insert **/
    	UserCtxEntityMgr entityDB = new UserCtxEntityMgr();
    	entityDB.setOperatorId(entity.toString());
    	entityDB.setType(entity.getType());
    	entityDB.setObjectNumber(entity.getObjectNumber());
    	entityDB.setLastModified(date);
    	entityBo.save(entityDB);

//		callback.ctxEntityCreated(entity);
    	return entity;
	}


	@Override
	public List<CtxIdentifier> lookup(CtxModelType arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxEntityIdentifier> lookupEntities(String arg0, String arg1, Serializable arg2,
			Serializable arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerForUpdates(CtxAttributeIdentifier arg0) {
		// TODO Auto-generated method stub
	
	}


	@Override
	public void registerForUpdates(CtxEntityIdentifier scope, String attributeType) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public CtxModelObject remove(CtxIdentifier arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgr#retrieve(org.societies.api.context.model.CtxIdentifier, org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback)
	 */
	@Override
	public CtxAttribute retrieve(CtxIdentifier id) {
//		callback.ctxModelObjectRetrieved(this.modelObjects.get(id));
	return (CtxAttribute) this.modelObjects.get(id);
	}

	public CtxModelObject retrieveSynch(CtxIdentifier id) {
		return this.modelObjects.get(id);
	}

	@Override
	public void unregisterForUpdates(CtxAttributeIdentifier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterForUpdates(CtxEntityIdentifier arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CtxModelObject update(CtxModelObject modelObject) {

		if (this.modelObjects.containsValue(modelObject)) {
			this.modelObjects.put(modelObject.getId(), modelObject);

//			callback.ctxModelObjectUpdated(modelObject);
		}

		UserCtxAttributeMgr attrDBupdate = attributeBo.findByCode(modelObject.getType());
    	
    	System.out.println(attrDBupdate);

		Date date = new Date();
    	/** insert **/
//    	userDBupdate.setOperatorId(modelObject.getId().getObjectNumber());
    	attrDBupdate.setType(modelObject.getType());
    	attrDBupdate.setObjectNumber(modelObject.getObjectNumber());
    	attrDBupdate.setLastModified(date);
    	attributeBo.update(attrDBupdate);

    	return modelObject;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgr#createIndividualCtxEntity(java.lang.String, org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback)
	 */
	@Override
	public IndividualCtxEntity createIndividualCtxEntity(String type)  {

		CtxEntityIdentifier identifier = new CtxEntityIdentifier(this.privateId,
				type, CtxModelObjectNumberGenerator.getNextValue());
		IndividualCtxEntity entity = new IndividualCtxEntity(identifier);
		this.modelObjects.put(entity.getId(), entity);

		Date date = new Date();
    	/** insert **/
    	UserCtxEntityMgr entityDB = new UserCtxEntityMgr();
    	entityDB.setOperatorId(entity.toString());
    	entityDB.setType(entity.getType());
    	entityDB.setObjectNumber(entity.getObjectNumber());
    	entityDB.setLastModified(date);
    	entityBo.save(entityDB);
//		callback.ctxIndividualCtxEntityCreated(entity);
    	return entity;
	}
}