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
package org.societies.personalisation.CAUI.api.CAUITaskManager;


import java.io.Serializable;
import java.util.HashMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;

/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

public interface ICAUITaskManager {

	public boolean actionBelongsToModel(IUserIntentAction userAction);
	
	public boolean taskBelongsToModel(IUserIntentTask userTask);
	
	/**
	 * 
	 * @return
	 */
	public UserIntentModelData retrieveModel();
	
	/**
	 * Set an active User Intent model  
	 * 
	 * @param UserIntentModelData
	 */
	public void updateModel(UserIntentModelData model);

	/**
	 * Creates an UserAction object based on the specified string ID.
	 * 
	 * @param par
	 * @param val
	 * @param transProb
	 * @return the UserAction object
	 */
	public IUserIntentAction createAction(ServiceResourceIdentifier serviceID, String serviceType, String par, String val);

	/**
	 * Creates a task and assigns a task id to it.
	 * @return the UserTask
	 * 
	 * @param taskID
	 * @param trans Prob
	 */
	public IUserIntentTask createTask(String taskName,LinkedHashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> actions);
	
	/**
	 * Creates a task and assigns a task id to it.
	 * @return the UserTask
	 * 
	 * @param taskID
	 * @param trans Prob
	 */
	public UserIntentModelData createModel();
	
	/**
	 * Returns the UserAction object of the specified id.
	 * @return the action object
	 * 
	 * @param actionID
	 */
	public IUserIntentAction retrieveAction(String actionID);

	/**
	 * Returns a list of Actions from the model of the same type and value with these
	 * defined in the parameters.
	 * @return list of actions
	 * 
	 * @param par    the parameter name
	 * @param val    the value
	 */
	public List<IUserIntentAction> retrieveActionsByTypeValue(String par, String val);
	
	/**
	 * Returns a list of Actions from the model of the same type with this
	 * defined in the parameter.
	 * @return list of actions
	 * 
	 * @param par    the parameter name
	 */
	public List<IUserIntentAction>  retrieveActionsByContext(Map<String,Serializable> situationConext);
	
	public List<IUserIntentAction> retrieveActionsByType(String par);
	
	public List<IUserIntentAction> retrieveActionsByServiceTypeValue(String serviceId,String actionType, String actionValue);
	
	public List<IUserIntentAction> retrieveActionsByServiceType (String serviceId,String actionType) ;
	
	/**
	 * Allows any service to request a context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */	
	public UserIntentAction retrieveCurrentIntentAction(IIdentity requestor, IIdentity ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * Returns the UserTask specified by the taskID
	 * @return the user task object
	 * 
	 * @param taskID
	 */
	public IUserIntentTask retrieveTask(String taskID);

	/**
	 * Identifies a IUserAction and the corresponding IUserTask that has the same
	 * parameter name, value and context in User Intent model.
	 * @return map
	 * 
	 * @param par
	 * @param val
	 * @param currentContext
	 * @param lastAction
	 */
	public Map<IUserIntentAction, IUserIntentTask> identifyActionTaskInModel(String par, String val, HashMap<String, Serializable> currentContext, String[] lastAction);


	public void setActionLink(IUserIntentAction sourceAction ,IUserIntentAction targetAction, Double transProb);

	public Map<IUserIntentAction, Double> retrieveNextActions(IUserIntentAction currentAction);
	
	public HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> getCAUIActiveModel();
}