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
package org.societies.api.privacytrust.trust.event;

import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This event is fired whenever a trust value is updated. A 
 * <code>TrustUpdateEvent</code> object is sent as an argument to the
 * {@link ITrustUpdateEventListener} methods.
 * <p>
 * Normally, TrustUpdateEvents are accompanied by the updated 
 * {@link TrustRelationship} which contains the {@link TrustedEntityId
 * identifiers} of the trustor and the trustee whose trust value was updated,
 * the {@link TrustValueType type} of the value, as well as, the time and date
 * when the value was updated. 
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.2
 */
public class TrustUpdateEvent extends TrustEvent {

	private static final long serialVersionUID = -4292086499526921194L;
	
	/**
	 * Constructs a <code>TrustUpdateEvent</code> object for the specified
	 * trust relationship.
	 *  
	 * @param trustRelationship
	 *            the trust relationship that was updated.
	 * @throws IllegalArgumentException
	 *            if the specified trustRelationship is <code>null</code>.
	 * @since 1.0
	 */
	public TrustUpdateEvent(final TrustRelationship trustRelationship) {
		
		super(trustRelationship);
	}
	
	/**
	 * Returns the updated trust relationship.
	 * 
	 * @return the updated trust relationship.
	 * @since 1.0
	 */
	public TrustRelationship getTrustRelationship() {
		
		return (TrustRelationship) super.getSource();
	}
	
	/*
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("TrustUpdateEvent {");
		sb.append(this.getTrustRelationship());
		sb.append("}");
		
		return sb.toString();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		result = prime * result
				+ ((this.getTrustRelationship() == null) ? 0 : this.getTrustRelationship().hashCode());
		
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		final TrustUpdateEvent other = (TrustUpdateEvent) that;
		if (this.getTrustRelationship() == null) {
			if (other.getTrustRelationship() != null)
				return false;
		} else if (!this.getTrustRelationship().equals(other.getTrustRelationship()))
			return false;
		
		return true;
	}
}