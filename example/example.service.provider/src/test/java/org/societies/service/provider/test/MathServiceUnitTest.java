package org.societies.service.provider.test;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.service.provider.impl.MathService;

public class MathServiceUnitTest {

	private MathService myMathService;
	
	@Before
	public void setUp() {
		myMathService = new MathService(1,1,"Test");
	}

	@After
	public void tearDown()  {
		myMathService = null;
	}

	@Test
	public void testAdd() {
		assertEquals(16,myMathService.add(1,15));
	}
	
	@Test
	public void testmultiply() throws InterruptedException, ExecutionException {
		Future<Integer> res = null;
		res = myMathService.multiply(1,15);
		try {
			assertEquals(15,res.get());
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testsubtractNeg() {
		assertEquals(-1,myMathService.subtract(14,15));
	}


}
