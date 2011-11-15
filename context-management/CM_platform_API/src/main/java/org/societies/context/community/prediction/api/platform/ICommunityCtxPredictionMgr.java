package org.societies.context.community.prediction.api.platform;

import java.util.Date;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxIdentifier;
import org.societies.context.user.prediction.api.platform.PredictionMethod;


/**
 * @author yboul
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxPredictionMgr {

	/**
	 * 
	 * @param cisID
	 */
	public void getCommunity(EntityIdentifier cisID);

	/**
	 * 
	 * @param predictionModel
	 * @param ctxObjModel
	 * @param date
	 */
	public CtxIdentifier predictContext(PredictionMethod predictionModel, CtxAttributeIdentifier ctxObjModel, Date date);

}