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
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback;
import org.societies.context.user.db.impl.UserCtxDBMgr;

public class UseCaseTest {

	static UserCtxDBMgr userDB ;
	static CtxAttribute attribute;
	
	
	UseCaseTest(){
		userDB = new UserCtxDBMgr();
		System.out.println("start testing");
		testCreateEntitySynch();	
		testCreateIndividualCtxEntity();
		testCreateAttribute();
		testRetrieveAttribute();
		testUpdateAttributeValue();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new UseCaseTest(); 
	}


	private void testCreateIndividualCtxEntity(){
		
		System.out.println("---- testCreateIndividualCtxEntity");
		IUserCtxDBMgrCallback callback = new CallbackImpl();
		userDB.createIndividualCtxEntity("person", callback);
	}

	private void testCreateEntitySynch(){

		System.out.println("---- testCreateEntitySynch");
	
		CtxEntity ctxEnt1 = userDB.createEntitySynch("sensor", null);
		CtxEntity ctxEnt2 = userDB.createEntitySynch("sensor", null);

		System.out.println("Created Sensor Entity 1 "+ ctxEnt1.getId());
		System.out.println("Created Sensor Entity 2 "+ ctxEnt2.getId());

		CtxEntity ctxEntRetrieved1 = (CtxEntity) userDB.retrieveSynch(ctxEnt1.getId());
		System.out.println("Retieve entity from repository");
		if (ctxEntRetrieved1.getId().equals(ctxEntRetrieved1.getId())) System.out.println("Retrieved Sensor Entities are equal");
	
	}


	private void testCreateAttribute(){
		
		System.out.println("---- testCreateAttribute");
		CtxEntity ctxEnt3 = userDB.createEntitySynch("sensor", null);
		attribute = userDB.createAttributeSynch(ctxEnt3.getId(), "Temperature");
		attribute.setIntegerValue(5);

		System.out.println("attribute id: "+attribute.getId() +" type:"+attribute.getType()+" value:"+attribute.getIntegerValue());	
	}
	
	private void testRetrieveAttribute(){
		System.out.println("---- testRetrieveAttribute");
		CtxAttribute ctxAttrRetrieved = (CtxAttribute) userDB.retrieveSynch(attribute.getId());
		System.out.println("ctxAttrRetrieved id: "+ctxAttrRetrieved.getId() +" type:"+ctxAttrRetrieved.getType()+" ctxAttrRetrieved:"+attribute.getIntegerValue());	
		if (attribute.getId().equals(ctxAttrRetrieved.getId())) System.out.println("Retrieved attributes are equal");

	}
	
	private void testUpdateAttributeValue(){
		System.out.println("---- testUpdateAttributeValue");
		final IUserCtxDBMgrCallback callback = new CallbackImpl();
		
		CtxAttribute ctxAttrRetrieved = (CtxAttribute) userDB.retrieveSynch(attribute.getId());
		System.out.println("ctxAttrRetrieved id: "+ctxAttrRetrieved.getId() +" type:"+ctxAttrRetrieved.getType()+" ctxAttrRetrieved:"+ctxAttrRetrieved.getIntegerValue());	
		ctxAttrRetrieved.setIntegerValue(10);
		userDB.update(ctxAttrRetrieved, callback);
		
		CtxAttribute ctxAttrUpdated = (CtxAttribute) userDB.retrieveSynch(attribute.getId());
		System.out.println("ctxAttrUpdated id: "+ctxAttrUpdated.getId() +" type:"+ctxAttrUpdated.getType()+" ctxAttrUpdated:"+ctxAttrUpdated.getIntegerValue());	
	}

	private class CallbackImpl implements IUserCtxDBMgrCallback {

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxAttributeCreated(org.societies.api.context.model.CtxAttribute)
		 */
		@Override
		public void ctxAttributeCreated(CtxAttribute attribute) {
			System.out.println("Test attribute created: " + attribute.getId());
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
			System.out.println("Test entity created: " + entity.getId());
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxIndividualCtxEntityCreated(org.societies.api.context.model.CtxEntity)
		 */
		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity entity) {
			System.out.println("Test individual entity created: " + entity.getId());
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxModelObjectRetrieved(org.societies.api.context.model.CtxModelObject)
		 */
		@Override
		public void ctxModelObjectRetrieved(CtxModelObject modelObject) {
			System.out.println("Test model object retrieved: " + modelObject.getId());
		}

		/* (non-Javadoc)
		 * @see org.societies.api.internal.context.user.db.IUserCtxDBMgrCallback#ctxModelObjectUpdated(org.societies.api.context.model.CtxModelObject)
		 */
		@Override
		public void ctxModelObjectUpdated(CtxModelObject modelObject) {
			System.out.println("Test model object updated: " + modelObject.getId());
		}
	}
}