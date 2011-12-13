package org.societies.context.broker.test;

import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.context.broker.impl.InternalCtxBroker;


public class InternalCtxBrokerTest {

	private static InternalCtxBroker internalCtxBroker = null;
	BrokerCallbackImpl callback ;

	//Constructor
	InternalCtxBrokerTest(){

		callback = new  BrokerCallbackImpl();

		internalCtxBroker = new InternalCtxBroker();

		System.out.println("-- start of testing --");
		testCreateCtxEntity();
		testCreateCtxAttribute();
		testRetrieveAttribute();
		testUpdateAttribute();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new InternalCtxBrokerTest();
	}


	private void testCreateCtxEntity(){
		System.out.println("---- test CreateCtxEntity");
		internalCtxBroker.createEntity("person", callback);
	}

	private void testCreateCtxAttribute(){
		System.out.println("---- test testCreateCtxAttribute");
		internalCtxBroker.createAttribute(callback.getCtxEntity().getId(), CtxAttributeValueType.INDIVIDUAL, "name", callback);
	}

	private void testRetrieveAttribute(){
		System.out.println("---- testRetrieveCtxAttribute");
		CtxAttribute ctxAttribute = callback.getCtxAttribute();
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
	}

	private void testUpdateAttribute(){
		System.out.println("---- testUpdateAttribute");
		CtxAttribute ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		ctxAttribute.setIntegerValue(100);
		internalCtxBroker.update(ctxAttribute, callback);
		//verify update
		internalCtxBroker.retrieve(ctxAttribute.getId(), callback);
		ctxAttribute = (CtxAttribute) callback.getCtxModelObject();
		System.out.println("attribute value should be 100 and it is:"+ctxAttribute.getIntegerValue());
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

		}

		@Override
		public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
			// TODO Auto-generated method stub

		}

		@Override
		public void ctxEntityCreated(CtxEntity ctxEntity) {
			System.out.println("Entity created "+ ctxEntity.getId());
			this.ctxEntity = ctxEntity;
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

}
