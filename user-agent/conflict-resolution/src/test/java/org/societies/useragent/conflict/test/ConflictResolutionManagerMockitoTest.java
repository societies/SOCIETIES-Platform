package org.societies.useragent.conflict.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.conflict.ConflictResolutionRule;
import org.societies.useragent.conflict.ConflictResolutionManager;

public class ConflictResolutionManagerMockitoTest {
	private IOutcome intent;
	private IOutcome preference;
    private ConflictResolutionRule mock; 
    private ConflictResolutionManager classUnderTest;
    @Before  
    public void setUp() {  
//    	creat mocked class
        mock = mock(ConflictResolutionRule.class); 
        intent=mock(IOutcome.class);
        preference=mock(IOutcome.class);
//      creat an instance of your tested class
        classUnderTest = new ConflictResolutionManager();
        
//      set mock class to the test class
        classUnderTest.addRule(mock);   
    }  
    
    @Test  
    public void collaborationCallTest() {  
//    	set mock class Behavior
    	when(intent.getvalue()).thenReturn(UUID.randomUUID().toString());
    	when(preference.getvalue()).thenReturn(UUID.randomUUID().toString());
    	when(mock.match(intent, preference)).thenReturn(false);
//    	call the method of your class under test 
    	classUnderTest.resolveConflict(intent,preference);
    	
//    	then check whether the mock call has been as expected
    	verify(mock).match(intent, preference);
    }  
    
    @After  
    public void tearDown(){  
    	intent=null;
    	preference=null;
    	mock = null;
    	classUnderTest = null;
    }  
}
