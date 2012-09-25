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
package org.societies.privacytrust.trust.impl.repo.model;

import javax.persistence.CascadeType;
//import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;

/**
 * This class represents trusted services. A TrustedService object is referenced
 * by its TrustedEntityId, while the associated Trust value objects express the
 * trustworthiness of this service, i.e. direct, indirect and user-perceived. Each
 * trusted service is also associated with a TrustedCSS which represents its
 * provider.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = TableName.TRUSTED_SERVICE, 
		uniqueConstraints = { @UniqueConstraint(columnNames = { "trustor_id", "trustee_id" }) }
)
public class TrustedService extends TrustedEntity implements ITrustedService {

	private static final long serialVersionUID = 8253551733059925542L;
	
	/** The type of this service. */
	//@Column(name = "type", nullable = false, updatable = false, length = 256)
	//private final String type;
	
	/** The CSS providing this service. */
	@ManyToOne(
			cascade = CascadeType.MERGE,
			targetEntity = TrustedCss.class,
			fetch = FetchType.EAGER,
			optional = true
	)
	@JoinColumn(
			name = TableName.TRUSTED_CSS + "_id",
			nullable = true,
			updatable = true
	)
	private ITrustedCss provider;
	
	/** The developer of this service. */
	//private ITrustedDeveloper developer;
	
	/* The communities sharing this service. */
	//private final Set<TrustedCis> communities = new HashSet<TrustedCis>();

	/* Empty constructor required by Hibernate */
	private TrustedService() {
		
		super(null);
		//this.type = null;
	}
	
	public TrustedService(final TrustedEntityId teid) {
		
		super(teid);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#getType()
	 *
	@Override
	public String getType() {
		
		return this.type;
	}*/
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#getProvider()
	 */
	@Override
	public ITrustedCss getProvider() {
		
		return this.provider;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#setProvider(org.societies.privacytrust.trust.api.model.ITrustedCss)
	 */
	@Override
	public void setProvider(ITrustedCss provider) {
		
		if (this.provider == null && provider != null) {
			
			if (!provider.getServices().contains(this))
				provider.getServices().add(this);
		} else if (this.provider != null) {
		
			if (this.provider.getServices().contains(this))
				this.provider.getServices().remove(this);
			
			if (provider != null && !provider.getServices().contains(this))
				provider.getServices().add(this);
		}
		
		this.provider = provider;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#getDeveloper()
	 *
	@Override
	public ITrustedDeveloper getDeveloper() {
		
		return this.developer;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#setDeveloper(org.societies.privacytrust.trust.api.model.TrustedDeveloper)
	 *
	@Override
	public void setDeveloper(ITrustedDeveloper developer) {
		
		this.developer = developer;
	}*/
}