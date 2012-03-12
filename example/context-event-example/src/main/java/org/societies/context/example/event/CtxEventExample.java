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
package org.societies.context.example.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Context event subscription/publishing example 
 */
@Service
public class CtxEventExample {

	private static final Logger LOG = LoggerFactory.getLogger(CtxEventExample.class);
	
	private ICtxEventMgr ctxEventMgr;
	
	@Autowired(required=true)
	public CtxEventExample(ICtxEventMgr ctxEventMgr) {
		LOG.info("CtxEventExample instantiated");
		this.ctxEventMgr = ctxEventMgr;
		this.registerListenerByCtxId();
		this.registerListenerByScopeAttrType();
		this.sendEvent();
	}

	private void registerListenerByCtxId() {
		
		LOG.info("registerListenerByCtxId");
		if (this.ctxEventMgr == null) {
			LOG.error("Could not register context event listener: CtxEventMgr is not available");
			return;
		}
		
		final CtxEntityIdentifier scope = new CtxEntityIdentifier(null, "person", 1l);
		final CtxAttributeIdentifier id = new CtxAttributeIdentifier(scope, "location", 2l);
		try {
			this.ctxEventMgr.registerChangeListener(new MyCtxChangeEventListener(), new String[] {CtxChangeEventTopic.UPDATED}, id);
		} catch (CtxException ce) {
			LOG.error("Could not register context event listener", ce);
		}
	}
	
	private void registerListenerByScopeAttrType() {
		
		LOG.info("registerListenerByScopeAttrType");
		if (this.ctxEventMgr == null) {
			LOG.error("Could not register context event listener: CtxEventMgr is not available");
			return;
		}
		
		final CtxEntityIdentifier scope = new CtxEntityIdentifier(null, "person", 1l);
		final String attrType = "location";
		try {
			this.ctxEventMgr.registerChangeListener(new MyCtxChangeEventListener(), new String[] {CtxChangeEventTopic.UPDATED}, scope, attrType);
		} catch (CtxException ce) {
			LOG.error("Could not register context event listener", ce);
		}
	}
	
	private void sendEvent() {
		
		LOG.info("sendEvent");
		if (this.ctxEventMgr == null) {
			LOG.error("Could not send context event: CtxEventMgr is not available");
			return;
		}
		
		final CtxEntityIdentifier scope = new CtxEntityIdentifier(null, "person", 1l);
		final CtxAttributeIdentifier id = new CtxAttributeIdentifier(scope, "location", 2l);
		final CtxChangeEvent event = new CtxChangeEvent(id);
		try {
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, CtxEventScope.LOCAL);
		} catch (CtxException ce) {
			LOG.error("Could not send event: " + event, ce);
		}
	}
	
	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {
			
			LOG.info("Context CREATED event: " + event.getId());
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			
			LOG.info("Context UPDATED event: " + event.getId());
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {
			
			LOG.info("Context MODIFIED event: " + event.getId());
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {
			
			LOG.info("Context REMOVED event: " + event.getId());
		}
	}
}