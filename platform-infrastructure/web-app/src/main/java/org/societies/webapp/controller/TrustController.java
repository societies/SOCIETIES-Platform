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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.webapp.models.TrustedEntity;
import org.societies.webapp.service.UserService;

@ManagedBean(name = "trustController")
@RequestScoped
public class TrustController extends BasePageController {

	private static final long serialVersionUID = -1250855340010366453L;

	private static Logger LOG = LoggerFactory.getLogger(TrustController.class);

	@ManagedProperty(value = "#{trustBroker}")
	private ITrustBroker trustBroker;

	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	
	private final List<TrustedEntity> users = new ArrayList<TrustedEntity>();
	private List<TrustedEntity> filteredUsers = new ArrayList<TrustedEntity>();
	
	private final List<TrustedEntity> communities = new ArrayList<TrustedEntity>();
	private List<TrustedEntity> filteredCommunities = new ArrayList<TrustedEntity>();
	
	private final List<TrustedEntity> services = new ArrayList<TrustedEntity>();
	private List<TrustedEntity> filteredServices = new ArrayList<TrustedEntity>();
	
	public TrustController() {
		// controller constructor - called every time this page is requested!
		if (LOG.isDebugEnabled())
			LOG.debug(this.getClass().getName() + " instantiated");
	}
	
	@PostConstruct
	public void init() {
		
		if (LOG.isDebugEnabled())
			LOG.debug(this.getClass().getName() + " initialising");
		
		this.users.addAll(this.retrieveTrustedEntities(TrustedEntityType.CSS));
		this.filteredUsers.addAll(this.users);
		
		this.communities.addAll(this.retrieveTrustedEntities(TrustedEntityType.CIS));
		this.filteredCommunities.addAll(this.communities);
		
		this.services.addAll(this.retrieveTrustedEntities(TrustedEntityType.SVC));
		this.filteredServices.addAll(this.services);
	}

	public ITrustBroker getTrustBroker() {

		return trustBroker;
	}

	public void setTrustBroker(ITrustBroker trustBroker) {

		this.trustBroker = trustBroker;
	}

	public UserService getUserService() {

		return userService;
	}

	public void setUserService(UserService userService) {

		this.userService = userService;
	}
	
	public List<TrustedEntity> getUsers() {
		
		if (LOG.isDebugEnabled())
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
		
		if (LOG.isDebugEnabled())
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
		
		if (LOG.isDebugEnabled())
			LOG.debug("getServices");
		return this.services;
	}
	
	public List<TrustedEntity> getFilteredServices() {
		
        return this.filteredServices;  
    }  
  
    public void setFilteredServices(List<TrustedEntity> filteredServices) {
    	
        this.filteredServices = filteredServices;  
    }
	
	private List<TrustedEntity> retrieveTrustedEntities(TrustedEntityType entityType) {
		
		final List<TrustedEntity> result = new ArrayList<TrustedEntity>();
		
		if (this.userService.isUserLoggedIn()) {
			try {
				final TrustedEntityId myTeid = new TrustedEntityId(
						TrustedEntityType.CSS, this.userService.getUserID());
				final List<TrustRelationship> dbResult = new ArrayList<TrustRelationship>();
				dbResult.addAll(this.trustBroker.retrieveTrustRelationships(myTeid, entityType).get());
				final Map<TrustedEntityId, TrustedEntity> trustedEntities = 
						new HashMap<TrustedEntityId, TrustedEntity>();
				for (final TrustRelationship tr : dbResult) {
					TrustedEntity trustedEntity = trustedEntities.get(tr.getTrusteeId()); 
					if (trustedEntity == null)
						trustedEntity = new TrustedEntity(myTeid, tr.getTrusteeId());
					if (TrustValueType.DIRECT == tr.getTrustValueType()) {
						trustedEntity.getDirectTrust().setValue(tr.getTrustValue());
						trustedEntity.getDirectTrust().setLastUpdated(tr.getTimestamp());
					} else if (TrustValueType.INDIRECT == tr.getTrustValueType()) {
						trustedEntity.getIndirectTrust().setValue(tr.getTrustValue());
						trustedEntity.getIndirectTrust().setLastUpdated(tr.getTimestamp());
					} else if (TrustValueType.USER_PERCEIVED == tr.getTrustValueType()) {
						trustedEntity.getUserPerceivedTrust().setValue(tr.getTrustValue());
						trustedEntity.getUserPerceivedTrust().setLastUpdated(tr.getTimestamp());
					}
					trustedEntities.put(tr.getTrusteeId(), trustedEntity);
				}
				result.addAll(trustedEntities.values());
			} catch (Exception e) {

				super.addGlobalMessage("A surprising new error has occurred!", 
						e.getLocalizedMessage(), FacesMessage.SEVERITY_ERROR);
			}
		} // end if userIsLoggedIn
		
		return result;
	}
}