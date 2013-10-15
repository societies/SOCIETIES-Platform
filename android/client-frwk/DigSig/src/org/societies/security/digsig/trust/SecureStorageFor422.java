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

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import org.societies.security.digsig.apiinternal.ISecureStorage;
import org.societies.security.digsig.sign.DigSigException;
import org.societies.security.digsig.trust.AndroidSecureStorage.State;

import android.util.Log;

/**
 * Helper class for retrieving X.509 certificates and private keys from android secure storage.
 * 
 * This class is not public.
 *
 * @author Mitja Vardjan, based on code from Miroslav Pavleski
 *
 */
class SecureStorageFor422 implements ISecureStorage {

	private static final String TAG = SecureStorageFor422.class.getSimpleName();
	
	private AndroidSecureStorage secureStorage;
	private KeyFactory keyFactory;
	private CertificateFactory certFactory;

	public SecureStorageFor422() throws DigSigException {
		try {
			secureStorage = AndroidSecureStorage.getInstance();
			keyFactory = KeyFactory.getInstance("RSA");
			certFactory = CertificateFactory.getInstance("X.509");
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize", e);
			throw new DigSigException(e);
		}
	}

	@Override
	public X509Certificate getCertificate(int index) {
		
		String certKey = Keywords.certificate(index);

		byte[] encodedCert = secureStorage.getWithStringKey(certKey);
		if (encodedCert == null) {
			Log.w(TAG, "Could not get certificate for identity No. " + index);
			return null;
		}

		X509Certificate cert = null;

		try {
			cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encodedCert));
		} catch (Exception e) { 
			Log.w(TAG, "Failed while decoding certificate for identity No. " + index, e);
		}
		return cert;
	}
	
	@Override
	public PrivateKey getPrivateKey(int index) {
		
		String keyKey = Keywords.key(index);

		byte[] encodedKey = secureStorage.getWithStringKey(keyKey);
		if (encodedKey == null) {
			Log.w(TAG, "Could not get private key for identity No. " + index);
			return null;
		}

		PrivateKey key = null;

		try {
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey);            
			key = keyFactory.generatePrivate(privKeySpec);
		} catch (Exception e) { 
			Log.w(TAG, "Failed while decoding private key for identity No. " + index, e);
		}
		return key;
	}

	@Override
	public int put(X509Certificate certificate, PrivateKey key) {

		int index = size();
		
		try {
			secureStorage.put(Keywords.certificate(index), certificate.getEncoded());
			Log.i(TAG, "Stored certificate of type " + certificate.getType());
			secureStorage.put(Keywords.key(index), key.getEncoded());
			Log.i(TAG, "Stored key of format " + key.getFormat());
			return index;
		} catch (CertificateEncodingException e) {
			Log.w(TAG, "Could not put certificate and key", e);
			return -1;
		}
	}
	
	@Override
	public boolean isReady() {

		try {
			State state = secureStorage.state();
			Log.d(TAG, "Secure storage state = " + state);
			return state == State.UNLOCKED;
		} catch (Exception e) {
			Log.w(TAG, "Could not get secure storage state", e);
			return false;
		}
	}
	
	@Override
	public boolean containsCertificateAndKey(int index) {
		
		boolean keyExists = secureStorage.getWithStringKey(Keywords.key(index)) != null;
		boolean certExists = secureStorage.getWithStringKey(Keywords.certificate(index)) != null;
		
		return (keyExists && certExists);
	}
	
	private int size() {
		for (int k = 0; true; k++) {
			if (!secureStorage.contains(Keywords.certificate(k)) &&
					!secureStorage.contains(Keywords.key(k))) {
				return k;
			}
		}
	}
}
