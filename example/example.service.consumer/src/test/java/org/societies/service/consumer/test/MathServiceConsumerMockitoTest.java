package org.societies.service.consumer.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.service.api.IMathService;
import org.societies.service.api.IMathServiceCallBack;
import org.societies.service.consumer.MathServiceCallBack;
import org.societies.service.consumer.MathServiceConsumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MathServiceConsumerMockitoTest {
	 
    private IMathService mock; 
    private MathServiceConsumer classUnderTest;
    
    @Before  
    public void setUp() {  
//    	create mocked class
        mock = mock(IMathService.class); 
        
//      create an instance of your tested class
        classUnderTest = new MathServiceConsumer(1,1);
        
//      Initialize the set method (normally called by spring */
        classUnderTest.setMathService(mock);   
    }  
    
    @Test  
    public void collaborationCallTest() {  
//    	set mock class Behavior
    	when(mock.add(1, 15)).thenReturn(16);
    	
//    	call the method of your class under test 
    	classUnderTest.collaborationCall(1, 15);
    	
//    	then check whether the mock call has been as expected
    	verify(mock).add(1, 15) ; 
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
	public void statefulCallTest() {

		when(mock.add(1, 1)).thenReturn(2);
		when(mock.multiply(1, 1)).thenReturn(1);
		System.out.println("there");
		/* call the method of your class under test */
		/*test state<10 branch*/
		classUnderTest.setHiddenState(8);
		assertTrue(classUnderTest.getHiddenState()<10);
		classUnderTest.callStatefulMethod();
		verify(mock).multiply(1, 1);
		/* then check whether the call has been as expected */
		/*test state>10 branch*/
		classUnderTest.setHiddenState(20);
		assertTrue(classUnderTest.getHiddenState()>10);
		classUnderTest.callStatefulMethod();
		verify(mock).add(1, 1);
		/* then check whether the call has been as expected */
	}

    @After  
    public void tearDown(){  
    	mock = null;
    	classUnderTest = null;
    }  
}
