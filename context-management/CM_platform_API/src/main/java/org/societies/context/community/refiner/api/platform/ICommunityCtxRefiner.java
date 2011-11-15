package org.societies.context.community.refiner.api.platform;

import org.societies.context.model.api.CtxAttributeIdentifier;


/**
 * @author fran_ko
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxRefiner {

	/**
	 * 
	 * @param attrId
	 * @return 
	 */
	public  void refineContext(CtxAttributeIdentifier attrId);

}