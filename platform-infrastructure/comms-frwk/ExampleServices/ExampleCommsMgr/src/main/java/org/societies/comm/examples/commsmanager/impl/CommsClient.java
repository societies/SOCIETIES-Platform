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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.examples.calculatorbean.CalcBean;
import org.societies.comm.examples.calculatorbean.CalcBeanResult;
import org.societies.comm.examples.calculatorbean.OperationType;
import org.societies.comm.examples.commsmanager.IExampleCommsManager;
import org.societies.comm.examples.fortunecookie.datatypes.Cookie;
import org.societies.comm.examples.fortunecookiebean.FortuneCookieBean;
import org.societies.comm.examples.fortunecookiebean.FortuneCookieBeanResult;
import org.societies.comm.examples.fortunecookiebean.MethodName;
import org.societies.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.datatypes.Identity.IdentityType;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommCallback;
import org.societies.comm.xmpp.interfaces.CommManager;

/**
 * Comms Client that initiates the remote communication
 *
 * @author aleckey
 *
 */
public class CommsClient implements CommCallback, IExampleCommsManager{
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
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveResult(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza returnStanza, Object msgBean) {
		Identity endUser = returnStanza.getTo();
		
		//CHECK WHICH END SERVICE IS SENDING US A MESSAGE
		// --------- CALCULATOR BUNDLE ---------
		if (msgBean.getClass().equals(CalcBeanResult.class)) {
			CalcBeanResult calcResult = (CalcBeanResult) msgBean;
			//return the calcResult to the calling client
		}
		
		// -------- FORTUNE COOKIE BUNDLE ---------
		else if (msgBean.getClass().equals(FortuneCookieBeanResult.class)) {
			FortuneCookieBeanResult fcBeanResult = (FortuneCookieBeanResult) msgBean;
			//return the fcBeanResult to the calling client
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveError(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	public void Add(int valA, int valB) {
		Identity id = new Identity(IdentityType.CSS, "alec", "red.local"); 
		Stanza stanza = new Stanza(id);

		CalcBean calc = new CalcBean();
		calc.setA(valA); calc.setB(valB);
		calc.setOperation(OperationType.ADD);
		try {
			commManager.sendIQGet(stanza, calc, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
	}

	public void Subtract(int valA, int valB) {
		Identity id = new Identity(IdentityType.CSS, "alec", "red.local"); 
		Stanza stanza = new Stanza(id);

		CalcBean calc = new CalcBean();
		calc.setA(valA); calc.setB(valB);
		calc.setOperation(OperationType.SUBTRACT);
		try {
			commManager.sendIQGet(stanza, calc, this);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
	}

	public void getCookie() {
	}
}
