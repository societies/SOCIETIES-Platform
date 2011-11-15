package org.societies.context.user.inheritance.api.platform;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxAttributeValueType;


/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface IUserCtxInheritanceMgr {

	/**
	 * 
	 * @param cisId
	 */
	public void getCIS(EntityIdentifier cisId);

	/**
	 * 
	 * @param contextAttributeIdentifier
	 * @param type
	 * @param cisId
	 */
	public void getContextAttribute(CtxAttributeIdentifier contextAttributeIdentifier, CtxAttributeValueType type, EntityIdentifier cisId);

	/**
	 * 
	 * @param contextAttributeIdentifier
	 * @param type
	 */
	public void inheritContextAttribute(CtxAttributeIdentifier contextAttributeIdentifier, CtxAttributeValueType type);

	/**
	 * 
	 * @param conflictResolutionsAlgorithms
	 */
	public void resolveConflicts(ConflictResolutionAlgorithm conflictResolutionsAlgorithms);

}