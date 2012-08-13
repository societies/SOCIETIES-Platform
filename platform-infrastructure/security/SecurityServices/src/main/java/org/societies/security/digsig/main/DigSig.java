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
package org.societies.security.digsig.main;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.security.digsig.DigsigException;
import org.societies.security.digsig.util.StringUtil;

/**
 * Signing and verifying digital signatures.
 *
 * @author Mitja Vardjan
 *
 */
public class DigSig {

	private static Logger LOG = LoggerFactory.getLogger(DigSig.class);

	public static final String ALGORITHM = "MD5WithRSA";
	public static final String ENCODING = "UTF8";

	public String sign(String textToSign, PrivateKey privateKey) throws DigsigException {

		byte[] dataToSign = str2bytes(textToSign);

		return sign(dataToSign, privateKey);
	}

	public String sign(byte[] dataToSign, PrivateKey privateKey) throws DigsigException {

		LOG.debug("Signing {} with {}", dataToSign, privateKey);

		Signature sig;
		byte[] signature;
		String signatureStr;

		try {
			sig = Signature.getInstance(ALGORITHM);
			sig.initSign(privateKey);
			sig.update(dataToSign);
			signature = sig.sign();
			signatureStr = bytes2str(signature);
		} catch (SignatureException e) {
			LOG.warn("Signing failed", e);
			throw new DigsigException(e);
		} catch (NoSuchAlgorithmException e) {
			LOG.warn("Signing failed", e);
			throw new DigsigException(e);
		} catch (InvalidKeyException e) {
			LOG.warn("Signing failed", e);
			throw new DigsigException(e);
		}

		LOG.debug(sig.getProvider().getInfo());
		LOG.debug("Signature: {}", signatureStr);

		return signatureStr;
	}

	/** 
	 * Verify the signature with the public key
	 * 
	 * @param data
	 * @param signature
	 * @param publicKey
	 * @return True if signature verification succeeded and signature is valid. False if signature invalid or on error.
	 */
	private boolean verify(byte[] data, byte[] signature, PublicKey publicKey) {

		LOG.debug("Verifying signature {} with {}", signature, publicKey);

		Signature sig;
		boolean valid;

		try {
			sig = Signature.getInstance(ALGORITHM);
			sig.initVerify(publicKey);
			sig.update(data);
			valid = sig.verify(signature);
		} catch (SignatureException e) {
			LOG.warn("Signature verification failed", e);
			return false;
		} catch (InvalidKeyException e) {
			LOG.warn("Signature verification failed", e);
			return false;
		} catch (NoSuchAlgorithmException e) {
			LOG.warn("Signature verification failed", e);
			return false;
		}
		LOG.debug("Signature validity: {}", valid);
		return valid;
	}

	public boolean verify(byte[] data, String signature, PublicKey publicKey) {

		byte[] signatureBytes;

		signatureBytes = hexstr2bytes(signature);

		return verify(data, signatureBytes, publicKey);
	}

	public boolean verify(String data, String signature, PublicKey publicKey) {

		byte[] dataBytes;

		dataBytes = str2bytes(data);
		
		return verify(dataBytes, signature, publicKey);
	}

	private byte[] str2bytes(String str) {

		byte[] bytes;
		
		try {
			bytes = str.getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
        	LOG.warn("str2bytes({}), str", e);
        	return null;
		}

		return bytes;
	}

	private byte[] hexstr2bytes(String str) {

		byte[] bytes;

		bytes = new StringUtil().hexStringToByteArray(str);
		
		return bytes;
	}

	private String bytes2str(byte[] bytes) {
		
		String str;
		
//		str = new String(bytes, ENCODING);
//		str = String.format("%02X", bytes);
		str = new StringUtil().bytesToHexString(bytes);
		
		return str;
	}
}
