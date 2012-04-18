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
package org.societies.personalisation.CACI.api.CACITaskManager;

import java.util.List;

import org.societies.personalisation.CAUI.api.model.CommunityIntentAction;
import org.societies.personalisation.CAUI.api.model.CommunityIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentTask;


/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

public interface ICACITaskManager {

	
	/**
	 * This method allows the creation of a community Action
	 * @param type
	 */
	public void createCommAction(String type);

	/**
	 * This method allows the creation of a community Task
	 * @param type
	 */
	public void createCommTask(String type);

	/**
	 * This method returns the level of commonality for  a community action.
	 * 
	 * @param actionID
	 */
	public double getActionCommLevel(String actionID);

	/**
	 * This method returns a list of Community Action objects based on the defined type.
	 * 
	 * @param type
	 */
	public List <CommunityIntentAction> getCommActByType(String type);

	/**
	 * This method returns a Community Action object based on the action ID.
	 * 
	 * @param actionID
	 */
	public CommunityIntentAction getCommAction(String actionID);

	/**
	 * This method returns a Community Task object based on the action ID.
	 * 
	 * @param taskID
	 */
	public CommunityIntentTask getCommTask(String taskID);

	/**
	 * This method returns the next Community Action object as it is defined in CAUI Model.
	 * 
	 * @param currentComAction
	 */
	public UserIntentAction getNextCommAction(CommunityIntentAction currentComAction);

	/**
	 * This method returns the next Community Task object as it is defined in CAUI Model.
	 * 
	 * @param currentComTask
	 */
	public UserIntentTask getNextCommTask(CommunityIntentTask currentComTask);

	/**
	 * This method returns the level of commonality for  a community task.
	 * 
	 * @param actionID
	 */
	public double getTaskCommLevel(String actionID);

	/**
	 * This methods allows to add User Actions to User Task.
	 * 
	 * @param commTaskID
	 * @param listCommActions
	 */
	public void populateCommTask(String commTaskID, List<CommunityIntentAction> listCommActions);

	/**
	 * resets the active task model.
	 */
	public void resetTaskModel();

	/**
	 * Retrieves a model based on an id.
	 * 
	 * @param modelId
	 */
	public UserIntentModelData retrieveModel(String modelId);

	/**
	 * This method sets the level of commonality for  a community action.
	 * 
	 * @param actionID
	 * @param commonalityLevel
	 */
	public void setActionCommLevel(String actionID, Double commonalityLevel);

	/**
	 *  This method sets the level of commonality for a community task.
	 * 
	 * @param actionID
	 * @param commonalityLevel
	 */
	public void setTaskCommLevel(String actionID, Double commonalityLevel);

}
