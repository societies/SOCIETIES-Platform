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
import org.societies.api.comm.xmpp.interfaces.ICommManager;
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
 * Context event subscription/publishing example for CM components
 */
@Service
public class CtxEventExample {

	private static final Logger LOG = LoggerFactory.getLogger(CtxEventExample.class);
	
	private static final String ENTITY_TYPE = "person";
	private static final long SCOPE_OBJECT_NUMBER = 100l;
	
	private static final String ATTRIBUTE_TYPE = "name";
	private static final long ATTRIBUTE_OBJECT_NUMBER = 200l;
	
	private static final String ATTRIBUTE_TYPE2 = "location";
	private static final long ATTRIBUTE_OBJECT_NUMBER2 = 300l;
	
	private CtxAttributeIdentifier ctxAttrId;
	private CtxAttributeIdentifier ctxAttrId2;
	
	private ICtxEventMgr ctxEventMgr;
	
	@Autowired(required=true)
	public CtxEventExample(ICtxEventMgr ctxEventMgr, ICommManager commMgr)
		throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info("*** " + this.getClass() + " instantiated");
		this.ctxEventMgr = ctxEventMgr;
		final String localIdentity = commMgr.getIdManager().getThisNetworkNode().getBareJid();
		final CtxEntityIdentifier scope = new CtxEntityIdentifier(localIdentity, ENTITY_TYPE, SCOPE_OBJECT_NUMBER);
		this.ctxAttrId = new CtxAttributeIdentifier(scope, ATTRIBUTE_TYPE, ATTRIBUTE_OBJECT_NUMBER);
		this.ctxAttrId2 = new CtxAttributeIdentifier(scope, ATTRIBUTE_TYPE2, ATTRIBUTE_OBJECT_NUMBER2);
		this.registerListenerByCtxId();
		this.registerListenerByScope();
		this.registerListenerByScopeAttrType();
		this.sendLocalEvent();
		this.sendLocalEvent2();
		this.sendRemoteEvent();
		this.sendRemoteEvent2();
		this.sendBroadcastEvent();
		this.sendBroadcastEvent2();
	}

	private void registerListenerByCtxId() {
		
		LOG.info("*** registerListenerByCtxId");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not register context event listener: CtxEventMgr is not available");
			return;
		}
		
		try {
			this.ctxEventMgr.registerChangeListener(new MyCtxChangeEventListener("registerListenerByCtxId"),
					new String[] {CtxChangeEventTopic.UPDATED}, this.ctxAttrId);
		} catch (CtxException ce) {
			LOG.error("*** Could not register context event listener", ce);
		}
	}
	
	private void registerListenerByScope() {
		
		LOG.info("*** registerListenerByScope");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not register context event listener: CtxEventMgr is not available");
			return;
		}
		
		final CtxEntityIdentifier scope = this.ctxAttrId.getScope();
		try {
			this.ctxEventMgr.registerChangeListener(new MyCtxChangeEventListener("registerListenerByScope"), 
					new String[] {CtxChangeEventTopic.UPDATED}, scope, null);
		} catch (CtxException ce) {
			LOG.error("*** Could not register context event listener", ce);
		}
	}
	
	private void registerListenerByScopeAttrType() {
		
		LOG.info("*** registerListenerByScopeAttrType");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not register context event listener: CtxEventMgr is not available");
			return;
		}
		
		final CtxEntityIdentifier scope = this.ctxAttrId.getScope();
		final String attrType = ATTRIBUTE_TYPE;
		try {
			this.ctxEventMgr.registerChangeListener(new MyCtxChangeEventListener("registerListenerByScopeAttrType"), 
					new String[] {CtxChangeEventTopic.UPDATED}, scope, attrType);
		} catch (CtxException ce) {
			LOG.error("Could not register context event listener", ce);
		}
	}
	
	private void sendLocalEvent() {
		
		LOG.info("*** sendLocalEvent");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not send context event: CtxEventMgr is not available");
			return;
		}

		final CtxChangeEvent event = new CtxChangeEvent(this.ctxAttrId);
		try {
			Thread.sleep(1000l);
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, CtxEventScope.LOCAL);
		} catch (Exception e) {
			LOG.error("*** Could not send event: " + event + ": " 
					+ e.getLocalizedMessage(), e);
		}
	}
	
	private void sendLocalEvent2() {
		
		LOG.info("*** sendLocalEvent2");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not send context event: CtxEventMgr is not available");
			return;
		}
		
		final CtxChangeEvent event = new CtxChangeEvent(this.ctxAttrId2);
		try {
			Thread.sleep(1000l);
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, CtxEventScope.LOCAL);

		} catch (Exception e) {
			LOG.error("*** Could not send event: " + event + ": " 
					+ e.getLocalizedMessage(), e);
		}
	}
	
	private void sendRemoteEvent() {
		
		LOG.info("*** sendRemoteEvent");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not send context event: CtxEventMgr is not available");
			return;
		}

		final CtxChangeEvent event = new CtxChangeEvent(this.ctxAttrId);
		try {
			Thread.sleep(1000l);
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, 
					CtxEventScope.INTER_CSS);
		} catch (Exception e) {
			LOG.error("*** Could not send event: " + event + ": " 
					+ e.getLocalizedMessage(), e);
		}
	}
	
	private void sendRemoteEvent2() {
		
		LOG.info("*** sendRemoteEvent2");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not send context event: CtxEventMgr is not available");
			return;
		}
		
		final CtxChangeEvent event = new CtxChangeEvent(this.ctxAttrId2);
		try {
			Thread.sleep(1000l);
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, 
					CtxEventScope.INTER_CSS);
		} catch (Exception e) {
			LOG.error("*** Could not send event: " + event + ": " 
					+ e.getLocalizedMessage(), e);
		}
	}
	
	private void sendBroadcastEvent() {
		
		LOG.info("*** sendBroadcastEvent");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not send context event: CtxEventMgr is not available");
			return;
		}

		final CtxChangeEvent event = new CtxChangeEvent(this.ctxAttrId);
		try {
			Thread.sleep(1000l);
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, 
					CtxEventScope.BROADCAST);
		} catch (Exception e) {
			LOG.error("*** Could not send event: " + event + ": " 
					+ e.getLocalizedMessage(), e);
		}
	}
	
	private void sendBroadcastEvent2() {
		
		LOG.info("*** sendBroadcastEvent2");
		if (this.ctxEventMgr == null) {
			LOG.error("*** Could not send context event: CtxEventMgr is not available");
			return;
		}
		
		final CtxChangeEvent event = new CtxChangeEvent(this.ctxAttrId2);
		try {
			Thread.sleep(1000l);
			this.ctxEventMgr.post(event, new String[] {CtxChangeEventTopic.UPDATED}, 
					CtxEventScope.BROADCAST);
		} catch (Exception e) {
			LOG.error("*** Could not send event: " + event + ": " 
					+ e.getLocalizedMessage(), e);
		}
	}
	
	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		private final String name;
		
		private MyCtxChangeEventListener(final String name) {
			
			this.name = name;
		}
		
		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {
			
			LOG.info("*** " + this.name + ": Context CREATED event: " + event.getId());
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {
			
			LOG.info("*** " + this.name + ": Context UPDATED event: " + event.getId());
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {
			
			LOG.info("*** " + this.name + ": Context MODIFIED event: " + event.getId());
		}

		/*
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {
			
			LOG.info("*** " + this.name + ": Context REMOVED event: " + event.getId());
		}
	}
}
