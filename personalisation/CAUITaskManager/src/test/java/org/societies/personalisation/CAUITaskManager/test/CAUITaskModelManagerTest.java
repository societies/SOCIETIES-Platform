package org.societies.personalisation.CAUITaskManager.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
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

	private ICAUITaskManager modelManager;
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
		List<IUserIntentAction> userActionA = modelManager.retrieveActionsByTypeValue("A-homePc", "off");
		//System.out.println("Retrieved_userActionA:"+ userActionA);
		//System.out.println("Retrieved_userActionA:"+ userActionA.get(0));
		assertEquals("css://nikosk@societies.org/HelloEarth#A-homePc=off/13", userActionA.get(0).toString());
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

		System.out.println("testRetrieveActionsByContext");
		createModel();
		UserIntentModelData modelData = modelManager.retrieveModel();

		System.out.println("actionsList  "+ modelData.getActionModel().keySet());

		Map<String, Serializable> currentSituationConext1 = new HashMap<String, Serializable>();
		currentSituationConext1.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "moon");
		currentSituationConext1.put(CtxAttributeTypes.STATUS, "busy");
		List<IUserIntentAction> results1 =  modelManager.retrieveActionsByContext(currentSituationConext1);

		assertEquals(1,results1.size());
		IUserIntentAction action1 = results1.get(0);
		assertEquals("A-homePc",action1.getparameterName());
		assertEquals("on",action1.getvalue());
		HashMap<String,Serializable> context = action1.getActionContext();
		assertEquals( "moon" , context.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
		assertEquals( "busy" , context.get(CtxAttributeTypes.STATUS));
		
		System.out.println("estimated action  "+ action1);
		System.out.println("context loc "+ context.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
		System.out.println("context status "+ context.get(CtxAttributeTypes.STATUS));

///----------		
		Map<String, Serializable> situationConext2 = new HashMap<String, Serializable>();
		situationConext2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "mars");
		situationConext2.put(CtxAttributeTypes.STATUS, "online");
		List<IUserIntentAction> results2 =  modelManager.retrieveActionsByContext(situationConext2);
		System.out.println("results2  "+ results2);
		assertEquals(1,results2.size());
		IUserIntentAction action2 = results2.get(0);
		
		assertEquals("C-radio",action2.getparameterName());
		assertEquals("mute",action2.getvalue());
		HashMap<String,Serializable> context2 = action2.getActionContext();
		assertEquals( "mars" , context2.get(CtxAttributeTypes.LOCATION_SYMBOLIC));
		assertEquals( "online" , context2.get(CtxAttributeTypes.STATUS));
		
		
		Map<String, Serializable> situationConext3 = new HashMap<String, Serializable>();
		situationConext3.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "null");
		situationConext3.put(CtxAttributeTypes.STATUS, "free");
		//situationConext2.put(CtxAttributeTypes.TEMPERATURE, "15");
		List<IUserIntentAction> results3 =  modelManager.retrieveActionsByContext(situationConext3);
		//System.out.println("output 3 "+ results3);
		assertEquals(1,results3.size());

	
		Map<String, Serializable> situationConext4 = new HashMap<String, Serializable>();
		situationConext4.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "moon");
		situationConext4.put(CtxAttributeTypes.STATUS, "free");
		//situationConext2.put(CtxAttributeTypes.TEMPERATURE, "15");
		List<IUserIntentAction> results4 =  modelManager.retrieveActionsByContext(situationConext4);
		System.out.println("output for ctx loc:moon and status:free "+ results4);
		assertEquals(2,results4.size());

		Map<String, Serializable> situationConext5 = new HashMap<String, Serializable>();
		situationConext5.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "xxx");
		situationConext5.put(CtxAttributeTypes.STATUS, "yyyy");
		//situationConext2.put(CtxAttributeTypes.TEMPERATURE, "15");
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
		contextMap.put(CtxAttributeTypes.STATUS,"free");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
		userActionA.setActionContext(contextMap);

		IUserIntentAction userActionB = modelManager.createAction(serviceId,"ServiceType","A-homePc","on");
		contextMap = new HashMap<String,Serializable>(); 
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"moon");
		contextMap.put(CtxAttributeTypes.STATUS,"busy");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
		userActionB.setActionContext(contextMap);

		IUserIntentAction userActionC = modelManager.createAction(serviceId,"ServiceType","C-radio","mute");
		contextMap = new HashMap<String,Serializable>(); 
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"mars");
		contextMap.put(CtxAttributeTypes.STATUS,"online");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
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