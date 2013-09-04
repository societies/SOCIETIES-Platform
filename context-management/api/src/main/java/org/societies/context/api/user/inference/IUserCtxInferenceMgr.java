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
package org.societies.context.api.user.inference;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.societies.api.identity.IIdentity;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxQuality;

/**
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 */
public interface IUserCtxInferenceMgr {

	/**
	 * Returns <code>true</code> if the specified quality is poor; 
	 * <code>false</code> otherwise.
	 * 
	 * @param quality
	 * @return <code>true</code> if the specified quality is poor; 
	 *         <code>false</code> otherwise
	 * @since 0.5
	 */
	public boolean isPoorQuality(final CtxQuality quality);

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
	 * Inherits the Context Attribute belonging to a CIS.
	 * 
	 * @param ctxAttrId
	 * @param type
	 * @param cisid
	 * @since 0.0.1
	 */
	public void inheritContext(CtxAttributeIdentifier attrId, CtxAttributeValueType type, IIdentity cisid);
	public CtxAttribute inheritContext(CtxAttributeIdentifier ctxAttrId);
	
	/**
	 * Predicts context using indicated date. 
	 * 
	 * @param attrId
	 * @param date
	 * @returns context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier attrId, Date date);

	/**
	 * Predicts context using indicated index.
	 * 
	 * @param attrId
	 * @param index
	 * @returns context attribute with predicted context
	 * @since 0.0.1
	 */
	public CtxAttribute predictContext(CtxAttributeIdentifier attrId, int index);

	/**
	 * Refines the identified context attribute. The method returns 
	 * <code>null</code> if the identified attribute cannot be refined.
	 * 
	 * @param attrId
	 *            the identifier of the context attribute to be refined.
	 * @return the refined context attribute or <code>null</code> if the
	 *         identified attribute cannot be refined.
	 * @since 0.5
	 */
	public CtxAttribute refineOnDemand(final CtxAttributeIdentifier attrId) 
			throws UserCtxInferenceException;
	
	/**
	 * Refines the identified context attribute. 
	 * 
	 * @param attrId
	 * 			the identifier of the context attribute to refine.
	 * @param updateFrequency
	 * @since 0.5
	 */
	public void refineContinuously(final CtxAttributeIdentifier attrId, 
			final Double updateFrequency) throws UserCtxInferenceException;

	/**
	 * Returns a list of CtxAttributeTypes that can be inferred. 
	 * 
	 *  @return Set of ctxAttributeTypes
	 *  @since 0.0.8
	 */
	public List<String> getInferrableTypes();
	
	/**
	 * Adds the specified type to the list of CtxAttributeTypes that can be inferred. 
	 * 
	 *  @param the new type of inferrable attribute
	 *  @since 0.5
	 */
	public void addInferrableType(final String attrType);
	
	/**
	 * Removes the specified type from the list of CtxAttributeTypes that can be inferred. 
	 * 
	 *  @param the new type of inferrable attribute
	 *  @since 0.5
	 */
	public void removeInferrableType(final String attrType);
}