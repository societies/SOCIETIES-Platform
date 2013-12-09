package org.societies.personalisation.CAUITaskManager.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;

public class CAUITaskModelManagerTest {

	private CAUITaskManager modelManager;
	String taskID = "";
	final UserIntentModelData modelData = null;
	String actionIDString = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		modelManager = new CAUITaskManager();
	}

	@After
	public void tearDown() throws Exception {
	}

	private static final long serialVersionUID = 1L;



	@Test
	public void testRetrieveModel() {

		assertNull(modelData);
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();
		System.out.println(" "+modelData.getActionModel().size());
		assertNotNull(modelData);
		assertEquals(3,modelData.getActionModel().size());
	}


	@Test
	public void testUpdateModel() {

		modelManager.updateModel(null);
		UserIntentModelData modelData = modelManager.retrieveModel();
		assertNull(modelData);
		createModel();
		UserIntentModelData modelData2 = modelManager.retrieveModel();
		//System.out.println(" modelData2 "+modelData2.getActionModel());
		//System.out.println(" modelData2 size "+modelData2.getActionModel().size());
		assertNotNull(modelData2);
		assertEquals(3,modelData2.getActionModel().size());
		modelManager.updateModel(null);
	}

	@Test
	public void testCompareActions() {
		
		
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));

			serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
			
			serviceId2.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));

			serviceId2.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		IUserIntentAction userActionA = modelManager.createAction(serviceId1,"ServiceType","A-homePc","off");
		HashMap<String,Serializable> actionCtx = new HashMap<String,Serializable>();
		actionCtx.put("temperature", 32);
		actionCtx.put("noiseLevel", "loud");

		userActionA.setActionContext(actionCtx);
		userActionA.setConfidenceLevel(65);
		userActionA.setTaskID("taskID");
		
		IUserIntentAction userActionB = modelManager.createAction(serviceId2,"ServiceType","A-homePc","off");
		HashMap<String,Serializable> actionCtx2 = new HashMap<String,Serializable>();
		actionCtx2.put("temperature", 32);
		actionCtx2.put("noiseLevel", "loud");

		userActionB.setActionContext(actionCtx2);
		userActionB.setConfidenceLevel(65);
		userActionB.setTaskID("taskID");
				
		assertEquals(userActionA,userActionB);
	}
	
	@Test
	public void testCreateAction() {

		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));

			serviceId.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
			IUserIntentAction userActionA = modelManager.createAction(serviceId,"ServiceType","A-homePc","off");

			userActionA.setDuration(12);
			HashMap<String,Serializable> actionCtx = new HashMap<String,Serializable>();
			actionCtx.put("temperature", 32);
			actionCtx.put("noiseLevel", "loud");

			userActionA.setActionContext(actionCtx);
			userActionA.setConfidenceLevel(65);
			userActionA.setTaskID("taskID");

			assertNotNull(userActionA);
			assertEquals(userActionA.getparameterName(),"A-homePc");
			assertEquals(userActionA.getvalue(), "off");
			assertEquals(userActionA.getActionContext().size(),2);
			assertEquals(userActionA.getActionContext().get("temperature"),32);
			assertEquals(userActionA.getActionContext().get("noiseLevel"),"loud");
			assertEquals(userActionA.getConfidenceLevel(),65);
			assertEquals(userActionA.getTaskID(),"taskID");
			System.out.println("service id "+userActionA.getServiceID().getServiceInstanceIdentifier());

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testFindBestMatchingAction() {
	
		List<IUserIntentAction> candidateActions = new ArrayList<IUserIntentAction>();
		 
		Map<String, Serializable> situationContext = new HashMap<String, Serializable>();
		situationContext.put(CtxAttributeTypes.HOUR_OF_DAY, 2 );
		situationContext.put(CtxAttributeTypes.DAY_OF_WEEK, "monday" );
		situationContext.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "earth");
		
		List<IUserIntentAction> result = modelManager.findBestMatchingAction(candidateActions, situationContext);
		System.out.println("testFindBestMatchingAction out: "+result);
		
		
		
	}
	
	
	@Ignore
	@Test
	public void testCreateTaskStringDouble() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveTask() {
		fail("Not yet implemented");
	}


	@Test
	public void testRetrieveAction() {

		System.out.println("testRetrieveAction");
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();
		assertNotNull(modelData);
		IUserIntentAction action = modelManager.retrieveAction(this.actionIDString);
		assertEquals(actionIDString, action.getActionID());
	}


	@Test
	public void testRetrieveActionsByTypeValue() {

		System.out.println("testRetrieveActionsByTypeValue");
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();
		System.out.println("testRetrieveActionsByTypeValue getActionModel "+ modelData.getActionModel());
		List<IUserIntentAction> userActionAList = modelManager.retrieveActionsByTypeValue("A-homePc", "off");
		System.out.println("Retrieved_userActionA:"+ userActionAList);
		//System.out.println("Retrieved_userActionA:"+ userActionA.get(0));
		IUserIntentAction act = userActionAList.get(0);
		assertEquals("A-homePc",act.getparameterName());
		assertEquals("off",act.getvalue());

	}


	@Test
	public void testRetrieveActionsByServiceIDTypeValue() {

		System.out.println("testRetrieveActionsByServiceIDTypeValue");
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();
		System.out.println("testRetrieveActionsByServiceIDTypeValue getActionModel "+ modelData.getActionModel());

		List<IUserIntentAction> userActionList = modelManager.retrieveActionsByServiceTypeValue("css://nikosk@societies.org/HelloEarth","A-homePc", "off");
		//System.out.println("userActionList:"+ userActionList);

		IUserIntentAction userActionA = userActionList.get(0);
		//System.out.println("userAction:"+ userActionA.getServiceID().toString());
		assertEquals("css://nikosk@societies.org/HelloEarth", userActionA.getServiceID().getServiceInstanceIdentifier());
		assertEquals(userActionA.getparameterName(),"A-homePc");
		assertEquals(userActionA.getvalue(),"off");
	}

	@Test
	public void testRetrieveActionsByServiceIDType() {

		System.out.println("testRetrieveActionsByServiceIDType");
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();
		System.out.println("testRetrieveActionsByServiceIDType getActionModel "+ modelData.getActionModel());
		List<IUserIntentAction> userActionList = modelManager.retrieveActionsByServiceType("css://nikosk@societies.org/HelloEarth","A-homePc");
		System.out.println("userActionList:"+ userActionList);
		assertEquals(2,userActionList.size());
		IUserIntentAction userActionA = userActionList.get(0);
		System.out.println("userAction service id :"+ userActionA.getServiceID().getServiceInstanceIdentifier());
	}


	@Test
	public void testRetrieveActionsByContext() {

		System.out.println("______________________ testRetrieveActionsByContext");
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();

//		System.out.println("actionsList  "+ modelData.getActionModel().keySet());
		System.out.println("************* printing model with context ********************");
		for(IUserIntentAction act :  modelData.getActionModel().keySet()){
		
			System.out.println("action : "+ act+" with ctx:"+ act.getActionContext());
			
		}
		
		
		
		Map<String, Serializable> currentSituationConext1 = new HashMap<String, Serializable>();
		currentSituationConext1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "moon");
		currentSituationConext1.put(CtxAttributeTypes.DAY_OF_WEEK, "Tuesday");
		currentSituationConext1.put(CtxAttributeTypes.HOUR_OF_DAY, 3);
		

		System.out.println(" ***** test 1 retrieveActionsByContext  with currentSituationConext1: "+currentSituationConext1);
		List<IUserIntentAction> results1 =  modelManager.retrieveActionsByContext(currentSituationConext1);
		System.out.println("results  : "+results1);
		
		assertEquals(1,results1.size());
		IUserIntentAction action1 = results1.get(0);
		assertEquals("A-homePc",action1.getparameterName());
		assertEquals("on",action1.getvalue());
		
		HashMap<String,Serializable> actionContext = action1.getActionContext();
		assertEquals( "moon" , actionContext.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
		assertEquals( "Tuesday" , actionContext.get(CtxAttributeTypes.DAY_OF_WEEK));
		assertEquals( 3 , actionContext.get(CtxAttributeTypes.HOUR_OF_DAY));
		System.out.println("estimated action  "+ action1);
		System.out.println("estimated action  score  "+ action1.getConfidenceLevel());
		
		assertEquals(100 , action1.getConfidenceLevel());
		System.out.println("context loc "+ actionContext.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
		System.out.println("context DAY_OF_WEEK "+ actionContext.get(CtxAttributeTypes.DAY_OF_WEEK));
		System.out.println("context HOUR_OF_DAY "+ actionContext.get(CtxAttributeTypes.HOUR_OF_DAY));

		
		
		///----------		
		Map<String, Serializable> situationConext2 = new HashMap<String, Serializable>();
		situationConext2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "mars");
		situationConext2.put(CtxAttributeTypes.DAY_OF_WEEK, "Wednesday");
		List<IUserIntentAction> results2 =  modelManager.retrieveActionsByContext(situationConext2);
		
		System.out.println("*****  test 2 retrieveActionsByContext  with situationConext2: "+situationConext2);
		System.out.println("results2  "+ results2);
		assertEquals(1,results2.size());
		IUserIntentAction action2 = results2.get(0);
		System.out.println("action 2 estimated action  score  "+ action2.getConfidenceLevel());
		
		assertEquals("C-radio",action2.getparameterName());
		assertEquals("mute",action2.getvalue());
		HashMap<String,Serializable> context2 = action2.getActionContext();
		assertEquals( "mars" , context2.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
		assertEquals( "Wednesday" , context2.get(CtxAttributeTypes.DAY_OF_WEEK));
		
		
		Map<String, Serializable> situationConext3 = new HashMap<String, Serializable>();
		situationConext3.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "null");
		situationConext3.put(CtxAttributeTypes.DAY_OF_WEEK, "monday");
		situationConext3.put(CtxAttributeTypes.HOUR_OF_DAY, 2 );
		System.out.println("*****  test 3 retrieveActionsByContext  with situationConext3: "+situationConext3);
		List<IUserIntentAction> results3 =  modelManager.retrieveActionsByContext(situationConext3);
		//System.out.println("output 3 "+ results3);
		IUserIntentAction action3 = results3.get(0);
		System.out.println("action confidence :"+ action3.getConfidenceLevel());
		assertEquals(66 ,action3.getConfidenceLevel());
		assertEquals(1,results3.size());

		
		Map<String, Serializable> situationConext4 = new HashMap<String, Serializable>();
		situationConext4.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "moon");
		situationConext4.put(CtxAttributeTypes.DAY_OF_WEEK, "Tuesday");
		situationConext4.put(CtxAttributeTypes.HOUR_OF_DAY, 3);
		List<IUserIntentAction> results4 =  modelManager.retrieveActionsByContext(situationConext4);
		System.out.println("*****  test 4 retrieveActionsByContext  with situationConext3: "+situationConext4);
		System.out.println("output for ctx loc:moon and DAY_OF_WEEK: Tuesday and hod:3 "+ results4);
		IUserIntentAction action4 = results4.get(0);
		
		System.out.println("action confidence :"+ action4.getConfidenceLevel());
		assertEquals(100,action4.getConfidenceLevel());
		assertEquals(1,results4.size());

		
		Map<String, Serializable> situationConext5 = new HashMap<String, Serializable>();
		situationConext5.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "xxx");
		situationConext5.put(CtxAttributeTypes.DAY_OF_WEEK, "yyyy");
		//situationConext2.put(CtxAttributeTypes.TEMPERATURE, "15");
		System.out.println("*****  test 5 retrieveActionsByContext  with situationConext3: "+situationConext5);
		List<IUserIntentAction> results5 =  modelManager.retrieveActionsByContext(situationConext5);
		System.out.println("output for ctx loc:xxx and status:yyy "+ results5);
		assertEquals(0,results5.size());
	}

	@Ignore
	@Test
	public void testRetrieveActionsByType() {

	}

	@Ignore
	@Test
	public void testSetNextActionLink() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testSetNextTaskLink() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveNodeTask() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveNodeAction() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveNextAction() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveNextTask() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveTasks() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testIdentifyActionTaskInModel() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testIdentifyNextAction() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateModel(){
		assertNull(modelData);
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();
		//System.out.println(" "+modelData.getActionModel().size());
		assertNotNull(modelData);
		assertEquals(3,modelData.getActionModel().size());
	}

	@Ignore
	@Test
	public void testCreateTaskString() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveCurrentIntentAction() {
		fail("Not yet implemented");
	}


	private void createModel(){

		modelManager.createModel();

		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));
			serviceId.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//create Task A
		IUserIntentAction userActionA = modelManager.createAction(serviceId,"ServiceType","A-homePc","off");
		this.actionIDString = userActionA.getActionID();

		HashMap<String,Serializable> contextMap = new HashMap<String,Serializable>(); 
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"earth");
		contextMap.put(CtxAttributeTypes.DAY_OF_WEEK,"monday");
		contextMap.put(CtxAttributeTypes.HOUR_OF_DAY,2);
		userActionA.setActionContext(contextMap);

		IUserIntentAction userActionB = modelManager.createAction(serviceId,"ServiceType","A-homePc","on");
		contextMap = new HashMap<String,Serializable>(); 
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"moon");
		contextMap.put(CtxAttributeTypes.DAY_OF_WEEK,"Tuesday");
		contextMap.put(CtxAttributeTypes.HOUR_OF_DAY,3);
		userActionB.setActionContext(contextMap);

		IUserIntentAction userActionC = modelManager.createAction(serviceId,"ServiceType","C-radio","mute");
		contextMap = new HashMap<String,Serializable>(); 
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"mars");
		contextMap.put(CtxAttributeTypes.DAY_OF_WEEK,"Wednesday");
		contextMap.put(CtxAttributeTypes.HOUR_OF_DAY, 4);
		userActionC.setActionContext(contextMap);

		modelManager.setActionLink(userActionA, userActionB, 0.82);
		modelManager.setActionLink(userActionB, userActionC, 0.42);
		modelManager.setActionLink(userActionA, userActionC, 0.18);

		LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction, Double>> actions = new LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

		HashMap<IUserIntentAction, Double> targetActionsMapActionA = new HashMap<IUserIntentAction, Double>();
		targetActionsMapActionA.put(userActionB, 0.82);
		targetActionsMapActionA.put(userActionC, 0.18);
		actions.put(userActionA,targetActionsMapActionA);

		HashMap<IUserIntentAction, Double> targetActionsMapActionB = new HashMap<IUserIntentAction, Double>();
		targetActionsMapActionB.put(userActionC, 0.82);
		actions.put(userActionB,targetActionsMapActionB);

		//actions.put(userActionA, userActionB, 0.82)
		//System.out.println("model :   "+ actions);
		IUserIntentTask task = modelManager.createTask("TaskA", actions);
		taskID = task.getTaskID() ;
	}

}