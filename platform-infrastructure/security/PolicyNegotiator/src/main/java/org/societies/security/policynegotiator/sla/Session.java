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
package org.societies.security.policynegotiator.sla;

import java.util.Random;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.security.policynegotiator.NegotiationType;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class Session {

	private static Random rnd = new Random();
	
	private int sessionId;
	private IIdentity requester;
	private IIdentity provider;
	private SLA sla;
	private String serviceId;
	private NegotiationType type;

	/**
	 * Constructor. Session ID is generated automatically.
	 */
	public Session(NegotiationType type) {
		this.sessionId = rnd.nextInt();
		this.type = type;
	}
	
	/**
	 * Constructor. Session ID is specified explicitly.
	 */
//	public Session(int sessionId, NegotiationType type) {
//		this.sessionId = sessionId;
//		this.type = type;
//	}

	/**
	 * @return Session ID
	 */
	public int getId() {
		return sessionId;
	}

	/**
	 * Get Service Operation Policy (SOP) or the final Service Level Agreement (SLA)
	 * 
	 * @return SOP or SLA
	 */
	public SLA getSla() {
		return sla;
	}
	
	/**
	 * Set Service Operation Policy (SOP) or the final Service Level Agreement (SLA)
	 * 
	 * @param sla SOP or SLA
	 */
	public void setSla(SLA sla) {
		this.sla = sla;
	}

	public IIdentity getRequester() {
		return requester;
	}
	
	public void setRequester(IIdentity requester) {
		this.requester = requester;
	}

	public IIdentity getProvider() {
		return provider;
	}
	
	public void setProvider(IIdentity provider) {
		this.provider = provider;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public NegotiationType getType() {
		return type;
	}
}
