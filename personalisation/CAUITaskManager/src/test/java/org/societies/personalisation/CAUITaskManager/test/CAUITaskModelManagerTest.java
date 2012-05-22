package org.societies.personalisation.CAUITaskManager.test;


import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.ArrayList;
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
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
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

		IUserIntentAction userActionA = modelManager.createAction(null,"ServiceType","A-homePc","off");

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

	
	@Test
	public void testCreateModel(){
		
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

}
