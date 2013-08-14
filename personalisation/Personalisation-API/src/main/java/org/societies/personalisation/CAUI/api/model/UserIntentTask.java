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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserIntentTask implements IUserIntentTask,Serializable{

	private static final long serialVersionUID = 1L;
	
	LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>> actions = new  LinkedHashMap<IUserIntentAction,HashMap<IUserIntentAction,Double>>();

	String taskID;

	int confidenceLevel;

	//private double transProb;

	//A map that contains the context type and the context values assigned to this UserTask;
	Map<String, Serializable> taskContext;

	
	UserIntentTask(){
	}


	public UserIntentTask (String taskName, Long uniqueNumber, LinkedHashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> actions ){
		this.taskID = taskName+"/"+uniqueNumber;
		this.confidenceLevel = 51;
		setActions(actions);
	}


	@Override
	public String getTaskID() {
		return taskID;
	}


	@Override
	public Map<String, Serializable> getTaskContext() {
		return taskContext;
	}

	
	@Override
	public void setTaskContext(Map<String, Serializable> taskContext) {
		this.taskContext = taskContext;
	}

	public String toString(){
		String string = this.taskID;
		return string;
	}


	@Override
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}
	
	public LinkedHashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> getActions() {
		return actions;
	}


	public void setActions(
			LinkedHashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> actions) {
		this.actions = actions;
	}
	
}