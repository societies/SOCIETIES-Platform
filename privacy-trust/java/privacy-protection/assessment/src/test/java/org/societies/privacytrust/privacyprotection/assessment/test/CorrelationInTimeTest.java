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
import org.societies.privacytrust.privacyprotection.assessment.logic.CorrelationInTime;

/**
 * Test case for Privacy Assessment
 *
 * @author Mitja Vardjan (SETCCE)
 *
 */
public class CorrelationInTimeTest {
	
	private static Logger LOG = LoggerFactory.getLogger(CorrelationInTimeTest.class.getSimpleName());
	
	private CorrelationInTime correlationInTimeDefault;
	private CorrelationInTime correlationInTimeCustom;
	
	double valueAtInf = 0.43921;
	long timeShift = 7927;

	// Octave plot:
	// dt = 0:100:10000; a = 0.2; b = 3; plot(dt, (1 - 1 ./ (1 + exp(-(dt/1000-b)))) * (1-a) / (1 - 1 ./ (1 + exp(-(0-b)))) + a); grid
	
	// Octave calculate value for specific time difference:
	// dt = XXX; valueAtInf = 0.43921; timeShift = 7927;
	// (1 - 1 ./ (1 + exp(-(dt-timeShift)/1e3))) * (1-valueAtInf) / (1 - 1 ./ (1 + exp(-(0-timeShift)/1e3))) + valueAtInf 
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		LOG.debug("setUp()");

		correlationInTimeDefault = new CorrelationInTime();
		correlationInTimeCustom = new CorrelationInTime(valueAtInf, timeShift);
	}
	
	@Test
	public void testConstructorParameters() {
		
		assertEquals(valueAtInf, correlationInTimeCustom.getValueAtInf(), 1e-5 * valueAtInf);
		assertEquals(timeShift, correlationInTimeCustom.getTimeShift());
		
		assertTrue(correlationInTimeDefault.getTimeShift() != correlationInTimeCustom.getTimeShift());
		assertTrue(correlationInTimeDefault.getValueAtInf() != correlationInTimeCustom.getValueAtInf());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		correlationInTimeDefault = null;
		correlationInTimeCustom = null;
	}
	
	@Test
	public void testInvalidConstructorParameter() {
		
		CorrelationInTime correlationInTime;
		
		correlationInTime = new CorrelationInTime(1, timeShift);
		assertTrue(correlationInTime.getValueAtInf() < 1);
		assertTrue(correlationInTime.getValueAtInf() > 0);
		assertEquals(correlationInTime.getTimeShift(), timeShift);

		correlationInTime = new CorrelationInTime(1 + valueAtInf, timeShift);
		assertTrue(correlationInTime.getValueAtInf() < 1);
		assertTrue(correlationInTime.getValueAtInf() > 0);
		assertEquals(correlationInTime.getTimeShift(), timeShift);

		correlationInTime = new CorrelationInTime(-0.5641, timeShift);
		assertTrue(correlationInTime.getValueAtInf() < 1);
		assertTrue(correlationInTime.getValueAtInf() > 0);
		assertEquals(correlationInTime.getTimeShift(), timeShift);
	}

	@Test
	public void testCorrelationDefault() {
		
		LOG.debug("testCorrelationDefault()");
		
		long dt;
		double result;
		
		dt = Long.MIN_VALUE;
		result = correlationInTimeDefault.correlation(dt);
		assertEquals(0, result, 1e-5);
		
		dt = -2800;
		result = correlationInTimeDefault.correlation(dt);
		assertEquals(0, result, 1e-5);
		
		dt = 0;
		result = correlationInTimeDefault.correlation(dt);
		assertEquals(1, result, 1e-5);
		
		dt = 3100;
		result = correlationInTimeDefault.correlation(dt);
		assertTrue(result < 1);
		assertTrue(result > 0);
		
		dt = Long.MAX_VALUE;
		result = correlationInTimeDefault.correlation(dt);
		assertTrue(result < 1);
		assertTrue(result > 0);
	}

	@Test
	public void testCorrelationCustom() {
		
		LOG.debug("testCorrelationCustom()");
		
		long dt;
		double result;
		
		dt = Long.MIN_VALUE;
		result = correlationInTimeCustom.correlation(dt);
		assertEquals(0, result, 1e-5);
		
		dt = -2800;
		result = correlationInTimeCustom.correlation(dt);
		assertEquals(0, result, 1e-5);
		
		dt = 0;
		result = correlationInTimeCustom.correlation(dt);
		assertEquals(1, result, 1e-5);
		
		dt = 3100;
		result = correlationInTimeCustom.correlation(dt);
		assertEquals(0.99574, result, 1e-5);
		
		dt = 8097;
		result = correlationInTimeCustom.correlation(dt);
		assertEquals(0.69592, result, 1e-5);
		
		dt = Long.MAX_VALUE;
		result = correlationInTimeCustom.correlation(dt);
		assertEquals(0.43921, result, 1e-5);
	}
}
