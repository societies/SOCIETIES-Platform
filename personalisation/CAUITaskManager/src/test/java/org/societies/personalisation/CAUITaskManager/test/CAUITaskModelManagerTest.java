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
		//create Task A
		IUserIntentAction userActionA = modelManager.createAction(null,"ServiceType","A-homePc","off");
		IUserIntentAction userActionB = modelManager.createAction(null,"ServiceType","F-homePc","off");
		IUserIntentAction userActionC = modelManager.createAction(null,"ServiceType","C-homePc","off");
		IUserIntentAction userActionD = modelManager.createAction(null,"ServiceType","D-homePc","off");

		List<IUserIntentAction> actionList = new ArrayList<IUserIntentAction>();
		actionList.add(0,userActionA);
		actionList.add(1,userActionB);
		actionList.add(2,userActionC);
		actionList.add(3,userActionD);

		Double [][] actionMatrixA  = new Double[actionList.size()][actionList.size()] ;

		for(int i=0; i<actionList.size();i++){
			for (int j=0; j<actionList.size();j++){
				actionMatrixA[i][j] = 0.0  ;
			}
		}

		actionMatrixA[0][1]=1.0;
		actionMatrixA[1][2]=1.0;
		actionMatrixA[2][3]=1.0;

		IUserIntentTask taskA = modelManager.createTask("TaskA", actionList, actionMatrixA);

		modelManager.displayTask(taskA);


		//create Task B
		IUserIntentAction userActionE = modelManager.createAction(null,"ServiceType","A-homePc","on");
		IUserIntentAction userActionF = modelManager.createAction(null,"ServiceType","F-homePc","off");
		IUserIntentAction userActionG = modelManager.createAction(null,"ServiceType","G-homePc","off");
		//IUserIntentAction userActionH = modelManager.createAction(null,"ServiceType","H-homePc","off");

		List<IUserIntentAction> actionListB = new ArrayList<IUserIntentAction>();
		actionListB.add(0,userActionE);
		actionListB.add(1,userActionF);
		actionListB.add(2,userActionG);
		//actionListB.add(3,userActionH);
		Double [][] actionMatrixB  = new Double[actionListB.size()][actionListB.size()] ;

		for(int i=0; i<actionListB.size();i++){
			for (int j=0; j<actionListB.size();j++){
				actionMatrixB[i][j] = 0.0  ;
			}
		}

		actionMatrixB[0][1]=0.5;
		actionMatrixB[0][2]=0.5;
		actionMatrixB[1][2]=1.0;
		actionMatrixB[2][1]=1.0;
		IUserIntentTask taskB = modelManager.createTask("TaskB", actionListB, actionMatrixB);
		modelManager.displayTask(taskB);

		// create model
		List<IUserIntentTask> taskList = new ArrayList<IUserIntentTask>();
		taskList.add(0,taskA);
		taskList.add(1,taskB);

		Double [][] taskMatrix = new Double[taskList.size()][taskList.size()] ;
		for(int i=0; i<taskList.size();i++){
			for (int j=0; j<taskList.size();j++){
				taskMatrix[i][j] = 0.0  ;
			}
		}
		taskMatrix[0][1] = 1.0;

		UserIntentModelData modelData = modelManager.createModel(taskList, taskMatrix);
		modelManager.displayModel(modelData);

		modelManager.updateModel(modelData);
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
