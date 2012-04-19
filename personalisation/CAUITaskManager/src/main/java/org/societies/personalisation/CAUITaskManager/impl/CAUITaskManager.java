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
package org.societies.personalisation.CAUITaskManager.impl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUI.api.model.UIModelObjectNumberGenerator;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentTask;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * CAUITaskManager
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITaskManager implements ICAUITaskManager{


	private ICtxBroker ctxBroker;


	public ICtxBroker getCtxBroker() {
		System.out.println(this.getClass().getName()+": Return ctxBroker");

		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		System.out.println(this.getClass().getName()+": Got ctxBroker");

		this.ctxBroker = ctxBroker;
	}

	// constructor
	public void initialiseCAUITaskManager(){

	}

	public CAUITaskManager(){

	}


	UserIntentModelData activeModel = null;


	@Override
	public IUserIntentAction createAction(ServiceResourceIdentifier serviceID, String serviceType, String par, String val) {
		UserIntentAction action = new UserIntentAction (serviceID, serviceType, par,val, UIModelObjectNumberGenerator.getNextValue());

		return action;
	}


	@Override
	public IUserIntentTask createTask(String taskName,
			List<IUserIntentAction> actions, Double [][] matrix) {

		IUserIntentTask userTask = new UserIntentTask(taskName,UIModelObjectNumberGenerator.getNextValue(),actions, matrix) ;
		return userTask;
	}


	@Override
	public UserIntentModelData retrieveModel() {
		return this.activeModel;
	}


	@Override
	public void updateModel(UserIntentModelData model) {
		this.activeModel = model;
	}


	@Override
	public UserIntentModelData createModel(List<IUserIntentTask> tasks, Double[][] matrix) {
		UserIntentModelData model = null;
		model = new UserIntentModelData(tasks,matrix);

		return model;
	}


	@Override
	public IUserIntentAction retrieveAction(String actID) {
		IUserIntentAction actionResult = null;

		List<IUserIntentTask> tasks = this.activeModel.getTaskList();
		for(IUserIntentTask task : tasks){
			List<IUserIntentAction> actions = task.getActions();
			for(IUserIntentAction action : actions){
				if (action.getActionID().equals(actID)) actionResult=action;

			}
		}
		return actionResult;
	}



	@Override
	public List<IUserIntentAction> retrieveActionsByType(String actionType) {
		List<IUserIntentAction> actionResult = new ArrayList<IUserIntentAction>();

		List<IUserIntentTask> tasks = this.activeModel.getTaskList();
		for(IUserIntentTask task : tasks){
			List<IUserIntentAction> actions = task.getActions();
			for(IUserIntentAction action : actions){
				if (action.getparameterName().equals(actionType)) actionResult.add(action);

			}
		}
		return actionResult;
	}


	@Override
	public List<IUserIntentAction> retrieveActionsByTypeValue(String actionType, String actionValue) {

		List<IUserIntentAction> actionResult = new ArrayList<IUserIntentAction>();

		List<IUserIntentTask> tasks = this.activeModel.getTaskList();
		for(IUserIntentTask task : tasks){
			List<IUserIntentAction> actions = task.getActions();
			for(IUserIntentAction action : actions){
				if (action.getparameterName().equals(actionType) && action.getvalue().equals(actionValue) ) actionResult.add(action);
			}
		}
		return actionResult;
	}


	@Override
	public boolean actionBelongsToModel(IUserIntentAction arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Map<IUserIntentAction, IUserIntentTask> identifyActionTaskInModel(
			String actionType, String actionValue, HashMap<String, Serializable> context,
			String[] lastAction) {

		Map<IUserIntentAction, IUserIntentTask> results = new HashMap<IUserIntentAction, IUserIntentTask>();


		List<IUserIntentTask> tasks = this.activeModel.getTaskList();
		for(IUserIntentTask task : tasks){
			List<IUserIntentAction> actions = task.getActions();
			for(IUserIntentAction action : actions){
				if (action.getparameterName().equals(actionType) && action.getvalue().equals(actionValue) ) results.put(action,task);
			}
		}	

		return results;
	}


	@Override
	public UserIntentAction retrieveCurrentIntentAction(IIdentity arg0,
			IIdentity arg1, ServiceResourceIdentifier arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IUserIntentTask retrieveTask(String taskID) {

		IUserIntentTask taskResult = null;
		List<IUserIntentTask> tasks = this.activeModel.getTaskList();
		for(IUserIntentTask task : tasks){
			if (task.getTaskID().equals(taskID)) taskResult=task;
		}
		return taskResult;
	}


	@Override
	public boolean taskBelongsToModel(IUserIntentTask arg0) {
		// TODO Auto-generated method stub
		return false;
	}




	//*********************************************
	public void displayTask (IUserIntentTask task) {
		Double [][] matrix = task.getMatrix();
		List<IUserIntentAction> actionList = task.getActions();

		System.out.print(actionList+"\n");
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				System.out.print(" "+matrix[i][j]+"                 ");
			}
			System.out.println();
		}
	}


	public void displayModel (UserIntentModelData model) {

		Double [][] matrix = model.getMatrix();
		List<IUserIntentTask> taskList = model.getTaskList();

		System.out.print(taskList+"\n");
		//	System.out.print(matrix+"\n");
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				System.out.print(" "+matrix[i][j]+"                 ");
			}
			System.out.println();
		}
	}
}