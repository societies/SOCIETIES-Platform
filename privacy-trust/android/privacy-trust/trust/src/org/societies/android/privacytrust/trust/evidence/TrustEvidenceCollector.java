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
package org.societies.android.privacytrust.trust.evidence;

import java.io.Serializable;
import java.util.Date;

import org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public class TrustEvidenceCollector extends Service 
	implements ITrustEvidenceCollector {
	
	private static final String TAG = TrustEvidenceCollector.class.getName();
	
	private IBinder binder;

	/*
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate () {
		
		Log.i(TAG, "Starting");
		this.binder = new LocalBinder();
	}
	
	/*
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		
		return this.binder;
	}
	
	/*
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		
		Log.i(TAG, "Stopping");
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	public void addDirectEvidence(final TrustedEntityId teid, 
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info) throws TrustException {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("Add direct evidence:");
		sb.append("teid=");
		sb.append(teid);
		sb.append("type=");
		sb.append(type);
		sb.append("timestamp=");
		sb.append(timestamp);
		sb.append("info=");
		sb.append(info);
		
		Log.i(TAG, sb.toString());
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addIndirectEvidence(java.lang.String, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	public void addIndirectEvidence(final String source, 
			final TrustedEntityId teid, final TrustEvidenceType type,
			final Date timestamp, final Serializable info) 
					throws TrustException {
		
		if (source == null)
			throw new NullPointerException("source can't be null");
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		// TODO Auto-generated method stub
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity, double, java.util.Date)
	 */
	public void addTrustRating(final IIdentity trustor, 
			final IIdentity trustee, final double rating, Date timestamp)
					throws TrustException {
		
		if (trustor == null)
			throw new NullPointerException("trustor can't be null");
		if (trustee == null)
			throw new NullPointerException("trustee can't be null");
		
		if (!IdentityType.CSS.equals(trustor.getType()))
			throw new IllegalArgumentException("trustor is not a CSS");
		if (!IdentityType.CSS.equals(trustee.getType()) && !IdentityType.CIS.equals(trustee.getType()))
			throw new IllegalArgumentException("trustee is neither a CSS nor a CIS");
		if (rating < 0d || rating > 1d)
			throw new IllegalArgumentException("rating out of range [0,1]");
		
		// if timestamp is null assign current time
		if (timestamp == null)
			timestamp = new Date();
		
		// TODO Auto-generated method stub
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, double, java.util.Date)
	 */
	public void addTrustRating(final IIdentity trustor, 
			final ServiceResourceIdentifier trustee, final double rating,
			Date timestamp) throws TrustException {
		
		if (trustor == null)
			throw new NullPointerException("trustor can't be null");
		if (trustee == null)
			throw new NullPointerException("trustee can't be null");
		
		if (!IdentityType.CSS.equals(trustor.getType()))
			throw new IllegalArgumentException("trustor is not a CSS");
		if (rating < 0d || rating > 1d)
			throw new IllegalArgumentException("rating is not in the range of [0,1]");
		
		// if timestamp is null assign current time
		if (timestamp == null)
			timestamp = new Date();
		
		// TODO Auto-generated method stub		
	}
	
	public class LocalBinder extends Binder {
		
		TrustEvidenceCollector getService() {
			return TrustEvidenceCollector.this;
		}
	}
}