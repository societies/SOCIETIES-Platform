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
package org.societies.integration.test.bit.asyncmessage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.test.Testnode;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class ServerCallback implements IFeatureServer {

	private static final String MESSAGE_1_REPLY = "Message Reply Test774";
	private static final String INFOQUERY_1_REPLY = "Info Query Reply Test774";
	private static Logger LOG = LoggerFactory.getLogger(ServerCallback.class);
	private Testnode messageObject = null;
	private String messageReceived = null;
	
	/**@return the messageReceived */
	public String getMessageReceived() {
		return messageReceived;
	}

	/**@return the Message Object */
	public Testnode getMessageObject() {
		return messageObject;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() {
		//ADD LIST OF PACKAGES TO ADD SCHEMA OBJECTS
		List<String> packageList = new ArrayList<String>();
		packageList.add("org.societies.test");
		return packageList;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() {
		List<String> nsList = new ArrayList<String>();
		nsList.add("http://societies.org/test");
		return nsList;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object) */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		LOG.info("### Info Query Received from: " + stanza.getFrom().getJid());
		//CHECK WHAT PAYLOAD IS
		if (payload.getClass().equals(Testnode.class)) {
			messageObject = (Testnode)payload;
			messageReceived = messageObject.getTestattribute();
			synchronized (this) {
	            notifyAll( );
	        }
			//USE SAME OBJECT FOR REPLY - CHANGE THE MESSAGE WITHIN
			messageObject.setTestattribute(INFOQUERY_1_REPLY);
		}
		return messageObject;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object) */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		LOG.info("### Message Received from: " + stanza.getFrom().getJid());
		//CHECK WHAT PAYLOAD IS
		if (payload.getClass().equals(Testnode.class)) {
			messageObject = (Testnode)payload;
			messageReceived = messageObject.getTestattribute();
			synchronized (this) {
	            notifyAll( );
	        }
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object) */
	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		return null;
	}
}
