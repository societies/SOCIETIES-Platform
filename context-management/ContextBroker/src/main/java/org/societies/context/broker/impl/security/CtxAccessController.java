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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.context.broker.api.security.CtxAccessControllerException;
import org.societies.context.broker.api.security.CtxPermission;
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
	private IPrivacyDataManager privacyDataMgr;

	CtxAccessController() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.context.broker.api.security.ICtxAccessController#checkPermission(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, org.societies.context.broker.api.security.CtxPermission)
	 */
	@Override
	public void checkPermission(final Requestor requestor, 
			final IIdentity target, final CtxPermission perm)
			throws CtxAccessControlException, CtxAccessControllerException {

		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (target == null)
			throw new NullPointerException("target can't be null");
		if (perm == null)
			throw new NullPointerException("perm can't be null");
		
		if (LOG.isDebugEnabled())
			LOG.debug("Checking permission " + perm);
		
		if (perm.getActions().indexOf(CtxPermission.READ) != -1)
			this.doCheckPermission(requestor, target, perm.getResource(), ActionConstants.READ);
		if (perm.getActions().indexOf(CtxPermission.WRITE) != -1)
			this.doCheckPermission(requestor, target, perm.getResource(), ActionConstants.WRITE);
		if (perm.getActions().indexOf(CtxPermission.CREATE) != -1)
			this.doCheckPermission(requestor, target, perm.getResource(), ActionConstants.CREATE);
		if (perm.getActions().indexOf(CtxPermission.DELETE) != -1)
			this.doCheckPermission(requestor, target, perm.getResource(), ActionConstants.DELETE);
	}
	
	private void doCheckPermission(final Requestor requestor, 
			final IIdentity target, final CtxIdentifier ctxId, 
			final ActionConstants action) throws CtxAccessControlException,
			CtxAccessControllerException {
		
		try {
			if (LOG.isDebugEnabled())
				LOG.debug("Checking " + action + " permission: requestor=" + requestor 
						+ ",target=" + target + ",ctxId="	+ ctxId);
			final ResponseItem response = this.privacyDataMgr.checkPermission(
					requestor, target, ctxId, new Action(action));
			if (LOG.isDebugEnabled())
				LOG.debug("ResponseItem is " + response);
			if (response == null || !Decision.PERMIT.equals(response.getDecision()))
				throw new CtxAccessControlException(action + " access denied for requestor "
						+ requestor + " on target " + target); 
		} catch (PrivacyException pe) {
			throw new CtxAccessControllerException("Failed to perform access control: "
					+ "PrivacyDataManager checkPermission failed: "
					+ pe.getLocalizedMessage(), pe);
		} catch (ServiceUnavailableException sue) {
			throw new CtxAccessControllerException("Failed to perform access control: "
					+ "PrivacyDataManager service is not available");
		}
	}
	
	/**
	 * 
	 * @param privacyDataMgr
	 */
	@Autowired(required=false)
	public void setPrivacyDataMgr(IPrivacyDataManager privacyDataMgr) {
		
		this.privacyDataMgr = privacyDataMgr;
	}
}