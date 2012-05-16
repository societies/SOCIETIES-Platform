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
package org.societies.privacytrust.privacyprotection.privacypolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManager implements IPrivacyPolicyManager {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyPolicyManager.class);

	ICommManager commManager;
	ICtxBroker ctxBroker;
	PrivacyPolicyRegistryManager registryManager;

	
	public void init() {
		registryManager = new PrivacyPolicyRegistryManager(ctxBroker);
	}
	
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#getPrivacyPolicy(org.societies.api.identity.IIdentity)
	 */
	@Override
	public RequestPolicy getPrivacyPolicy(IIdentity cisId)
			throws PrivacyException {
		IIdentity ownerId = commManager.getIdManager().getThisNetworkNode();
		RequestorCis requestor = new RequestorCis(ownerId, cisId);
		RequestPolicy privacyPolicy = registryManager.getPolicy(requestor);
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#getPrivacyPolicy(org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public RequestPolicy getPrivacyPolicy(ServiceResourceIdentifier serviceId)
			throws PrivacyException {
		IIdentity ownerId = commManager.getIdManager().getThisNetworkNode();
		RequestorService requestor = new RequestorService(ownerId, serviceId);
		RequestPolicy privacyPolicy = registryManager.getPolicy(requestor);
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#updatePrivacyPolicy(org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy)
	 */
	@Override
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy)
			throws PrivacyException {
		Requestor requestor = privacyPolicy.getRequestor();
		registryManager.addPolicy(requestor, privacyPolicy);
		return privacyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#deletePrivacyPolicy(org.societies.api.identity.IIdentity)
	 */
	@Override
	public boolean deletePrivacyPolicy(IIdentity cisId) throws PrivacyException {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#deletePrivacyPolicy(org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public boolean deletePrivacyPolicy(ServiceResourceIdentifier serviceId)
			throws PrivacyException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Try to infer a privacy policy a configuration map
	 * At the moment it only returns an empty privacy policy
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager#inferPrivacyPolicy(int, java.util.Map)
	 */
	@Override
	public RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType,
			Map configuration) throws PrivacyException {
		List<RequestItem> requests = new ArrayList<RequestItem>();
		RequestPolicy privacyPolicy = new RequestPolicy(requests);
		return privacyPolicy;
	}
	
	
	// -- Dependency Injection
	
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DependencyInjection] CommManager injected");
	}
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
		LOG.info("[DependencyInjection] ICtxBroker injected");
	}
}
