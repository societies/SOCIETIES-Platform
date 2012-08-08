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
package org.societies.security.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.security.storage.StorageException;
import org.societies.security.digsig.util.StreamUtil;


/**
 * @author Miroslav Pavleski, Mitja Vardjan
 */
public class CertStorage {

	private static Logger LOG = LoggerFactory.getLogger(CertStorage.class);

	private static final String defaultCertificate = "default_certificate.p12";
	
	private static CertStorage instance;

	// private XmlManipulator xml = Config.getInstance().getXml();
	private X509Certificate ourCert;
	private PrivateKey ourKey;

	private CertStorage() throws StorageException {
		initOurIdentity();
	}

	private void initOurIdentity() throws StorageException {
		
		InputStream ksStream;
		
		Security.addProvider(new BouncyCastleProvider());
		
		String fileName = "my_certificate.p12"; // TODO
		String pass = "p"; // TODO

		try {
			ksStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			LOG.warn("Certificate file \"{}\" not found. Using default built-in certificate.", fileName);
			ksStream = getClass().getClassLoader().getResourceAsStream(defaultCertificate);
		}

		try {

			KeyStore ks = KeyStore.getInstance("PKCS12", "BC");

			ks.load(ksStream, pass.toCharArray());

			String alias = ks.aliases().nextElement();
			ourCert = (X509Certificate) ks.getCertificate(alias);
			ourKey = (PrivateKey) ks.getKey(alias, pass.toCharArray());

			if (ourCert == null || ourKey == null)
				throw new NullPointerException();
		} catch (Exception e) {
			throw new StorageException("Failed to initialize identity information", e);
		} finally {
			StreamUtil.closeStream(ksStream);
		}
	}

	public X509Certificate getOurCert() {
		return ourCert;
	}

	/**
	 * Get the private key. For the public key use {@link #getOurCert()} and invoke
	 * {@link X509Certificate#getPublicKey()} on that.
	 * 
	 * @return The private key
	 */
	public PrivateKey getOurKey() {
		return ourKey;
	}

	public static synchronized CertStorage getInstance() throws StorageException {
		if (instance == null)
			instance = new CertStorage();
		return instance;
	}
}
