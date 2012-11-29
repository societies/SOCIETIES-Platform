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
package org.societies.context.user.db.impl.event.hibernate;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.context.api.event.CtxChangeEventTopic;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.user.db.impl.model.CtxModelObjectDAO;
import org.springframework.beans.factory.annotation.Autowired;

import cern.colt.Arrays;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public class PostInsertUserCtxEventListener implements PostInsertEventListener {

	private static final long serialVersionUID = -4959294918762372873L;

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(PostInsertUserCtxEventListener.class);
	
	private static final String[] EVENT_TOPICS = new String[] { CtxChangeEventTopic.CREATED };
	
	private static final CtxEventScope EVENT_SCOPE = CtxEventScope.BROADCAST;
	
	/** The Context Event Mgmt service reference. */
	@Autowired(required=true)
	private ICtxEventMgr ctxEventMgr;
	
	PostInsertUserCtxEventListener() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.hibernate.event.PostInsertEventListener#onPostInsert(org.hibernate.event.PostInsertEvent)
	 */
	@Override
	public void onPostInsert(PostInsertEvent event) {
		
		if (LOG.isTraceEnabled())
			LOG.trace("Received PostInsertEvent for context hibernate entity " + event.getId());
		
		// Ignore events for non CtxModelObjects
		if (!(event.getEntity() instanceof CtxModelObjectDAO))
			return;
		
		final CtxIdentifier ctxId = ((CtxModelObjectDAO) event.getEntity()).getId();
		if (this.ctxEventMgr != null) {
			try { 
				if (LOG.isDebugEnabled())
					LOG.debug("Sending context change event for '" + ctxId
							+ "' to topics '" + Arrays.toString(EVENT_TOPICS) 
							+ "' with scope '" + EVENT_SCOPE + "'");
				this.ctxEventMgr.post(new CtxChangeEvent(ctxId), EVENT_TOPICS, EVENT_SCOPE);
			} catch (Exception e) {
				
				LOG.error("Could not send context change event for '" + ctxId 
						+ "' to topics '" + Arrays.toString(EVENT_TOPICS) 
						+ "' with scope '" + EVENT_SCOPE + "': "
						+ e.getLocalizedMessage(), e);
			}
		} else {
			LOG.error("Could not send context change event for '" + ctxId
					+ "' to topics '" + Arrays.toString(EVENT_TOPICS) 
					+ "' with scope '" + EVENT_SCOPE + "': "
					+ "ICtxEventMgr service is not available");
		}
	}
}