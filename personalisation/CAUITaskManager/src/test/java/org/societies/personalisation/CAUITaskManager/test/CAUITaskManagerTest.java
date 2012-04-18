/**
 * Copyright (c) 2011, SOCIETIES Consortium
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.personalisation.CAUITaskManager.test;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;

/**
 * CAUITaskManagerTest  tests CAUITaskManager methods.
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITaskManagerTest {



	ICAUITaskManager modelManager;



	CAUITaskManagerTest(){
		modelManager = new CAUITaskManager();

		createModel();
		retrieveTests();

	}


	private void  retrieveTests(){

		IUserIntentAction retrievedAction = modelManager.retrieveAction("A-homePc=off/0");
		System.out.println("retrievedAction "+ retrievedAction.getparameterName()+" "+retrievedAction.getvalue());

		IUserIntentTask retrievedTask = modelManager.retrieveTask("TaskA/4");
		System.out.println("retrievedTask "+ retrievedTask.getTaskID());

		List<IUserIntentAction> resultsType = modelManager.retrieveActionsByType("A-homePc");
		System.out.println("getActionsByType(homePC) " + resultsType);

		List<IUserIntentAction> resultsTypeValue = modelManager.retrieveActionsByTypeValue("A-homePc","on");
		System.out.println("getActionsByType(homePC,on) " + resultsTypeValue);

		List<IUserIntentAction> resultsTypeValue2 = modelManager.retrieveActionsByTypeValue("A-homePc","tttt");
		System.out.println("getActionsByType(homePC,ttt) " + resultsTypeValue2);

		//identify the task that contains the "B-homePc","off"
		// no context or previous actions are used
		HashMap<String, Serializable> currentContext = new HashMap<String, Serializable>();
		String[] lastAction = {"none"};
		Map<IUserIntentAction, IUserIntentTask> actionTasksMap = modelManager.identifyActionTaskInModel("F-homePc","off",currentContext, lastAction);
		System.out.println("modelManager.identifyActionTaskInModel(B-homePc,off): "+ actionTasksMap);
		}

	private void createModel(){

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

	public static void main(String[] args) {
		new CAUITaskManagerTest();
	}
}
