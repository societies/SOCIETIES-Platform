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
package org.societies.context.similarity.impl;

import java.util.HashMap;
import java.util.Map;

public class evaluationResult {

	private Boolean result; // overall yes/no from group
	private HashMap<String, String> attributeSummary;  //  
	private HashMap attBreakDown;
	public final String STRONG = "Strongly associated"; //      > 80%
	public final String MEDIUM = "Medium association";  //   50 - 80%
	public final String WEAK = "Weakly associated";     //   25 - 50%
	public final String NONE = "Not associated";		//      < 25%
	
	public evaluationResult(){
		this.result = true;
		this.attributeSummary = new HashMap<String, String>();
		this.attBreakDown = new HashMap();
	}
	
	public void init(){
		this.result = true;
		this.attributeSummary = new HashMap<String, String>();
		this.attBreakDown = new HashMap();
	}
	
	/***
	 *   result
	 */
	public void setResult(Boolean result){
	
		this.result = result;
	}
	//
	public Boolean getResult(){
		return this.result;
	}
	
	/***
	 *   attBreakDown
	 */
	public void setAttSummary(String att, String value){
	
		this.attributeSummary.put(att, value);
	}
	//
	public HashMap<String, String> getSummary(){
		return this.attributeSummary;
	}	
	
	/***
	 *   attBreakDown
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setAattBreakDown(HashMap breakdown){
		attBreakDown.putAll(breakdown);
	}
	//
	public HashMap getAttBreakDown(){
		return this.attBreakDown;
	}	
	
}
