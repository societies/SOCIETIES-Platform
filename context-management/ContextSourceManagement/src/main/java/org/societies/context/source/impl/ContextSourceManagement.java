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
package org.societies.context.source.impl;

import java.io.Serializable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.context.source.ICtxSourceMgrCallback;
import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;
import org.societies.context.api.user.db.IUserCtxDBMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContextSourceManagement implements ICtxSourceMgr{

	private static Logger LOG = LoggerFactory.getLogger(ContextSourceManagement.class);
	/**
	 * The User Context DB Mgmt service reference.
	 * 
	 * @see {@link #setUserCtxDBMgr(IUserCtxDBMgr)}
	 */
	@Autowired(required=true)
	private IUserCtxDBMgr userCtxDBMgr = null;

	/**
	 * The Context Broker service reference.
	 * 
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	@Autowired(required=true)
	private ICtxBroker ctxBroker = null;
	
	/**
	 * The Device Manager service reference
	 * 
	 * @see {@link #setDeviceManager(IDeviceManager)}
	 */
	@Autowired(required=true)
	private IDeviceManager deviceManager;
	
	/**
	 * Sets the Device Manager service reference.
	 * 
	 * @param deviceManager
	 *            the Device Manager service reference to set.
	 */
	public void setDeviceManager(IDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	private NewDeviceListener newDeviceListener;

	/**
	 * Sets the User Context DB Mgmt service reference.
	 * 
	 * @param userDB
	 *            the User Context DB Mgmt service reference to set.
	 */
	public void setUserCtxDBMgr(IUserCtxDBMgr userDB) {
		this.userCtxDBMgr = userDB;
	}

	/**
	 * Sets the Context Broker service reference
	 * 
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	
	public ContextSourceManagement(){
		this.newDeviceListener = new NewDeviceListener(deviceManager);
		newDeviceListener.run();
		LOG.info("{}", "CSM started");
		
	}
	
	@Override
	public void register(String arg0, String arg1, ICtxSourceMgrCallback arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public Future<String> registerFuture(String arg0, String arg1, ICtxSourceMgrCallback arg2) {
		return null;
		
	}

	@Override
	public void sendUpdate(String arg0, Serializable arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendUpdate(String arg0, Serializable arg1, CtxEntity arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregister(String arg0) {
		// TODO Auto-generated method stub
		
	}

}
