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
package org.societies.security.digsig;

import static org.junit.Assert.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.security.digsig.main.DigSig;
import org.societies.api.security.digsig.DigsigException;

/**
 * @author Mitja Vardjan
 */
public class DigSigTest {

	private static Logger LOG = LoggerFactory.getLogger(DigSigTest.class);

	DigSig digSig;
	
	PublicKey publicKey;
	PrivateKey privateKey;
	
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
        
        LOG.info("RSA key pair generated");

        privateKey = key.getPrivate();
        publicKey = key.getPublic();
        
        LOG.info("Public key: {}", publicKey);
        LOG.info("Private key: {}", privateKey);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link DigSig#sign(String, java.security.PrivateKey)}.
	 * @throws DigsigException 
	 */
	@Test
	public void testSignStringPrivateKey() throws DigsigException {
		
		String signature;
		
		signature = digSig.sign(textToSign, privateKey);
		
		LOG.info("Signature = {}", signature);
		
		assertNotNull(signature);
		assertTrue(signature.length() > 10);
		assertTrue(signature.length() < 5000);
		assertEquals(256.0, signature.length(), 0.0);
	}

	/**
	 * Test method for {@link DigSig#sign(byte[], java.security.PrivateKey)}.
	 * @throws DigsigException 
	 */
	@Test
	public void testSignByteArrayPrivateKey() throws DigsigException {
		
		String signature;
		
		signature = digSig.sign(dataToSign, privateKey);

		LOG.info("Signature = {}", signature);

		assertNotNull(signature);
		assertTrue(signature.length() > 10);
		assertTrue(signature.length() < 5000);
		assertEquals(256.0, signature.length(), 0.0);
	}

	/**
	 * Test method for {@link DigSig#verify(byte[], String, java.security.PublicKey)}.
	 * @throws DigsigException 
	 */
	@Test
	public void testVerifyValidSig() throws DigsigException {

		String signature;
		boolean result;
		
		signature = digSig.sign(dataToSign, privateKey);
		result = digSig.verify(dataToSign, signature, publicKey);
		assertTrue(result);
	}

	/**
	 * Test method for {@link DigSig#verify(byte[], String, java.security.PublicKey)}.
	 * @throws DigsigException 
	 */
	@Test
	public void testVerifyInvalidSig() throws DigsigException {

		String signature;
		String invalidSignature;
		boolean result;
		
		signature = digSig.sign(dataToSign, privateKey);
		if (signature.contains("A")) {
			invalidSignature = signature.replace("A", "B");
		}
		else {
			invalidSignature = signature.substring(0, signature.length() - 2) + "A";
		}
		result = digSig.verify(dataToSign, invalidSignature, publicKey);
		assertFalse(result);
	}

	/**
	 * Test method for {@link DigSig#verify(String, String, java.security.PublicKey)}.
	 * @throws DigsigException 
	 */
	@Test
	public void testVerifyStringStringPublicKey() throws DigsigException {
		
		String signature;
		String invalidSignature;
		boolean result;
		
		signature = digSig.sign(textToSign, privateKey);
		result = digSig.verify(textToSign, signature, publicKey);
		assertTrue(result);
		if (signature.contains("A")) {
			invalidSignature = signature.replace("A", "B");
		}
		else {
			invalidSignature = signature.substring(0, signature.length() - 2) + "A";
		}
		result = digSig.verify(dataToSign, invalidSignature, publicKey);
		assertFalse(result);
	}
}
