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

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.societies.api.identity.IIdentity;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.api.security.storage.StorageException;
import org.societies.security.digsig.bc.DigSig;
import org.societies.security.storage.CertStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mitja Vardjan
 */
public class SignatureMgr implements ISignatureMgr {

	private static Logger LOG = LoggerFactory.getLogger(SignatureMgr.class);

	private DigSig digSig = new DigSig();
	
	public SignatureMgr() {
		
		LOG.info("SignatureMgr()");
		
		CertStorage certs;
		try {
			certs = CertStorage.getInstance();
		} catch (StorageException e) {
			LOG.error("Could not initialize storage", e);
			return;
		}
		X509Certificate cert = certs.getOurCert();
		Key key = certs.getOurKey();
		PrivateKey privateKey = (PrivateKey) key;
		PublicKey publicKey = cert.getPublicKey();
		
		LOG.debug("Certificate: {}", cert);
		LOG.debug("Public key: {}", publicKey);
		LOG.debug("Private key: {}", privateKey);
	}
	
	@Override
	public String signXml(String xml, String xmlNodeId, IIdentity identity) {
		
		LOG.debug("signXml(..., {}, {})", xmlNodeId, identity);

		return xml;  // FIXME
	}

	@Override
	public boolean verifyXml(String xml) {
		LOG.debug("verify()");
		return true;  // FIXME
	}
	
	@Override
	public String sign(byte[] dataToSign, PrivateKey privateKey) throws DigsigException {
		return digSig.sign(dataToSign, privateKey);
	}
	
	@Override
	public boolean verify(byte[] data, String signature, PublicKey publicKey) {
		return digSig.verify(data, signature, publicKey);
	}
}
