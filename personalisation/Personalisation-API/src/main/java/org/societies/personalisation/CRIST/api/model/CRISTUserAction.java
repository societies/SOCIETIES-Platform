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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * This class is used to define the data structure and methods for modelling and
 * managing user actions. Meanwhile, it is also responsible for establishing the
 * relationship between user actions and user situations.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 20:21:44
 */
public class CRISTUserAction extends Action implements ICRISTUserAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Serializable> actionContext;
	String actionID;
	/**
	 * A list of situations and the corresponding probability related to the
	 * IntentAction
	 */
	HashMap<ICRISTUserSituation, Double> actionSituations;
	/**
	 * The confidence level of the IntentAction
	 */
	@SuppressWarnings("unused")
	private int confidenceLevel;
	public CRISTUserTask m_CRISTUserTask;


	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * Constructor
	 */
	public CRISTUserAction() {
		
	}

	/**
	 * Constructor
	 * 
	 * @param actionID
	 */
	public CRISTUserAction(String actionID) {
		setActionID(actionID);
	}

	/**
	 * Constructor with Action parameter
	 * @param action
	 */
	public CRISTUserAction(IAction action) {
		this.setServiceID(action.getServiceID());
		this.setServiceType(action.getServiceType());
		this.setparameterName(action.getparameterName());
		this.setvalue(action.getvalue());
		this.actionID = getActionID();
	}
	
	
	/**
	 * This method will return the related context information of the current
	 * action
	 * 
	 * @return
	 */
	public Map<String, Serializable> getActionContext() {
		return this.actionContext;
	}

	/**
	 * This method will return the ID of the current action
	 * this ID contains all information about this action: serviceId, servicetype, paraname, and value. seperated by :
	 * @return
	 */
	public String getActionID() {
		if (actionID == null)
		{
			actionID = this.getServiceID().getIdentifier() + ": " + this.getServiceType()+ ": " + this.getparameterName() + ": " + this.getvalue();
		}
		return this.actionID;
	}

	/**
	 * This method will return the related situations of the current action
	 * 
	 * @return
	 */
	public HashMap<ICRISTUserSituation, Double> getActionSituations() {
		return this.actionSituations;
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
	public void setActionContext(HashMap<String, Serializable> context) {
		this.actionContext = context;
	}

	/**
	 * This method will set the current action's ID as actionID
	 * 
	 * @param actionID
	 */
	public void setActionID(String actionID) {
		this.actionID = actionID;
		if (actionID == null)
			return;
		String[] fields = actionID.split(": ");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI(fields[0]));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.setServiceID(serviceId);
		this.setServiceType(fields[1]);
		this.setparameterName(fields[2]);
		this.setvalue(fields[3]);
	}

	/**
	 * This method will assign a list of situations to the current action
	 * 
	 * @param actionSituations
	 */
	public void setActionSituations1(
			HashMap<ICRISTUserSituation, Double> actionSituations) {
		this.actionSituations = actionSituations;
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
		return super.toString();
	}

	/**
	 * @return the name of the action (i.e. volume)
	 */
	public String getparameterName() {
		return super.getparameterName();
	}

	/**
	 * @return any other names this action might also be called
	 */
	public ArrayList<String> getparameterNames() {
		return super.getparameterNames();
	}

	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public ServiceResourceIdentifier getServiceID() {
		return super.getServiceID();
	}

	/**
	 * @return the type of service this action can be applied to
	 */
	public String getServiceType() {
		return super.getServiceType();
	}

	/**
	 * @return a list of alternative types of service this action can be applied
	 *         to.
	 */
	public List<String> getServiceTypes() {
		return super.getServiceTypes();
	}

	/**
	 * @return the value of this action (i.e. if the action is volume then the
	 *         value would be an int from 0 to 100
	 */
	public String getvalue() {
		return super.getvalue();
	}

	/**
	 * 
	 * @param id
	 *            the identifier of the service this action is applied to
	 */
	public void setServiceID(ServiceResourceIdentifier id) {
		super.setServiceID(id);

	}

	/**
	 * 
	 * @param type
	 *            the type of service this action is applied to
	 */
	public void setServiceType(String type) {
		super.setServiceType(type);

	}

	/**
	 * 
	 * @param types
	 *            a list of alternative types this action can be applied to
	 */
	public void setServiceTypes(List<String> types) {
		super.setServiceTypes(types);

	}



	@Override
	public void setActionSituations(
			HashMap<ICRISTUserSituation, Double> actionSituations) {
		// TODO Auto-generated method stub
		
	}

}
