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
package org.societies.privacytrust.trust.impl.common.hibernate.event;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.privacytrust.trust.event.TrustUpdateEvent;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.impl.repo.model.Trust;
import org.societies.privacytrust.trust.impl.repo.model.TrustedEntity;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Service
public class PostUpdateEventUserListener implements PostUpdateEventListener {

	private static final long serialVersionUID = -2738207177168793008L;
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(PostUpdateEventUserListener.class);
	
	private static final Map<String, TrustValueType> TRUST_PROPERTY_MAP;
    
	static {
		
        final Map<String, TrustValueType> aMap = new HashMap<String, TrustValueType>();
        aMap.put("directTrust", TrustValueType.DIRECT);
        aMap.put("indirectTrust", TrustValueType.INDIRECT);
        aMap.put("userPerceivedTrust", TrustValueType.USER_PERCEIVED);
        TRUST_PROPERTY_MAP = Collections.unmodifiableMap(aMap);
    }

	PostUpdateEventUserListener() {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.hibernate.event.PostUpdateEventListener#onPostUpdate(org.hibernate.event.PostUpdateEvent)
	 */
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		
		if (event.getEntity() instanceof TrustedEntity) {
		
			final String[] propNames = event.getPersister().getPropertyNames();
			
			// iterate over property names, extract corresponding property values
	        for (int i = 0; i < propNames.length; ++i) {
	 
	            final String propName = propNames[i];
	            if (!TRUST_PROPERTY_MAP.containsKey(propName))
	            	continue;
	            
	            final Trust oldTrust = (Trust) event.getOldState()[i];
	            final Trust newTrust = (Trust) event.getState()[i];
	            if (areDifferent(oldTrust, newTrust)) {
	            	if (LOG.isDebugEnabled())
	            		LOG.debug(propName + ": Old trust=" + oldTrust + ", New trust=" + newTrust);
	            	final TrustedEntity entity = (TrustedEntity) event.getEntity();
	            	final TrustedEntityId trustorId = entity.getTrustorId();
	            	final TrustedEntityId trusteeId = entity.getTrusteeId();
	            	final TrustValueType trustValueType = TRUST_PROPERTY_MAP.get(propName);
	            	final Double newTrustValue = (newTrust != null) 
	            			? (newTrust).getValue() : null;
	            	if (newTrustValue == null)  {
	            		if (LOG.isDebugEnabled())
	            			LOG.debug("Skipping TrustUpdateEvent: New "
	            					+ propName + " value is null");
	            		continue;
	            	}
	            	final Date timestamp;
	            	if (TrustValueType.DIRECT == trustValueType)
	            		timestamp = entity.getDirectTrust().getLastUpdated();
	            	else if (TrustValueType.INDIRECT == trustValueType)
	            		timestamp = entity.getIndirectTrust().getLastUpdated();
	            	else // if (TrustValueType.USER_PERCEIVED == trustValueType)
	            		timestamp = entity.getUserPerceivedTrust().getLastUpdated();
	            	
	            	final TrustUpdateEvent trustUpdateEvent = new TrustUpdateEvent(
	            			new TrustRelationship(trustorId, trusteeId, 
	            					trustValueType, newTrustValue, timestamp));
	            	
	            	if (LOG.isDebugEnabled())
	            		LOG.debug("Queueing TrustUpdateEvent " + trustUpdateEvent);
	            	entity.getUpdateEventQueue().add(trustUpdateEvent);
	            }
	        }
		}
	}
	
	private static boolean areDifferent(final Trust x, final Trust y) {
		
		if (x == y)
			return false;
		
		if (x == null || y == null)
			return true;

		if (x.getValue() == null) {
			if (y.getValue() != null)
				return true;
		} else if (!x.getValue().equals(y.getValue()))
			return true;
		
		return false;
	}
}