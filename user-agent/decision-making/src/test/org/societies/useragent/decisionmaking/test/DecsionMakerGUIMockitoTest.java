package org.societies.useragent.decisionmaking.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.useragent.conflict.ConflictResolutionRule;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.useragent.conflict.ConflictResolutionManager;
import org.societies.useragent.decisionmaking.DecisionMaker;
import org.societies.useragent.decisionmaking.DecisionMakingCallback;

public class DecsionMakerGUIMockitoTest {
	private IOutcome intent;
	private IOutcome preference;
    private ConflictResolutionManager mock; 
    private DecisionMaker classUnderTest;
    private ServiceResourceIdentifier id;
    private IUserFeedback GUI;
    @Before  
    public void setUp() {  
//    	creat mocked class
        mock = mock(ConflictResolutionManager.class); 
        intent=mock(IOutcome.class);
        preference=mock(IOutcome.class);
        id=mock(ServiceResourceIdentifier.class);
        GUI=mock(IUserFeedback.class);
//      creat an instance of your tested class
        classUnderTest = new DecisionMaker();
//      set mock class to the test class
        classUnderTest.setManager(mock);
        classUnderTest.setFeedbackHandler(GUI);
    }  
    
    @Test  
    public void collaborationCallTest() {  
//    	set mock class Behavior
    	String value$1=UUID.randomUUID().toString();
    	String value$2=UUID.randomUUID().toString();
    	String name="hi";
    	when(intent.getvalue()).thenReturn(value$1);
    	when(intent.getparameterName()).thenReturn(name);
    	when(intent.getServiceID()).thenReturn(id);
    	when(preference.getvalue()).thenReturn(value$2);
    	when(preference.getparameterName()).thenReturn(name);
    	when(preference.getServiceID()).thenReturn(id);
    	//when(intent.getServiceID()).thenReturn(UUID.randomUUID().toString());
    	when(preference.getvalue()).thenReturn(UUID.randomUUID().toString());
    	when(mock.resolveConflict(intent, preference)).thenReturn(null);
//    	call the method of your class under test 
    	List<IOutcome> intents=new ArrayList<IOutcome>();
    	List<IOutcome> preferences=new ArrayList<IOutcome>();
    	intents.add(intent);
    	preferences.add(preference);
    	classUnderTest.makeDecision(intents, preferences);
    	
//    	then check whether the mock call has been as expected
    	verify(intent).getServiceID();
    	verify(intent).getparameterName();
    	verify(intent).getvalue();
    	verify(preference).getServiceID();
    	verify(preference).getparameterName();
    	verify(preference).getvalue();
    	verify(mock).resolveConflict(intent, preference);
    	/*examing GUI's behavior requires on the equals function of GUI lib*/
//    	List<String> options=new ArrayList<String>();
//    	options.add(intent.toString());
//    	options.add(preference.toString());
//    	ExpProposalContent epc=new ExpProposalContent("Conflict Detected!",
//				options.toArray(new String[options.size()]));
//    	HashSet<IOutcome> pres=new HashSet<IOutcome>();
//    	pres.add(preference);
//    	verify(GUI).getExplicitFB(ExpProposalType.RADIOLIST, epc, new DecisionMakingCallback(classUnderTest,intent,pres));
    }  
    
    @After  
    public void tearDown(){  
    	intent=null;
    	preference=null;
    	mock = null;
    	classUnderTest = null;
    }  

}
