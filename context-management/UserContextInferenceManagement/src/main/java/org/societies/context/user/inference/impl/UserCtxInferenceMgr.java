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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

@Service
public class UserCtxInferenceMgr implements IUserCtxInferenceMgr {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(UserCtxInferenceMgr.class);

	IndividualCtxEntity ownerEntity;
	List<String> inferableTypesList = new ArrayList<String>();
	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;


	private ICtxBroker internalCtxBroker;
	ICommManager commMgr;

	UserCtxInferenceMgr(){
		
	}
	
	@Autowired(required=true)
	UserCtxInferenceMgr(ICtxBroker internalCtxBroker, ICommManager commMgr){

		this.internalCtxBroker = internalCtxBroker;
		LOG.info(this.getClass() + "internalCtxBroker instantiated "+ this.internalCtxBroker);

		this.commMgr = commMgr;
		LOG.info(this.getClass() + "commMgr instantiated " +this.commMgr);

		this.assignInfAttributeTypes();
		LOG.info(this.getClass() + " instantiated broker " +internalCtxBroker);	
	}


	private void assignInfAttributeTypes(){
		
		LOG.info ("inside assignInfAttributeTypes type: " + this.getInferrableTypes());
		inferableTypesList.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		inferableTypesList.add(CtxAttributeTypes.LOCATION_COORDINATES);
		inferableTypesList.add(CtxAttributeTypes.STATUS);
		inferableTypesList.add(CtxAttributeTypes.TEMPERATURE);
		this.setInferrableTypes(inferableTypesList);
		
		LOG.info ("getInferrableTypes " + this.getInferrableTypes());
		try {
			this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
			LOG.debug("*** cssNodeId = " + this.cssNodeId);

			final String cssOwnerStr = this.cssNodeId.getBareJid();
			this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
			LOG.debug("*** cssOwnerId = " + this.cssOwnerId);

			this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
			LOG.debug("*** cssOwnerId = " + this.cssOwnerId);

			this.ownerEntity = this.internalCtxBroker.retrieveIndividualEntity(cssOwnerId).get();
			List<String> infTypesList = this.getInferrableTypes();
			
			for(String inferableType: infTypesList){
				
				LOG.debug("now checking inf type 1 "+inferableType+" size: " +this.ownerEntity.getAttributes(inferableType).size());
				LOG.debug("get attributes2  : "+this.ownerEntity.getAttributes());
				LOG.debug("get attributes3 : "+this.ownerEntity.getAttributes(inferableType));
				
				if (this.ownerEntity.getAttributes(inferableType).size() == 0) {
					CtxAttribute ctxAttr = this.internalCtxBroker.createAttribute(this.ownerEntity.getId(), inferableType).get();
					LOG.debug("inf Attr created "+ ctxAttr.getId());
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void checkQuality(CtxModelObject arg0) {
		// TODO Auto-generated method stub

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
		CtxAttribute ctxAttribute = null;
		LOG.debug("predict context " +date);
		LOG.debug("no value predict context " +ctxAttribute);
		try {
			ctxAttribute = this.internalCtxBroker.retrieveAttribute(attrID, false).get();
			ctxAttribute.setStringValue("HOME");
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.debug("with value predict context return " +ctxAttribute);
		return ctxAttribute;
	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier arg0, int arg1) {
		CtxAttribute ctxAttribute = null;

		return ctxAttribute;
	}

	@Override
	public CtxAttribute refineContext(CtxAttributeIdentifier arg0) {
		CtxAttribute ctxAttribute = null;

		return ctxAttribute;
	}

	@Override
	public void setInferrableTypes(List<String> inferableTypes){

		LOG.debug("setInferrableTypes this.internalCtxBroker "+ this.internalCtxBroker);
		this.inferableTypesList = inferableTypes;
	}

	@Override
	public List<String> getInferrableTypes(){

		LOG.debug("getInferrableTypes this.internalCtxBroker "+ this.internalCtxBroker);
		return this.inferableTypesList;
	}
}