package org.societies.context.community.inheritance.api.platform;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxAttributeValueType;

/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxInheritanceMgr {

	/**
	 * 
	 * @param cidIdentifier
	 */
	public void getParentCis(EntityIdentifier cidIdentifier);

	/**
	 * 
	 * @param ctxAttributeIdentifier
	 * @param type
	 * @param cisId
	 */
	public void inheritContext(CtxAttributeIdentifier ctxAttributeIdentifier, CtxAttributeValueType type, EntityIdentifier cisId);

	/**
	 * 
	 * @param ctxAttributeIdentifier
	 * @param type
	 * @param cisId
	 */
	public void retrieveCtx(CtxAttributeIdentifier ctxAttributeIdentifier, CtxAttributeValueType type, EntityIdentifier cisId);

}