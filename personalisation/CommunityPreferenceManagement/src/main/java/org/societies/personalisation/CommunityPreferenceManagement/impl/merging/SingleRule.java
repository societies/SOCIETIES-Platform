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
package org.societies.personalisation.CommunityPreferenceManagement.impl.merging;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.OperatorConstants;

/**
 * @author Elizabeth
 *
 */
public class SingleRule{
	private ArrayList<IPreferenceCondition> conditions;
	private IPreferenceOutcome outcome;
	private int confidence;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public SingleRule(){
		this.conditions = new ArrayList<IPreferenceCondition>();
	}
		
	public void setConditions(ArrayList<IPreferenceCondition> conditions) {
		this.conditions = conditions;
	}
	public void addConditions(IPreferenceCondition cond){
		this.conditions.add(cond);
	}
	public ArrayList<IPreferenceCondition> getConditions() {
		return this.conditions;
	}
	public void setOutcome(IPreferenceOutcome outcome) {
		this.outcome = outcome;
	}
	public IPreferenceOutcome getOutcome() {
		return outcome;
	}
	
	
	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}

	public int getConfidence() {
		return confidence;
	}
	
	

	public String toString(){
		String ret = "";
		for (int i=0; i<this.conditions.size(); i++){
			ret = ret + " + "+ this.conditions.get(i).toString();
		}
		ret = ret + " > "+this.outcome.toString();
		
		return ret;
	}
	
	public boolean hasCondition(IPreferenceCondition pc){
		//return this.conditions.contains(pc);
		
		//CtxAttributeIdentifier ctxId = pc.getCtxIdentifier();
		String contextType = pc.getname();
		if (logging.isDebugEnabled()){
			this.logging.debug("%%%%%%%   Context name: "+contextType+" %%%%%%%%%%%%%%%%");
		}
		String value = pc.getvalue();
		OperatorConstants op = pc.getoperator();
		
		
		for (int i=0; i< this.conditions.size(); i++){
			IPreferenceCondition con = this.conditions.get(i);
			if (con.getname().equals(contextType) && con.getvalue().equals(value) && con.getoperator().equals(op)){
				return true;
			}
		}
		
		return false;
	}
	
	public void removeCondition(IPreferenceCondition pc){
		//CtxAttributeIdentifier ctxId = pc.getCtxIdentifier();
		String contextType = pc.getname();
		String value = pc.getvalue();
		OperatorConstants op = pc.getoperator();
		
		
		for (int i=0; i< this.conditions.size(); i++){
			IPreferenceCondition con = this.conditions.get(i);
			if (con.getname().equals(contextType) && con.getvalue().equals(value) && con.getoperator().equals(op)){
				this.conditions.remove(i);
			}
		}
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result + confidence;
		result = prime * result + ((outcome == null) ? 0 : outcome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object sr){
		if (!(sr instanceof SingleRule)){
			return false;
		}
		if (!(((SingleRule) sr).getOutcome().equals(this.outcome))){
			return false;
		}
		
		return this.hasSameConditions((SingleRule) sr);
	}
	
	private boolean hasSameConditions(SingleRule sr){
		if (sr.getConditions().size()!=this.conditions.size()){
			return false;
		}
		
		for (int i=0; i<sr.getConditions().size(); i++){
			IPreferenceCondition pc = sr.getConditions().get(i);
			if (!(this.hasCondition(pc))){
				return false;
			}
		}
		
		for (int i=0; i<this.conditions.size(); i++){
			IPreferenceCondition pc = this.conditions.get(i);
			if (!(sr.hasCondition(pc))){
				return false;
			}
		}
		
		return true;		
	}
	
	
	public boolean conflicts(SingleRule sr){
		if (sr.getOutcome().equals(this.outcome)){
			return false;			
		}
		
		return this.hasSameConditions(sr);
		
	}
}

