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
import java.util.Map;

import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * This class is used to define the data structure and methods for modelling and
 * managing community actions. Meanwhile, it is also responsible for
 * establishing the relationship between community actions and community
 * situations.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 21:18:50
 */
public class CRISTCommunityAction extends Action implements
		ICRISTCommunityAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Serializable> communityActionContext;
	String communityActionID;
	/**
	 * A list of situations and the corresponding probability related to the
	 * CommunityIntentAction
	 */
	HashMap<ICRISTCommunitySituation, Double> communityActionSituations;
	/**
	 * The confidence level of the CommunityIntentAction
	 */
	private int confidenceLevel;
	public CRISTCommunityTask m_CRISTCommunityTask;

	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * Constructor
	 */
	public CRISTCommunityAction() {

	}

	/**
	 * Constructor
	 * 
	 * @param actionID
	 */
	public CRISTCommunityAction(String actionID) {

	}

	/**
	 * This method will link the given situations with the current community
	 * action
	 * 
	 * @param actionSituations
	 */
	public void addActionSituations(
			HashMap<ICRISTCommunitySituation, Double> actionSituations) {

	}

	/**
	 * This method will return the related context information of the current
	 * action
	 * 
	 * @return
	 */
	public Map<String, Serializable> getActionContext() {
		return this.communityActionContext;
	}

	/**
	 * This method will return the ID of the current action
	 * 
	 * @return
	 */
	public String getActionID() {
		return this.communityActionID;
	}

	/**
	 * This method will return the related situations of the current action
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunitySituation, Double> getActionSituations() {
		return this.communityActionSituations;
	}

	/**
	 * This method will return the confidence level of the current action
	 */
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}

	/**
	 * This method will set the related context information of the current
	 * action with the given context
	 * 
	 * @param context
	 */
	public void setActionContext(Map<String, Serializable> context) {
		this.communityActionContext = context;
	}

	/**
	 * This method will assign the given confidenceLevel as the confidence level
	 * of the current action
	 * 
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * This method will return the contents of this action in a String mode
	 */
	public String toString() {
		return this.toString();
	}

	/**
	 * @return the name of the action (i.e. volume)
	 */
	public String getparameterName() {
		return "";
	}

	/**
	 * @return any other names this action might also be called
	 */
	public ArrayList<String> getparameterNames() {
		return null;
	}

	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public ServiceResourceIdentifier getServiceID() {
		return null;
	}

	/**
	 * @return the type of service this action can be applied to
	 */
	public String getServiceType() {
		return "";
	}

	/**
	 * @return a list of alternative types of service this action can be applied
	 *         to.
	 */
	public ArrayList<String> getServiceTypes() {
		return null;
	}

	/**
	 * @return the value of this action (i.e. if the action is volume then the
	 *         value would be an int from 0 to 100
	 */
	public String getvalue() {
		return "";
	}

	/**
	 * 
	 * @param id
	 *            the identifier of the service this action is applied to
	 */
	public void setServiceID(ServiceResourceIdentifier id) {

	}

	/**
	 * 
	 * @param type
	 *            the type of service this action is applied to
	 */
	public void setServiceType(String type) {

	}

	/**
	 * 
	 * @param types
	 *            a list of alternative types this action can be applied to
	 */
	public void setServiceTypes(ArrayList<String> types) {

	}
}