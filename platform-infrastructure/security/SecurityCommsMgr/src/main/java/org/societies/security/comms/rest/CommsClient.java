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
package org.societies.security.comms.rest;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.domainauthority.IClientJarServerCallback;
import org.societies.api.internal.domainauthority.IClientJarServerRemote;
import org.societies.api.internal.schema.domainauthority.rest.ClientJarBean;
import org.societies.api.internal.schema.domainauthority.rest.MethodType;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.scheduling.annotation.Async;

/**
 * Comms Client that initiates the remote communication
 * 
 * @author Mitja Vardjan
 * 
 */
//@Component
public class CommsClient implements IClientJarServerRemote {
	
	private ICommManager commMgr;
	private static Logger LOG = LoggerFactory.getLogger(CommsClient.class);
	private IIdentityManager idMgr;
	private CommsClientCallback clientCallback;
	
//	@Autowired
//	public CommsClient(ICommManager commManager) {
//		
//		this.commManager = commManager;
//		
//		LOG.info("CommsClient({})", commManager);
//	}
	
	public CommsClient() {
		LOG.info("CommsClient()");
	}

//	@PostConstruct
	public void init() {

		LOG.debug("init()");
		
		clientCallback = new CommsClientCallback();
		
		// REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			commMgr.register(clientCallback);
			LOG.debug("init(): commMgr registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}

		idMgr = commMgr.getIdManager();

		if (idMgr == null) {
			LOG.error("init({}): Could not get IdManager from ICommManager");
		}

	}

	// Getters and setters for beans
	public ICommManager getCommMgr() {
		return commMgr;
	}
	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
	}

	@Override
	@Async
	public void shareFiles(IIdentity toIdentity, URI serviceId, IIdentity provider,
			String signature, List<String> files, IClientJarServerCallback callback) {
		
		LOG.debug("shareFiles(..., {}, {}, ...)", serviceId, provider);
		
		sendIQ(toIdentity, MethodType.SHARE_FILES, serviceId, provider, signature, files, callback);
	}

	/**
	 * Send information query (IQ) using the comms framework.
	 * IQ is a message where an async result is expected.
	 * 
	 * @return Stanza ID for success, null for error
	 */
	private String sendIQ(IIdentity toIdentity, MethodType method, URI serviceId, IIdentity provider,
			String signature, List<String> files, IClientJarServerCallback callback) {
		
		LOG.debug("send(" + toIdentity + ", " + method + ", " + serviceId +
				", " + provider + ", " + signature + ", ...)");
		
		// Create stanza
		Stanza stanza = new Stanza(toIdentity);
		stanza.setId(StanzaIdGenerator.next());
		//stanza.setFrom(idMgr.getThisNetworkNode());

		// Create message bean
		ClientJarBean payload = new ClientJarBean();
		payload.setMethod(method);
		payload.setServiceId(serviceId);
		payload.setProviderIdentity(provider.getJid());
		payload.setSignature(signature);
		//payload.setFiles(files);  // ArrayList causes org.simpleframework.xml.transform.TransformException
		if (files != null && files.size() > 0) {
			payload.setFile1(files.get(0));
			if (files.size() > 1) {
				payload.setFile2(files.get(1));
			}
			if (files.size() > 2) {
				payload.setFile3(files.get(2));
			}
		}
		
		// Just to avoid theoretical race condition, add callback BEFORE sending IQ
		clientCallback.addCallback(stanza.getId(), callback);
		
		// Send information query
		try {
			commMgr.sendIQGet(stanza, payload, clientCallback);
			LOG.debug("sendIQ({}): IQ sent to {}", serviceId, toIdentity.getJid());
			return stanza.getId();
		} catch (CommunicationException e) {
			LOG.warn("sendIQ({}): could not send IQ to " + toIdentity.getJid(), serviceId, e);
			clientCallback.removeCallback(stanza.getId());
			return null;
		}
	}

//	@Override
//	public IIdentityManager getIdMgr() {
//		return idMgr;
//	}
}
