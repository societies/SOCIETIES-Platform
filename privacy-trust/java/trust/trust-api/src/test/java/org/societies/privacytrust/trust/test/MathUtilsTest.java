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
package org.societies.privacytrust.trust.test;

import static org.junit.Assert.*;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.privacytrust.trust.api.util.MathUtils;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public class MathUtilsTest {
	
	private static double DELTA = 0.000001d;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.util.MathUtils#normalise(double[])}.
	 */
	@Test
	public void testNormalise() {
		
		final double[] doubles = new double[] { 3.5d, 2.3d, -10.1d, 6.5d};
		final double[] normDoubles =  MathUtils.normalise(doubles);
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		for (final double normDouble : normDoubles)
			stats.addValue(normDouble);
		assertEquals(0.0d, stats.getMean(), DELTA);
		assertEquals(1.0d, stats.getStandardDeviation(), DELTA);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.util.MathUtils#stanine(double[])}.
	 */
	@Ignore
	@Test
	public void testStanine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.util.MathUtils#min(double[])}.
	 */
	@Test
	public void testMin() {
		
		final double[] doubles = new double[] { 3.5d, 2.3d, -10.1d, 6.5d};
		assertEquals(6.5d, MathUtils.max(doubles), DELTA);
	}

	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.util.MathUtils#max(double[])}.
	 */
	@Test
	public void testMax() {
		
		final double[] doubles = new double[] { 3.5d, 2.3d, -10.1d, 6.5d};
		assertEquals(-10.1d, MathUtils.min(doubles), DELTA);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.util.MathUtils#mean(double[])}.
	 */
	@Test
	public void testMean() {
		
		final double[] doubles = new double[] { -1.0d, 0.0d, 1.0d, 12.0d};
		assertEquals(3.0d, MathUtils.mean(doubles), DELTA);
	}
	
	/**
	 * Test method for {@link org.societies.privacytrust.trust.api.util.MathUtils#cos(double[], double[])}.
	 */
	@Test
	public void testCos() {

		final double[] a_sample = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		final double[] b_sample = {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
		final double[] c_sample = {0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9, 0.9};
		final double[] d_sample = {0.7, 0.3, 0.95, 0.6, 0.5, 0.75, 0.6, 0.8, 0.45, 0.8, 0.4, 0.85, 0.9};
		final double[] e_sample = {0.2, 0.1, 0.15, 0.1, 0.2, 0.15, 0.2, 0.1, 0.15, 0.1, 0.2, 0.15, 0.2};
		final double[] f_sample = {0.2, 0.1, 0.15, 0.1, 0.2, 0.15, 0.2, 0.1, 0.15, 0.1};
	 
		assertEquals(1.0d, MathUtils.cos(a_sample, b_sample), DELTA);
		assertEquals(1.0d, MathUtils.cos(b_sample, a_sample), DELTA);
		assertEquals(1.0d, MathUtils.cos(a_sample, a_sample), DELTA);
		assertEquals(1.0d, MathUtils.cos(b_sample, c_sample), DELTA);
		assertTrue(MathUtils.cos(b_sample, d_sample) < MathUtils.cos(b_sample, e_sample));
		boolean caughtException = false;
		try {
			MathUtils.cos(b_sample, f_sample);
		} catch (IllegalArgumentException iae) {
			assertTrue(iae.getMessage().contains("Vector length mismatch"));
			caughtException = true;
		}
		assertTrue(caughtException);
	}
}