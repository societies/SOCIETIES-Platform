package org.societies.personalisation.CAUIPrediction.impl;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class UIModelBroker {
	
	
	private ICtxBroker ctxBroker;
	private ICAUITaskManager modelManager;
	
	
	UIModelBroker(ICtxBroker ctxBroker, ICAUITaskManager cauiTaskManager){
		this.ctxBroker = ctxBroker; 
		this.modelManager = cauiTaskManager;
	}

	public void setActiveModel(IIdentity requestor){
		// retrieve model from Context DB
		// set model as active in CauiTaskManager
		// until then create and use a fake model
		createFakeModel();
	}

	private void createFakeModel(){
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

}
