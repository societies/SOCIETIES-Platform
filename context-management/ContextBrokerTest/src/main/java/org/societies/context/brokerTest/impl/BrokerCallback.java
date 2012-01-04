package org.societies.context.brokerTest.impl;

import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

public class BrokerCallback implements IUserCtxBrokerCallback {

	CtxBrokerTest brokerTest;
	
	BrokerCallback(CtxBrokerTest brokerTest){
		this.brokerTest = brokerTest;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntityCreated(CtxEntity ctxEntity) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
