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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacypreference;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import org.societies.api.internal.mock.ICtxAttributeIdentifier;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypreference.constants.PrivacyPreferenceTypeConstants;


/**
 * This class represents a Rule in XACML format. The PPNPOutcome class contains the following:
 * Effect : PrivacyOutcomeConstants (ALLOW, BLOCK)
 * RuleTarget: A target specifies:
 * 		the Subject: by IDigitalPersonalIdentifier and if applicable an IServiceIdentifier
 * 		the Resource: by ICtxAttributeIdentifier
 * 		the Action: READ,WRITE,CREATE,DELETE
 * Conditions: a list of conditions that have to be satisfied by the other party. These are processed during the negotiation phase 
 * and not during the PPN preference evaluation phase.
 * 
 * @author Elizabeth
 *
 */
public class PPNPOutcome implements IPrivacyOutcome, Serializable {


	private ICtxAttributeIdentifier ctxID;
	private PrivacyOutcomeConstants effect;
	private RuleTarget rule;
	private List<Condition> conditions;
	private PrivacyPreferenceTypeConstants myOutcomeType;
	private int confidenceLevel;
	public PPNPOutcome(PrivacyOutcomeConstants effect, RuleTarget target, List<Condition> conditions) throws URISyntaxException{
		this.rule = target;
		this.effect = effect;
		this.ctxID = rule.getResource().getCtxIdentifier();
		this.conditions = conditions;
		this.myOutcomeType = PrivacyPreferenceTypeConstants.PPNP;
		
		
	}
	
	
	public RuleTarget getRuleTarget(){
		return this.rule;
	}
	public PrivacyOutcomeConstants getEffect(){
		return this.effect;
	}
	
	public boolean affectsSubject(Subject subject){
		if (null==this.rule.getSubjects()){
			return true;
		}
		if (this.rule.getSubjects().contains(subject)){
			return true;
		}
		return false;
	}
	
	public boolean isActionIncluded(Action a){
		if (null==this.rule.getActions()){
			return true;
		}
		if (this.rule.getActions().contains(a)){
			return true;
		}
		return false;
	}
	
	public List<Condition> getConditions(){
		return this.conditions;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.PPNP;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getConfidenceLevel()
	 */
	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}
	
	public void setConfidenceLevel(int c){
		this.confidenceLevel = c;
	}
	
	public String toString(){
		String str = "";
		str = str.concat(this.effect.toString()+":");
		List<Action> actions = this.rule.getActions();
		for (int i=0; i<actions.size(); i++){
			str = str.concat(actions.get(i).getActionType().toString());
			if (i<actions.size()-1){
				str = str.concat(",");
			}
		}
		
		
		return str;
	}
	public String toFullString(){
		String print = "Outcome:\n";
		
		print = print.concat("Effect: "+this.effect+"\n");
		
		print = print.concat(this.rule.toString());
		
		for (Condition c: conditions){
			print = print.concat(c.toString());
		}
		return print;
	}
	
	
}
