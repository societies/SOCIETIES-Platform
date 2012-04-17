package org.societies.service.consumer.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.service.api.IMathService;
import org.societies.service.api.IMathServiceCallBack;
import org.societies.service.consumer.MathServiceCallBack;
import org.societies.service.consumer.MathServiceConsumer;
import org.springframework.scheduling.annotation.AsyncResult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MathServiceConsumerMockitoTest {

	private IMathService mock;
	private MathServiceConsumer classUnderTest;

	@Before
	public void setUp() {
		// create mocked class
		mock = mock(IMathService.class);

		// create an instance of your tested class
		classUnderTest = new MathServiceConsumer(1, 1);

		// Initialize the set method (normally called by spring */
		classUnderTest.setMathService(mock);
	}

	@Test
	public void collaborationCallTest() {
		// set mock class Behavior
		int expectResult = 16;
		when(mock.add(1, 15)).thenReturn(expectResult);

		// call the method of your class under test
		int res = classUnderTest.collaborationCall(1, 15);

		// then check whether the call to mock has been performed as expected
		verify(mock).add(1, 15);

		// check if the return result is conform to actual result ;
		assertEquals(res, expectResult);

	}

	@Test
	public void collaborationAsynchronousCallTest() {
		// set mock class Behavior
		Future<Integer> res = new AsyncResult<Integer>(16);
		when(mock.multiply(1, 15)).thenReturn(res);

		// call the method of your class under test
		try {
			classUnderTest.collaborationAsynchronousCall(1, 15, 16);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		// then check whether the call to mock has been performed as expected
		verify(mock).multiply(1, 15);

		// check if the return result is conform to actual result ;
		// assertEquals(res,expectResult);

	}

	@Test
	public void divisionCallTest() {

		IMathServiceCallBack divCallBack = new MathServiceCallBack();
		/* we need this injector for the Call Back */
		classUnderTest.setMathServiceCallBack(divCallBack);
		when(mock.divise(1, 1, divCallBack)).thenReturn((boolean) true);

		System.out.println("there");
		/* call the method of your class under test */
		classUnderTest.callDivisionWithCallBack(1, 1);
		/* then check whether the call has been as expected */
		divCallBack.resultDivision(1);
		verify(mock).divise(1, 1, divCallBack);
	}

	@Test
	public void barycenterTest() throws InterruptedException, ExecutionException {
		// set mock class Behavior
		Future<Integer> res = new AsyncResult<Integer>(2);
		when(mock.multiply(1, 2)).thenReturn(res);

		// call the method of your class under test
		float f = classUnderTest.barycenter(1, 2,1);

		System.out.println("barycenter = " + f);
		
		// then check whether the call to mock has been performed as expected
		verify(mock).multiply(1, 2);

		//assertEquals(f,2);

	}

	
	@After
	public void tearDown() {
		mock = null;
		classUnderTest = null;
	}
}
