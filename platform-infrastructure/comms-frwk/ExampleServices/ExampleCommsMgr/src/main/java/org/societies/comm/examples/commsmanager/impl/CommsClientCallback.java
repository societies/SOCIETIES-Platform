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

import java.util.List;
import java.util.concurrent.Future;

import org.societies.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.datatypes.XMPPError;
import org.societies.comm.xmpp.datatypes.XMPPInfo;
import org.societies.comm.xmpp.datatypes.XMPPNode;
import org.societies.comm.xmpp.interfaces.CommCallback;
import org.societies.example.calculatorservice.schema.CalcBeanResult;
import org.societies.example.fortunecookieservice.schema.FortuneCookieBeanResult;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class CommsClientCallback implements CommCallback{

	private Future<?>returnObj;
	private int returnInt=0;
	
	/** @return the returnInt  */
	public int getReturnInt() {
		return returnInt;
	}

	/** @param returnInt the returnInt to set */
	public void setReturnInt(int returnInt) {
		this.returnInt = returnInt;
	}

	public CommsClientCallback(Future<?> returnObj) {
		this.returnObj = returnObj;
	}

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
			//this.returnObj = new AsyncResult<Integer>(calcResult.getResult());
			this.setReturnInt(calcResult.getResult() );
		}
		
		// -------- FORTUNE COOKIE BUNDLE ---------
		else if (msgBean.getClass().equals(FortuneCookieBeanResult.class)) {
			FortuneCookieBeanResult fcBeanResult = (FortuneCookieBeanResult) msgBean;
			//return the fcBeanResult to the calling client
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveError(org.societies.comm.xmpp.datatypes.Stanza, org.societies.comm.xmpp.datatypes.XMPPError)
	 */
	@Override
	public void receiveError(Stanza returnStanza, XMPPError info) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveInfo(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza returnStanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveItems(org.societies.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza returnStanza, String node, List<XMPPNode> info) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.CommCallback#receiveMessage(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza returnStanza, Object messageBean) {
		// TODO Auto-generated method stub
		
	}
	

}
