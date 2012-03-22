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

import java.util.Random;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.schema.security.policynegotiator.SlaBean;
import org.societies.api.security.digsig.ISignatureMgr;

//@Component
public class NegotiationProvider implements INegotiationProvider {

	private static Logger LOG = LoggerFactory.getLogger(NegotiationProvider.class);
	
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;
	
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
		
		LOG.debug("init(): signed = {}", signatureMgr.signXml("xml", "xmlNodeId", "identity"));
		LOG.debug("init(): signature valid = {}", signatureMgr.verify("xml"));
		
		LOG.debug("init(): group manager = {}", groupMgr.toString());
		groupMgr.reject(0);
	}
	
	// Getters and setters for beans
	public INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		this.groupMgr = groupMgr;
	}
	public ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		this.signatureMgr = signatureMgr;
	}
	
	@Override
	public Future<SlaBean> getPolicyOptions(String serviceId) {
		
		SlaBean sla = new SlaBean();
		Random rnd = new Random();
		int sessionId = rnd.nextInt();
		
		sla.setSessionId(sessionId);
		// TODO: store session ID
		sla.setSla("<a/>");  // FIXME
		
		return new AsyncResult<SlaBean>(sla);
	}

	@Override
	public Future<SlaBean> acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified) {
		
		SlaBean sla = new SlaBean();
		String finalSla;
		
		sla.setSessionId(sessionId);
		finalSla = signedPolicyOption;  //TODO: add provider's signature
		
		if (!signatureMgr.verify(signedPolicyOption)) {
			LOG.info("acceptPolicyAndGetSla({}): invalid signature", sessionId);
			//sla.setError();
		}

		sla.setSla(finalSla);
		return new AsyncResult<SlaBean>(sla);
	}

	@Override
	public void reject(int sessionId) {

		LOG.debug("reject({})", sessionId);
	}
}
