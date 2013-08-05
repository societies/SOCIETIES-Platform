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
import org.societies.useragent.conflict.EnsembleConflictResolutionRule;
import org.societies.useragent.conflict.EnsembleConflictResolutionRule.Operator;

public class EnsembleConflictResolutionRuleORMockitoTest {
	private IOutcome intent;
	private IOutcome preference;
	private ConflictResolutionRule mock$1; 
	private ConflictResolutionRule mock$2; 
    private EnsembleConflictResolutionRule classUnderTest;
    @Before  
    public void setUp() {  
//    	creat mocked class
        mock$1 = mock(ConflictResolutionRule.class); 
        mock$2 = mock(ConflictResolutionRule.class); 
        intent=mock(IOutcome.class);
        preference=mock(IOutcome.class);
//      creat an instance of your tested class 
//      set mock class to the test class
        classUnderTest = new EnsembleConflictResolutionRule(mock$1,mock$2, Operator.OR);
    }  
    
    @Test  
    public void collaborationCallTest() {  
//    	set mock class Behavior
    	String trtr=UUID.randomUUID().toString();
    	when(intent.getvalue()).thenReturn(trtr);
    	when(preference.getvalue()).thenReturn(UUID.randomUUID().toString());
    	when(mock$1.match(intent, preference)).thenReturn(true);
    	when(mock$2.match(intent, preference)).thenReturn(false);
//    	call the method of your class under test 
    	classUnderTest.match(intent, preference);
//    	then check whether the mock call has been as expected
    	verify(mock$1).match(intent, preference);
//    	verify(mock$2).match(intent, preference);
    }  
    
    @After  
    public void tearDown(){  
    	intent=null;
    	preference=null;
    	mock$1 = null;
    	mock$2 = null;
    	classUnderTest = null;
    }  

}
