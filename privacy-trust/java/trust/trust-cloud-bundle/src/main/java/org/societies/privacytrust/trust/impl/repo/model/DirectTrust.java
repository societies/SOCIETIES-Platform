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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.societies.privacytrust.trust.api.model.IDirectTrust;

/**
 * Implementation of the {@link IDirectTrust} interface.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@Embeddable
public class DirectTrust extends Trust implements IDirectTrust {

	private static final long serialVersionUID = 2604976855460869815L;
	
	@Column(name = "direct_rating")
	private Double rating;
	
	@Column(name = "direct_score")
	private Double score = INIT_SCORE;

	/*
	 * @see org.societies.privacytrust.trust.api.model.IDirectTrust#getRating()
	 */
	@Override
	public Double getRating() {
		
		return this.rating;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.IDirectTrust#setRating(java.lang.Double)
	 */
	@Override
	public void setRating(Double rating) {
		
		this.rating = rating;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.IDirectTrust#getScore()
	 */
	@Override
	public Double getScore() {
		
		return this.score;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.IDirectTrust#setScore(java.lang.Double)
	 */
	@Override
	public void setScore(Double score) {
		
		this.score = score;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append("value=" + super.value);
		sb.append(",");
		sb.append("lastModified=" + super.lastModified);
		sb.append(",");
		sb.append("lastUpdated=" + super.lastUpdated);
		sb.append(",");
		sb.append("rating=" + this.rating);
		sb.append(",");
		sb.append("score=" + this.score);
		sb.append(">");
		
		return sb.toString();
	}
}