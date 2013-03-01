/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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
package org.societies.orchestration.CSM.main.java.csm;

import java.io.Serializable;
import java.util.ArrayList;

import org.societies.api.internal.orchestration.ICommunitySuggestion;

public class CommunitySuggestion implements ICommunitySuggestion, Serializable {

	private String suggestionType;
	private ArrayList<String> membersList;
	private ArrayList<String> conditionsList;
	private String name;
	
	public CommunitySuggestion(){
		this.suggestionType = "";
		this.name = "";
		membersList = new ArrayList<String>();
		conditionsList = new ArrayList<String>();
	}
	//
	public void setSuggestionType(String suggestion){
		this.suggestionType = suggestion;
	}
	//
	/* (non-Javadoc)
	 * @see org.societies.orchestration.csm.ICommunitySuggestion#getSuggestionType()
	 */
	public String getSuggestionType(){
		return this.suggestionType;
	}
	//
	public void setMembersList(ArrayList<String> ml ){
		this.membersList.addAll(ml);
	}
	//
	/* (non-Javadoc)
	 * @see org.societies.orchestration.csm.ICommunitySuggestion#getSuggestionType()
	 */
	public String getName(){
		return this.name;
	}
	//
	public void setName(String ml ){
		this.name = ml;
	}
	//
	/* (non-Javadoc)
	 * @see org.societies.orchestration.csm.ICommunitySuggestion#getMembersList()
	 */
	public ArrayList<String> getMembersList(){
		return this.membersList;
	}
	//
	public void setconditionsList(ArrayList<String> ml ){
		this.conditionsList.addAll(ml);
	}
	//
	/* (non-Javadoc)
	 * @see org.societies.orchestration.csm.ICommunitySuggestion#getconditionsList()
	 */
	public ArrayList<String> getconditionsList(){
		return this.conditionsList;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.orchestration.ICommunitySuggestion#getConditionsList()
	 */
	@Override
	public ArrayList<String> getConditionsList() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
