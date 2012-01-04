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
package org.societies.context.brokerTest.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;

import org.societies.api.mock.EntityIdentifier;

//import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

/**
 * 3p Context Broker Implementation
 * This class implements the community and user broker api methods along with the callback methods 
 * of the internal context broker api
 */
public class CtxBrokerTest 	{

	
	
	private IUserCtxBroker ctxBroker;
	//private IUserCtxBrokerCallback userCtxBrokerCallback;
	
	
	public CtxBrokerTest() {
		System.out.println(this.getClass().getName()+" empty");
		
		if (this.ctxBroker==null){
			System.out.println(this.getClass().getName()+"CtxBroker is null");
		}else{
			System.out.println(this.getClass().getName()+"CtxBroker is NOT null");
		}
		//startTesting();
	}
	

	
	public CtxBrokerTest(IUserCtxBroker userCtxBroker) {
		this.ctxBroker=userCtxBroker;
		System.out.println(this.getClass().getName()+" full");
	}
	
	
	public void setCtxBroker(IUserCtxBroker broker){
		this.ctxBroker = broker;
	}
	
	public IUserCtxBroker getCtxBroker(){
		System.out.println(this.getClass().getName()+"Return CtxBroker");
		return this.ctxBroker;
	}
	/*
	public void setUserCtxBrokerCallback(IUserCtxBrokerCallback userCtxBrokerCallback) {
		this.userCtxBrokerCallback = userCtxBrokerCallback;
	}
	*/
	
	
	private void startTesting(){
		System.out.println(this.getClass().getName()+" startTesting");
		
		
		
		
		this.ctxBroker.createEntity("person", new BrokerCallback(this));
	
	}
	
	
	
}
