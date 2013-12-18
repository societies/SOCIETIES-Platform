/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druï¿½be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAï¿½ï¿½O, SA (PTIN), IBM Corp., 
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
package org.societies.personalisation.CAUI.api.model;


import java.io.Serializable;
import java.util.HashMap;

import org.societies.api.internal.personalisation.model.IOutcome;

/**
 * This interface models user actions that are part of the user intent model.
 * 
 * @author <a href="mailto:nikoskal@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @since 0.0.1
 */
public interface IUserIntentAction  extends IOutcome, Serializable{


	/**
	 * Returns the id of the UserAction. This id is created and set upon creation 
	 * of the user action object. TaskModelManager creates the UserAction object.
	 * 
	 * @return string
	 */
	public String getActionID();


	/**
	 * Returns a map with context types and values associated with this UserAction.
	 * In the map the key is the type of context model object and the object the 
	 * respective value.
	 * 
	 * @return map with context types and values
	 */
	public HashMap<String, Serializable> getActionContext();

	/**
	 * Associates a map of context types and values to the UserAction.
	 *
	 * @param context
	 */
	public void setActionContext(HashMap<String, Serializable> context);

	/**
	 * Returns a string representation of the the action. 
	 *
	 * 
	 * @return string
	 */
	public String toString();

	/**
	 * Sets the confidence level
	 * 
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel);

	/**
	 * Retrieves the confidence level
	 * 
	 */
	public int getConfidenceLevel();

	/**
	 * Returns the transition probability from previous UserAction to this UserAction.
	 * @return
	 */
	//  public double getTransProb(); 

	/**
	 * This method returns the durations in milliseconds that an action is expected to last.
	 * @return
	 */
	public long getDuration(); 

	/**
	 * This method sets the durations in milliseconds that an action is expected to last.
	 * @param duration
	 */
	public void setDuration(long duration); 

	/**
	 * This method sets the ID of the task that this action belongs to. 
	 * @param taskID
	 */
	public void setTaskID(String taskID);

	/**
	 * This method returns the ID of the task that this action belongs to. 
	 * @return taskID
	 */
	public String getTaskID();

	/**
	 * This flag indicates if an Action is referring to a community of users
	 * 
	 * @return boolean
	 */
	public Boolean isCommunity();

	/**
	 * Set the  flag thath indicates if an Action is referring to a community of users
	 * @param community
	 */
	public void setCommunity(Boolean community);

	public void setImplementable(Boolean implementable);

	public void setProactive(Boolean proactive);
}