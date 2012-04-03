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
package org.societies.privacytrust.trust.api.model;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This class represents trusted CSSs. A <code>TrustedCss</code> object is
 * referenced by its {@link TrustedEntityId}, while the associated 
 * {@link Trust} value objects express the trustworthiness of this CSS, i.e.
 * direct, indirect and user-perceived. Each trusted CSS is assigned a set of
 * {@link TrustedCis} objects representing the communities this CSS is member
 * of. In addition, the services provided by a TrustedCss are modelled as
 * {@link TrustedService} objects.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public class TrustedCss extends TrustedEntity {
	
	private static final long serialVersionUID = -5663024798098392757L;
	
	/** The communities this CSS is member of. */
	private final Set<TrustedCis> communities = new CopyOnWriteArraySet<TrustedCis>();
	
	/** The services provided by this CSS. */
	private final Set<TrustedService> services = new CopyOnWriteArraySet<TrustedService>();
	
	/**
	 * 
	 * @param trustor
	 * @param teid
	 */
	public TrustedCss(TrustedEntityId trustor, TrustedEntityId teid) {
		
		super(trustor, teid);
	}

	public Set<TrustedCis> getCommunities(){
		
		return this.communities;
	}
	
	/**
	 * 
	 * @param community
	 * @since 0.0.3
	 */
	public void addCommunity(final TrustedCis community) {
		
		if (!this.communities.contains(community))
			this.communities.add(community);
		
		if (!community.getMembers().contains(this))
			community.getMembers().add(this);
	}
	
	/**
	 * 
	 * @param community
	 * @since 0.0.3
	 */
	public void removeCommunity(final TrustedCis community) {
		
		if (this.communities.contains(community))
			this.communities.remove(community);
		
		if (community.getMembers().contains(this))
			community.getMembers().remove(this);
	}

	/**
	 * Returns a set containing the services provided by this CSS.
	 * 
	 * @return a set containing the services provided by this CSS.
	 */
	public Set<TrustedService> getServices() {
		
		return this.services;
	}

	/*
	 * TODO 
	 * @param serviceType
	 *
	public Set<TrustedService> getServices(String serviceType) {
		return null;
	}*/
	
	public void addService(final TrustedService service) {
		
		if (!this.services.contains(service))
			this.services.add(service);
	}
}