package org.societies.integration.test.bit.math_example;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NominalTestCase {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	private ConsumerCallbackImpl consumerCallbackImpl = new ConsumerCallbackImpl();
	
	public NominalTestCase() {
	}

	@Before
	public void setUp() {
		LOG.info("###743... setUp");
	}
	
	@Test
	public void asyncTest() {
		LOG.info("###743... asyncTest");

		TestCase743.mathServiceConsumer.asyncBarycenter(1, 2, 2, consumerCallbackImpl);
	
		LOG.info("###743... 1");
		
		 synchronized (consumerCallbackImpl) {

			 	try {
			 		LOG.info("###743... 2");
			 		consumerCallbackImpl.wait(15*1000);
			 		LOG.info("###743... 3");
				} catch (InterruptedException e) {
					LOG.info("###743... 4");
					fail("InterruptedException");
					LOG.info("###743... 5");
					e.printStackTrace();
				}
		    }
		 
		 LOG.info("###743... 6");
		
		 // if the method times out, fail
		 assertNotNull("Timed out", consumerCallbackImpl.getAsyncResult());
		 
		 // otherwise, check the result
		 // Get the result
		 Integer result = consumerCallbackImpl.getAsyncResult();
		 LOG.info("asyncTest1 result: " + result);
		 assertEquals(new Integer(1), result);
	}
	
	@Test
	public void futureTest() {
		LOG.info("###743... futureTest");

		Integer result = null;
		Future<Integer> futureRes = TestCase743.mathServiceConsumer.futureBarycenter(1, 2, 2);
	
		
		LOG.info("after call");
		
			try {
				LOG.info("before geting result: result isDone? " + futureRes.isDone());
				result = futureRes.get(15, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				fail("InterruptedException");
				e.printStackTrace();
			} catch (ExecutionException e) {
				fail("ExecutionException");
				e.printStackTrace();
			} catch (TimeoutException e) {
				fail("TimeoutException");
				e.printStackTrace();
			}

			LOG.info("futureTest result: " + result);
			
		 // if the method times out, fail
		 assertNotNull("Timed out", result);
		 
		 // otherwise, check the result
		 assertEquals(new Integer(1), result);
	}
	

	@Test
	public void body1() {
		LOG.info("###743... body1");
			
		assertEquals(new Integer(0),TestCase743.mathServiceConsumer.barycenter(1, 2, 4) );

	}
	
	@Test
	public void body2() {
		LOG.info("###743... body2");
		
		assertEquals(new Integer(1),TestCase743.mathServiceConsumer.barycenter(2, 2, 2) );

	}
	
	@Test
	public void body3() {
		LOG.info("###743... body4");
		
		assertEquals(new Integer(2),TestCase743.mathServiceConsumer.barycenter(2, 2, 2) );
	}
	
	
	@Test
	public void body4() {
		LOG.info("###743... body4");
			
		assertEquals(new Integer(2), TestCase743.mathServiceConsumer.barycenter(1, 2, 2) );
	}

	
	@After
	public void tearDown() {
		LOG.info("###743... tearDown");
	}
}