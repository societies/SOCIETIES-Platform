package org.societies.context.user.refiner.api.platform;

import org.societies.context.model.api.CtxAttributeIdentifier;


/**
 * @author fran_ko
 * @version 1.0
 * @created 12-Nov-2011 7:15:16 PM
 */
public interface IUserCtxRefiner {

	/**
	 * 
	 * @param attrId
	 */
	public void refineContext(CtxAttributeIdentifier attrId);

}