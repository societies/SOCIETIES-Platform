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
package org.societies.context.user.inference.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.CtxQuality;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.societies.context.api.user.inference.UserCtxInferenceException;
import org.societies.context.api.user.prediction.IUserCtxPredictionMgr;
import org.societies.context.api.user.refinement.IUserCtxRefiner;
import org.societies.context.api.user.inheritance.IUserCtxInheritanceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCtxInferenceMgr implements IUserCtxInferenceMgr {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserCtxInferenceMgr.class);

	private final List<String> inferrableTypes = new CopyOnWriteArrayList<String>();
	
	private IIdentity cssOwnerId;

	@Autowired(required=false)
	private IUserCtxRefiner userCtxRefiner;
	
	@Autowired(required=false)
	private IUserCtxInheritanceMgr userCtxInheritance;
	
	@Autowired(required=false)
	private IUserCtxPredictionMgr userPredMgr;
	
	
	private ICtxBroker internalCtxBroker;
	
	private ICommManager commMgr;
	
	@Autowired(required=true)
	UserCtxInferenceMgr(ICtxBroker internalCtxBroker, ICommManager commMgr) {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.internalCtxBroker = internalCtxBroker;
		this.commMgr = commMgr;
		this.assignInfAttributeTypes(); // TODO remove!!	
	}

	private void assignInfAttributeTypes(){
		
		// TODO should be added by individual inference algorithms 
		this.inferrableTypes.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		this.inferrableTypes.add(CtxAttributeTypes.LOCATION_COORDINATES);
		//inferrableTypes.add(CtxAttributeTypes.STATUS);
		//inferrableTypes.add(CtxAttributeTypes.TEMPERATURE);
		
		if (LOG.isDebugEnabled())
			LOG.debug("Inferrable Types=" + this.getInferrableTypes());
		try {
			final String cssOwnerStr = this.commMgr.getIdManager().getThisNetworkNode().getBareJid();
			this.cssOwnerId = this.commMgr.getIdManager().fromJid(cssOwnerStr);
			final IndividualCtxEntity ownerEntity = 
					this.internalCtxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			
			for(final String inferrableType: this.inferrableTypes) {
				if (ownerEntity.getAttributes(inferrableType).size() == 0) {
					CtxAttribute ctxAttr = this.internalCtxBroker.createAttribute(ownerEntity.getId(), inferrableType).get();
					ctxAttr.setHistoryRecorded(true);
					this.internalCtxBroker.update(ctxAttr);
					if (LOG.isDebugEnabled())
						LOG.debug("Inferrable attribute created: "+ ctxAttr.getId());
				}
			}
		} catch (Exception e) {
			LOG.error("Could not initialise inferrable attributes: "
					+ e.getLocalizedMessage(), e);
		}
	}

	/*
	 * @see org.societies.context.api.user.inference.IUserCtxInferenceMgr#isPoorQuality(org.societies.api.context.model.CtxQuality)
	 */
	@Override
	public boolean isPoorQuality(CtxQuality quality) {
		
		if (quality == null)
			throw new NullPointerException("quality can't be null");
		
		boolean isPoorQuality;

		if (LOG.isDebugEnabled())
			LOG.debug(quality.getAttribute().getId() +  ": freshness = " 
					+ quality.getFreshness() + " updateFrequency = " 
					+ quality.getUpdateFrequency());

		if (null == quality.getUpdateFrequency()) {

			isPoorQuality = false;

		} else {

			final double timeBetweenUpdatesMillis = (1.0 / quality.getUpdateFrequency()) * 1000.0;
			if (LOG.isDebugEnabled())
				LOG.debug(quality.getAttribute().getId() 
						+  ": time between updates (in milliseconds) = " + timeBetweenUpdatesMillis);
			isPoorQuality = (double) quality.getFreshness() > timeBetweenUpdatesMillis;
		}
		if (LOG.isDebugEnabled())
			LOG.debug(quality.getAttribute().getId() +  ": is poor quality = " + isPoorQuality);

		return isPoorQuality;
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
	public void inheritContext(CtxAttributeIdentifier arg0,
			CtxAttributeValueType arg1, IIdentity arg2) {


	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier attrID, Date date) {
		
		if (attrID == null) {
			throw new NullPointerException("attribute Id can't be null");
		}
		
		LOG.debug("predictContext: attrId={}, date={}", attrID, date);
		
		CtxAttribute result = null;
		
		try {
			result = this.userPredMgr.predictContext(attrID, date);
			
		} catch (Exception e) {
			LOG.error("Exception on predicting context attribute :"+attrID+".  "+ e.getLocalizedMessage());
			e.printStackTrace();
		} 

		LOG.debug("retrieveFuture: result={}", result);
		return result;
	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier attrID, int arg1) {
		
				
		CtxAttribute result = null;
		Date date = new Date();
		
		if (LOG.isDebugEnabled())LOG.debug("predict context " +date);
		
		try {
			result = this.userPredMgr.predictContext(attrID, date);
			
		} catch (Exception e) {
			LOG.error("Exception on predicting context attribute :"+attrID+".  "+ e.getLocalizedMessage());
			e.printStackTrace();
		} 

		if (LOG.isDebugEnabled())LOG.debug("retrieveFuture: result={}", result);
		return result;
	}

	/*
	 * @see org.societies.context.api.user.inference.IUserCtxInferenceMgr#refineOnDemand(org.societies.api.context.model.CtxAttributeIdentifier)
	 */
	@Override
	public CtxAttribute refineOnDemand(CtxAttributeIdentifier attrId) throws UserCtxInferenceException {

		if (LOG.isDebugEnabled())
			LOG.debug("Refining attribute '" + attrId + "'");
		CtxAttribute refinedAttribute;
		if (CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()) || CtxAttributeTypes.LOCATION_COORDINATES.equals(attrId.getType()))
			refinedAttribute = this.userCtxRefiner.refineOnDemand(attrId);
		else
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "': Unsupported attribute type: " + attrId.getType());
		
		if (refinedAttribute != null)
			try {
				refinedAttribute = (CtxAttribute) this.internalCtxBroker.update(refinedAttribute).get();
			} catch (Exception e) {
				throw new UserCtxInferenceException("Could not update refined attribute in the database:"
					+ e.getLocalizedMessage(), e);
			}

		return refinedAttribute;
	}
	
	/*
	 * @see org.societies.context.api.user.inference.IUserCtxInferenceMgr#refineContinuously(org.societies.api.context.model.CtxAttributeIdentifier, java.lang.Double)
	 */
	@Override
	public void refineContinuously(final CtxAttributeIdentifier attrId, 
			final Double updateFrequency) throws UserCtxInferenceException {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Refining attribute '" + attrId + "'");
		if (CtxAttributeTypes.LOCATION_SYMBOLIC.equals(attrId.getType()))
			this.userCtxRefiner.refineContinuously(attrId, 0d); // TODO handle updateFrequency
		else if(CtxAttributeTypes.LOCATION_COORDINATES.equals(attrId.getType())){
			LOG.debug("todo : refine gps coordinates");
		}
		else 
			throw new UserCtxInferenceException("Could not refine attribute '"
					+ attrId + "' continuously: Unsupported attribute type: " + attrId.getType());
	}

	/*
	 * @see org.societies.context.api.user.inference.IUserCtxInferenceMgr#getInferrableTypes()
	 */
	@Override
	public List<String> getInferrableTypes(){

		if (LOG.isDebugEnabled())
			LOG.debug("getInferrableTypes this.internalCtxBroker "+ this.internalCtxBroker);
		return Collections.unmodifiableList(this.inferrableTypes);
	}
	
	/*
	 * @see org.societies.context.api.user.inference.IUserCtxInferenceMgr#addInferrableType(java.lang.String)
	 */
	@Override
	public void addInferrableType(String attrType){

		if (LOG.isDebugEnabled())
			LOG.debug("Adding '" + attrType + "' to list of inferrable attributes");
		if (!this.inferrableTypes.contains(attrType)) {
			this.inferrableTypes.add(attrType);
		}
	}
	
	/*
	 * @see org.societies.context.api.user.inference.IUserCtxInferenceMgr#removeInferrableType(java.lang.String)
	 */
	@Override
	public void removeInferrableType(String attrType){

		if (LOG.isDebugEnabled())
			LOG.debug("Removing '" + attrType + "' from list of inferrable attributes");
		if (this.inferrableTypes.contains(attrType))
			this.inferrableTypes.remove(attrType);
	}

	@Override
	public CtxAttribute inheritContext(CtxAttributeIdentifier ctxAttrId) {
		
		CtxAttribute attr = null;
		LOG.debug("inference manager inheritContext called for:" + ctxAttrId);
		//TODO add more controls
		attr = this.userCtxInheritance.communityInheritance(ctxAttrId);
		return attr;
	}
}