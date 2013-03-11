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
 * managing community situations. Meanwhile, it is also responsible for
 * establishing the relationship between community situation and community
 * actions as well as the relationship between community situations and
 * community tasks.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 21:08:55
 */
public class CRISTCommunitySituation extends Action implements ICRISTCommunitySituation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<ICRISTCommunityAction, Double> communitySituationActions;
	Map<String, Serializable> communitySituationContext;
	String communitySituationID;
	HashMap<ICRISTCommunityTask, Double> communitySituationTasks;
	public CRISTCommunityTask m_CRISTCommunityTask;

	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 */
	public CRISTCommunitySituation() {

	}

	/**
	 * Constructor
	 * 
	 * @param situationID
	 */
	public CRISTCommunitySituation(String situationID) {

	}

	/**
	 * This method will link the given ICRISTCommunityAction list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityActions
	 */
	public void addActions(
			HashMap<ICRISTCommunityAction, Double> communityActions) {

	}

	/**
	 * This method will link the given ICRISTCommunityTask list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityTasks
	 */
	public void addTasks(
			HashMap<ICRISTCommunityTask, Double> communityTasks) {

	}

	/**
	 * This method will return the related actions of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityAction, Double> getSituatioinActions() {
		return this.communitySituationActions;
	}

	/**
	 * This method will return the related context information of the current
	 * situation
	 * 
	 * @return
	 */
	public Map<String, Serializable> getSituationContext() {
		return this.communitySituationContext;
	}

	/**
	 * This method will return the ID of the current situation
	 * 
	 * @return
	 */
	public String getSituationID() {
		return this.communitySituationID;
	}

	/**
	 * This method will return the related tasks of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityTask, Double> getSituationTasks() {
		return this.communitySituationTasks;
	}

	/**
	 * This method will assign the current situation context with the given
	 * situationContext
	 * 
	 * @param situationContext
	 */
	public void setSituationContext(Map<String, Serializable> situationContext) {
		this.communitySituationContext = situationContext;
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

	@Override
	public boolean isContextDependent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isImplementable() {
		// TODO Auto-generated method stub
		return super.isImplementable();
	}

	@Override
	public boolean isProactive() {
		// TODO Auto-generated method stub
		return false;
	}

}