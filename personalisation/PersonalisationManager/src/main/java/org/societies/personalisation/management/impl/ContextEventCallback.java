package org.societies.personalisation.management.impl;

import java.util.List;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;

public class ContextEventCallback implements IUserCtxBrokerCallback{

	private final PersonalisationManager pm;

	public ContextEventCallback(PersonalisationManager pm){
		this.pm = pm;
		
	}

	@Override
	public void cancel(CtxIdentifier arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxAssociationCreated(CtxAssociation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxAttributeCreated(CtxAttribute arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxEntityCreated(CtxEntity arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxIndividualCtxEntityCreated(CtxEntity arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectRemoved(CtxModelObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectRetrieved(CtxModelObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxModelObjectUpdated(CtxModelObject attribute) {
		this.pm.updateReceived(attribute);
		
	}

	@Override
	public void ctxModelObjectsLookedup(List<CtxIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void futureCtxRetrieved(List<CtxAttribute> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void futureCtxRetrieved(CtxAttribute arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historyCtxRetrieved(CtxHistoryAttribute arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void historyCtxRetrieved(List<CtxHistoryAttribute> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok(CtxIdentifier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok_list(List<CtxIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ok_values(List<Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void similartyResults(List<Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateReceived(CtxModelObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesRemoved(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesRetrieved(List<CtxAttributeIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesSet(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ctxHistoryTuplesUpdated(List<CtxAttributeIdentifier> arg0) {
		// TODO Auto-generated method stub
		
	}
}
