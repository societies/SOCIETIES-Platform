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
package org.societies.rfid.client.comm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.rfid.schema.server.RfidServerBean;
import org.societies.api.rfid.schema.server.RfidServerMethodType;
import org.societies.rfid.server.api.remote.IRfidServer;




/**
 * Comms Client that initiates the remote communication
 *
 * @author elizap
 *
 */
public class CommsClient implements IRfidServer, ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/rfid/schema/server"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
		  Arrays.asList("org.societies.api.rfid.schema.server"));
	//PRIVATE VARIABLES
	private ICommManager commManager;
	private static Logger logging = LoggerFactory.getLogger(CommsClient.class);
	private IIdentityManager idMgr;
	
	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public CommsClient() {	}

	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commManager.getIdManager();
	}
	
	
	

	/*
	 * (non-Javadoc)
	 * @see ac.hw.rfid.server.api.remote.IRfidServer#registerRFIDTag(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerRFIDTag(String serverIdentity, String tagNumber,
			String userIdentity, String serviceID, String password) {
		if(logging.isDebugEnabled()) logging.debug("Sending message - registerRFIDTag");
		try{
		IIdentity toIdentity = idMgr.fromJid(serverIdentity);
		
		RfidServerBean serverBean = new RfidServerBean();
		serverBean.setMethod(RfidServerMethodType.REGISTER_RFID_TAG);
		serverBean.setIdentity(userIdentity);
		serverBean.setTagNumber(tagNumber);
		serverBean.setServiceID(serviceID);
		serverBean.setPassword(password);
		
		Stanza stanza = new Stanza(toIdentity);
		
		commManager.sendMessage(stanza, serverBean);
		if(logging.isDebugEnabled()) logging.debug("Sent message - registerRFIDTag");
		} catch (InvalidFormatException e) {
			if(logging.isDebugEnabled()) logging.debug("Error sending message - registerRFIDTag");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			if(logging.isDebugEnabled()) logging.debug("Error sending message - registerRFIDTag");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void ackDeleteTag(String serverJid, String tag) {
		if(logging.isDebugEnabled()) logging.debug("Sending message - deleteTag");
		try{
		IIdentity toIdentity = idMgr.fromJid(serverJid);
		
		RfidServerBean serverBean = new RfidServerBean();
		serverBean.setMethod(RfidServerMethodType.ACK_DELETE_TAG);	
		serverBean.setTagNumber(tag);
		Stanza stanza = new Stanza(toIdentity);
		
		commManager.sendMessage(stanza, serverBean);
		if(logging.isDebugEnabled()) logging.debug("Sent message - deleteTag");
		} catch (InvalidFormatException e) {
			if(logging.isDebugEnabled()) logging.debug("Error sending message - deleteTag");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			if(logging.isDebugEnabled()) logging.debug("Error sending message - deleteTag");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void unregisterRFIDTag(String serverIdentity, String tagNumber,
			String userIdentity, String serviceID, String password) {
		if(logging.isDebugEnabled()) logging.debug("Sending message - unregisterRFIDTag");
		try{
		IIdentity toIdentity = idMgr.fromJid(serverIdentity);
		
		RfidServerBean serverBean = new RfidServerBean();
		serverBean.setMethod(RfidServerMethodType.UNREGISTER_RFID_TAG);
		serverBean.setIdentity(userIdentity);
		serverBean.setTagNumber(tagNumber);
		serverBean.setServiceID(serviceID);
		serverBean.setPassword(password);
		
		Stanza stanza = new Stanza(toIdentity);
		
		commManager.sendMessage(stanza, serverBean);
		if(logging.isDebugEnabled()) logging.debug("Sent message - unregisterRFIDTag");
		} catch (InvalidFormatException e) {
			if(logging.isDebugEnabled()) logging.debug("Error sending message - unregisterRFIDTag");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			if(logging.isDebugEnabled()) logging.debug("Error sending message - unregisterRFIDTag");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) { }

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) { }

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) { }

	@Override
	public void receiveResult(Stanza arg0, Object arg1) { }


}
