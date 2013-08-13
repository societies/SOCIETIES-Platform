/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.test.dataobfuscation.obfuscator;

import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.util.RandomBetween;

/**
 * @author Olivier Maridat (Trialog)
 */
@RunWith(JUnitParamsRunner.class)
public class RandomBetweenTest {
	private static Logger LOG = LoggerFactory.getLogger(RandomBetweenTest.class.getSimpleName());

	public RandomBetween randomer;

	@Before
	public void setUp() throws Exception {
		randomer = new RandomBetween();
	}
	@After
	public void tearDown() throws Exception {
		randomer = null;
	}


	@Test
	public void testNextFloatBetween() {
		LOG.info("[Test] testNextFloatBetween");
		float a = 0;
		float b = 0;
		// No interval
		float result = randomer.nextFloatBetween(a, a);
		assertTrue("Should be equal", a == result);
		a = 122;
		result = randomer.nextFloatBetween(a, a);
		assertTrue("Should be equal", a == result);
		// Interval ]1, 2[
		a = 1;
		b = 2;
		result = randomer.nextFloatBetween(a, b);
		assertTrue("1) Should be more than "+a, result > a);
		assertTrue("1) Should be less than "+b, result < b);
		result = randomer.nextFloatBetween(b, a);
		assertTrue("1) Should be more than "+a, result > a);
		assertTrue("1) Should be less than "+b, result < b);
		// Interval ]0.01, 0.02[
		a = 0.01F;
		b = 0.02F;
		int nbOfIteration = 2000;
		for (int i=0; i<nbOfIteration; i++) {
			result = randomer.nextFloatBetween(a, b);
			assertTrue(i+". Should be more than "+a, result > a);
			assertTrue(i+". Should be less than "+b, result < b);
			result = randomer.nextFloatBetween(b, a);
			assertTrue(i+". Should be more than "+a, result > a);
			assertTrue(i+". Should be less than "+b, result < b);
		}

		// Interval ]1.56, 2287.456[
		a = 1.56F;
		b = 287.456F;
		result = randomer.nextFloatBetween(a, b);
		assertTrue("3) Should be more than "+a, result > a);
		assertTrue("3) Should be less than "+b, result < b);
		result = randomer.nextFloatBetween(b, a);
		assertTrue("3) Should be more than "+a, result > a);
		assertTrue("3) Should be less than "+b, result < b);
	}


	@Test
	public void testNextDoubleBetween() {
		LOG.info("[Test] testNextDoubleBetween");
		double a = 0;
		double b = 0;
		// No interval
		double result = randomer.nextDoubleBetween(a, a);
		assertTrue("Should be equal", a == result);
		a = 122;
		result = randomer.nextDoubleBetween(a, a);
		assertTrue("Should be equal", a == result);
		// Interval ]1, 2[
		a = 1;
		b = 2;
		result = randomer.nextDoubleBetween(a, b);
		assertTrue("1) Should be more than "+a, result > a);
		assertTrue("1) Should be less than "+b, result < b);
		result = randomer.nextDoubleBetween(b, a);
		assertTrue("1) Should be more than "+a, result > a);
		assertTrue("1) Should be less than "+b, result < b);

		// Interval ]0.01, 0.02[
		a = 0.01;
		b = 0.02;
		int nbOfIteration = 2000;
		for (int i=0; i<nbOfIteration; i++) {
			result = randomer.nextDoubleBetween(a, b);
			assertTrue("2) "+i+". Should be more than "+a, result > a);
			assertTrue("2) "+i+". Should be less than "+b, result < b);
			result = randomer.nextDoubleBetween(b, a);
			assertTrue("2) "+i+". Should be more than "+a, result > a);
			assertTrue("2) "+i+". Should be less than "+b, result < b);
		}

		// Interval ]1.56, 2287.456[
		a = 1.56;
		b = 287.456;
		result = randomer.nextDoubleBetween(a, b);
		assertTrue("3) Should be more than "+a, result > a);
		assertTrue("3) Should be less than "+b, result < b);
		result = randomer.nextDoubleBetween(b, a);
		assertTrue("3) Should be more than "+a, result > a);
		assertTrue("3) Should be less than "+b, result < b);
	}
}
