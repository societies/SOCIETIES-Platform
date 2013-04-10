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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;


public class ContextCacheUpdater /*extends EventListener*/ implements CtxChangeEventListener{

	private ICtxBroker ctxBroker;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private PrivateContextCache contextCache;
	private ArrayList<CtxAttributeIdentifier> attrList;
	public ContextCacheUpdater(ICtxBroker broker, PrivateContextCache cache){
		this.ctxBroker = broker;
		this.contextCache = cache;
		this.attrList = new ArrayList<CtxAttributeIdentifier>();
	}

	public void registerForContextEvent(CtxAttributeIdentifier id){
		if (this.attrList.contains(id)){
			this.logging.debug("Already Registered for context events for : "+id.getType()+" ID: "+id.toUriString());
			return;
		}
		try {
			//this.broker.registerUpdateNotification(this, id);
			
			this.ctxBroker.registerForChanges(this, id);
			this.logging.debug("Registered for context events for : "+id.getType()+" ID: "+id.toUriString());
		} catch (CtxException e) {
			this.logging.debug("Unable to register for context events for : "+id.getType()+" ID: "+id.toUriString());
			e.printStackTrace();
		}
		
	}
/*	@Override
	public void handlePSSEvent(PSSEvent arg0) {
		// nothing to do. Context Events are only sent as Peer Events
		
	}

	@Override
	public void handlePeerEvent(PeerEvent event) {
		try{
			this.logging.debug("Peer Event Received " + event.geteventType());
			if (event.geteventType().equals(PSSEventTypes.CONTEXT_UPDATE_EVENT)){
				ICtxAttribute ctxAttr = (ICtxAttribute) event.geteventInfo();
				String type = ctxAttr.getType();
				String value = ctxAttr.getStringValue();
				ICtxIdentifier id = ctxAttr.getCtxIdentifier();

				this.logging.debug("Event received: type: "+type+" value: "+value);
				this.contextCache.updateCache(ctxAttr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}*/

	@Override
	public void onCreation(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModification(CtxChangeEvent event) {
		CtxIdentifier ctxId = event.getId();
		try {
			CtxAttribute ctxAttr = (CtxAttribute) ctxBroker.retrieve(ctxId).get();
			if (ctxAttr!=null){
				String type = ctxAttr.getType();
				String value = ctxAttr.getStringValue();
				this.logging.debug("Event received: type: "+type+" value: "+value);
				
				this.contextCache.updateCache(ctxAttr);
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
		}
		
		
		
	}

	@Override
	public void onRemoval(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(CtxChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
