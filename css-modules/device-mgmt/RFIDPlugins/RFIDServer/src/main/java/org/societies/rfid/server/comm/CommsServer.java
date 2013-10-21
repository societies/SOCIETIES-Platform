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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.rfid.server.comm;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.rfid.schema.server.RfidServerBean;
import org.societies.api.rfid.schema.server.RfidServerMethodType;
import org.societies.rfid.server.api.IRfidServer;


public class CommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/rfid/schema/server"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.rfid.schema.server"));
	private IRfidServer rfidServer;

	//PRIVATE VARIABLES
	private ICommManager commManager;

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	//PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	//METHODS
	public CommsServer() {
	}

	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		if(LOG.isDebugEnabled()) LOG.debug("Received message ");
		if (payload instanceof RfidServerBean){
			RfidServerBean rfidServerBean = (RfidServerBean) payload;
			if(rfidServerBean.getMethod().equals(RfidServerMethodType.REGISTER_RFID_TAG))
			{
				if(LOG.isDebugEnabled()) LOG.debug("Received Register RfidServerBean: tag: "+rfidServerBean.getTagNumber()+" from:"+rfidServerBean.getIdentity());
				this.rfidServer.registerRFIDTag(rfidServerBean.getTagNumber(), rfidServerBean.getIdentity(), rfidServerBean.getServiceID(), rfidServerBean.getPassword());

			}


			else if(rfidServerBean.getMethod().equals(RfidServerMethodType.UNREGISTER_RFID_TAG))
			{
				if(LOG.isDebugEnabled()) LOG.debug("Received Unregister RfidServerBean: tag: "+rfidServerBean.getTagNumber()+" from:"+rfidServerBean.getIdentity());
				this.rfidServer.unregisterRFIDTag(rfidServerBean.getTagNumber(), rfidServerBean.getIdentity(), rfidServerBean.getServiceID(), rfidServerBean.getPassword());
			}
			else if(rfidServerBean.getMethod().equals(RfidServerMethodType.ACK_DELETE_TAG))
			{
				if(LOG.isDebugEnabled()) LOG.debug("Received delete RfidServerBean: tag: "+rfidServerBean.getTagNumber());
				this.rfidServer.deleteTag(rfidServerBean.getTagNumber());
			}
		}
		else{
			if(LOG.isDebugEnabled()) LOG.debug("Payload object not of type: "+RfidServerBean.class.getName()+ ". Ignoring message from: "+stanza.getFrom().getJid());
		}
	}


	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		//CHECK WHICH END BUNDLE TO BE CALLED THAT I MANAGE
		System.out.println("Generic query handler, doing nothing");
		return null;
	}



	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the rfidServer
	 */
	public IRfidServer getRfidServer() {
		return rfidServer;
	}

	/**
	 * @param rfidServer the rfidServer to set
	 */
	public void setRfidServer(IRfidServer rfidServer) {
		this.rfidServer = rfidServer;
	}


}
