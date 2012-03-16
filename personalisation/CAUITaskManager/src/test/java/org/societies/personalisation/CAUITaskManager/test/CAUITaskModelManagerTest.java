package org.societies.personalisation.CAUITaskManager.test;


import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;

public class CAUITaskModelManagerTest {

	private ICAUITaskManager modelManager;

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


	private void  retrieveTests(){
	
		UserIntentAction retrievedAction = modelManager.retrieveAction("Actions=start/0");
		System.out.println("retrievedAction "+ retrievedAction.getparameterName()+" "+retrievedAction.getvalue());

		List<UserIntentAction> resultsType = modelManager.retrieveActionsByType("A-homePc");
		System.out.println("getActionsByType(homePC) " + resultsType);

		List<UserIntentAction> resultsTypeValue = modelManager.retrieveActionsByTypeValue("A-homePc","off");
		System.out.println("getActionsByType(homePC,off) " + resultsTypeValue);

		IUserIntentAction actionsResult = resultsTypeValue.get(0);
		System.out.println ("action:"+actionsResult.toString()+" "+this.modelManager.actionBelongsToModel(actionsResult));
	}

	@Ignore
	@Test
	public void testRetrieveModel() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testUpdateModel() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateAction() {
		
		IUserIntentAction userActionA = modelManager.createAction("A-homePc","off",50);

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
		
		DefaultMutableTreeNode model = modelManager.retrieveModel();
		IUserIntentAction userActionA = modelManager.createAction("A-homePc","off",50);
		userActionA.setDuration(12);
		HashMap<String,Serializable> actionCtx = new HashMap<String,Serializable>();
		actionCtx.put("temperature", 32);
		actionCtx.put("noiseLevel", "loud");
		
		userActionA.setActionContext(actionCtx);
		userActionA.setConfidenceLevel(65);
		userActionA.setTaskID("taskID");
		
		String actionID = userActionA.getActionID();
		System.out.println("action id "+ actionID);
		
		modelManager.updateModel(model);
		
		IUserIntentAction userActionRetrieved = modelManager.retrieveAction(actionID);
		System.out.println(userActionRetrieved);
	}

	@Ignore
	@Test
	public void testRetrieveActionsByTypeValue() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveActionsByType() {
		fail("Not yet implemented");
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

	@Ignore
	@Test
	public void testCreateTaskString() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testActionBelongsToModel() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testTaskBelongsToModel() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testRetrieveCurrentIntentAction() {
		fail("Not yet implemented");
	}

}
