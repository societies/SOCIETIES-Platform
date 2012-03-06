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
package org.societies.context.event.impl;

import org.osgi.service.event.EventAdmin;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.context.api.event.CtxEventScope;
import org.societies.context.api.event.ICtxEventMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ICtxEventMgr} interface.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.4
 */
@Service("ctxEventMgr")
public final class CtxEventMgr implements ICtxEventMgr {

	@Autowired(required=true)
	private EventAdmin eventAdmin;
	
	/* (non-Javadoc)
	 * @see org.societies.context.api.event.ICtxEventMgr#registerListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void registerListener(CtxChangeEventListener listener,
			String[] topics, CtxIdentifier ctxId) throws CtxException {
	}

	/* (non-Javadoc)
	 * @see org.societies.context.api.event.ICtxEventMgr#unregisterListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxIdentifier)
	 */
	@Override
	public void unregisterListener(CtxChangeEventListener listener,
			String[] topics, CtxIdentifier ctxId) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.context.api.event.ICtxEventMgr#registerListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void registerListener(CtxChangeEventListener listener,
			String[] topics, CtxEntityIdentifier scope, String attrType)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.context.api.event.ICtxEventMgr#unregisterListener(org.societies.api.context.event.CtxChangeEventListener, java.lang.String[], org.societies.api.context.model.CtxEntityIdentifier, java.lang.String)
	 */
	@Override
	public void unregisterListener(CtxChangeEventListener listener,
			String[] topics, CtxEntityIdentifier scope, String attrType)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.context.api.event.ICtxEventMgr#publish(org.societies.api.context.event.CtxChangeEvent, java.lang.String[], org.societies.context.api.event.CtxEventScope)
	 */
	@Override
	public void publish(CtxChangeEvent event, String[] topics,
			CtxEventScope scope) throws CtxException {
		// TODO Auto-generated method stub
		
	}
}