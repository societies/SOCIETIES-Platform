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
package org.societies.api.privacytrust.trust.model;

import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
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
		teidBean.setEntityType(fromTrustedEntityType(teid.getEntityType()));
		teidBean.setEntityId(teid.getEntityId());
		
		return teidBean;
	}
	
	public TrustedEntityId fromTrustedEntityIdBean(TrustedEntityIdBean teidBean) throws MalformedTrustedEntityIdException {
		
		if (teidBean == null)
			throw new NullPointerException("teidBean can't be null");
		
		final TrustedEntityId teid = new TrustedEntityId(
				fromTrustedEntityTypeBean(teidBean.getEntityType()),
				teidBean.getEntityId());
		
		return teid;
	}
	
	public TrustedEntityTypeBean fromTrustedEntityType(TrustedEntityType trustedEntityType) {
		
		if (trustedEntityType == null)
			throw new NullPointerException("trustedEntityType can't be null");
		
		switch (trustedEntityType) {
		case CSS:
			return TrustedEntityTypeBean.CSS;
		case CIS:
			return TrustedEntityTypeBean.CIS;
		case SVC:
			return TrustedEntityTypeBean.SVC;
		case LGC:
			return TrustedEntityTypeBean.LGC;
		default:
			throw new IllegalArgumentException("'" + trustedEntityType 
					+ "': Unsupported trusted entity type");
		}
	}
	
	public TrustedEntityType fromTrustedEntityTypeBean(TrustedEntityTypeBean trustedEntityTypeBean) {
		
		if (trustedEntityTypeBean == null)
			throw new NullPointerException("trustedEntityTypeBean can't be null");
		
		switch (trustedEntityTypeBean) {
		case CSS:
			return TrustedEntityType.CSS;
		case CIS:
			return TrustedEntityType.CIS;
		case SVC:
			return TrustedEntityType.SVC;
		case LGC:
			return TrustedEntityType.LGC;
		default:
			throw new IllegalArgumentException("'" + trustedEntityTypeBean 
					+ "': Unsupported trusted entity type bean");
		}	
	}
	
	public TrustEvidenceTypeBean fromTrustEvidenceType(TrustEvidenceType trustEvidenceType) {
		
		if (trustEvidenceType == null)
			throw new NullPointerException("trustEvidenceType can't be null");
		
		switch (trustEvidenceType) {
		case USED_SERVICE:
			return TrustEvidenceTypeBean.USED_SERVICE;
		case FRIENDED_USER:
			return TrustEvidenceTypeBean.FRIENDED_USER;
		case UNFRIENDED_USER:
			return TrustEvidenceTypeBean.UNFRIENDED_USER;
		case JOINED_COMMUNITY:
			return TrustEvidenceTypeBean.JOINED_COMMUNITY;
		case LEFT_COMMUNITY:
			return TrustEvidenceTypeBean.LEFT_COMMUNITY;
		case RATED:
			return TrustEvidenceTypeBean.RATED;
		default:
			throw new IllegalArgumentException("'" + trustEvidenceType 
					+ "': Unsupported trust evidence type");
		}
	}
	
	public TrustEvidenceType fromTrustEvidenceTypeBean(TrustEvidenceTypeBean trustEvidenceTypeBean) {
		
		if (trustEvidenceTypeBean == null)
			throw new NullPointerException("trustEvidenceTypeBean can't be null");
		
		switch (trustEvidenceTypeBean) {
		case USED_SERVICE:
			return TrustEvidenceType.USED_SERVICE;
		case FRIENDED_USER:
			return TrustEvidenceType.FRIENDED_USER;
		case UNFRIENDED_USER:
			return TrustEvidenceType.UNFRIENDED_USER;
		case JOINED_COMMUNITY:
			return TrustEvidenceType.JOINED_COMMUNITY;
		case LEFT_COMMUNITY:
			return TrustEvidenceType.LEFT_COMMUNITY;
		case RATED:
			return TrustEvidenceType.RATED;
		default:
			throw new IllegalArgumentException("'" + trustEvidenceTypeBean 
					+ "': Unsupported trust evidence type bean");
		}	
	}
	
	/*
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}