package org.societies.service.consumer.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.service.api.IMathService;
import org.societies.service.consumer.MathServiceConsumer;

import static org.mockito.Mockito.*;

public class MathServiceConsumerMockitoTest {
	 
    private IMathService mock; 
    private MathServiceConsumer classUnderTest;
    
    @Before  
    public void setUp() {  
//    	creat mocked class
        mock = mock(IMathService.class); 
        
//      creat an instance of your tested class
        classUnderTest = new MathServiceConsumer(1,1);
        
//      set mock class to the test class
        classUnderTest.setMathService(mock);   
    }  
    
    @Test  
    public void collaborationCallTest() {  
//    	set mock class Behavior
    	when(mock.add(1, 15)).thenReturn(16);
    	
//    	call the method of your class under test 
    	classUnderTest.collaborationCall(1, 15);
    	
//    	then check whether the mock call has been as expected
    	verify(mock).add(1, 15);
    }  
    
    @After  
    public void tearDown(){  
    	mock = null;
    	classUnderTest = null;
    }  
}
