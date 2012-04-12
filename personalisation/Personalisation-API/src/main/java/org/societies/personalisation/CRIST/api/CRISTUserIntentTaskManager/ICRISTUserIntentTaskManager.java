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
package org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRIST.api.model.CRISTUserSituation;
import org.societies.personalisation.CRIST.api.model.CRISTUserTask;
import org.societies.personalisation.CRIST.api.model.CRISTUserTaskModelData;

/**
* @author Zhu WANG
* @version 1.0
* @created 14-Nov-2011 18:15:15
*/
public interface ICRISTUserIntentTaskManager {
	/**
	 * This method will link the related user actions and user situations to the given user task
	 * 
	 * @param userTask			- the task object where the userActions set will be added
	 * @param userActions		- a linked hashmap with the action objects and the transition probabilities
	 * @param userSituations	- a linked hashmap with the situation objects and the transition probabilities 
	 */
	public CRISTUserTask addSituationsAndActionsToTask(CRISTUserTask userTask, HashMap<CRISTUserAction,Double> userActions, HashMap<CRISTUserSituation,Double> userSituations);

	/**
	 * This method will retrieve the user action based on the given action ID
	 * 
	 * @param actionID		- the ID of the given action
	 */
	public CRISTUserAction getAction(String actionID);
	
	/**
	 * This method will retrieve a list of user actions based on the given action type and value
	 * 
	 * @param actionType	- the type of user action
	 * @param actionValue	- the value of user action
	 */
	public ArrayList<CRISTUserAction> getActionsByType(String actionType, String actionValue);

	/**
	 * This method will retrieve the user's current intent action
	 * 
	 * @param requestor		- the ID of the requestor of the current user intent action
	 * @param ownerID		- the ID of the owner of the current user intent action
	 * @param serviceID		- the ID of the related service 
	 */
	public CRISTUserAction getCurrentIntentAction(IIdentity requestor, IIdentity ownerID, ServiceResourceIdentifier serviceID);

	/**
	 * This method will retrieve the user's current action
	 */
	public CRISTUserAction getCurrentUserAction(IIdentity entityID);

	/**
	 * This method will retrieve the user's current situation
	 */
	public CRISTUserSituation getCurrentUserSituation(IIdentity entityID);
	
	/**
	 * This method will retrieve the user's current context clique
	 */
	public ArrayList<String> getCurrentUserContext(IIdentity entityID);

	public void updateUserSituation(IIdentity entityID, CtxAttribute ctxAttribute); 
	/**
	 * This method returns a map of next userActions and the relevant probabilities given the
	 * current action. Next userActions are only one step from the given action.
	 *  
	 * @param userAction	- the given user action
	 */
	public ArrayList<CRISTUserAction> getNextActions(IIdentity entityID, CRISTUserAction userAction, CRISTUserSituation userSituation);
	
	/**
	 
	 */
	public ArrayList<CRISTUserAction> predictUserIntent(IIdentity entityID, CtxAttribute ctxAttribute);
	
	public ArrayList<CRISTUserAction> predictUserIntent(IIdentity entityID, CRISTUserAction userAction);
	
	public CRISTUserAction getCurrentUserIntent(IIdentity entityID, ServiceResourceIdentifier serviceID, String parameterName);

	/**
	 * This method returns a map of next user tasks and the relevant probabilities given the
	 * current task. Next user tasks are only one step from the given task.
	 * 
	 * @param userTask		- the given user task
	 */
	public ArrayList<CRISTUserTask> getNextTasks(CRISTUserTask userTask);
	
	/**
	 * This method returns the user task according to the given task ID
	 * 
	 * @param taskID		- the ID of the given task
	 */
	public CRISTUserTask getTask(String taskID);
	
	/**
	 * This method returns the CRIST User Task Model Data object that contains the user intent model.
	 * 
	 * @return the CRISTUserTaskModelData object
	 */
	public CRISTUserTaskModelData getTaskModelData();

	/**
	 * This method returns the related user tasks according to the given user action and the user situation
	 * 
	 * @param userAction	- the given user action
	 * @param userSituation	- the given user situation
	 */
	public ArrayList<CRISTUserTask> getTasks(CRISTUserAction userAction, CRISTUserSituation userSituation);
	
	/**
	 * This method will identify all the user actions based on historical recordings 
	 */
	public ArrayList<CRISTUserAction> identifyActions();

	/**
	 * This method identifies user tasks accroding to the given action type and value
	 * 
	 * @param actionType	- the type of the given user action
	 * @param actionValue	- the value of the gien user actioin
	 * @param currentContext	- a set of the current context 
	 */
	public HashMap<CRISTUserAction, CRISTUserTask> identifyActionTaskInModel(String actionType, String actionValue, HashMap<String, Serializable> currentContext);

	/**
	 * This method will identify all the user situations based on historical recordings 
	 */
	public ArrayList<CRISTUserSituation> identifySituations();

	/**
	 * This method will identify all the user tasks 
	 */
	public ArrayList<CRISTUserTask> identifyTasks();

	/**
	 * This method resets the task model.
	 */
	public void resetTaskModelData();

	/**
	 * This method will set up the link between the two given actions 
	 * 
	 * @param sourceAction	- the source action
	 * @param targetAction	- the target action
	 * @param weigth		- the probability that the target action becomes the 
	 * next action of the given source action
	 */
	public void setNextActionLink(CRISTUserAction sourceAction, CRISTUserAction targetAction, Double weigth);

	/**
	 * This method will set up the link between the two given situations 
	 * 
	 * @param sourceSituation	- the source situation
	 * @param targetSituation	- the target situation
	 * @param weigth			- the probability that the target situation becomes the 
	 * next situation of the given source situation
	 */
	public void setNextSituationLink(CRISTUserSituation sourceSituation, CRISTUserSituation targetSituation, Double weigth);
	
	/**
	 * This method will set up the link between the two given tasks 
	 * 
	 * @param sourceTask	- the source task
	 * @param targetTask	- the target task
	 * @param weigth		- the probability that the target task becomes the 
	 * next task of the given source task
	 */
	public void setNextTaskLink(CRISTUserTask sourceTask, CRISTUserTask targetTask, Double weigth);
}
