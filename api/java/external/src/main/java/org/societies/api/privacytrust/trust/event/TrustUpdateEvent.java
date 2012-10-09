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

import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This event is fired whenever a trust value is updated. A 
 * <code>TrustUpdateEvent</code> object is sent as an argument to the
 * {@link ITrustUpdateEventListener} methods.
 * <p>
 * Normally, TrustUpdateEvents are accompanied by the source, i.e. the 
 * {@link TrustedEntityId identifier} of the entity whose trust value was
 * updated, as well as, the old and new value. 
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.2
 */
public class TrustUpdateEvent extends TrustEvent {

	private static final long serialVersionUID = -4292086499526921194L;

	/** The old trust value */
	private final Double oldValue;
	
	/** The new trust value */
	private final Double newValue;
	
	/**
	 * Constructs a <code>TrustUpdateEvent</code> object with the specified
	 * source, old and new trust value.
	 *  
	 * @param source
	 *            the identifier of the entity whose trust value was updated
	 * @param oldValue
	 *            the old trust value
	 * @param newValue
	 *            the new trust value
	 */
	public TrustUpdateEvent(TrustedEntityId source, Double oldValue, Double newValue) {
		
		super(source);
		
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/**
	 * Returns the identifier of the entity whose trust value was updated.
	 * 
	 * @return the identifier of the entity whose trust value was updated.
	 * @since 0.0.7
	 */
	public TrustedEntityId getId() {
		
		return (TrustedEntityId) super.getSource();
	}
	
	/**
	 * Returns the old trust value.
	 * 
	 * @return the old trust value.
	 */
	public Double getOldValue() {
		
		return this.oldValue;
	}
	
	/**
	 * Returns the new trust value.
	 * 
	 * @return the new trust value.
	 */
	public Double getNewValue() {
		
		return this.newValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=" + this.getId());
		sb.append(", ");
		sb.append("oldValue=" + this.getOldValue());
		sb.append(", ");
		sb.append("newValue=" + this.getNewValue());
		sb.append("}");
		
		return sb.toString();
	}
}