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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
class CtxChangeEventDispatcher implements Runnable {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxChangeEventDispatcher.class);
	
	private final ICtxEventMgr ctxEventMgr;
	
	private final CtxChangeEvent ctxChangeEvent;
	
	private final String[] topics; 
	
	private final CtxEventScope scope;
	
	CtxChangeEventDispatcher(final ICtxEventMgr ctxEventMgr, 
			final CtxChangeEvent ctxChangeEvent, final String[] topics, 
			final CtxEventScope scope) {
		
		this.ctxEventMgr = ctxEventMgr;
		this.ctxChangeEvent = ctxChangeEvent;
		this.topics = topics;
		this.scope = scope;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	
		if (this.ctxEventMgr != null) {
			try {
				if (LOG.isDebugEnabled())
					LOG.debug("Sending context change event " + this.ctxChangeEvent
							+ " to topics '" + Arrays.toString(this.topics) 
							+ "' with scope '" + this.scope + "'");
				this.ctxEventMgr.post(this.ctxChangeEvent, this.topics, this.scope);
			} catch (Exception e) {
				
				LOG.error("Could not send context change event " + this.ctxChangeEvent 
						+ " to topics '" + Arrays.toString(this.topics) 
						+ "' with scope '" + this.scope + "': "
						+ e.getLocalizedMessage(), e);
			}
		} else {
			LOG.error("Could not send context change event '" + this.ctxChangeEvent
					+ " to topics '" + Arrays.toString(this.topics) 
					+ "' with scope '" + this.scope + "': "
					+ "ICtxEventMgr service is not available");
		}
	}
}