/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.personalisation.management.test;



/*import java.io.IOException;
import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.api.internal.context.user.db.IUserCtxDBMgr;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.user.db.impl.UserCtxDBMgr;

import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.management.impl.PersonalisationManager;*/

public class TestPersonalisationManager {

/*
	private IUserCtxBroker  broker;
	private IInternalPersonalisationManager perMan;
	private IUserCtxDBMgr ctxDBManager;
	
	public TestPersonalisationManager(){
		broker = new InternalCtxBroker();
		ctxDBManager = new UserCtxDBMgr();
		((InternalCtxBroker) broker).setUserCtxDBMgr(ctxDBManager);
		 perMan = new PersonalisationManager(broker);
		
		BrokerCallbackImpl callback = new BrokerCallbackImpl();
		broker.createEntity("Person", callback);
		
		
	}
	
	public void testRegisterForUpdates(CtxAttributeIdentifier ctxAttrId){
		perMan.registerForContextUpdate(null, this.getClass().getName(), ctxAttrId);
	}
	
	public static void main(String[] args) throws IOException{
		TestPersonalisationManager testManager = new TestPersonalisationManager();
		
	}
	
	private class BrokerCallbackImpl implements IUserCtxBrokerCallback{

		CtxEntity ctxEntity = null;
		CtxAttribute ctxAttribute = null;
		CtxModelObject ctxModelObject = null;
		
		
		public CtxEntity getCtxEntity(){
			return ctxEntity;
		}

		public CtxAttribute getCtxAttribute(){
			return ctxAttribute;
		}

		public CtxModelObject getCtxModelObject(){
			return  this.ctxModelObject;
		}

		@Override
		public void cancel(CtxIdentifier c_id, String reason) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAssociationCreated(CtxAssociation ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxAttributeCreated(CtxAttribute ctxAttribute) {
			System.out.println("CtxAttribute created "+ ctxAttribute.getId());
			this.ctxAttribute = ctxAttribute;
			testRegisterForUpdates(ctxAttribute.getId());
			
		}

		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxEntityCreated(CtxEntity ctxEntity) {
			System.out.println("Entity created "+ ctxEntity.getId());
			this.ctxEntity = ctxEntity;
			broker.createAttribute(this.ctxEntity.getId(), CtxAttributeValueType.INDIVIDUAL, "symloc", this);
			
		}

		@Override
		public void ctxIndividualCtxEntityCreated(CtxEntity ctxEntity) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRemoved(CtxModelObject ctxModelObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject) {
			System.out.println("ctxModelObject Retrieved "+ ctxModelObject.getId());
			this.ctxModelObject = ctxModelObject;
		}

		@Override
		public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
			// TODO Auto-generated method stub

		}

		@Override
		public void futureCtxRetrieved(List<CtxAttribute> futCtx) {
			// TODO Auto-generated method stub

		}

		@Override
		public void futureCtxRetrieved(CtxAttribute futCtx) {
			// TODO Auto-generated method stub

		}

		@Override
		public void historyCtxRetrieved(CtxHistoryAttribute hoc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok(CtxIdentifier c_id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok_list(List<CtxIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ok_values(List<Object> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void similartyResults(List<Object> results) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateReceived(CtxModelObject ctxModelObj) {
			// TODO Auto-generated method stub

		}

	}
*/
}
