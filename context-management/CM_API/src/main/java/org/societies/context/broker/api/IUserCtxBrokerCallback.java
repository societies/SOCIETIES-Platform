package org.societies.context.broker.api;

import java.util.List;

import org.societies.context.model.api.CtxAssociation;
import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxEntity;
import org.societies.context.model.api.CtxEntityIdentifier;
import org.societies.context.model.api.CtxHistoryAttribute;
import org.societies.context.model.api.CtxIdentifier;
import org.societies.context.model.api.CtxModelObject;
import org.societies.context.model.api.IndividualCtxEntity;

public interface IUserCtxBrokerCallback {
	
	/**
	 * 
	 * @param c_id
	 * @param reason
	 */
	public void cancel(CtxIdentifier c_id, String reason);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxAssociationCreated(CtxAssociation ctxEntity);

	/**
	 * 
	 * @param ctxAttribute
	 */
	public void ctxAttributeCreated(CtxAttribute ctxAttribute);

	/**
	 * 
	 * @param list
	 */
	public void ctxEntitiesLookedup(List<CtxEntityIdentifier> list);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxEntityCreated(CtxEntity ctxEntity);

	/**
	 * 
	 * @param ctxEntity
	 */
	public void ctxIndividualCtxEntityCreated(IndividualCtxEntity ctxEntity);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRemoved(CtxModelObject ctxModelObject);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectRetrieved(CtxModelObject ctxModelObject);

	/**
	 * 
	 * @param list
	 */
	public void ctxModelObjectsLookedup(List<CtxIdentifier> list);

	/**
	 * 
	 * @param ctxModelObject
	 */
	public void ctxModelObjectUpdated(CtxModelObject ctxModelObject);

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(List <CtxAttribute> futCtx);

	/**
	 * 
	 * @param futCtx
	 */
	public void futureCtxRetrieved(CtxAttribute futCtx);

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(CtxHistoryAttribute hoc);

	/**
	 * 
	 * @param hoc
	 */
	public void historyCtxRetrieved(List<CtxHistoryAttribute> hoc);

	/**
	 * 
	 * @param c_id
	 */
	public void ok(CtxIdentifier c_id);

	/**
	 * 
	 * @param list
	 */
	public void ok_list(List<CtxIdentifier> list);

	/**
	 * 
	 * @param list
	 */
	public void ok_values(List<Object> list);

	/**
	 * needs further refinement
	 * 
	 * @param results
	 */
	public void similartyResults(List<Object> results);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(CtxModelObject ctxModelObj);
}