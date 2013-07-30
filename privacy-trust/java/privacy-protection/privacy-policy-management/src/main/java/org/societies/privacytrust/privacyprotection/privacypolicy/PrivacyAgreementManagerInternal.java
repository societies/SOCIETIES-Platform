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
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.util.model.privacypolicy.AgreementEnvelopeUtils;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal;

/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyAgreementManagerInternal implements IPrivacyAgreementManagerInternal {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyAgreementManagerInternal.class.getSimpleName());

	ICommManager commManager;
	ICtxBroker ctxBroker;


	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal#updateAgreement(org.societies.api.identity.Requestor, org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope)
	 */
	@Override
	public CtxIdentifier updateAgreement(Requestor requestor, AgreementEnvelope agreement) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy agreement. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PolicyAgreementManagerInternal not ready");
		}

		// -- Update Agreement
		String requestorId = getRequestorId(requestor);
		CtxIdentifier agreementId = null;
		try {
			// Retrieve existing id (if possible)
			List<CtxIdentifier> agreementIdList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, requestorId).get();
			CtxAttribute agreementData = null;
			// - Creation
			if (null == agreementIdList || agreementIdList.size() <= 0) {
				// Retrieve the context entity: Privacy Policy Agreement
				List<CtxIdentifier> agreementEntityIdList = ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY_AGREEMENT).get();

				// Create it if necessary
				CtxEntityIdentifier agreementEntityId = null;
				if (null == agreementEntityIdList || agreementEntityIdList.size() <= 0) {
					agreementEntityId = createPolicyAgreementEntity();
				}
				else {
					agreementEntityId = (CtxEntityIdentifier) agreementEntityIdList.get(0);
				}

				// Create the new context attribute to store the agreement
				agreementData = ctxBroker.createAttribute(agreementEntityId, requestorId).get();
				agreementId = agreementData.getId();
				LOG.debug("Created attribute: "+agreementData.getType());
			}

			// - Update
			else {
				agreementId = agreementIdList.get(0);
				// Retrieve the existing context attribute to store the agreement
				agreementData = (CtxAttribute) ctxBroker.retrieve(agreementId).get();
				LOG.debug("Updated attribute:"+agreementData.getType());
			}

			// - Save the agreement
			agreementData.setBinaryValue(SerialisationHelper.serialise(AgreementEnvelopeUtils.toAgreementEnvelopeBean(agreement)));
			ctxBroker.update(agreementData);
		} catch (CtxException e) {
			LOG.error("[Error updateAgreement] Can't find the agreement. Context error.", e);
		} catch (IOException e) {
			LOG.error("[Error updateAgreement] Can't find the agreement. IO error.", e);
		} catch (InterruptedException e) {
			LOG.error("[Error updateAgreement] Can't find the agreement.", e);
		} catch (ExecutionException e) {
			LOG.error("[Error updateAgreement] Can't find the agreement.", e);
		}
		return agreementId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.IPrivacyAgreementManagerInternal#deleteAgreement(org.societies.api.identity.Requestor)
	 */
	@Override
	public boolean deleteAgreement(Requestor requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy agreement. Requestor needed.");
		}
		// Dependency injection not ready
		if (!isDepencyInjectionDone()) {
			throw new PrivacyException("[Dependency Injection] PolicyAgreementManagerInternal not ready");
		}

		// -- Delete agreement
		try {
			// - Retrieve existing id (if possible)
			String requestorId = getRequestorId(requestor);
			List<CtxIdentifier> agreementIdList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, requestorId).get();
			CtxIdentifier agreementId = null;
			// No agreement for this requestor
			if (null == agreementIdList || agreementIdList.size() <= 0) {
				return true;
			}
			else {
				agreementId = agreementIdList.get(0);
			}

			// - Remove from context
			ctxBroker.remove(agreementId);
			return true;
		} catch (CtxException e) {
			LOG.error("[Error deleteAgreement] Can't find the agreement. Context error.", e);
		} catch (InterruptedException e) {
			LOG.error("[Error deleteAgreement] Can't find the agreement.", e);
		} catch (ExecutionException e) {
			LOG.error("[Error deleteAgreement] Can't find the agreement.", e);

		}
		return false;
	}


	// -- Private methods
	/**
	 * Util method to create a context agreement entity
	 * @return The id of the agreement entity
	 * @throws PrivacyException
	 */
	private CtxEntityIdentifier createPolicyAgreementEntity() throws PrivacyException {
		try {
			// -- Retrieve the CSS Entity
			CtxEntity css = ctxBroker.retrieveCssOperator().get();
			if (null == css) {
				throw new PrivacyException("Error can't retrieve CSS Operator Entity in Context.");
			}

			// -- Create the Agreement Entity
			CtxEntity agreementEntity = ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY_AGREEMENT).get();

			// -- Retrieve and update the relevant association
			Set<CtxAssociationIdentifier> hasPrivacyPolicyAgreementsList = css.getAssociations(CtxAssociationTypes.HAS_PRIVACY_POLICY_AGREEMENTS);
			CtxAssociation hasPrivacyPolicyAgreements = null;
			// Create it if necessary
			if (null == hasPrivacyPolicyAgreementsList || hasPrivacyPolicyAgreementsList.size() <= 0) {
				hasPrivacyPolicyAgreements = ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICY_AGREEMENTS).get();
				hasPrivacyPolicyAgreements.setParentEntity(css.getId());
			}
			else {
				hasPrivacyPolicyAgreements = (CtxAssociation) ctxBroker.retrieve(hasPrivacyPolicyAgreementsList.iterator().next()).get();
			}
			// Add the agreement entity to this association
			hasPrivacyPolicyAgreements.addChildEntity(agreementEntity.getId());
			// TODO: add link to CIS entity or 3P service entity? I don't know yet.
			ctxBroker.update(hasPrivacyPolicyAgreements);
			return agreementEntity.getId();
		} catch (CtxException e) {
			LOG.error("[Error createPolicyAgreementEntity] Can't find the agreement. Context error.", e);
		} catch (InterruptedException e) {
			LOG.error("[Error createPolicyAgreementEntity] Can't find the agreement.", e);
		} catch (ExecutionException e) {
			LOG.error("[Error createPolicyAgreementEntity] Can't find the agreement.", e);
		}
		return null;
	}

	/**
	 * To find the real relevant requestor id
	 * @param requestor
	 * @return
	 * @throws PrivacyException 
	 */
	public static String getRequestorId(Requestor requestor) throws PrivacyException {
		if (null == requestor) {
			throw new PrivacyException("Bad requestor, can't store the agreement in the context.");
		}
		return CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT+requestor.hashCode();
	}

	/**
	 * To find the agreement id on the context
	 * @param requestor
	 * @param ownerId
	 * @return
	 * @throws PrivacyException 
	 */
	public static String getAgreementIdOnCtx(Requestor requestor, IIdentity ownerId) throws PrivacyException {
		if (null == requestor) {
			throw new PrivacyException("Bad requestor, can't store the agreement in the context.");
		}
		return CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT+requestor.hashCode()+ownerId.hashCode();
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
