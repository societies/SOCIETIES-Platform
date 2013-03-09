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
 * managing user situations. Meanwhile, it is also responsible for establishing
 * the relationship between user situation and user actions as well as the
 * relationship between user situations and user tasks.
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 18:40:57
 */
public class CRISTUserSituation extends Action implements ICRISTUserSituation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<ICRISTUserAction,Double> situationActions;
	Map<String, Serializable> situationContext;
	String situationID;
	HashMap<ICRISTUserTask,Double> situationTasks;
	public CRISTUserTask m_CRISTUserTask;



	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 */
	public CRISTUserSituation(){

	}

	/**
	 * Constructor
	 * 
	 * @param situationID
	 */
	public CRISTUserSituation(String situationID){

	}

	/**
	 * This method will link the given ICRISTUserAction list to the current situation
	 * along with the corresponding transition probabilities
	 * 
	 * @param userActions
	 */
	public void addActions(HashMap<ICRISTUserAction,Double> userActions){

	}

	/**
	 * This method will link the given ICRISTUserTask list to the current situation
	 * along with the corresponding transition probabilities
	 * 
	 * @param userTasks
	 */
	public void addTasks(HashMap<ICRISTUserTask,Double> userTasks){

	}

	/**
	 * This method will return the related actions of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTUserAction,Double> getSituatioinActions(){
		HashMap<ICRISTUserAction, Double> actionMap = new HashMap<ICRISTUserAction, Double>();
		// TODO

		return actionMap;
	}

	/**
	 * This method will return the related context information of the current
	 * situation
	 * 
	 * @return
	 */
	public Map<String, Serializable> getSituationContext(){
		return this.situationContext;
	}

	/**
	 * This method will return the ID of the current situation
	 * 
	 * @return
	 */
	public String getSituationID(){
		return this.situationID;
	}

	/**
	 * This method will return the related tasks of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTUserTask,Double> getSituationTasks(){
		return this.situationTasks;
	}

	/**
	 * This method will assign the current situation context with the given situationContext  
	 * 
	 * @param situationContext
	 */
	public void setSituationContext(Map<String, Serializable> situationContext){
		this.situationContext = situationContext;
	}

	/**
	 * This method will assign the current situation ID with the given situationID  
	 * 
	 * @param situationID
	 */
	public void setSituationID(String situationID){
		this.situationID = situationID;
	}

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString(){
		return this.situationID;
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