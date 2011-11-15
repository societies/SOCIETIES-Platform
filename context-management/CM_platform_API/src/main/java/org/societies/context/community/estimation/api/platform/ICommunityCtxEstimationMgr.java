package org.societies.context.community.estimation.api.platform;

import java.util.List;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxEntity;



/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxEstimationMgr {

	/**
	 * 
	 * @param estimationModel
	 * @param list
	 */
	public void estimateContext(EstimationModels estimationModel, List<CtxAttribute> list);

	/**
	 * 
	 * @param Current
	 * @param communityID
	 * @param list
	 */
	public void retrieveCurrentCisContext(boolean Current, EntityIdentifier communityID, List<CtxAttribute> list);

	/**
	 * 
	 * @param Current
	 * @param communityID
	 * @param list
	 */
	public void retrieveHistoryCisContext(boolean Current, EntityIdentifier communityID, List<CtxAttribute> list);

	/**
	 * 
	 * @param estimatedContext
	 */
	public void updateContextModelObject(CtxEntity estimatedContext);

}