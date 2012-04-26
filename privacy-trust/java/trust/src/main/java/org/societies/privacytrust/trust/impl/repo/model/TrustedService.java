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

import javax.persistence.Entity;
import javax.persistence.Table;

import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedDeveloper;
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
@Table(name="t_services")
public class TrustedService extends TrustedEntity implements ITrustedService {

	private static final long serialVersionUID = 8253551733059925542L;
	
	/** The CSS providing this service. */
	private final ITrustedCss provider;
	
	/* The communities sharing this service. */
	//private final Set<TrustedCis> communities = new CopyOnWriteArraySet<TrustedCis>();
	
	/** The type of this service. */
	private final String type;
	
	/** The developer of this service. */
	private ITrustedDeveloper developer;

	public TrustedService(TrustedEntityId teid, String type, ITrustedCss provider) {
		
		super(teid);
		this.type = type;
		this.provider = provider;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#getType()
	 */
	@Override
	public String getType() {
		
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#getProvider()
	 */
	@Override
	public ITrustedCss getProvider() {
		
		return this.provider;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#getDeveloper()
	 */
	@Override
	public ITrustedDeveloper getDeveloper() {
		
		return this.developer;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedService#setDeveloper(org.societies.privacytrust.trust.api.model.TrustedDeveloper)
	 */
	@Override
	public void setDeveloper(ITrustedDeveloper developer) {
		
		this.developer = developer;
	}
}