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
package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.privacytrust.trust.model.util.TrustValueFormat;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.services.ServiceUtils;
import org.societies.webapp.models.TrustedEntity;
import org.societies.webapp.service.UserService;

@ManagedBean(name = "trustController")
@ViewScoped
public class TrustController extends BasePageController {

	private static final long serialVersionUID = -1250855340010366453L;

	private static Logger LOG = LoggerFactory.getLogger(TrustController.class);
	
	/** The time to wait for CIS Directory responses in milliseconds. */
	private static final long WAIT_CIS_DIR = 1000l;

	@ManagedProperty(value = "#{trustBroker}")
	private ITrustBroker trustBroker;
	
	@ManagedProperty(value = "#{trustEvidenceCollector}")
	private ITrustEvidenceCollector trustEvidenceCollector;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	
	/** The CIS Directory service reference. */
	@ManagedProperty(value = "#{cisDirectoryRemote}")
	private ICisDirectoryRemote cisDirectory;
	
	/** The Service Discovery service reference. */
	@ManagedProperty(value = "#{serviceDiscovery}")
	private IServiceDiscovery serviceDiscovery;
	
	private List<TrustedEntity> users = new ArrayList<TrustedEntity>();
	private List<TrustedEntity> filteredUsers = new ArrayList<TrustedEntity>();
	
	private List<TrustedEntity> communities = new ArrayList<TrustedEntity>();
	private List<TrustedEntity> filteredCommunities = new ArrayList<TrustedEntity>();
	
	private List<TrustedEntity> services = new ArrayList<TrustedEntity>();
	private List<TrustedEntity> filteredServices = new ArrayList<TrustedEntity>();
	
	private TrustedEntity selectedEntity;
	
	public TrustController() {
		// controller constructor - called every time this page is requested!
		LOG.info("{} instantiated", this.getClass());
	}
	
	@PostConstruct
	public void init() {
		
		LOG.debug("init");
		
		this.users.addAll(this.retrieveTrustedEntities(TrustedEntityType.CSS));
		this.filteredUsers.addAll(this.users);
		
		this.communities.addAll(this.retrieveTrustedEntities(TrustedEntityType.CIS));
		this.filteredCommunities.addAll(this.communities);
		
		this.services.addAll(this.retrieveTrustedEntities(TrustedEntityType.SVC));
		this.filteredServices.addAll(this.services);
	}

	public ITrustBroker getTrustBroker() {

		return this.trustBroker;
	}

	public void setTrustBroker(ITrustBroker trustBroker) {

		this.trustBroker = trustBroker;
	}
	
	public ITrustEvidenceCollector getTrustEvidenceCollector() {

		return this.trustEvidenceCollector;
	}

	public void setTrustEvidenceCollector(ITrustEvidenceCollector trustEvidenceCollector) {

		this.trustEvidenceCollector = trustEvidenceCollector;
	}

	public UserService getUserService() {

		return this.userService;
	}

	public void setUserService(UserService userService) {

		this.userService = userService;
	}
	
	public ICisDirectoryRemote getCisDirectory() {

		return this.cisDirectory;
	}

	public void setCisDirectory(ICisDirectoryRemote cisDirectory) {

		this.cisDirectory = cisDirectory;
	}
	
