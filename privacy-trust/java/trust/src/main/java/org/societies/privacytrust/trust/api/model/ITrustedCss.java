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

import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This interface represents trusted CSSs. An <code>ITrustedCss</code> object is
 * referenced by its {@link TrustedEntityId}, while the associated 
 * {@link Trust} value objects express the trustworthiness of this CSS, i.e.
 * direct, indirect and user-perceived. Each trusted CSS is assigned a set of
 * {@link TrustedCis} objects representing the communities this CSS is member
 * of. In addition, the services provided by a TrustedCss are modelled as
 * {@link TrustedService} objects.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.7
 */
public interface ITrustedCss extends ITrustedEntity {

	/**
	 * Returns a set containing the communities this CSS is member of.
	 * 
	 * @return a set containing the communities this CSS is member of.
	 */
	public Set<ITrustedCis> getCommunities();

	/**
	 * 
	 * @param community
	 */
	public void addCommunity(final ITrustedCis community);

	/**
	 * 
	 * @param community
	 */
	public void removeCommunity(final ITrustedCis community);

	/**
	 * Returns a set containing the services provided by this CSS.
	 * 
	 * @return a set containing the services provided by this CSS.
	 */
	public Set<ITrustedService> getServices();
	
	/*
	 * TODO 
	 * @param serviceType
	 *
	public Set<TrustedService> getServices(String serviceType) {
		return null;
	}*/
}