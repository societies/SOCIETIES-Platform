package org.societies.personalisation.CAUITaskManager.test;


import static org.junit.Assert.*;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

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
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
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
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"moon");
		contextMap.put(CtxAttributeTypes.STATUS,"free");
		contextMap.put(CtxAttributeTypes.TEMPERATURE,15);
		userActionB.setActionContext(contextMap);

		IUserIntentAction userActionC = modelManager.createAction(serviceId,"ServiceType","C-radio","mute");
		contextMap.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"mars");
		contextMap.put(CtxAttributeTypes.STATUS,"free");
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

		IUserIntentTask task = modelManager.createTask("TaskA", actions);
		taskID = task.getTaskID() ;
	}

}