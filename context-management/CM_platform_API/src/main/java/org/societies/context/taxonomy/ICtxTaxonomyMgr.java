package org.societies.context.taxonomy;

import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxAttributeValueType;


/**
 * @author TI
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICtxTaxonomyMgr {

	/**
	 * it returns the children of the element in the taxonomy tree
	 * 
	 * @param attrubute
	 */
	public CtxAttribute[] getChildren(CtxAttribute attrubute);

	/**
	 * 
	 * @param attributeB
	 * @param attributeA
	 */
	public int getDistance(CtxAttribute attributeB, CtxAttribute attributeA);

	/**
	 * it returns the parent of the element in the taxonomy tree
	 * 
	 * @param attribute
	 */
	public CtxAttribute getParent(CtxAttribute attribute);

	/**
	 * 
	 * @param attribute
	 */
	public CtxAttributeValueType getSemanticDescription(CtxAttribute attribute);

	/**
	 * It return an array of context attributes which are on the same level on the
	 * specific tree leaf
	 * 
	 * @param attribute
	 */
	public CtxAttribute[] getSiblings(CtxAttribute attribute);

	/**
	 * 
	 * @param attrubute
	 */
	public boolean isContextAttributeAvailable(CtxAttribute attrubute);

}