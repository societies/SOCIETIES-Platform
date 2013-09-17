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
package org.societies.example.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxRemoteProfileRetriever {
	
	/** Logging facility */
	private static final Logger LOG = LoggerFactory.getLogger(CtxRemoteProfileRetriever.class);
	
	private final static List<String> PROFILE_ATTR_TYPES = Arrays.asList(
			CtxAttributeTypes.NAME,
			CtxAttributeTypes.EMAIL,
			CtxAttributeTypes.SEX,
			CtxAttributeTypes.WORK_POSITION
			);
	
	/** MUST match the JID of an up and running remote CSS */
	private static final String TARGET_CSS_JID = "emma.ict-societies.eu";
	
	private ICtxBroker internalCtxBroker;
	
	@Autowired(required=true)
	public CtxRemoteProfileRetriever(ICtxBroker internalCtxBroker, ICommManager commMgr) throws Exception {
		
		LOG.info("{} instantiated", this.getClass().getName());
		
		this.internalCtxBroker = internalCtxBroker;
		
		try {
			final IIdentity targetCssId = commMgr.getIdManager().fromJid(TARGET_CSS_JID);
			this.retrieveRemoteProfile(targetCssId);
		} catch (Exception e) {
			LOG.error("Example failed: " + e.getLocalizedMessage(), e);
		}
	}
	
	private void retrieveRemoteProfile(final IIdentity targetCssId) 
			throws InterruptedException, ExecutionException, CtxException {

		final List<CtxIdentifier> profileAttrIds = new ArrayList<CtxIdentifier>();
		for (final String type : PROFILE_ATTR_TYPES) {
			profileAttrIds.addAll(this.internalCtxBroker.lookup(targetCssId, type).get());
		}
		if (profileAttrIds.isEmpty()) {
			LOG.info("Could not find any profile attributes");
			return;
		}
		
		final List<CtxModelObject> profileAttrs = 
				this.internalCtxBroker.retrieve(profileAttrIds).get();
		if (profileAttrs.isEmpty()) {
			LOG.info("Could not retrieve any profile attributes");
			return;
		}
		for (final CtxModelObject profileAttr : profileAttrs) {
			// Check if result can be cast to CtxAttribute to get the value
			if (profileAttr instanceof CtxAttribute) {
				LOG.info("Retrieved ATTRIBUTE of type '" + profileAttr.getType() 
						+ "' with value '" + ((CtxAttribute) profileAttr).getStringValue() + "'");
			} else {
				LOG.info("Retrieved " + profileAttr.getModelType() + " of type '"
						+ profileAttr.getType() + "'");
			}
		}
	}
}
