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
package org.societies.context.community.inference.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.societies.context.api.community.inference.ICommunityCtxInferenceMgr;
import org.societies.context.api.community.prediction.ICommunityCtxPredictionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityCtxInferenceMgr implements ICommunityCtxInferenceMgr{


	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityCtxInferenceMgr.class);

	private final List<String> inferrableTypes = new CopyOnWriteArrayList<String>();

	@Autowired(required=false)
	private ICtxBroker internalCtxBroker;

	@Autowired(required=false)
	private ICommunityCtxEstimationMgr communityContextEstimation;

	@Autowired(required=false)
	private ICommManager commMgr;

	@Autowired(required=false)
	private ICommunityCtxPredictionMgr communityContextPrediction;

	
	CommunityCtxInferenceMgr(){

		LOG.info(this.getClass() + "instantiated ");
		assignInfAttributeTypes();

		//	this.internalCtxBroker = internalCtxBroker;
		//	LOG.info(this.getClass() + "internalCtxBroker instantiated "+ this.internalCtxBroker);
		//	this.commMgr = commMgr;
		//	LOG.info(this.getClass() + "commMgr instantiated " +this.commMgr);

		//	this.communityCtxEstimation = communityCtxEstimation; 
		//	LOG.info(this.getClass() + "communityCtxEstimation instantiated " +this.communityCtxEstimation);

	}


	private void assignInfAttributeTypes(){

		// inference is supported for the following community attribute types 
		this.inferrableTypes.add(CtxAttributeTypes.TEMPERATURE);
		this.inferrableTypes.add(CtxAttributeTypes.INTERESTS);
		this.inferrableTypes.add(CtxAttributeTypes.AGE);
		this.inferrableTypes.add(CtxAttributeTypes.LANGUAGES);
		this.inferrableTypes.add(CtxAttributeTypes.LOCATION_COORDINATES);
		this.inferrableTypes.add(CtxAttributeTypes.OCCUPATION);
		this.inferrableTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		this.inferrableTypes.add(CtxAttributeTypes.BOOKS);
		this.inferrableTypes.add(CtxAttributeTypes.FAVOURITE_QUOTES);
		this.inferrableTypes.add(CtxAttributeTypes.MOVIES);

	}

	@Override
	public List<String> getInferrableTypes(){

		if (LOG.isDebugEnabled())
			LOG.debug("getInferrableTypes "+ this.inferrableTypes);
		return Collections.unmodifiableList(this.inferrableTypes);
	}

	@Override
	public CtxAttribute estimateCommunityContext(CtxEntityIdentifier communityEntIdentifier,
			CtxAttributeIdentifier communityAttrId) {
	
		if (communityEntIdentifier == null)
			throw new NullPointerException("communityEntIdentifier can't be null");
		
		if (communityAttrId == null)
			throw new NullPointerException("communityAttrId can't be null");
		
		CtxAttribute ctxAttrReturn = null; 
		//LOG.info("0 commCtxInfMgr :" +communityEntIdentifier +" ");
		try {
		//	ctxAttrReturn = this.internalCtxBroker.retrieveAttribute(communityAttrId, false).get();
			if (LOG.isDebugEnabled()) 	{
				LOG.debug("communityEntIdentifier "+communityEntIdentifier.toString());
				LOG.debug("communityAttrId "+communityAttrId.toString());
			}
			//LOG.info("1 commCtxInfMgr ctxAttrReturn:" +ctxAttrReturn);
			ctxAttrReturn = this.communityContextEstimation.estimateCommunityCtx(communityEntIdentifier, communityAttrId);

		} catch (Exception e) {
			LOG.error("Could not estimate community context for communityAttrId: "+communityAttrId +" "
					+ e.getLocalizedMessage(), e);
		}
		return ctxAttrReturn;
	}


	@Override
	public Double evaluateSimilarity(CtxAttributeIdentifier arg0,
			CtxAttributeIdentifier arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<CtxAttributeIdentifier, Double> evaluateSimilarity(
			List<CtxAttributeIdentifier> arg0, List<CtxAttributeIdentifier> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier attrID, Date date) {
		
		CtxAttribute result; 
		
		//communityContextPrediction.predictContext(attrID, date);
		
		return null;
	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refineContext(CtxAttributeIdentifier arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void inheritContext(CtxAttributeIdentifier arg0,
			CtxAttributeValueType arg1, IIdentity arg2) {
		// TODO Auto-generated method stub

	}

}