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

import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;



/**
 * This class represents an action performed by a user and modeled in user intent model.
 * 
 * @author <a href="mailto:nikoskal@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 *
 */

public class UserIntentAction extends Action implements IUserIntentAction, Serializable {

	String taskID = "none";
	String actionID ;
	HashMap<String,Serializable> actionContext = new HashMap<String,Serializable>(); 
	private int confidenceLevel;

	// is set to true if this action refers to a community of users
	public Boolean isCommunity = false;
	
	public Boolean isImplementable = true;
	public Boolean isProactive = true;
	//private double transProb;
	private long duration;

	public UserIntentAction(ServiceResourceIdentifier serviceID, String serviceType, String par, String val,Long id){
		super(serviceID, serviceType, par, val);
		this.actionID = serviceID.getServiceInstanceIdentifier()+"#"+par +"="+val+"/"+id; 
	//	this.transProb = transProb;
		this.confidenceLevel = 51;
	}


	private static final long serialVersionUID = 1L;


	@Override
	public String getActionID() {

		return this.actionID;
	}

	@Override
	public String toString() {
		String string = this.actionID;
		return string;
	}

	@Override
	public HashMap<String, Serializable> getActionContext() {
		return this.actionContext;
	}

	@Override
	public void setActionContext(HashMap<String, Serializable> context) {
		this.actionContext = context;
	}

	@Override
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;

	}

	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}


	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	@Override
	public String getTaskID() {
		return this.taskID ;
	}
	
	public Boolean isCommunity() {
		return isCommunity;
	}

	public void setCommunity(Boolean community) {
		this.isCommunity = community;
	}

	@Override
	public void setImplementable(Boolean implementable) {
		this.isImplementable = implementable;
	}
	
	@Override
	public boolean isImplementable() {
		return this.isImplementable;
	}
	
	@Override
	public void setProactive(Boolean proactive) {
		this.isProactive = proactive;
	}
	
	@Override
	public boolean isProactive() {
		return this.isProactive;
	}
	
}