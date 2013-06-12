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
package org.societies.privacytrust.trust.impl.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.util.TrustedEntityIdFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * This class is used to update the CSS Activity Feed with trust-related events.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
@Service
@Lazy(false)
public class TrustActivityFeed implements ITrustUpdateEventListener {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(TrustActivityFeed.class);
	
	/** The CSS activity feed. */
	private IActivityFeed cssActivityFeed;
	
	private final String cssActivityFeedId;

	@Autowired(required=true)
	TrustActivityFeed(ITrustBroker trustBroker, 
			IActivityFeedManager activityFeedMgr,
			ICommManager commMgr) throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		try {
			if (LOG.isInfoEnabled())
				LOG.info("Obtaining reference to CSS Activity Feed");
			this.cssActivityFeedId = commMgr.getIdManager().getThisNetworkNode().toString(); 
			this.cssActivityFeed = activityFeedMgr.getOrCreateFeed(
					cssActivityFeedId, cssActivityFeedId, false);

			final IIdentity cssOwnerId = commMgr.getIdManager().getCloudNode();
			final TrustedEntityId cssTeid = TrustedEntityIdFactory.fromIIdentity(cssOwnerId);
			if (LOG.isInfoEnabled())
				LOG.info("Registering for updates of user-preceived trust");
			trustBroker.registerTrustUpdateListener(this, new TrustQuery(cssTeid)
					.setTrustValueType(TrustValueType.USER_PERCEIVED));
			
		} catch (Exception e) {

			LOG.error("Could not instantiate " + this.getClass()
					+ "': " + e.getLocalizedMessage(), e);
			throw e;
		}
	}
	
	/*
	 * @see org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.privacytrust.trust.event.TrustUpdateEvent)
	 */
	@Override
	public void onUpdate(TrustUpdateEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received event " + event);
		
		final TrustedEntityId trusteeId = event.getTrustRelationship().getTrusteeId();
		if (trusteeId.equals(event.getTrustRelationship().getTrustorId())) {
			if (LOG.isDebugEnabled())
				LOG.debug("Ignoring event " + event);
			return;
		}
		final String activity = "Trust value of '" + trusteeId + "' changed to "
				+ event.getTrustRelationship().getTrustValue();
		if (LOG.isDebugEnabled())
			LOG.debug("Adding activity '" + activity + "'");
		this.addCssActivity(activity);
	}
	
	private void addCssActivity(final String action){

	    final IActivity activity = this.cssActivityFeed.getEmptyIActivity();
	    activity.setActor(this.cssActivityFeedId);
	    activity.setObject(this.cssActivityFeedId);
	    activity.setVerb(action);

	    this.cssActivityFeed.addActivity(activity);
	}
}