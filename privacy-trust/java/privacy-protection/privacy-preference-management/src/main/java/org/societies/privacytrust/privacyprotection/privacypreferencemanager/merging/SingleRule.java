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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.ArrayList;

import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;


/**
 * @author Elizabeth
 *
 */
public class SingleRule{
	private ArrayList<IPrivacyPreferenceCondition> conditions;
	private IPrivacyOutcome outcome;
	private int confidence;
	
	public SingleRule(){
		this.conditions = new ArrayList<IPrivacyPreferenceCondition>();
	}
		
	public void setConditions(ArrayList<IPrivacyPreferenceCondition> conditions) {
		this.conditions = conditions;
	}
	public void addConditions(IPrivacyPreferenceCondition cond){
		this.conditions.add(cond);
	}
	public ArrayList<IPrivacyPreferenceCondition> getConditions() {
		return this.conditions;
	}
	public void setOutcome(IPrivacyOutcome outcome) {
		this.outcome = outcome;
	}
	public IPrivacyOutcome getOutcome() {
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
	
	public boolean hasCondition(IPrivacyPreferenceCondition pc){
		//return this.conditions.contains(pc);
		
		//CtxAttributeIdentifier ctxId = pc.getCtxIdentifier();
		String contextType = ((ContextPreferenceCondition) pc).getCtxIdentifier().getType();
		System.out.println("%%%%%%%   Context name: "+contextType+" %%%%%%%%%%%%%%%%");
		String value = ((ContextPreferenceCondition) pc).getValue();
		OperatorConstants op = ((ContextPreferenceCondition) pc).getOperator();
		
		
		for (int i=0; i< this.conditions.size(); i++){
			IPrivacyPreferenceCondition con = this.conditions.get(i);
			if (((ContextPreferenceCondition) con).getCtxIdentifier().getType().equals(contextType) && ((ContextPreferenceCondition) con).getValue().equals(value) && ((ContextPreferenceCondition) con).getOperator().equals(op)){
				return true;
			}
		}
		
		return false;
	}
	
	public void removeCondition(IPrivacyPreferenceCondition pc){
		//CtxAttributeIdentifier ctxId = pc.getCtxIdentifier();
		String contextType = ((ContextPreferenceCondition) pc).getCtxIdentifier().getType();
		String value = ((ContextPreferenceCondition) pc).getValue();
		OperatorConstants op = ((ContextPreferenceCondition) pc).getOperator();
		
		
		for (int i=0; i< this.conditions.size(); i++){
			IPrivacyPreferenceCondition con = this.conditions.get(i);
			if (((ContextPreferenceCondition) con).getCtxIdentifier().getType().equals(contextType) && ((ContextPreferenceCondition) con).getValue().equals(value) && ((ContextPreferenceCondition) con).getOperator().equals(op)){
				this.conditions.remove(i);
			}
		}
	}
	
	public boolean equals(SingleRule sr){
		
		if (!(sr.getOutcome().equals(this.outcome))){
			return false;
		}
		
		return this.hasSameConditions(sr);
	}
	
	private boolean hasSameConditions(SingleRule sr){
		if (sr.getConditions().size()!=this.conditions.size()){
			return false;
		}
		
		for (int i=0; i<sr.getConditions().size(); i++){
			IPrivacyPreferenceCondition pc = sr.getConditions().get(i);
			if (!(this.hasCondition(pc))){
				return false;
			}
		}
		
		for (int i=0; i<this.conditions.size(); i++){
			IPrivacyPreferenceCondition pc = this.conditions.get(i);
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

