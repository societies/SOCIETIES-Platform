/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
import java.util.List;
import java.util.Map;

/**
 * This interface models user tasks that are part of the user intent model. 
 * 
 * @author <a href="mailto:nikoskal@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @version 0.0.1
 */
public interface IUserIntentTask {
	
	/**
     * Returns a string with the UserTask ID. The task ID should be set upon creation of task.
     * A task is created by the TaskModelManager.
     * @return string id
     */
    public String getTaskID();

    /**
     * Returns a map with the userActions contained in the UserTask and the respective transition 
     * probability from previous action to next action. 
     * e.g. action1, 0
     *      action2, 33
     *      action3, 12
     *    
     * @return map
     */
  //  public Map<IUserIntentAction,Double > getActions();

    /**
     * Returns a List of UserIntentActions contained in the UserTask 
     *    
     * @return List
     */
    public List<IUserIntentAction> getActions();
    
    /**
     * Adds a map of userActions to the UserTask. The map contains the action object and the transition
     * probability to the next action. 
     * 
     * @param userActions
     */
  //  public void addActions(LinkedHashMap<IUserIntentAction,Double> userActions);

    /**
     * Adds a list of userActions to the UserTask.
     * 
     * @param userActions
     */
    public void setActions(List<IUserIntentAction> userActions);
 
    /**
     * Returns a map with context types and values associated with this task.
     * In the map the key is the type of context model object and the object the 
     * respective value.
     * 
     * @return map with context types and values
     */
    public Map<String, Serializable> getTaskContext() ;


    /**
     * Associates a map of context types and values to the task.
     * (Which context to use? Context data of the actions included in Task)?
     * 
     * @param taskContext
     */
    public void setTaskContext(Map<String, Serializable> taskContext);


    /**
     * Returns a string containing the task id and a string representation of the contained
     * action ids and transition probabilities.
     *  
     * @return string
     */
    public String toString ();

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
     * Returns the transition probability from previous UserTask to this UserTask.
     * @return
     */
    public Double [][] getMatrix();
    
    /**
     * Returns the transition probability from previous UserTask to this UserTask.
     * @return
     */
    public void setMatrix(Double [][] matrix);
    
    /**
     * Returns the transition probability from previous UserTask to this UserTask.
     * @return
     */
  //  public double getTransProb(); 
}
