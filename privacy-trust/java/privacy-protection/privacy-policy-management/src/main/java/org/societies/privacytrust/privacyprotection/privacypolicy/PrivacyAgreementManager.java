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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementEnvelopeUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;

/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyAgreementManager implements IPrivacyAgreementManager {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyAgreementManager.class.getSimpleName());

	private ICommManager commManager;
	private ICtxBroker ctxBroker;


	/*
	 * (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager#getAgreement(org.societies.api.identity.Requestor)
	 */
	@Override
	public AgreementEnvelope getAgreement(Requestor requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy agreement. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyAgreementManager not ready");
		}

		try {
			List<CtxIdentifier> agreementIdList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, PrivacyAgreementManagerInternal.getRequestorId(requestor)).get();
			if (null == agreementIdList || agreementIdList.size() <= 0) {
				return null;
			}
			CtxIdentifier agreementId = agreementIdList.get(0);
			CtxAttribute agreementData = (CtxAttribute) ctxBroker.retrieve(agreementId).get();
			org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreementTmp = (org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope) SerialisationHelper.deserialise(agreementData.getBinaryValue(), this.getClass().getClassLoader()); //ClassLoader.getSystemClassLoader());
			if (null == agreementTmp) {
				throw new NullPointerException("NullPointerException Deserialized agreement is null");
			}
			AgreementEnvelope agreement = AgreementEnvelopeUtils.toAgreementEnvelope(agreementTmp, commManager.getIdManager());
			return agreement;
		} catch (CtxException e) {
			LOG.error("[Error getAgreement] Can't find the agreement. Context error.", e);
		} catch (IOException e) {
			LOG.error("[Error getAgreement] Can't find the agreement. IO error.", e);
		} catch (ClassNotFoundException e) {
			LOG.error("[Error getAgreement] Can't find the agreement. ClassNotFound error.", e);
		} catch (InterruptedException e) {
			LOG.error("[Error getAgreement] Can't find the agreement.", e);
		} catch (ExecutionException e) {
			LOG.error("[Error getAgreement] Can't find the agreement.", e);
		} catch (InvalidFormatException e) {
			LOG.error("[Error getAgreement] Can't transform the agreement into a bean for serialization.", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager#checkAgreement(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity)
	 */
	@Override
	public boolean checkAgreement(Requestor requestor, IIdentity ownerId) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy agreement. Requestor needed.");
		}
		if (null == ownerId) {
			throw new PrivacyException("Not enought information to search a privacy policy agreement. Owner id needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PrivacyPolicyAgreementManager not ready");
		}

		try {
			List<CtxIdentifier> agreementIdList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, PrivacyAgreementManagerInternal.getAgreementIdOnCtx(requestor, ownerId)).get();
			if (null == agreementIdList || agreementIdList.size() <= 0) {
				return false;
			}
			CtxIdentifier agreementId = agreementIdList.get(0);
			CtxAttribute agreementData = (CtxAttribute) ctxBroker.retrieve(agreementId).get();
			org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope agreementTmp = (org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope) SerialisationHelper.deserialise(agreementData.getBinaryValue(), this.getClass().getClassLoader()); //ClassLoader.getSystemClassLoader());
			if (null == agreementTmp) {
				return false;
			}
			return true;
		} catch (CtxException e) {
			LOG.error("[Error getAgreement] Can't find the agreement. Context error.", e);
		} catch (IOException e) {
			LOG.error("[Error getAgreement] Can't find the agreement. IO error.", e);
		} catch (ClassNotFoundException e) {
			LOG.error("[Error getAgreement] Can't find the agreement. ClassNotFound error.", e);
		} catch (InterruptedException e) {
			LOG.error("[Error getAgreement] Can't find the agreement.", e);
		} catch (ExecutionException e) {
			LOG.error("[Error getAgreement] Can't find the agreement.", e);
		}
		return false;
	}


	// -- Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[DependencyInjection] ICommManager injected");
	}
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
		LOG.info("[DependencyInjection] ICtxBroker injected");
	}

	private boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	private boolean isDepencyInjectionDone(int level) {
		if (null == ctxBroker) {
			LOG.info("[Dependency Injection] Missing ICtxBorker");
			return false;
		}
		if (level == 0 || level == 1) {
			if (null == commManager) {
				LOG.info("[Dependency Injection] Missing ICommManager");
				return false;
			}
			if (null == commManager.getIdManager()) {
				LOG.info("[Dependency Injection] Missing IIdentityManager");
				return false;
			}
		}
		return true;
	}
}