	public IServiceDiscovery getServiceDiscovery() {

		return this.serviceDiscovery;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {

		this.serviceDiscovery = serviceDiscovery;
	}
	
	public List<TrustedEntity> getUsers() {
		
		LOG.debug("getUsers");
		return this.users;
	}
	
	public List<TrustedEntity> getFilteredUsers() {
		
        return this.filteredUsers;  
    }  
  
    public void setFilteredUsers(List<TrustedEntity> filteredUsers) {
    	
        this.filteredUsers = filteredUsers;  
    }
	
	public List<TrustedEntity> getCommunities() {
		
		LOG.debug("getCommunities");
		return this.communities;
	}
	
	public List<TrustedEntity> getFilteredCommunities() {
		
        return this.filteredCommunities;  
    }  
  
    public void setFilteredCommunities(List<TrustedEntity> filteredCommunities) {
    	
        this.filteredCommunities = filteredCommunities;  
    }
	
	public List<TrustedEntity> getServices() {
		
		LOG.debug("getServices");
		return this.services;
	}
	
	public List<TrustedEntity> getFilteredServices() {
		
        return this.filteredServices;  
    }  
  
    public void setFilteredServices(List<TrustedEntity> filteredServices) {
    	
        this.filteredServices = filteredServices;  
    }
    
    public TrustedEntity getSelectedEntity() {
    	
    	LOG.debug("getSelectedEntity={}", this.selectedEntity);
    	return this.selectedEntity;
    }
    
    public void setSelectedEntity(TrustedEntity selectedEntity) {
    	
    	LOG.debug("setSelectedEntity={}", selectedEntity);
    	this.selectedEntity = selectedEntity;
    }
    
    public void onRating(RateEvent rateEvent) {  
        
    	LOG.debug("onRating: event={}", rateEvent);
    	
    	try {
    		if (this.selectedEntity == null) {
    			throw new IllegalStateException("No trusted entity selected!");
    		}
    		final TrustedEntityId ratedTeid = this.selectedEntity.getTrusteeId();
    		final Double rating = 0.2d * new Double((Integer) rateEvent.getRating());
    		this.updateTrustRating(ratedTeid, rating);
    		
    	} catch (Exception e) {

    		super.addGlobalMessage("A surprising new problem has occurred!", 
    				e.getLocalizedMessage(), FacesMessage.SEVERITY_ERROR);
    		return;
    	}
    }
	
	private List<TrustedEntity> retrieveTrustedEntities(
			final TrustedEntityType entityType) {
		
		final List<TrustedEntity> result = new ArrayList<TrustedEntity>();
		
		if (this.userService.isUserLoggedIn()) {
			try {
				final TrustedEntityId myTeid = new TrustedEntityId(
						TrustedEntityType.CSS, this.userService.getUserID());
				final List<ExtTrustRelationship> dbResult = new ArrayList<ExtTrustRelationship>();
				dbResult.addAll(this.trustBroker.retrieveExtTrustRelationships(
						new TrustQuery(myTeid).setTrusteeType(entityType)).get());
				final Map<TrustedEntityId, TrustedEntity> trustedEntities = 
						new HashMap<TrustedEntityId, TrustedEntity>();
				for (final ExtTrustRelationship tr : dbResult) {
					// Omit *my* CSS from the list of trusted entities!
					if (myTeid.equals(tr.getTrusteeId())) {
						continue;
					}
					TrustedEntity trustedEntity = trustedEntities.get(tr.getTrusteeId()); 
					if (trustedEntity == null) {
						final String trusteeName = this.formatTeid(tr.getTrusteeId());
						trustedEntity = new TrustedEntity(myTeid, tr.getTrusteeId(), trusteeName);
					}
					if (TrustValueType.DIRECT == tr.getTrustValueType()) {
						trustedEntity.getDirectTrust().setValue(tr.getTrustValue());
						trustedEntity.getDirectTrust().setStringValue(
								this.formatTrustValue(tr.getTrustValue()));
						trustedEntity.getDirectTrust().setLastUpdated(tr.getTimestamp());
					} else if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
						trustedEntity.getIndirectTrust().setValue(tr.getTrustValue());
						trustedEntity.getIndirectTrust().setStringValue(
								this.formatTrustValue(tr.getTrustValue()));
						trustedEntity.getIndirectTrust().setLastUpdated(tr.getTimestamp());
					} else if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
						trustedEntity.getUserPerceivedTrust().setValue(tr.getTrustValue());
						trustedEntity.getUserPerceivedTrust().setStringValue(
								this.formatTrustValue(tr.getTrustValue()));
						trustedEntity.getUserPerceivedTrust().setLastUpdated(tr.getTimestamp());
					}
					LOG.debug("retrieveTrustedEntities: trusteeId={}, evidence={}", tr.getTrusteeId(), tr.getTrustEvidence());
					for (final TrustEvidence evidence : tr.getTrustEvidence()) {
						// Handle RATED evidence
						if (TrustEvidenceType.RATED == evidence.getType() && evidence.getInfo() instanceof Double) {
							final Double dblRating = (Double) evidence.getInfo() * 5.0d;
							final Integer intRating = dblRating.intValue();
							LOG.debug("retrieveTrustedEntities: trusteeId={}, rating={}", tr.getTrusteeId(), intRating);
							trustedEntity.setRating(intRating);
						}
					}
					trustedEntities.put(tr.getTrusteeId(), trustedEntity);
				}
				result.addAll(trustedEntities.values());
			} catch (Exception e) {

				super.addGlobalMessage("A surprising new problem has occurred!", 
						e.getLocalizedMessage(), FacesMessage.SEVERITY_ERROR);
			}
		} // end if userIsLoggedIn
		
