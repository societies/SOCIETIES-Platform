package org.societies.context.community.inference.api.platform;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAttribute;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxAttributeValueType;
import org.societies.context.user.prediction.api.platform.PredictionMethod;



/**
 * @author nikosk
 * @version 1.0
 * @created 12-Nov-2011 7:15:15 PM
 */
public interface ICommunityCtxInferenceMgr {

	/**
	 * 
	 * @param predictionMethodl
	 */
	public void addPredictionMethod(PredictionMethod  predictionMethod );

	/**
	 * 
	 * @param ctxID
	 * @param ctxID2
	 */
	public Double evaluateSimilarity(CtxAttributeIdentifier ctxID, CtxAttributeIdentifier ctxID2);

	/**
	 * 
	 * @param listCtxID
	 * @param listCtxID2
	 */
	public Map<CtxAttributeIdentifier,Double> evaluateSimilarity(List<CtxAttributeIdentifier> listCtxID, List<CtxAttributeIdentifier> listCtxID2);

	/**
	 * 
	 * @param predictionMethod
	 */
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod predictionMethod);

	/**
	 * 
	 * @param ctxAttrId
	 * @param type
	 * @param cisid
	 */
	public void inheritContext(CtxAttributeIdentifier ctxAttrId, CtxAttributeValueType type, EntityIdentifier cisid);

	/**
	 * 
	 * @param ctxAttrID
	 * @param predictionMethod
	 * @param date
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, PredictionMethod predictionMethod, Date date);

	/**
	 * 
	 * @param ctxAttrID
	 * @param predictionMethodl
	 * @param int
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, PredictionMethod predictionMethodl, int index );

	/**
	 * 
	 * @param ctxAttrID
	 * @param date
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, Date date);

	/**
	 * 
	 * @param ctxAttrID
	 * @param index
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, int index);

	/**
	 * 
	 * @param ctxAttrId
	 */
	public void refineContext(CtxAttributeIdentifier ctxAttrId);

	/**
	 * 
	 * @param predictionMethod
	 */
	public void removePredictionMethod(PredictionMethod predictionMethod);

	/**
	 * 
	 * @param predMethod
	 */
	public void setDefaultPredictionMethod(PredictionMethod predMethod);

}