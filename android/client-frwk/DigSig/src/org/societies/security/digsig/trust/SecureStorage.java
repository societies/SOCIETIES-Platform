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
package org.societies.security.digsig.trust;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.societies.security.digsig.apiinternal.ISecureStorage;
import org.societies.security.digsig.sign.DigSigException;

import android.os.Build;
import android.util.Log;

/**
 * Wrapper class for {@link SecureStorageFor422} and {@link SecureStorageFor43}.
 * The appropriate implementation is selected automatically.
 *
 * @author Mitja Vardjan
 */
public class SecureStorage implements ISecureStorage {

	private static final String TAG = SecureStorage.class.getSimpleName();
	
	private ISecureStorage impl;

	public SecureStorage() throws DigSigException {
		if (hasNewApi()) {
			this.impl = new SecureStorageFor43();
		}
		else {
			this.impl = new SecureStorageFor422();
		}
	}
	
	private boolean hasNewApi() {
		
		boolean result = Build.VERSION.SDK_INT >= 18;
		Log.d(TAG, "hasNewApi: " + result);
		return result;
	}
	
	@Override
	public X509Certificate getCertificate(int index) {
		return impl.getCertificate(index);
	}

	@Override
	public PrivateKey getPrivateKey(int index) {
		return impl.getPrivateKey(index);
	}

	@Override
	public int put(X509Certificate certificate, PrivateKey key) {
		return impl.put(certificate, key);
	}
	
	@Override
	public boolean isReady() {
		return impl.isReady();
	}
	
	@Override
	public boolean containsCertificateAndKey(int index) {
		return impl.containsCertificateAndKey(index);
	}
}
