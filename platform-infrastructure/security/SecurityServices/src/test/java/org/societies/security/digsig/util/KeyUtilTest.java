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

import static org.junit.Assert.*;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.junit.Before;
import org.junit.Test;
import org.societies.api.security.digsig.DigsigException;
import org.societies.security.digsig.main.DigSig;
import org.societies.security.storage.CertStorage;

/**
 * Describe your class here...
 *
 * @author Mitja Vardjan
 *
 */
public class KeyUtilTest {
	
	DigSig digSig;
	
	PublicKey publicKey;
	PrivateKey privateKey;
	X509Certificate cert;
	
	String textToSign = "abc";
	byte[] dataToSign = new byte[] {'d', 'e', 'f'};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		digSig = new DigSig();

		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        
        privateKey = key.getPrivate();
        publicKey = key.getPublic();
        
        CertStorage certStorage = new CertStorage();
        certStorage.setCertFile("foo");
        certStorage.setCertPassword("p");
        certStorage.init();
        
        cert = certStorage.getOurCert();
		assertNotNull(cert);
	}

	/**
	 * Test method for {@link org.societies.security.digsig.util.KeyUtil#str2key(java.lang.String)}.
	 * @throws DigsigException 
	 */
//	@Test
//	public void testStr2key() throws DigsigException {
//		
//		PrivateKey privateKeyCopy = (PrivateKey) key2str2key(privateKey);
//		PublicKey publicKeyCopy = (PublicKey) key2str2key(publicKey);
//		
//		assertNotNull(privateKey);
//		assertNotNull(publicKey);
//		assertEquals(privateKey, privateKeyCopy);
//		assertNotSame(privateKey, privateKeyCopy);
//		assertEquals(publicKey, publicKeyCopy);
//		assertNotSame(publicKey, publicKeyCopy);
//	}
//	
//	private Key key2str2key(Key key) throws DigsigException {
//		String keyStr = KeyUtil.key2str(key);
//		return KeyUtil.str2key(keyStr);
//	}
	
	@Test
	public void testCert2key() throws Exception {
		
		X509Certificate certCopy = cert2str2cert(cert);

		assertEquals(cert, certCopy);
		assertNotSame(cert, certCopy);
		
		assertEquals(cert.getPublicKey(), certCopy.getPublicKey());
		assertNotSame(cert.getPublicKey(), certCopy.getPublicKey());
	}

	private X509Certificate cert2str2cert(X509Certificate cert) throws DigsigException {
		String certStr = KeyUtil.cert2str(cert);
		return KeyUtil.str2cert(certStr);
	}
}
