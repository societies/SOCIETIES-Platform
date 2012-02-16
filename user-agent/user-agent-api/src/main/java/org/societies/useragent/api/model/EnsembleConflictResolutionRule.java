/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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

package org.societies.useragent.api.model;

import java.util.List;

import org.societies.api.internal.personalisation.model.IOutcome;

public class EnsembleConflictResolutionRule implements ConflictResolutionRule{
	private ConflictResolutionRule rightHandSide;
	private ConflictResolutionRule leftHandSide;
	private Operator operator;
	public static enum Operator{
		AND,
		OR
	}
	
	public ConflictResolutionRule getRightHandSide() {
		return rightHandSide;
	}

	public void setRightHandSide(ConflictResolutionRule rightHandSide) {
		this.rightHandSide = rightHandSide;
	}

	public ConflictResolutionRule getLeftHandSide() {
		return leftHandSide;
	}

	public void setLeftHandSide(ConflictResolutionRule leftHandSide) {
		this.leftHandSide = leftHandSide;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public EnsembleConflictResolutionRule(ConflictResolutionRule left){
			this.leftHandSide=left;
	}
	public void addANDRule(ConflictResolutionRule right){
		this.rightHandSide=right;
		this.operator=Operator.AND;
	}
	public void addORRule(ConflictResolutionRule right){
		this.rightHandSide=right;
		this.operator=Operator.OR;
	}
	public EnsembleConflictResolutionRule shiftToLeft(){
		return new EnsembleConflictResolutionRule(this);
	}
	public static ConflictResolutionRule 
			fold(List<ConflictResolutionRule> rules){
		if(rules.size()<1)
			return new EmptyRule();
		if(rules.size()<2)
			return rules.get(0);
		if(rules.size()<3)
			return new EnsembleConflictResolutionRule(rules.get(0),
					rules.get(1),Operator.OR);
		EnsembleConflictResolutionRule rule
			= new EnsembleConflictResolutionRule(rules.get(0),
					rules.get(1),Operator.OR);
		for(int i=2;i<rules.size();i++){
			rule=rule.shiftToLeft();
			rule.addORRule(rule);
		}
		return rule;
	}
	public EnsembleConflictResolutionRule(ConflictResolutionRule left,
			ConflictResolutionRule right,
			Operator operator){
			this.leftHandSide=left;
			this.rightHandSide=right;
			this.operator=operator;
	}

	@Override
	public boolean match(IOutcome intention, IOutcome preference) {
		// TODO Auto-generated method stub
		if(this.leftHandSide==null)
			return false;
		else if(this.rightHandSide==null)
			return this.leftHandSide.match(intention, preference);
		/*empty rule*/
		if(operator==Operator.AND){
			return (this.leftHandSide.match(intention, preference)
					&&this.rightHandSide.match(intention, preference));
		}else if(operator==Operator.OR){
			return (this.leftHandSide.match(intention, preference)
					||this.rightHandSide.match(intention, preference));
		}
		return false;
	}

	@Override
	public IOutcome tradeoff(IOutcome intention, IOutcome preference) {
		// TODO Auto-generated method stub
		if(this.rightHandSide==null)
			return this.leftHandSide.
					tradeoff(intention, preference);
		IOutcome lres=this.leftHandSide.
				tradeoff(intention, preference);
		IOutcome rres=this.rightHandSide.
				tradeoff(intention, preference);
		if(lres==null)
			return rres;
		if(rres!=null&&!lres.equals(rres)){
			if(this.operator==Operator.AND){
				return null;
			}else{
				return lres;
			}
		}else{
			return rres;
		}
	}

}
