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
package org.societies.api.internal.privacytrust.trust.remote;

import org.societies.api.internal.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.internal.schema.privacytrust.trust.model.TrustedEntityTypeBean;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

public final class TrustModelBeanTranslator {
	
	private static TrustModelBeanTranslator instance = new TrustModelBeanTranslator();
	
	/*
	 * Prevents instantiation
	 */
	private TrustModelBeanTranslator() {}
	
	public static synchronized TrustModelBeanTranslator getInstance() {
		
		return instance;
	}
	
	public TrustedEntityIdBean fromTrustedEntityId(TrustedEntityId teid) {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		
		final TrustedEntityIdBean teidBean = new TrustedEntityIdBean();
		teidBean.setTrustorId(teid.getTrustorId());
		teidBean.setEntityType(fromTrustedEntityType(teid.getEntityType()));
		teidBean.setTrusteeId(teid.getTrusteeId());
		
		return teidBean;
	}
	
	public TrustedEntityId fromTrustedEntityIdBean(TrustedEntityIdBean teidBean) throws MalformedTrustedEntityIdException {
		
		if (teidBean == null)
			throw new NullPointerException("teidBean can't be null");
		
		final TrustedEntityId teid = new TrustedEntityId(
				teidBean.getTrustorId(),
				fromTrustedEntityTypeBean(teidBean.getEntityType()),
				teidBean.getTrusteeId());
		
		return teid;
	}
	
	public TrustedEntityTypeBean fromTrustedEntityType(TrustedEntityType trustedEntityType) {
		
		if (trustedEntityType == null)
			throw new NullPointerException("trustedEntityType can't be null");
		
		return TrustedEntityTypeBean.valueOf(trustedEntityType.toString());	
	}
	
	public TrustedEntityType fromTrustedEntityTypeBean(TrustedEntityTypeBean trustedEntityTypeBean) {
		
		if (trustedEntityTypeBean == null)
			throw new NullPointerException("trustedEntityTypeBean can't be null");
		
		return TrustedEntityType.valueOf(trustedEntityTypeBean.toString());	
	}
	
	/*
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}