/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.api.internal.context.user.inference;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.user.prediction.PredictionMethod;
//import org.societies.context.user.prediction.api.platform.*;

/**
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 */
public interface IUserCtxInferenceMgr {

	/**
	 * Checks the quality of an indicated Context Model Object.
	 * 
	 * @param object
	 * @since 0.0.1
	 */
	public void checkQuality(CtxModelObject object);

	/**
	 * Evaluates the similatiry of two indicated Context Attributes.
	 * 
	 * @param ctxID
	 * @param ctxID2
	 * @return a number indicating the objects similarity
	 * @since 0.0.1
	 */
	public Double evaluateSimilarity(CtxAttributeIdentifier ctxID, CtxAttributeIdentifier ctxID2);

	/**
	 * This method returns a linked map with key the recorded
	 * CtxAttributeIdentifier and value the result of the 
	 * similarity evaluation. 
	 * 
	 * @param listCtxID
	 * @param listCtxID2
	 * @since 0.0.1
	 */
	public Map<CtxAttributeIdentifier,Double> evaluateSimilarity(List<CtxAttributeIdentifier> listCtxID, List<CtxAttributeIdentifier> listCtxID2);

	/**
	 * Gets the default prediction method. 
	 * 
	 * @param predictionMethod
	 * @return prediction method
	 * @since 0.0.1
	 */
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod predictionMethod);

	/**
	 * Gets a prediction method.
	 * 
	 * @param predictionMethodl
	 * @return prediction method
	 * @since 0.0.1
	 */
	public PredictionMethod getPredictionMethod(PredictionMethod predictionMethod);

	/**
	 * Inherits the Context Attribute belonging to a CIS.
	 * 
	 * @param ctxAttrId
	 * @param type
	 * @param cisid
	 * @since 0.0.1
	 */
	public void inheritContext(CtxAttributeIdentifier ctxAttrId, CtxAttributeValueType type, EntityIdentifier cisid);

	/**
	 * Predicts context using indicated prediction method and date.
	 *  
	 * @param ctxAttrID
	 * @param predictionMethod
	 * @param date
	 * @returns context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, PredictionMethod predictionMethod, Date date);

	/**
	 * Predicts context using indicated prediction method and index. 
	 * 
	 * @param ctxAttrID
	 * @param predictionMethodl
	 * @param index
	 * @returns context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, PredictionMethod predictionMethodl, int index);

	/**
	 * Predicts context using indicated date. 
	 * 
	 * @param ctxAttrID
	 * @param date
	 * @returns context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, Date date);

	/**
	 * Predicts context using indicated index.
	 * 
	 * @param ctxAttrID
	 * @param index
	 * @returns context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, int index);

	/**
	 * Refines context for an indicate Context Attribute. 
	 * 
	 * @param ctxAttrId
	 * @since 0.0.1
	 */
	public void refineContext(CtxAttributeIdentifier ctxAttrId);

	/**
	 * Removes a specified prediction method.
	 * 
	 * @param predictionMethod
	 * @since 0.0.1
	 */
	public void removePredictionMethod(PredictionMethod predictionMethod);

	/**
	 * Sets an indicated prediction method as default.
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void setDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * Sets a prediction method.
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void setPredictionMethod(PredictionMethod predMethod);

}