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
package org.societies.personalisation.CRIST.api.CRISTCommunityIntentTaskManager;

import java.util.ArrayList;
import java.util.HashMap;

import org.societies.personalisation.CRIST.api.model.CRISTCommunityTaskModelData;
import org.societies.personalisation.CRIST.api.model.CRISTCommunityAction;
import org.societies.personalisation.CRIST.api.model.CRISTCommunitySituation;
import org.societies.personalisation.CRIST.api.model.CRISTCommunityTask;

/**
 * @author Zhu WANG
 * @version 1.0
 */
public interface ICRISTCommunityIntentTaskManager {

	/**
	 * This method will retrieve the community action based on the given action ID
	 * 
	 * @param actionID		- the ID of the given community action
	 */
	public CRISTCommunityAction getCommunityAction(String actionID);
	
	/**
	 * This method will retrieve a list of community actions based on the given action type and value
	 * 
	 * @param actionType	- the type of user action
	 * @param actionValue	- the value of user action
	 */
	public ArrayList<CRISTCommunityAction> getCommunityActionsByType(String actionType, String actionValue);
	
	/**
	 * This method will retrieve the community situation based on the given situation ID
	 * 
	 * @param situationID		- the ID of the given community situation
	 */
	public CRISTCommunitySituation getCommunitySituation(String situationID);
	
	/**
	 * This method returns the community task according to the given task ID
	 * 
	 * @param taskID		- the ID of the given community task
	 */
	public CRISTCommunityTask getCommunityTask(String taskID);
	
	/**
	 * This method returns a map of next communityActions and the relevant probabilities given the
	 * current community action. Next communityActions are only one step from the given action.
	 *  
	 * @param communityAction	- the given community action
	 */
	public HashMap<CRISTCommunityAction, Double> getNextCommunityActions(CRISTCommunityAction communityAction);
	
	/**
	 * This method returns a map of next community tasks and the relevant probabilities given the
	 * current community task. Next community tasks are only one step from the given task.
	 * 
	 * @param communityTask		- the given community task
	 */
	public HashMap<CRISTCommunityTask, Double> getNextCommunityTasks(CRISTCommunityTask communityTask);
	
	/**
	 * This method will identify all the community actions based on historical recordings 
	 */
	public ArrayList<CRISTCommunityAction> identifyCommunityActions();
	
	/**
	 * This method will identify all the community situations based on historical recordings 
	 */
	public ArrayList<CRISTCommunitySituation> identifyCommunitySituations();
	
	/**
	 * This method will identify all the community tasks 
	 */
	public ArrayList<CRISTCommunityTask> identifyCommunityTasks();
	
	/**
	 * This method resets the task model.
	 */
	public void resetTaskModelData();
	
	/**
	 * This method sets the task model according to the given model.
	 * 
	 * @param taskModelData		- the given task model
	 */
	public void setTaskModelData(CRISTCommunityTaskModelData taskModelData);
}
