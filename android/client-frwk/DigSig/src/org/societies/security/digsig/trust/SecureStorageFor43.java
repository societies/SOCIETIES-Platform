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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.societies.security.digsig.apiinternal.ISecureStorage;
import org.societies.security.digsig.sign.DigSigException;

import android.util.Log;

/**
 * Helper class for retrieving X.509 certificates and private keys from android secure storage.
 * 
 * This class is not public.
 *
 * @author Mitja Vardjan
 *
 */
class SecureStorageFor43 implements ISecureStorage {

	private static final String TAG = SecureStorageFor43.class.getSimpleName();
	
	private KeyStore secureStorage;

	public SecureStorageFor43() throws DigSigException {

		try {
			secureStorage = KeyStore.getInstance("AndroidKeyStore");
			secureStorage.load(null);
			
			Log.i(TAG, "Key store initialized. Type: " + secureStorage.getType() + "");
			Log.d(TAG, "Key store size: " + secureStorage.size());
			Log.d(TAG, "Key store alises: " + secureStorage.aliases());
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize", e);
			throw new DigSigException(e);
		}
	}

	@Override
	public X509Certificate getCertificate(int index) {
		
		String certKey = Keywords.certificate(index);

		X509Certificate cert = null;
		try {
			cert = (X509Certificate) secureStorage.getCertificate(certKey);
		} catch (KeyStoreException e) {
			Log.w(TAG, "Could not get certificate for identity No. " + index, e);
		}
		return cert;
	}
	
	@Override
	public PrivateKey getPrivateKey(int index) {
		
		String keyKey = Keywords.key(index);
		PrivateKey key = null;
		
		try {
			key = (PrivateKey) secureStorage.getKey(keyKey, null);
		} catch (Exception e) {
			Log.w(TAG, "Could not get private key for identity No. " + index, e);
		}
		return key;
	}

	@Override
	public int put(X509Certificate certificate, PrivateKey key) {

		int index = size();

		try {
			secureStorage.setCertificateEntry("CERT_0", certificate);
			Log.i(TAG, "Stored certificate of type " + certificate.getType());
			secureStorage.setKeyEntry("KEY_0", key, null, new Certificate[] {certificate});
			Log.i(TAG, "Stored key of format " + key.getFormat());
			return index;
		} catch (Exception e) {
			Log.w(TAG, "put", e);
			return -1;
		}
	}
	
	@Override
	public boolean isReady() {

		if (secureStorage == null) {
			Log.w(TAG, "isReady(): secureStorage is null!");
		}
		return secureStorage != null;
	}
	
	@Override
	public boolean containsCertificateAndKey(int index) {

		try {
			boolean keyExists = secureStorage.getKey(Keywords.key(index), null) != null;
			boolean certExists = secureStorage.getCertificate(Keywords.certificate(index)) != null;
			return (keyExists && certExists);
		} catch (Exception e) {
			Log.w(TAG, "containsCertificateAndKey", e);
			return false;
		}
	}
	
	private int size() {
		try {
			for (int k = 0; true; k++) {
				if (!secureStorage.containsAlias(Keywords.certificate(k)) &&
						!secureStorage.containsAlias(Keywords.key(k))) {
					return k;
				}
			}
		} catch (KeyStoreException e) {
			Log.w(TAG, "size", e);
			return -1;
		}
	}
}
