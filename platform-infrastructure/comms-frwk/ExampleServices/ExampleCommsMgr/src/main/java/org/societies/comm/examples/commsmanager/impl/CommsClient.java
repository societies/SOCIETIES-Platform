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
package org.societies.comm.examples.commsmanager.impl;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.examples.commsmanager.ICalcRemote;
import org.societies.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.datatypes.Identity.IdentityType;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommManager;
import org.societies.example.calculatorservice.schema.CalcBean;
import org.societies.example.calculatorservice.schema.MethodType;
import org.springframework.scheduling.annotation.Async;

/**
 * Comms Client that initiates the remote communication
 *
 * @author aleckey
 *
 */
public class CommsClient implements ICalcRemote{
	//PRIVATE VARIABLES
	private CommManager commManager;
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);
	
	//PROPERTIES
	public CommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(CommManager commManager) {
		this.commManager = commManager;
	}

	public CommsClient() {}

	/* (non-Javadoc)
	 * @see org.societies.comm.examples.commsmanager.ICalcRemote#AddAsync(int, int)
	 */
	@Override
	@Async
	public Future<Integer> AddAsync(int valA, int valB) {
		Identity id = new Identity(IdentityType.CSS, "XCManager", "red.local"); 
		Stanza stanza = new Stanza(id);

		//SETUP RETURN STUFF
		Future<Integer> returnObj = null;
		CommsClientCallback callback = new CommsClientCallback(returnObj);
		
		CalcBean calc = new CalcBean();
		calc.setA(valA); 
		calc.setB(valB);
		calc.setMethod(MethodType.ADD_ASYNC);
		try {
			commManager.sendIQGet(stanza, calc, callback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		return returnObj;
	}
	
	public int Add(int valA, int valB) {
		Identity id = new Identity(IdentityType.CSS, "XCManager", "red.local"); 
		Stanza stanza = new Stanza(id);

		//SETUP RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(null);
		
		CalcBean calc = new CalcBean();
		calc.setA(valA); 
		calc.setB(valB);
		calc.setMethod(MethodType.ADD_ASYNC);
		try {
			commManager.sendIQGet(stanza, calc, callback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		return callback.getReturnInt();
	}

	public int Subtract(int valA, int valB) {
		return 0;
	}

}