		return result;
	}
	
	private String formatTeid(final TrustedEntityId teid) {

		final String entityId = teid.getEntityId();
		try {
			if (TrustedEntityType.CSS == teid.getEntityType()) {
				return entityId;
			} else if (TrustedEntityType.CIS == teid.getEntityType()) {
				final CisDirCallback cisDirCallback = new CisDirCallback();
				this.cisDirectory.searchByID(entityId, cisDirCallback);
				synchronized (cisDirCallback) {
					cisDirCallback.wait(WAIT_CIS_DIR);
					final List<CisAdvertisementRecord> cisAds = cisDirCallback.getCisAds(); 
					if (cisAds != null && !cisAds.isEmpty() && cisAds.get(0).getName() != null) {
						return cisAds.get(0).getName();
					}
				}
			} else if (TrustedEntityType.SVC == teid.getEntityType()) {
				final org.societies.api.schema.servicelifecycle.model.Service service = 
						this.serviceDiscovery.getService(ServiceUtils
								.generateServiceResourceIdentifierFromString(entityId)).get();
				if (service != null && service.getServiceName() != null) {
					return service.getServiceName();
				}
			}
		} catch (Exception e) {

			LOG.warn("Could not format TEID '" + teid + "': " 
					+ e.getLocalizedMessage());
			return entityId;
		}
		
		return teid.toString();
	}
	
	private String formatTrustValue(Double trustValue) {
		
		return TrustValueFormat.formatPercent(trustValue);
	}
	
	private void updateTrustRating(final TrustedEntityId ratedTeid,
			final Double rating) {
		
		if (this.userService.isUserLoggedIn()) {
    		try {
    			final TrustedEntityId myTeid = new TrustedEntityId(
						TrustedEntityType.CSS, this.userService.getUserID());
    			final CountDownLatch cdLatch = new CountDownLatch(1);
    			final TrustUpdateListener listener = new TrustUpdateListener(cdLatch);
    			this.trustBroker.registerTrustUpdateListener(listener, 
    					new TrustQuery(myTeid).setTrusteeId(ratedTeid)
    							.setTrustValueType(TrustValueType.USER_PERCEIVED));
    			LOG.debug("Adding trust evidence: '{}' rated '{}' with {}",
    					new Object[] { myTeid, ratedTeid, rating });
    			this.trustEvidenceCollector.addDirectEvidence(myTeid, ratedTeid,
    					TrustEvidenceType.RATED, new Date(), rating);
    			cdLatch.await(2, TimeUnit.SECONDS);
    			this.trustBroker.unregisterTrustUpdateListener(listener, 
    					new TrustQuery(myTeid).setTrusteeId(ratedTeid)
    							.setTrustValueType(TrustValueType.USER_PERCEIVED));
    			if (TrustedEntityType.CSS == ratedTeid.getEntityType()) {
    				this.users = this.retrieveTrustedEntities(TrustedEntityType.CSS);
    			} else if (TrustedEntityType.CIS == ratedTeid.getEntityType()) {
    				this.communities = this.retrieveTrustedEntities(TrustedEntityType.CIS);
    			} else if (TrustedEntityType.SVC == ratedTeid.getEntityType()) {
    				this.services = this.retrieveTrustedEntities(TrustedEntityType.SVC);
    			}
    			
    		} catch (Exception e) {

    			super.addGlobalMessage("A surprising new problem has occurred!", 
    					e.getLocalizedMessage(), FacesMessage.SEVERITY_ERROR);
    			return;
    		}
    		super.addGlobalMessage("Thanks for your feedback", 
					"We love you!", FacesMessage.SEVERITY_INFO);
    	} // end if userIsLoggedIn
	}
	
	private class TrustUpdateListener implements ITrustUpdateEventListener {
		
		private final CountDownLatch cdLatch;
		
		private TrustUpdateListener(CountDownLatch cdLatch) {
			
			this.cdLatch = cdLatch;
		}
		
		/*
		 * @see org.societies.api.privacytrust.trust.event.ITrustUpdateEventListener#onUpdate(org.societies.api.privacytrust.trust.event.TrustUpdateEvent)
		 */
		@Override
		public void onUpdate(TrustUpdateEvent trustUpdateEvent) {
			
			LOG.debug("onUpdate: trustUpdateEvent={}", trustUpdateEvent);
			this.cdLatch.countDown();
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