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

package org.societies.privacytrust.privacyprotection.assessment.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.privacytrust.privacyprotection.assessment.logic.CorrelationInData;

/**
 * Test case for Privacy Assessment
 *
 * @author Mitja Vardjan (SETCCE)
 *
 */
public class CorrelationInDataTest {
	
	private static Logger LOG = LoggerFactory.getLogger(CorrelationInDataTest.class.getSimpleName());
	
	private CorrelationInData correlationInDataDefault;
	private CorrelationInData correlationInDataCustom;
	
	// Octave plot:
	// deltaSize = 0:0.1:10; valueAtInf = 0.2; sizeScale = 1;
	// plot(deltaSize, exp(-(deltaSize / sizeScale).^2) * (1 - valueAtInf) + valueAtInf); grid
	
	// Octave: calculate value for specific size difference:
	// For sizeScaleRight and positive deltaSize values:
	// deltaSize = 0; valueAtInf = 0.2; sizeScale = 1;
	// exp(-(deltaSize / sizeScale).^2) * (1 - valueAtInf) + valueAtInf
	// Adapt the formula for negative deltaSize
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LOG.debug("setUp()");
		correlationInDataDefault = new CorrelationInData();
		correlationInDataCustom = new CorrelationInData(0.43921, 39.452, 3.6731);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		correlationInDataDefault = null;
		correlationInDataCustom = null;
	}

	@Test
	public void testCorrelationDefault() {
		
		LOG.debug("testCorrelationDefault()");
		
		long deltaSize;
		double result;
		
		deltaSize = Long.MIN_VALUE;
		result = correlationInDataDefault.correlation(deltaSize);
		assertTrue(result < 1);
		assertTrue(result > 0);
		
		deltaSize = -8;
		result = correlationInDataDefault.correlation(deltaSize);
		assertTrue(result < 1);
		assertTrue(result > 0);
		
		deltaSize = 0;
		result = correlationInDataDefault.correlation(deltaSize);
		assertEquals(1, result, 1e-5);
		
		deltaSize = 31;
		result = correlationInDataDefault.correlation(deltaSize);
		assertTrue(result < 1);
		assertTrue(result > 0);
		
		deltaSize = Long.MAX_VALUE;
		result = correlationInDataDefault.correlation(deltaSize);
		assertTrue(result < 1);
		assertTrue(result > 0);
	}

	@Test
	public void testCorrelationCustom() {
		
		LOG.debug("testCorrelationCustom()");
		
		long deltaSize;
		double result;
		
		deltaSize = Long.MIN_VALUE;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.43921, result, 1e-5);
		
		deltaSize = -29;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.76590, result, 1e-5);

		deltaSize = -8;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.97741, result, 1e-5);

		deltaSize = -2;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.99856, result, 1e-5);
		
		deltaSize = 0;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(1, result, 1e-5);

		deltaSize = 2;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.85612, result, 1e-5);
		
		deltaSize = 8;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.44409, result, 1e-5);

		deltaSize = 29;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.43921, result, 1e-5);
		
		deltaSize = Long.MAX_VALUE;
		result = correlationInDataCustom.correlation(deltaSize);
		assertEquals(0.43921, result, 1e-5);
	}
}
