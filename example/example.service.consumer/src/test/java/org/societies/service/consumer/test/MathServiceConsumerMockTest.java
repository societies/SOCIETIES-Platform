package org.societies.service.consumer.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.service.api.IMathService;
import org.societies.service.consumer.MathServiceConsumer;

import static org.easymock.EasyMock.*;

public class MathServiceConsumerMockTest {

	private IMathService mock;
	private MathServiceConsumer classUnderTest;
	
	@Before
	public void setUp() throws Exception {
		/* create the mock class used by your bundle */
		mock = createMock(IMathService.class);
		/* create an instance of your class tested */
		classUnderTest = new MathServiceConsumer(1,1);
		/* simulate the set of spring bean */
		classUnderTest.setMathService(mock);
	}

	@After
	public void tearDown() throws Exception {
		mock = null;
		classUnderTest = null;
	}

	@Test
	public void colloborationCallTest() {		
		/* Step1 : define the behaviour you expect
		 * the use of expect method of the EasyMock is mandatory as the method returns an int
		 * If the mock method returns void, just define the behaviour using mock.methodA 
		 */
		expect(mock.add(1,15)).andReturn((int) 11);
		/* "replay" means that you reset the mock... Remind that the easymock is just regsiter the call you are doing */ 
		replay(mock);
		/* call the method of your class under test */
		classUnderTest.collaborationCall(1, 15);
		/* then check whether the call has been as expected */
		verify(mock);
	}

}
