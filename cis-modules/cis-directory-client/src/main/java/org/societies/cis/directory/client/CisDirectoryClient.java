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
package org.societies.cis.directory.client;

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
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.directory.CisDirectoryBean;
import org.societies.api.schema.cis.directory.MethodType;
import org.societies.cis.directory.client.CisDirectoryClientCallback;

/**
 * Comms Client that initiates the remote communication for the cis discovery
 *
 * @author Maria Mannion
 *
 */
public class CisDirectoryClient implements ICisDirectoryRemote, ICommCallback {
	private static final List<String> NAMESPACES = Collections
			.unmodifiableList(Arrays
					.asList("http://societies.org/api/schema/cis/directory"));
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays
					.asList("org.societies.api.schema.cis.directory"));

	// PRIVATE VARIABLES
	private ICommManager commManager;
	private static Logger LOG = LoggerFactory.getLogger(CisDirectoryClient.class);
	private IIdentityManager idMgr;

	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public CisDirectoryClient() {
	}

	public void InitService() {
		// Registry Cis Directory with the Comms Manager

		if (LOG.isDebugEnabled())
			LOG.debug("Registering the Cis Directory with the XMPP Communication Manager");

		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commManager.getIdManager();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org
	 * .societies.api.comm.xmpp.datatypes.Stanza,
	 * org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.
	 * societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.cis.directory.ICisDirectoryRemote#addCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@Override
	public void addCisAdvertisementRecord(CisAdvertisementRecord cisAdvert) {
		
		LOG.info("addCisAdvertisementRecord called");
		// We want to send all messages for CisDirectory to the domain authority Node
		IIdentity toIdentity = idMgr.getDomainAuthorityNode();
		Stanza stanza = new Stanza(toIdentity);
		
		LOG.info("CISDirectory Client ADD RECORD >>>>>>>>>>>>>>>>>>>");
		LOG.info("CISDirectory Client Advertisement: ----- "+cisAdvert);

		// CREATE MESSAGE BEAN
		CisDirectoryBean cisDir = new CisDirectoryBean();
		cisDir.setCisA(cisAdvert);
		cisDir.setMethod(MethodType.ADD_CIS_ADVERTISEMENT_RECORD);
		LOG.info("going to send stanza to" + stanza.getTo().getBareJid());
		try {
			commManager.sendMessage(stanza, cisDir);
			LOG.info("msg sent");
		} catch (CommunicationException e) {
			LOG.info("exception at addCisAdvertisementRecord");
			LOG.warn(e.getMessage());
		}
		;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.api.cis.directory.ICisDirectoryRemote#
	 * deleteCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@Override
	public void deleteCisAdvertisementRecord(CisAdvertisementRecord cisAdvert) {
		// We want to send all messages for CisDirectory to the domain authority Node
		IIdentity toIdentity = idMgr.getDomainAuthorityNode();
		Stanza stanza = new Stanza(toIdentity);
		
		LOG.info("CISDirectory Client DELETE RECORD >>>>>>>>>>>>>>>>>>>");

		// CREATE MESSAGE BEAN
		CisDirectoryBean cisDir = new CisDirectoryBean();
		cisDir.setCisA(cisAdvert);
		cisDir.setMethod(MethodType.DELETE_CIS_ADVERTISEMENT_RECORD);
		try {
			commManager.sendMessage(stanza, cisDir);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.api.cis.directory.ICisDirectoryRemote#
	 * findAllCisAdvertisementRecords
	 * (org.societies.api.cis.directory.ICisDirectoryCallback)
	 */
	@Override
	public void findAllCisAdvertisementRecords(
			ICisDirectoryCallback cisDirCallback) {
		// We want to send all messages for CisDirectory to the domain authority Node
		IIdentity toIdentity = idMgr.getDomainAuthorityNode();
		Stanza stanza = new Stanza(toIdentity);
		
		
		LOG.info("CISDirectory Client FIND ALL RECORDs >>>>>>>>>>>>>>>>>>>");
		LOG.info("CISDirectory Client FIND ALL RECORDs cisDirCallback: ====== "+cisDirCallback);
		// SETUP CisDirectory CLIENT RETURN STUFF
		CisDirectoryClientCallback callback = new CisDirectoryClientCallback(stanza.getId(),
				cisDirCallback);

		// CREATE MESSAGE BEAN
		CisDirectoryBean cisDirBean = new CisDirectoryBean();

		LOG.info("CISDirectory Client FIND ALL RECORDs CALLBACK: ----- "+callback);
		LOG.info("CISDirectory Client FIND ALL RECORDs stanzaID: ----- "+stanza.getId());
		LOG.info("CISDirectory Client FIND ALL RECORDs NameSpace: ----- "+callback.getXMLNamespaces());
		LOG.info("CISDirectory Client FIND ALL RECORDs cisDirBean before: ----- "+cisDirBean.getMethod());
		cisDirBean.setMethod(MethodType.FIND_ALL_CIS_ADVERTISEMENT_RECORDS);
		LOG.info("CISDirectory Client FIND ALL RECORDs cisDirBean: ----- "+cisDirBean.getMethod());
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, cisDirBean, callback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.cis.directory.ICisDirectoryRemote#findForAllCis(org
	 * .societies.api.schema.cis.directory.CisAdvertisementRecord,
	 * org.societies.api.cis.directory.ICisDirectoryCallback)
	 */
	@Override
	public void findForAllCis(CisAdvertisementRecord filteredcis, String filter,
			ICisDirectoryCallback cisDirCallback) {
		// We want to send all messages for CisDirectory to the domain authority Node
		IIdentity toIdentity = idMgr.getDomainAuthorityNode();
		Stanza stanza = new Stanza(toIdentity);

		// SETUP CLIENT RETURN STUFF
		CisDirectoryClientCallback callback = new CisDirectoryClientCallback(stanza.getId(),
				cisDirCallback);

		// CREATE MESSAGE BEAN
		CisDirectoryBean cisDirBean = new CisDirectoryBean();
		cisDirBean.setCisA(filteredcis);

		cisDirBean.setMethod(MethodType.FIND_FOR_ALL_CIS);
		try {
			// SEND INFORMATION QUERY - RESPONSE WILL BE IN
			// "callback.RecieveMessage()"
			commManager.sendIQGet(stanza, cisDirBean, callback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.societies.api.cis.directory.ICisDirectoryRemote#
	 * updateCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord,
	 * org.societies.api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@Override
	public void updateCisAdvertisementRecord(
			CisAdvertisementRecord currentCisAdvert,
			CisAdvertisementRecord updatedCisAdvert) {
		// GET CURRENT NODE IDENTITY
		IIdentity toIdentity = idMgr.getDomainAuthorityNode();
		Stanza stanza = new Stanza(toIdentity);

		// CREATE MESSAGE BEAN
		CisDirectoryBean cisDir = new CisDirectoryBean();
		cisDir.setCisA(currentCisAdvert);
		cisDir.setCisB(updatedCisAdvert);
		cisDir.setMethod(MethodType.UPDATE_CIS_ADVERTISEMENT_RECORD);
		try {
			commManager.sendMessage(stanza, cisDir);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		}
		;

	}

}
