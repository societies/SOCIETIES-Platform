package org.societies.service.provider.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.service.provider.impl.MathService;

public class MathServiceUnitTest {

	private MathService myMathService;
	
	@Before
	public void setUp() throws Exception {
		myMathService = new MathService(1,1,"Test");
	}

	@After
	public void tearDown() throws Exception {
		myMathService = null;
	}

	@Test
	public void testAdd() {
		assertEquals(16,myMathService.add(1,15));
	}
	
	@Test
	public void testmultiply() {
		assertEquals(15,myMathService.multiply(1,15));
	}

	@Test
	public void testsubtractNeg() {
		assertEquals(-1,myMathService.subtract(14,15));
	}


}
