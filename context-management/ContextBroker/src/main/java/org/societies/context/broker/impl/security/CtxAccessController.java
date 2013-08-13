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
package org.societies.context.broker.impl.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.util.TrustedEntityIdFactory;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.context.broker.api.security.CtxAccessControllerException;
import org.societies.context.broker.api.security.ICtxAccessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.service.ServiceUnavailableException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ICtxAccessController} interface that uses the
 * {@link IPrivacyDataManager} for determining whether access requests should
 * be allowed or denied.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
@Service
public class CtxAccessController implements ICtxAccessController {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxAccessController.class);
	
	/** The Privacy Data Mgr service reference. */
	@Autowired(required=false)
	private IPrivacyDataManager privacyDataMgr;
	
	/** The Event Mgr service reference. */
	@Autowired(required=true)
	private IEventMgr eventMgr;
	
	/** The Comms Mgr service reference. */
	@Autowired(required=true)
	private ICommManager commMgr;
	
	/** The executor service. */
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	CtxAccessController() {
		
		LOG.info("{} instantiated", this.getClass());
	}
	
	/*
	 * @see org.societies.context.broker.api.security.ICtxAccessController#checkPermission(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxIdentifier, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants)
	 */
	@Override
	public void checkPermission(final Requestor requestor, 
			final CtxIdentifier ctxId, final ActionConstants actionConst)
			throws CtxAccessControlException, CtxAccessControllerException {

		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxId == null) {
			throw new NullPointerException("ctxId can't be null");
		}
		if (actionConst == null) {
			throw new NullPointerException("actionConst can't be null");
		}
		
		LOG.debug("checkPermission: requestor={}, ctxId={}, actionConst={}", 
				new Object[] { requestor, ctxId, actionConst.name() });
		
		boolean accessDenied = true;
		try {
			final Action action = new Action();
			action.setActionConstant(actionConst);
			final List<ResponseItem> responses = this.privacyDataMgr.checkPermission(
					RequestorUtils.toRequestorBean(requestor), ctxId, action);
			for (final ResponseItem response : responses) {
				LOG.debug("response: decision={}", response.getDecision());
				if (Decision.PERMIT == response.getDecision()) {
					accessDenied = false;
				}
			}
		} catch (ServiceUnavailableException sue) {
			throw new CtxAccessControllerException("Failed to perform access control: "
					+ "PrivacyDataManager service is not available");
		} catch (Exception e) {
			throw new CtxAccessControllerException("Failed to perform access control: "
					+ e.getLocalizedMessage(), e);
		}
		
		if (accessDenied) {
			this.executorService.submit(new TrustEvidenceDispatcher(
					requestor, ctxId, TrustEvidenceType.WITHHELD_CONTEXT));
			throw new CtxAccessControlException("'" + actionConst.name()
					+ "' access to '" + ctxId + "' denied for requestor '"
					+ requestor + "'");
		} else {
			this.executorService.submit(new TrustEvidenceDispatcher(
					requestor, ctxId, TrustEvidenceType.SHARED_CONTEXT));
		}
	}
	
	/*
	 * @see org.societies.context.broker.api.security.ICtxAccessController#checkPermission(org.societies.api.identity.Requestor, java.util.List, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants)
	 */
	public List<CtxIdentifier> checkPermission(final Requestor requestor, 
			final List<? extends CtxIdentifier> ctxIdList, 
			final ActionConstants actionConst) throws 
			CtxAccessControlException, CtxAccessControllerException {
		
		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxIdList == null) {
			throw new NullPointerException("ctxIdList can't be null");
		}
		if (actionConst == null) {
			throw new NullPointerException("actionConst can't be null");
		}
		
		LOG.debug("checkPermission: requestor={}, ctxIdList={}, actionConst={}",
				new Object[] { requestor, ctxIdList, actionConst.name() });
		
		final List<CtxIdentifier> result = new ArrayList<CtxIdentifier>(ctxIdList.size());
		try {
			final Action action = new Action();
			action.setActionConstant(actionConst);
			final List<DataIdentifier> dataIdList = new ArrayList<DataIdentifier>(ctxIdList.size());
			for (final CtxIdentifier ctxId : ctxIdList)
				dataIdList.add(ctxId);
			final List<ResponseItem> responses = this.privacyDataMgr.checkPermission(					
					RequestorUtils.toRequestorBean(requestor), dataIdList, action);
			for (final ResponseItem response : responses) {
				final String ctxIdStr = ResourceUtils.getDataIdUri(
						response.getRequestItem().getResource());
				final CtxIdentifier ctxId = CtxIdentifierFactory.getInstance().fromString(ctxIdStr); 
				LOG.debug("response: ctxId={}, decision={}", 
						ctxId, response.getDecision());
				if (Decision.PERMIT == response.getDecision()) {
					result.add(ctxId);
					this.executorService.submit(new TrustEvidenceDispatcher(
							requestor, ctxId, TrustEvidenceType.SHARED_CONTEXT));
				} else {
					this.executorService.submit(new TrustEvidenceDispatcher(
							requestor, ctxId, TrustEvidenceType.WITHHELD_CONTEXT));
				}
			}
		} catch (ServiceUnavailableException sue) {
			throw new CtxAccessControllerException("Failed to perform access control: "
					+ "PrivacyDataManager service is not available");
		} catch (Exception e) {
			throw new CtxAccessControllerException("Failed to perform access control: "
					+ e.getLocalizedMessage(), e);
		}
		
		if (result.isEmpty() && !ctxIdList.isEmpty()) {
			throw new CtxAccessControlException("'" + actionConst.name()
					+ "' access to '" + ctxIdList + "' denied for requestor '"
					+ requestor + "'");
		}
		
		LOG.debug("checkPermission: result={}", result);
		return result;
	}
	
	/*
	 * @see org.societies.context.broker.api.security.ICtxAccessController#obfuscate(org.societies.api.identity.Requestor, org.societies.api.context.model.CtxModelObject)
	 */
	@Override
	public CtxModelObject obfuscate(final Requestor requestor, 
			final CtxModelObject ctxModelObject) 
					throws CtxAccessControllerException {
	
		if (ctxModelObject == null) {
			throw new NullPointerException("ctxModelObject can't be null");
		}
		
		final List<CtxModelObject> ctxModelObjectList = new ArrayList<CtxModelObject>(1);
		ctxModelObjectList.add(ctxModelObject);
		
		return this.obfuscate(requestor, ctxModelObjectList).get(0);
	}
	
	/*
	 * @see org.societies.context.broker.api.security.ICtxAccessController#obfuscate(org.societies.api.identity.Requestor, java.util.List)
	 */
	@Override
	public List<CtxModelObject> obfuscate(final Requestor requestor, 
			final List<CtxModelObject> ctxModelObjectList) 
					throws CtxAccessControllerException {
			
		if (requestor == null) {
			throw new NullPointerException("requestor can't be null");
		}
		if (ctxModelObjectList == null) {
			throw new NullPointerException("ctxModelObjectList can't be null");
		}
		
		LOG.debug("obfuscate: requestor={}, ctxModelObjectList={}",
				requestor, ctxModelObjectList);
		
		final List<CtxModelObject> result = new ArrayList<CtxModelObject>(ctxModelObjectList.size());
		try {
			result.addAll(this.privacyDataMgr.obfuscateData(
					RequestorUtils.toRequestorBean(requestor), ctxModelObjectList).get());
			// TODO Check with Obfuscation Mgmt
			if (result.isEmpty()) {
				result.addAll(ctxModelObjectList);
			}
		} catch (ServiceUnavailableException sue) {
			throw new CtxAccessControllerException("Failed to perform obfuscation: "
					+ "PrivacyDataManager service is not available");
		} catch (Exception e) {
			throw new CtxAccessControllerException("Failed to perform obfuscation: "
					+ e.getLocalizedMessage(), e);
		}
		
		LOG.debug("obfuscate: result={}", result);
		return result;
	}
	
	private class TrustEvidenceDispatcher implements Runnable {
		
		private final Requestor requestor;
		
		private final CtxIdentifier ctxId;
		
		private final TrustEvidenceType evidenceType;
		
		private TrustEvidenceDispatcher(final Requestor requestor,
				final CtxIdentifier ctxId, final TrustEvidenceType evidenceType) {
			
			this.requestor = requestor;
			this.ctxId = ctxId;
			this.evidenceType = evidenceType;
		}
		
		/*
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			LOG.debug("TrustEvidenceDispatcher: requestor={}, ctxId={}, evidenceType={}",
					new Object[] { this.requestor, this.ctxId, this.evidenceType });
			
			try {
				final TrustedEntityId subjectId = this.extractSubjectId(this.ctxId);
				final TrustedEntityId objectId = this.extractObjectId(this.requestor);
				final Date timestamp = new Date();
				final Serializable info = this.ctxId.getType();
				final TrustEvidence trustEvidence = new TrustEvidence(
						subjectId, objectId, this.evidenceType, timestamp, info, null);
				
				LOG.debug("TrustEvidenceDispatcher: trustEvidence={}", trustEvidence);
				eventMgr.publishInternalEvent(new InternalEvent(
						EventTypes.TRUST_EVIDENCE_EVENT,           // eventType
						this.evidenceType.name(),                  // eventName
						CtxAccessController.class.getSimpleName(), // eventType
						trustEvidence                              // eventInfo
						));
			} catch (Exception e) {
				LOG.error("Could not dispatch trust evidence: " + e.getLocalizedMessage(), e);
			}
		}

		private TrustedEntityId extractSubjectId(final CtxIdentifier ctxId) throws Exception {

			final IIdentity ownerId = commMgr.getIdManager().fromJid(ctxId.getOwnerId());
			return TrustedEntityIdFactory.fromIIdentity(ownerId);
		}

		private TrustedEntityId extractObjectId(final Requestor requestor) throws Exception {

			if (requestor instanceof RequestorService) { // S E R V I C E
				return TrustedEntityIdFactory.fromServiceResourceIdentifier(
						((RequestorService) requestor).getRequestorServiceId());
			} else if (requestor instanceof RequestorCis) { // C O M M U N I T Y 
				return TrustedEntityIdFactory.fromIIdentity(
						((RequestorCis) requestor).getCisRequestorId());
			} else { // U S E R
				return TrustedEntityIdFactory.fromIIdentity(
						requestor.getRequestorId());
			}
		}
	}
}