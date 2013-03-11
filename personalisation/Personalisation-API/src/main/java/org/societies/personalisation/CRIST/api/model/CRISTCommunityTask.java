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
 * managing community tasks. Meanwhile, it is also responsible for establishing
 * the relationship between community tasks and community actions as well as the
 * relationship between community tasks and community actions.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 20:56:52
 */
public class CRISTCommunityTask extends Action implements ICRISTCommunityTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<ICRISTCommunityAction, Double> communityActions;
	Map<String, Serializable> communityTaskContext;
	String communityTaskID;
	HashMap<ICRISTCommunitySituation, Double> communityTaskSituations;

	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 */
	public CRISTCommunityTask() {

	}

	/**
	 * Constructor
	 * 
	 * @param taskID
	 */
	public CRISTCommunityTask(String taskID) {

	}

	/**
	 * Constructor
	 * 
	 * @param taskID
	 * @param communityActions
	 * @param taskSituations
	 */
	public CRISTCommunityTask(String taskID,
			HashMap<ICRISTCommunityAction, Double> communityActions,
			HashMap<ICRISTCommunitySituation, Double> taskSituations) {

	}

	/**
	 * This method will link the given ICRISTCommunityAction list to the current
	 * community task along with the corresponding transition probabilities
	 * 
	 * @param communityActions
	 */
	public void addActions(
			HashMap<ICRISTCommunityAction, Double> communityActions) {

	}

	/**
	 * This method will link the given ICRISTCommunitySituation list to the
	 * current community task along with the corresponding transition
	 * probabilities
	 * 
	 * @param taskSituations
	 */
	public void addSituations(
			HashMap<ICRISTCommunitySituation, Double> taskSituations) {

	}

	/**
	 * This method will return the related actions of the current community task
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityAction, Double> getActions() {
		return this.communityActions;
	}

	/**
	 * This method will return the related context information of the current
	 * community task
	 * 
	 * @return
	 */
	public Map<String, Serializable> getTaskContext() {
		return this.communityTaskContext;
	}

	/**
	 * This method will return the ID of the current community task
	 * 
	 * @return
	 */
	public String getTaskID() {
		return this.communityTaskID;
	}

	/**
	 * This method will return the related situations of the current community
	 * task
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunitySituation, Double> getTaskSituations() {
		return this.communityTaskSituations;
	}

	/**
	 * This method will set the related context information of the current
	 * community task with the given taskContext
	 * 
	 * @param taskContext
	 */
	public void setTaskContext(Map<String, Serializable> taskContext) {
		this.communityTaskContext = taskContext;
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