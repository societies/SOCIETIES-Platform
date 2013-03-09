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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * This class is used to define the data structure and methods for modelling and
 * managing user tasks. Meanwhile, it is also responsible for establishing the
 * relationship between user tasks and user actions as well as the relationship
 * between user tasks and user actions.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 18:14:59
 */
public class CRISTUserTask extends Action implements ICRISTUserTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Serializable> taskContext;
	String taskID;
	HashMap<ICRISTUserSituation, Double> taskSituations;
	HashMap<ICRISTUserAction, Double> userActions;

	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 * 
	 * @param taskID
	 * @param userActions
	 * @param taskSituations
	 */
	public CRISTUserTask(String taskID,
			HashMap<ICRISTUserAction, Double> userActions,
			HashMap<ICRISTUserSituation, Double> taskSituations) {

	}

	/**
	 * Constructor
	 * 
	 * @param taskID
	 */
	public CRISTUserTask(String taskID) {

	}

	/**
	 * Constructor
	 * 
	 * @param taskID
	 */
	public CRISTUserTask() {

	}

	/**
	 * This method will link the given ICRISTUserAction list to the current task
	 * along with the corresponding transition probabilities
	 * 
	 * @param userActions
	 */
	public void addActions(HashMap<ICRISTUserAction, Double> userActions) {

	}

	/**
	 * This method will link the given ICRISTUserSituation list to the current
	 * task along with the corresponding transition probabilities
	 * 
	 * @param taskSituations
	 */
	public void addSituations(
			HashMap<ICRISTUserSituation, Double> taskSituations) {

	}

	/**
	 * This method will return the related actions of the current task
	 * 
	 * @return
	 */
	public HashMap<ICRISTUserAction, Double> getActions() {
		HashMap<ICRISTUserAction, Double> actionMap = new HashMap<ICRISTUserAction, Double>();
		// TODO

		return actionMap;
	}

	/**
	 * This method will return the related context information of the current
	 * task
	 * 
	 * @return
	 */
	public Map<String, Serializable> getTaskContext() {
		Map<String, Serializable> contextMap = null;
		// TODO

		return contextMap;
	}

	/**
	 * This method will return the ID of the current task
	 * 
	 * @return
	 */
	public String getTaskID() {

		return this.taskID;
	}

	/**
	 * This method will return the related situations of the current task
	 * 
	 * @return
	 */
	public HashMap<ICRISTUserSituation, Double> getTaskSituations() {
		HashMap<ICRISTUserSituation, Double> situationMap = new HashMap<ICRISTUserSituation, Double>();
		// TODO

		return situationMap;
	}

	/**
	 * This method will set the related context information of the current task
	 * with the given taskContext
	 * 
	 * @param taskContext
	 */
	public void setTaskContext(Map<String, Serializable> taskContext) {
		this.taskContext = taskContext;

	}

	/**
	 * This method will set the ID of the current task with the given taskID
	 * 
	 * @param taskID
	 */
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString() {
		return this.toString();
	}

	@Override
	public int getConfidenceLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getvalue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getparameterName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getparameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResourceIdentifier getServiceID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getServiceTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceID(ServiceResourceIdentifier id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServiceType(String type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setServiceTypes(List<String> types) {
		// TODO Auto-generated method stub

	}

}