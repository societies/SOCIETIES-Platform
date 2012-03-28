/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.personalisation.CRIST.api.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * This class is responsible for defining the data structure of individual user
 * intent model, which is mainly based on three basic classes: CRISTUserAction,
 * CRISTUserSituation, CRISTUserTask.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 14-Nov-2011 18:15:15
 */
public class CRISTUserTaskModelData {

	/**
	 * Each CRISTUserAction object is linked with a list of other
	 * CRISTUserActions and the transition probability
	 */
	public HashMap<CRISTUserAction, HashMap<CRISTUserAction, Double>> actionList;
	/**
	 * Each CRISTUserSituation object is linked with a list of other
	 * CRISTUserSituations and the transition probability
	 */
	public HashMap<CRISTUserSituation, HashMap<CRISTUserSituation, Double>> situationList;
	/**
	 * Each CRISTUserTask object is linked with a list of other CRISTUserTasks
	 * and the transition probability
	 */
	public HashMap<CRISTUserTask, HashMap<CRISTUserTask, Double>> taskList;

	public LinkedHashMap<String, Integer> intentModel = new LinkedHashMap<String, Integer>();
	/**
	 * Constructor
	 */
	public CRISTUserTaskModelData() {

	}

	/**
	 * Constructor
	 * 
	 * @param taskList
	 * @param actionList
	 * @param situationList
	 */
	public CRISTUserTaskModelData(
			HashMap<ICRISTUserTask, HashMap<ICRISTUserTask, Double>> taskList,
			HashMap<ICRISTUserAction, HashMap<ICRISTUserAction, Double>> actionList,
			HashMap<ICRISTUserSituation, HashMap<ICRISTUserSituation, Double>> situationList) {

	}

	/**
	 * This method connects the given ICRISTUserTask with a list of
	 * ICRISTUserActions and ICRISTUserSituations
	 * 
	 * @param userTask
	 *            the task object where the userActions set will be added
	 * @param userActions
	 *            a linked hashmap with the action objects and the the
	 *            transition probabilities
	 * @param userSituations
	 */
	public ICRISTUserTask addSituationsAndActionsToTask(
			ICRISTUserTask userTask,
			HashMap<ICRISTUserAction, Double> userActions,
			HashMap<ICRISTUserSituation, Double> userSituations) {
		// TODO

		return userTask;
	}

	/**
	 * This method will retrieve all the ICIRSTUserActions in the current model,
	 * and each ICRISTUserAction is linked with a list of other
	 * ICRISTUserActions along the with transition probabilities
	 * 
	 * @return actionMap
	 */
	public HashMap<ICRISTUserAction, HashMap<ICRISTUserAction, Double>> getActionList() {
		HashMap<ICRISTUserAction, HashMap<ICRISTUserAction, Double>> actionMap = new HashMap<ICRISTUserAction, HashMap<ICRISTUserAction, Double>>();
		// TODO

		return actionMap;
	}

	/**
	 * This method will retrieve the user's current intent action
	 * 
	 * @param requestor
	 *            - the ID of the requestor of the current user intent action
	 * @param ownerID
	 *            - the ID of the owner of the current user intent action
	 * @param serviceID
	 *            - the ID of the related service
	 * 
	 * @return currentUserAction
	 */
	public CRISTUserAction getCurrentIntentAction(IIdentity requestor,
			IIdentity ownerID, ServiceResourceIdentifier serviceID) {
		CRISTUserAction currentUserAction = new CRISTUserAction();
		// TODO

		return currentUserAction;
	}

	/**
	 * This method will retrieve the user's current situation
	 * 
	 * @param requestor
	 *            - the ID of the requestor of the current user situation
	 * @param ownerID
	 *            - the ID of the owner of the current user situation
	 * @param serviceID
	 *            - the ID of the related service
	 * 
	 * @return currentUserSituation
	 */
	public CRISTUserSituation getCurrentUserSituation(
			IIdentity requestor, IIdentity ownerID,
			ServiceResourceIdentifier serviceID) {
		CRISTUserSituation currentUserSituation = new CRISTUserSituation();
		// TODO

		return currentUserSituation;
	}

	/**
	 * This method returns a map of next userActions and the relevant
	 * probabilities given the current action. Next userActions are only one
	 * step from the given action.
	 * 
	 * @return nextActionMap
	 * 
	 * @param userAction
	 */
	public HashMap<ICRISTUserAction, Double> getNextAction(
			ICRISTUserAction userAction) {
		HashMap<ICRISTUserAction, Double> nextActionMap = new HashMap<ICRISTUserAction, Double>();
		// TODO

		return nextActionMap;
	}

	/**
	 * This method will retrieve all the ICIRSTUserSituations in the current
	 * model, and each ICRISTUserSituatioins is linked with a list of other
	 * ICRISTUserSituations along the with transition probabilities
	 * 
	 * @return situationMap
	 */
	public HashMap<ICRISTUserSituation, HashMap<ICRISTUserSituation, Double>> getSituationList() {
		HashMap<ICRISTUserSituation, HashMap<ICRISTUserSituation, Double>> situationMap = new HashMap<ICRISTUserSituation, HashMap<ICRISTUserSituation, Double>>();
		// TODO

		return situationMap;
	}

	/**
	 * This method will retrieve all the ICIRSTUserTasks in the current
	 * model, and each ICRISTUserTasks is linked with a list of other
	 * ICRISTUserTasks along the with transition probabilities
	 * 
	 * @return taskMap
	 */
	public HashMap<ICRISTUserTask, HashMap<ICRISTUserTask, Double>> getTaskList() {
		HashMap<ICRISTUserTask, HashMap<ICRISTUserTask, Double>> taskMap = new HashMap<ICRISTUserTask, HashMap<ICRISTUserTask, Double>>();
		// TODO
		
		return taskMap;
	}

	/**
	 * This method returns the TaskModelData which contains the current user intent
	 * model.
	 * 
	 * @return the TaskModelData
	 */
	public CRISTUserTaskModelData getTaskModelData() {
		CRISTUserTaskModelData taskModel = new CRISTUserTaskModelData();
		
		return taskModel;
	}

	/**
	 * This method resets the current task model.
	 */
	public void resetTaskModelData() {

	}

	/**
	 * This method sets the actionList of the current task model with the given actionList
	 * 
	 * @param actionList
	 */
	public void setActionList(
			HashMap<ICRISTUserAction, HashMap<ICRISTUserAction, Double>> actionList) {

	}

	/**
	 * This method sets the situationList of the current task model with the given situationList
	 * 
	 * @param situationList
	 */
	public void setSituationList(
			HashMap<ICRISTUserSituation, HashMap<CRISTUserSituation, Double>> situationList) {

	}

	/**
	 * This method sets the taskList of the current task model with the given taskList
	 * 
	 * @param taskList
	 */
	public void setTaskList(
			HashMap<ICRISTUserTask, HashMap<ICRISTUserTask, Double>> taskList) {

	}

}