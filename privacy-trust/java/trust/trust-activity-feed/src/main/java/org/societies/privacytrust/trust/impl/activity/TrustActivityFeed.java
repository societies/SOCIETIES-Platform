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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.privacytrust.trust.model.util.TrustValueFormat;
import org.societies.api.privacytrust.trust.model.util.TrustedEntityIdFactory;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.services.ServiceUtils;
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
	
	private static final int MAX_ENTRIES = 64;
	private static final double VALUE_UPDATE_THRESHOLD = 0.15d;
	
	/** The time to wait for CIS Directory responses in milliseconds. */
	private static final long WAIT_CIS_DIR = 2000l;
	
	private final Map<TrustedEntityId, TrustActivity> cache = Collections.synchronizedMap(
			new LinkedHashMap<TrustedEntityId, TrustActivity>(MAX_ENTRIES+1, .75F, true) {
				
				private static final long serialVersionUID = 5204510380073235862L;

				// This method is called just after a new entry has been added
				public boolean removeEldestEntry(Map.Entry<TrustedEntityId, TrustActivity> eldest) {
					return this.size() > MAX_ENTRIES;
				}
			});
	
	/** The CSS activity feed. */
	private IActivityFeed cssActivityFeed;
	
	/** The CIS Directory service reference. */
	@Autowired(required=false)
	private ICisDirectoryRemote cisDir;
	
	/** The Service Discovery service reference. */
	@Autowired(required=false)
	private IServiceDiscovery serviceDisco;
	
	private final String cssActivityFeedId;

	@Autowired(required=true)
	TrustActivityFeed(ITrustBroker trustBroker, 
			IActivityFeedManager activityFeedMgr,
			ICommManager commMgr) throws Exception {

		LOG.info("{} instantiated", this.getClass());

		try {
			this.cssActivityFeedId = commMgr.getIdManager().getThisNetworkNode().toString();
			LOG.info("Obtaining reference to CSS Activity Feed of '{}'",
					this.cssActivityFeedId);
			this.cssActivityFeed = activityFeedMgr.getOrCreateFeed(
					this.cssActivityFeedId, this.cssActivityFeedId, false);

			final IIdentity cssOwnerId = commMgr.getIdManager().getCloudNode();
			final TrustedEntityId cssTeid = TrustedEntityIdFactory.fromIIdentity(cssOwnerId);
			LOG.info("Registering for updates of trust values as perceived by '{}'", cssTeid);
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
		
		LOG.debug("Received event {}", event);
		
		final TrustRelationship tr = event.getTrustRelationship();
		final TrustedEntityId trusteeId = tr.getTrusteeId();
		if (trusteeId.equals(tr.getTrustorId())) {
			LOG.debug("Ignoring event {}", event);
			return;
		}
		
		final String friendlyTrusteeId;
		final Double newTrustValue = tr.getTrustValue();
		// Check cache for last activity		
		final TrustActivity lastActivity = this.cache.get(trusteeId);
		if (lastActivity == null) {
			friendlyTrusteeId = this.formatTeid(trusteeId);
		} else {
			friendlyTrusteeId = lastActivity.getTrusteeId();
		}
		// Create new activity
		final TrustActivity newActivity = new TrustActivity(
				friendlyTrusteeId, newTrustValue); 
		// Check if new activity needs to be published
		if (lastActivity == null || lastActivity.getTrustValue() == null 
				|| newTrustValue == null || Math.abs(
				newTrustValue - lastActivity.getTrustValue()) > VALUE_UPDATE_THRESHOLD) {
			LOG.debug("Adding activity '{}'", newActivity);
			this.addCssActivity(newActivity.toString());
			// Cache new activity
			this.cache.put(trusteeId, newActivity);
		} else {
			LOG.debug("Ignoring activity '{}'", newActivity);
		}
		LOG.debug("onUpdate: cache={}", this.cache);
	}
	
	private void addCssActivity(final String action){

	    final IActivity activity = this.cssActivityFeed.getEmptyIActivity();
	    activity.setActor(this.cssActivityFeedId);
	    activity.setObject(this.cssActivityFeedId);
	    activity.setVerb(action);

	    this.cssActivityFeed.addActivity(activity);
	}
	
	private String formatTeid(final TrustedEntityId teid) {
		
		final String entityId = teid.getEntityId();
		try {
			if (TrustedEntityType.CSS == teid.getEntityType()) {
				return entityId;
			} else if (TrustedEntityType.CIS == teid.getEntityType()) {
				final CisDirCallback cisDirCallback = new CisDirCallback();
				this.cisDir.searchByID(entityId, cisDirCallback);
				synchronized (cisDirCallback) {
					cisDirCallback.wait(WAIT_CIS_DIR);
					final List<CisAdvertisementRecord> cisAds = cisDirCallback.getCisAds(); 
					if (cisAds != null && !cisAds.isEmpty() && cisAds.get(0).getName() != null) {
						return cisAds.get(0).getName();
					}
				}
			} else if (TrustedEntityType.SVC == teid.getEntityType()) {
				final org.societies.api.schema.servicelifecycle.model.Service service = 
						this.serviceDisco.getService(ServiceUtils
								.generateServiceResourceIdentifierFromString(entityId)).get();
				if (service != null && service.getServiceName() != null) {
					return service.getServiceName();
				}
			}
		} catch (Exception e) {

			LOG.warn("Could not format TEID '" + teid + "': " 
					+ e.getLocalizedMessage());
		}
		
		return teid.toString();
	}
	
	private class TrustActivity {
		
		private final String trusteeId;
		private final Double trustValue;
		
		private TrustActivity(final String trusteeId, final Double trustValue) {
			
			this.trusteeId = trusteeId;
			this.trustValue = trustValue;
		}
		
		private String getTrusteeId() {
			
			return this.trusteeId;
		}
		
		private Double getTrustValue() {
			
			return this.trustValue;
		}
		
		/*
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
	
			return "Trust level of " + this.trusteeId + " changed to "
					+ TrustValueFormat.formatPercent(this.trustValue);
		}
	}
	
	private class CisDirCallback implements ICisDirectoryCallback {

		private List<CisAdvertisementRecord> cisAds;
		
		/*
		 * @see org.societies.api.cis.directory.ICisDirectoryCallback#getResult(java.util.List)
		 */
		@Override
		public void getResult(List<CisAdvertisementRecord> cisAds) {
		
			LOG.debug("CisDirCallback.getResult: cisAds={}", cisAds);
			this.cisAds = cisAds;
			synchronized (this) {
	            this.notifyAll();
	        }
		}
		
		private List<CisAdvertisementRecord> getCisAds() {
			
			return this.cisAds;
		}
	}
}