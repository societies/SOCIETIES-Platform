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
package org.societies.privacytrust.trust.api.event;

import org.societies.api.privacytrust.trust.event.TrustEvent;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;

/**
 * This event is fired whenever a new piece of trust evidence is available. A 
 * <code>TrustEvidenceUpdateEvent</code> object is sent as an argument to the
 * {@link ITrustEvidenceUpdateEventListener} methods.
 * <p>
 * TrustEvidenceUpdateEvents are accompanied by the source, i.e. the 
 * {@link ITrustEvidence} object that was added to the trust evidence set. 
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public class TrustEvidenceUpdateEvent extends TrustEvent {

	private static final long serialVersionUID = 569610138360162405L;

	/**
	 * Constructs a <code>TrustEvidenceUpdateEvent</code> object with the
	 * specified source.
	 *  
	 * @param source
	 *            the {@link ITrustEvidence} object that was added to the trust
	 *            evidence set
	 */
	public TrustEvidenceUpdateEvent(final ITrustEvidence source) {
		
		super(source);
	}
	
	/**
	 * Returns the {@link ITrustEvidence} object that was added to the trust
	 * evidence set.
	 * 
	 * @return the {@link ITrustEvidence} object that was added to the trust
	 *         evidence set
	 * @since 0.3
	 */
	@Override
	public ITrustEvidence getSource() {
		
		return (ITrustEvidence) super.getSource();
	}
	
	/*
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {
				
		return this.getSource().toString();
	}
}