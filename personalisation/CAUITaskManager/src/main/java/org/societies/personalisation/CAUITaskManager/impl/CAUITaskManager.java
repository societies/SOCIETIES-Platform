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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;

import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.TaskModelData;
import org.societies.personalisation.CAUI.api.model.UIModelObjectNumberGenerator;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentTask;

/**
 * CAUITaskManager
 * 
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITaskManager  implements ICAUITaskManager{

	private JTree tree;
	DefaultMutableTreeNode top; 

		
	public CAUITaskManager(){
		top = new DefaultMutableTreeNode("User Intent Task model");	
	}


	public DefaultMutableTreeNode getRoot(){
		return top;
	}

	//call this after the top variable is filled with uiModel objects
	public JTree getTree(){
		tree = new JTree(this.top);
		return tree;
	}


	@Override
	public UserIntentAction createAction(String par, String val, double transProb) {
		UserIntentAction action = new UserIntentAction (par, val, UIModelObjectNumberGenerator.getNextValue(), transProb);
		return action;
	}

	@Override
	public UserIntentTask createTask(String taskName, double transProb) {
		UserIntentTask task= new UserIntentTask(taskName, UIModelObjectNumberGenerator.getNextValue(), transProb);

		return task;
	}
	

	@Override
	public UserIntentAction getAction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserIntentAction> getActionsByType(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction getCurrentIntentAction(EntityIdentifier arg0,
			EntityIdentifier arg1, IServiceResourceIdentifier arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<UserIntentAction, Double> getNextAction(UserIntentAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Double> getNextTask(UserIntentTask arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction getTask(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskModelData getTaskModelData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserIntentTask> getTasks(UserIntentAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<UserIntentAction, UserIntentTask> identifyActionTaskInModel(
			String arg0, String arg1, HashMap<String, Serializable> arg2,
			String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction identifyNextAction(
			Map<UserIntentAction, UserIntentTask> arg0, String arg1,
			Map<String, Serializable> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetTaskModelData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNextActionLink(UserIntentAction arg0, UserIntentAction arg1,
			Double arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNextTaskLink(UserIntentTask arg0, String arg1, Double arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTaskModel(TaskModelData arg0) {
		// TODO Auto-generated method stub

	}
	
}
