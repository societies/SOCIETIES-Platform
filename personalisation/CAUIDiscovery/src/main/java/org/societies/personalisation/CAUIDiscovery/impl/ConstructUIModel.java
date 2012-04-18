package org.societies.personalisation.CAUIDiscovery.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

public class ConstructUIModel {


	private ICAUITaskManager cauiTaskManager;
	private ICtxBroker ctxBroker;

	public ConstructUIModel(ICAUITaskManager cauiTaskManager,ICtxBroker ctxBroker ){
		this.cauiTaskManager =  cauiTaskManager;
		this.ctxBroker = ctxBroker;
	}


	public void constructModel(LinkedHashMap<String, HashMap<String, Double>> trans3ProbDictionary) {

		constructFakeModel();
	}


	void storeModelCtxDB(UserIntentModelData modelData){
		try {
			Future<CtxEntity> futureCtxEntity = ctxBroker.createEntity("FakeEntity");
			CtxEntity ctxEntity = futureCtxEntity.get();
			Future<CtxAttribute> futureCtxAttr =  ctxBroker.createAttribute(ctxEntity.getId(), "uiModel");
			CtxAttribute ctxAttr = futureCtxAttr.get();
			byte[] blobBytes = SerialisationHelper.serialise(modelData);

			//ctxAttr.setBinaryValue(blobBytes);

			ctxBroker.updateAttribute(ctxAttr.getId(), blobBytes);

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void constructFakeModel(){
		//create Task A
		
		
		IUserIntentAction userActionA = cauiTaskManager.createAction(null,"ServiceType","A-homePc","off");
		IUserIntentAction userActionB = cauiTaskManager.createAction(null,"ServiceType","F-homePc","off");
		IUserIntentAction userActionC = cauiTaskManager.createAction(null,"ServiceType","C-homePc","off");
		IUserIntentAction userActionD = cauiTaskManager.createAction(null,"ServiceType","D-homePc","off");

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

		IUserIntentTask taskA = cauiTaskManager.createTask("TaskA", actionList, actionMatrixA);

		cauiTaskManager.displayTask(taskA);


		//create Task B
		IUserIntentAction userActionE = cauiTaskManager.createAction(null,"ServiceType","A-homePc","on");
		IUserIntentAction userActionF = cauiTaskManager.createAction(null,"ServiceType","F-homePc","off");
		IUserIntentAction userActionG = cauiTaskManager.createAction(null,"ServiceType","G-homePc","off");
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
		IUserIntentTask taskB = cauiTaskManager.createTask("TaskB", actionListB, actionMatrixB);
		cauiTaskManager.displayTask(taskB);

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

		UserIntentModelData modelData = cauiTaskManager.createModel(taskList, taskMatrix);
		cauiTaskManager.displayModel(modelData);
		cauiTaskManager.updateModel(modelData);

		storeModelCtxDB(modelData);
	}
}