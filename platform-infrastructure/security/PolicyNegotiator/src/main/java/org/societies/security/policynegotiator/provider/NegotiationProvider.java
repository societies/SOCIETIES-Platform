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
package org.societies.security.policynegotiator.provider;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.policynegotiator.NegotiationException;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.policynegotiator.sla.SLA;
import org.societies.security.policynegotiator.sla.Session;
import org.societies.security.policynegotiator.sla.SopResource;
import org.societies.security.policynegotiator.xml.Xml;
import org.w3c.dom.Document;

//@Component
public class NegotiationProvider implements INegotiationProvider {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationProvider.class);
	
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;
	private ProviderServiceMgr providerServiceMgr;
	
	/**
	 * Negotiation sessions
	 */
	private Map<Integer, Session> sessions = new HashMap<Integer, Session>();
	
//	@Autowired
//	public NegotiationProvider(ISignatureMgr signatureMgr) {
//		
//		this.signatureMgr = signatureMgr;
//
//		LOG.info("NegotiationProvider({})", signatureMgr.toString());
//	}

	public NegotiationProvider() {
		LOG.info("NegotiationProvider()");
	}
	
//	@PostConstruct
	public void init() {
		
		//LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "xmlNodeId", "identity"));
		//LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
		
		LOG.debug("init()");
	}
	
	// Getters and setters for beans
	public INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		LOG.debug("setGroupMgr()");
		this.groupMgr = groupMgr;
	}
	public ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		LOG.debug("setSignatureMgr()");
		this.signatureMgr = signatureMgr;
	}
	public ProviderServiceMgr getProviderServiceMgr() {
		return providerServiceMgr;
	}
	public void setProviderServiceMgr(ProviderServiceMgr providerServiceMgr) {
		LOG.debug("setProviderServiceMgr()");
		this.providerServiceMgr = providerServiceMgr;
	}
	
	private SlaBean createSlaBean(boolean success, int sessionId, String sla) {
		
		SlaBean bean = new SlaBean();
		
		bean.setSuccess(success);
		bean.setSessionId(sessionId);
		bean.setSla(sla);
		
		return bean;
	}
	
	@Override
	public Future<SlaBean> getPolicyOptions(String serviceId) {
		
		LOG.debug("getPolicyOptions({})", serviceId);

		Session session = new Session();
		boolean success;
		String slaStr = null;
		Document doc;
		
		session.setServiceId(serviceId);
		
		try {
			doc = SopResource.getSop("PrintService.xml");  // TODO: Get from Marketplace
			if (doc != null) {
				Xml xml = new Xml(doc);
				SLA sla = new SLA(xml);
				slaStr = xml.toString();
				success = true;
				session.setSla(sla);
				LOG.debug("getPolicyOptions({}): SOP: {}", serviceId, doc);
			}
			else {
				success = false;
				LOG.warn("getPolicyOptions({}): could not get SOP", serviceId);
			}
		} catch (Exception e) {
			success = false;
			LOG.warn("getPolicyOptions({}): could not get SOP: ", serviceId, e);
		}
		if (success) {
			sessions.put(session.getId(), session);
		}
		
		SlaBean result = createSlaBean(success, session.getId(), slaStr);

		return new AsyncResult<SlaBean>(result);
	}

	@Override
	public Future<SlaBean> acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified) {

		LOG.debug("acceptPolicyAndGetSla({})", sessionId + ", ..., " + modified);

		Session session = sessions.get(sessionId);
		SlaBean sla = new SlaBean();
		String finalSla;
		List<URI> signedUris;
		String serviceId;
		
		sla.setSessionId(sessionId);
		finalSla = signedPolicyOption;  //TODO: add provider's signature
		
		if (session != null && signatureMgr.verifyXml(signedPolicyOption)) {
			
			sla.setSla(finalSla);
			
			// FIXME: only in case of service negotiation, not in case of CIS negotiation
			if (true) {
				serviceId = session.getServiceId();
				try {
					signedUris = providerServiceMgr.getSignedUris(serviceId);
					sla.setFileUris(signedUris);
				} catch (NegotiationException e) {
					LOG.warn("acceptPolicyAndGetSla()", e);
					//sla.setSuccess(false);
				}
			}

			sla.setSuccess(true);
		}
		else {
			LOG.info("acceptPolicyAndGetSla({}): invalid signature", sessionId);
			sla.setSuccess(false);
		}
		
		Future<SlaBean> result = new AsyncResult<SlaBean>(sla);

		return result;
	}

	@Override
	public Future<SlaBean> reject(int sessionId) {

		LOG.debug("reject({})", sessionId);

		Session session = sessions.remove(sessionId);
		boolean success;
		SlaBean result;
		
		if (session != null) {
			success = true;
			LOG.info("reject({}) successful", sessionId);
		}
		else {
			success = false;
			LOG.warn("reject({}): no such session", sessionId);
		}
		result = createSlaBean(success, sessionId, null);

		return new AsyncResult<SlaBean>(result);
	}
}
