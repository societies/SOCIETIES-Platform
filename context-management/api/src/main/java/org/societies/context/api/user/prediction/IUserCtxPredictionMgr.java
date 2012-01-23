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
package org.societies.context.api.user.prediction;

import java.util.Date;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;


/**
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 */
public interface IUserCtxPredictionMgr {

	/**
	 * Gets the default prediction method.
	 * 
	 * @param predMethod
	 * @return prediction method
	 * @since 0.0.1
	 */
	public PredictionMethod getDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * Gets a prediction method.
	 * 
	 * @param predMethod
	 * @return prediction method
	 * @since 0.0.1
	 */
	public PredictionMethod getPredictionMethod(PredictionMethod predMethod);

	/**
	 * Predicts context using indicated prediction method and date.
	 * 
	 * @param predictionModel
	 * @param ctxAttrID
	 * @param date
	 * @return context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(PredictionMethod predictionModel, CtxAttributeIdentifier ctxAttrID, Date date);

	/**
	 * Predicts context using indicated date.
	 * 
	 * @param ctxAttrID
	 * @param date
	 * @return context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, Date date);

	/**
	 * Predicts context using indicated index.
	 * 
	 * @param ctxAttrID
	 * @param index
	 * @return context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID, int index);

	/**
	 * Predicts context using indicated prediction method and index.
	 * 
	 * @param predictionModel
	 * @param ctxAttrID
	 * @param index
	 * @return context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(PredictionMethod predictionModel, CtxAttributeIdentifier ctxAttrID, int index);

	/**
	 * Removes an indicated prediction method.
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void removePredictionMethod(PredictionMethod predMethod);

	/**
	 * Sets an indicated prediction method as default.
	 * 
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void setDefaultPredictionMethod(PredictionMethod predMethod);

	/**
	 * Sets a prediction method.
	 * @param predMethod
	 * @since 0.0.1
	 */
	public void setPredictionMethod(PredictionMethod predMethod);

}