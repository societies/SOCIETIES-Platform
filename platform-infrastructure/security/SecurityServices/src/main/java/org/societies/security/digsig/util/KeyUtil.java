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
package org.societies.security.digsig.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.societies.api.security.digsig.DigsigException;

/**
 * Encryption key manipulation
 *
 * @author Mitja Vardjan
 *
 */
public class KeyUtil {

	/**
	 * Convert Base64 {@link String} representation of public key to {@link PublicKey}
	 * 
	 * @param keyStr Base64 representation of the {@link PublicKey}
	 * @return The {@link PublicKey}
	 * @throws CertificateException 
	 */
//	public static PublicKey str2key(String keyStr) throws DigsigException {
//
//		byte[] keyBytes = Base64.decodeBase64(keyStr);
////		Key key = new SecretKeySpec(keyBytes, DigSig.ALGORITHM);
//
//		// get the public key
////		try {
////			CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
////			Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(keyBytes));
////			PublicKey key = certificate.getPublicKey();
////	
////			return key;
////		} catch (CertificateException e) {
////			throw new DigsigException(e);
////		}
//
////		try {
////			Key key = (Key) PublicKeyFactory.createKey(keyBytes);
////		} catch (IOException e) {
////			throw new DigsigException(e);
////		}
//		
//		KeyFactory keyFactory;
//		KeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//		try {
//			keyFactory = KeyFactory.getInstance("RSA");
//			PublicKey key = keyFactory.generatePublic(keySpec);
//			return key;
//		} catch (NoSuchAlgorithmException e) {
//			throw new DigsigException(e);
//		} catch (InvalidKeySpecException e) {
//			throw new DigsigException(e);
//		}
//	}

	/**
	 * Convert public or private {@link Key} to {@link String}
	 * 
	 * @param key The key to convert
	 * @return Base64 representation of the {@link Key}
	 */
//	public static String key2str(Key key) {
//		
//		byte[] keyBytes = key.getEncoded();
//		String keyStr = Base64.encodeBase64String(keyBytes);
//		
//		return keyStr;
//	}
	
	/**
	 * Serialize X.509 certificate to String
	 *
	 * @param cert The certificate to convert to String
	 * @return Serialized certificate
	 */
	public static String cert2str(X509Certificate cert) throws DigsigException {
		
		byte[] certBytes;
		String certStr;
		
		try {
			certBytes = cert.getEncoded();
		} catch (CertificateEncodingException e) {
			throw new DigsigException(e);
		}
		certStr = Base64.encodeBase64String(certBytes);
		
		return certStr;
	}
	
	public static X509Certificate str2cert(String certStr) throws DigsigException {

		byte[] certBytes = Base64.decodeBase64(certStr);
		InputStream is = new ByteArrayInputStream(certBytes);
		CertificateFactory cf;
		X509Certificate cert;

		try {
			cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(is);
			return cert;
		} catch (CertificateException e) {
			throw new DigsigException(e);
		}
	}
}
