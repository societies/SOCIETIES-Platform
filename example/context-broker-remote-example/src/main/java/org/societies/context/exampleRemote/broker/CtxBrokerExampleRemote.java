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
package org.societies.context.exampleRemote.broker;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.context.exampleRemote.broker.IContextAware3pService;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxBrokerExampleRemote 	{
	
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerExampleRemote.class);
	private ICtxBroker ctxBroker;
	private ICommManager commMgrService;
	private IIdentity targetCSSiD; 
	private IContextAware3pService ca3pService;
	
	@Autowired(required=true)
	public CtxBrokerExampleRemote(ICtxBroker ctxBroker, ICommManager commMgr,IContextAware3pService ca3pService) throws InvalidFormatException {
		
		this.ca3pService = ca3pService;
		this.ctxBroker = ctxBroker;
		LOG.info("*** CtxBrokerExampleRemote instantiation broker service: "+this.ctxBroker);
						
		this.commMgrService = commMgr;

		CtxEntity remoteEntity = this.ca3pService.createRemoteCtxEntity("remoteEntityType");
		LOG.info("*** remoteEntity id : "+ remoteEntity.getId());

		CtxAttribute remoteAttribute = this.ca3pService.createRemoteCtxAttribute(remoteEntity.getId(), CtxAttributeTypes.ADDRESS_HOME_CITY);
		LOG.info("*** remoteAttribute id : "+ remoteAttribute.getId());		
		
		//this.ca3pService.registerForContextUpdates(remoteAttribute.getId(), new MyCtxChangeEventListener());

		remoteAttribute.setStringValue("CarnabyStreet12");
		
		LOG.info("*** perform updated ");
		CtxAttribute updatedAttr = (CtxAttribute) this.ca3pService.updateCtxModelObject(remoteAttribute);
		LOG.info("*** updated remoteAttribute id : "+ updatedAttr.getId());	
		LOG.info("*** updated remoteAttribute value : "+ updatedAttr.getStringValue());
		
		List<CtxIdentifier> lookupResults = this.ca3pService.lookupRemoteCtxAttribute(CtxAttributeTypes.ADDRESS_HOME_CITY);
		
		LOG.info("remote lookup results size "+ lookupResults.size());

		for(CtxIdentifier id : lookupResults ){
			LOG.info("remote lookup results id "+ id);
			LOG.info("retrieve object based on id"+ id);
			CtxAttribute ctxAttrRetrieved = this.ca3pService.retrieveCtxObject(id);
			LOG.info("retrieved object id"+ ctxAttrRetrieved.getId());
			LOG.info("retrieved object id"+ ctxAttrRetrieved.getStringValue());
		}
	
		
	}
	
	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		@Override
		public void onCreation(CtxChangeEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpdate(CtxChangeEvent event) {
			LOG.info("*** updated event received : "+ event.toString());
			
		}

		@Override
		public void onModification(CtxChangeEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRemoval(CtxChangeEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
}