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

public class BrokerCallbackTest implements IUserCtxBrokerCallback {

	CtxBrokerTest brokerTest;

	BrokerCallbackTest(CtxBrokerTest brokerTest){
		this.brokerTest = brokerTest;
	}


	CtxEntity ctxEntity = null;
	CtxAttribute ctxAttribute = null;
	CtxModelObject ctxModelObject = null;
	
	
	public CtxModelObject getCtxModelObject() {
		return ctxModelObject;
	}

	private void setCtxModelObject(CtxModelObject ctxModelObject) {
		this.ctxModelObject = ctxModelObject;
	}
/*
	public CtxEntity getCtxEntity(){
		return ctxEntity;
	}

	private void setCtxEntity(CtxEntity ctxEntity){
		this.ctxEntity = ctxEntity;
	}


	public CtxAttribute getCtxAttribute() {
		return ctxAttribute;
	}

	private void setCtxAttribute(CtxAttribute ctxAttribute) {
		this.ctxAttribute = ctxAttribute;
	}
*/

	
	

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
		System.out.println("CtxAttribute "+ctxAttribute+" created");
		setCtxModelObject(ctxAttribute); 
			
	}

	@Override
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ctxEntityCreated(CtxEntity ctxEntity) {
		System.out.println("CtxEntity "+ctxEntity+" created");
		setCtxModelObject(ctxEntity); 
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
		System.out.println("CtxModelObject "+ctxModelObject+" Retrieved");
		setCtxModelObject(ctxModelObject); 
	}

	@Override
	public void ctxModelObjectsLookedup(List<CtxIdentifier> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ctxModelObjectUpdated(CtxModelObject ctxModelObject) {
		System.out.println("CtxModelObject "+ctxModelObject+" updated");
		setCtxModelObject(ctxModelObject); 
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
