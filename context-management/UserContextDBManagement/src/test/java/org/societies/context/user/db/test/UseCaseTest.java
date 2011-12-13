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
package org.societies.context.user.db.test;

import java.util.List;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback;
import org.societies.context.user.db.impl.UserCtxDBMgr;

public class UseCaseTest {

	static UserCtxDBMgr userDB ;
	static CtxEntity entity;
	static CtxAttribute attribute;
	
	// not using the IUserCtxDBMgrCallback because getEntity() is missing
	CallbackImpl callback ;

	UseCaseTest(){
		userDB = new UserCtxDBMgr();
		//callback = new CallbackImpl(userDB);
		callback = new CallbackImpl();
	
		System.out.println("start testing");
		testCreateIndividualCtxEntity();
		testCreateAttribute();
		testRetrieveAttribute();
		testUpdateAttribute();
		}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new UseCaseTest(); 
	}


	private void testCreateIndividualCtxEntity(){
		System.out.println("---- testCreateIndividualCtxEntity");
		userDB.createIndividualCtxEntity("person", callback);
		entity = callback.getCtxEntity();
	}

	private void testCreateAttribute(){
		System.out.println("---- testCreateAttribute");
		userDB.createAttribute(entity.getId(), CtxAttributeValueType.INDIVIDUAL, "name", callback);
		attribute = callback.getCtxAttribute();
	}

	private void testRetrieveAttribute(){
		System.out.println("---- testRetrieveAttribute");
		userDB.retrieve(attribute.getId(), callback);
		attribute = (CtxAttribute) callback.getCtxModelObject();
	}
	
	private void testUpdateAttribute(){
		System.out.println("---- testUpdateAttribute");
		userDB.retrieve(attribute.getId(), callback);
		attribute = (CtxAttribute) callback.getCtxModelObject();
		attribute.setIntegerValue(5);
		userDB.update(attribute, callback);
		//verify update
		userDB.retrieve(attribute.getId(), callback);
		attribute = (CtxAttribute) callback.getCtxModelObject();
		System.out.println("attribute value should be 5 and it is:"+attribute.getIntegerValue());
	}
	
	private class CallbackImpl implements IUserCtxDBMgrCallback {

		CtxEntity callbackEntity = null;
		CtxAttribute callbackAttribute = null;
		CtxModelObject ctxModelObject = null;
	
		CallbackImpl(){
		}

		
		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxAttributeCreated(org.societies.api.context.model.CtxAttribute)
		 */
		@Override
		public void ctxAttributeCreated(CtxAttribute attribute) {
			System.out.println("Test attribute created: " + attribute.getId());
			this.callbackAttribute = attribute;
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxEntitiesLookedup(java.util.List)
		 */
		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> arg0) {
			// TODO Auto-generated method stub
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxEntityCreated(org.societies.api.context.model.CtxEntity)
		 */
		@Override
		public void ctxEntityCreated(CtxEntity entity) {
			System.out.println("callback : Test entity created: " + entity.getId());

			this.callbackEntity = entity;
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxIndividualCtxEntityCreated(org.societies.api.context.model.CtxEntity)
		 */
		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity entity) {
			System.out.println("callback : Test individual entity created: " + entity.getId());
			this.callbackEntity = entity;
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxModelObjectRetrieved(org.societies.api.context.model.CtxModelObject)
		 */
		@Override
		public void ctxModelObjectRetrieved(CtxModelObject modelObject) {
			System.out.println("callback : Test model object retrieved: " + modelObject.getId());
			this.ctxModelObject =  modelObject;
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxModelObjectUpdated(org.societies.api.context.model.CtxModelObject)
		 */
		@Override
		public void ctxModelObjectUpdated(CtxModelObject modelObject) {
			System.out.println("callback : Test model object updated: " + modelObject.getId());
		}
	
		public CtxAttribute getCtxAttribute(){
			return this.callbackAttribute;
		}
		
		public CtxEntity getCtxEntity(){
			return  this.callbackEntity;
		}

		public CtxModelObject getCtxModelObject(){
			return  this.ctxModelObject;
		}
			
	}
}