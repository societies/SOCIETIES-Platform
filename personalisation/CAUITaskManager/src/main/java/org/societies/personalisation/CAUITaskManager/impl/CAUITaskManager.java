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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.TaskModelData;
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
public class CAUITaskManager  implements ICAUITaskManager{


	private ICtxBroker ctxBroker;

	private JTree tree;

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
		
		this.model = new DefaultMutableTreeNode("User Intent Task model");	
	}

	
	//the tree model
	DefaultMutableTreeNode model; 

	public CAUITaskManager(){
		this.model = new DefaultMutableTreeNode("User Intent Task model");	
	}


	public DefaultMutableTreeNode retrieveModel(){
		return this.model;
	}


	public void updateModel(DefaultMutableTreeNode top){
		this.model = top;
	}


	//call this after the top variable is filled with uiModel objects
	public JTree getModelTree(){
		System.out.println("inside model "+ this.model.getChildCount());
		tree = new JTree(this.model);
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
	public IUserIntentTask createTask(String taskName) {
		// TODO Auto-generated method stub
		return null;
	}

	//this method needs to be added to ifc.
	@Override
	public void setNextActionLink(IUserIntentAction actionSrc, Map<IUserIntentAction,Double> actionTrgts) {

		DefaultMutableTreeNode sourceNode =null;
		DefaultMutableTreeNode targetNode = null ;
		//System.out.println(actionTrgts.size());
		//HashMap<IUserIntentAction,Double> actionTargets = actionTrgts; 

		if(actionSrc != null) {
			if (this.retrieveAction(actionSrc.getActionID()) != null){
				sourceNode = this.retrieveNodeAction(actionSrc);
				//			System.out.println("sourceNode = "+sourceNode + " "+ retrieveAction(actionSrc.getActionID()));

				for(IUserIntentAction action : actionTrgts.keySet()){
					//System.out.println("acti = "+action + "source "+sourceNode );
					targetNode = new DefaultMutableTreeNode(action);
					sourceNode.add(targetNode);
				}
			}
		}
		//	System.out.println("actionSrc = "+actionSrc+" actionTrgts"+actionTrgts);
		if(actionSrc == null && actionTrgts != null ) {
			//System.out.println("actionSrc = "+actionSrc);
			for(IUserIntentAction action : actionTrgts.keySet()){
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(action);
				this.model.add(newChild);
			}
		}
	}

	@Override
	public void setNextTaskLink(IUserIntentTask sourceTask, IUserIntentTask targetTask, Double weight) {

		DefaultMutableTreeNode targetNode= new DefaultMutableTreeNode(targetTask);
		DefaultMutableTreeNode sourceNode= new DefaultMutableTreeNode(sourceTask);

		if(sourceTask == null) this.model.add(targetNode);

		if(sourceTask != null) {
			if (this.retrieveTask(sourceTask.getTaskID()) != null){
				this.retrieveNodeTask(targetTask);
			}
			this.model.add(sourceNode);
		}
	}



	//********************************************************
	//   retrieval methods
	//********************************************************

	public UserIntentTask retrieveTask (String taskID) {

		UserIntentTask userTask = null;
		DefaultMutableTreeNode node = null;
		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof UserIntentAction && node.getUserObject() != null){
				userTask =  (UserIntentTask)node.getUserObject();
				//	System.out.println("node casted to userTask "+ userTask.getTaskID());
				if (taskID.equals(userTask.getTaskID())) {
					System.out.println("FOUND "+taskID);
					return userTask;
				}
			}
		}
		return null;
	}


	public UserIntentAction retrieveAction(String actionID) {

		UserIntentAction userAction = null;
		DefaultMutableTreeNode node = null;
		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof UserIntentAction && node.getUserObject() != null){
				userAction =  (UserIntentAction)node.getUserObject();
				System.out.println("node casted to userAction: "+ userAction.getActionID());
				if (actionID.equals(userAction.getActionID())) {
					System.out.println("FOUND "+actionID);
					return userAction;
				}
			}
		}
		return null;
	}

	/*
	 * rename to retrieve 
	 */

	@Override
	public List<UserIntentAction> retrieveActionsByTypeValue(String par, String value) {

		List<UserIntentAction> results = new ArrayList<UserIntentAction>();
		UserIntentAction userAction = null;

		DefaultMutableTreeNode node = null;
		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof UserIntentAction && node.getUserObject() != null){
				userAction =  (UserIntentAction)node.getUserObject();
				System.out.println("node casted to userAction: "+ userAction.getActionID());
				if (userAction.getparameterName().equals(par) && userAction.getvalue().equals(value)) {
					System.out.println("FOUND "+userAction);
					results.add(userAction);
				}
			}
		}
		return results;
	}


	/*
	 * If userTask has previously be added to the model it should exist as a DefaultMutableTreeNode
	 * This method returns a reference to this node.
	 */
	public DefaultMutableTreeNode retrieveNodeTask(IUserIntentTask userTask){

		DefaultMutableTreeNode node = null;
		IUserIntentTask tempTask = null;

		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof IUserIntentTask && node.getUserObject() != null){
				tempTask =  (IUserIntentTask)node.getUserObject();
				//System.out.println("node casted to userAction: "+ userTask.getTaskID());
				if ((userTask.getTaskID()).equals(tempTask.getTaskID())) {
					System.out.println("node FOUND "+node);
					return node;
				}
			}
		}
		return null;
	}


	public DefaultMutableTreeNode retrieveNodeAction(IUserIntentAction userAction){
		//DefaultMutableTreeNode treeNode = null;

		DefaultMutableTreeNode node = null;
		IUserIntentAction tempAction = null;

		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof IUserIntentAction && node.getUserObject() != null){
				tempAction =  (IUserIntentAction)node.getUserObject();
				//System.out.println("node casted to userAction: "+ userTask.getTaskID());
				if (userAction.equals(tempAction)) {
					System.out.println("node FOUND "+node);
					return node;
				}
			}
		}
		return null;
	}

	/*
	public DefaultMutableTreeNode retrieveNodeAction(IUserIntentAction userAction){
		//DefaultMutableTreeNode treeNode = null;

		DefaultMutableTreeNode node = null;
		IUserIntentTask tempAction = null;

		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof IUserIntentTask && node.getUserObject() != null){
				tempAction =  (IUserIntentTask)node.getUserObject();
				//System.out.println("node casted to userAction: "+ userTask.getTaskID());
				if ((userAction.getTaskID()).equals(tempAction.getTaskID())) {
					System.out.println("node FOUND "+node);
					return node;
				}
			}
		}
		return null;
	}
	 */

	/*
	 * add this method to interface 
	 * @param par
	 * @return
	 */

	public List<UserIntentAction> retrieveActionsByType(String par) {

		List<UserIntentAction> results = new ArrayList<UserIntentAction>();
		UserIntentAction userAction = null;

		DefaultMutableTreeNode node = null;
		Enumeration e =this.retrieveModel().breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject() instanceof UserIntentAction && node.getUserObject() != null){
				userAction =  (UserIntentAction)node.getUserObject();
				System.out.println("node casted to userAction: "+ userAction.getActionID());
				if (userAction.getparameterName().equals(par)) {
					System.out.println("FOUND "+userAction);
					results.add(userAction);
				}
			}
		}
		return results;
	}


	@Override
	public HashMap<UserIntentAction, Double> retrieveNextAction(UserIntentAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Double> retrieveNextTask(IUserIntentTask arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<IUserIntentTask> retrieveTasks(UserIntentAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	//************************************************
	// model object identification methods
	//************************************************

	@Override
	public Map<UserIntentAction, IUserIntentTask> identifyActionTaskInModel(
			String arg0, String arg1, HashMap<String, Serializable> arg2,
			String[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserIntentAction identifyNextAction(
			Map<UserIntentAction, IUserIntentTask> arg0, String arg1,
			Map<String, Serializable> arg2) {
		// TODO Auto-generated method stub
		return null;
	}


	//************************************************
	// helper methods
	//************************************************

	@Override
	public boolean actionBelongsToModel(IUserIntentAction userAction) {

		if( this.retrieveNodeAction(userAction) != null && this.retrieveNodeAction(userAction) instanceof IUserIntentAction) {
			return true; 
		}else return false;
	}


	@Override
	public boolean taskBelongsToModel(IUserIntentTask userTask) {
		if( this.retrieveNodeTask(userTask) != null && this.retrieveNodeTask(userTask) instanceof IUserIntentTask) return true; 
		else return false;
	}


	@Override
	public UserIntentAction retrieveCurrentIntentAction(IIdentity arg0,
			IIdentity arg1, ServiceResourceIdentifier arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}


	public void visualiseModel(){
		new TreeModelVisualizer(this.getModelTree());
	}


	@Override
	public void setNextActionLink(IUserIntentAction arg0,
			IUserIntentAction arg1, Double arg2) {
		// TODO Auto-generated method stub

	}

}