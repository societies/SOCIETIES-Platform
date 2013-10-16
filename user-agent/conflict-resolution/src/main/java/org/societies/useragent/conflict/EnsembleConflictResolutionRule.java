package org.societies.useragent.conflict;

import java.util.List;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.conflict.ConflictResolutionRule;

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
		EnsembleConflictResolutionRule rule
			= new EnsembleConflictResolutionRule(rules.get(0));
		for(int i=1;i<rules.size();i++){
			rule=rule.shiftToLeft();
			rule.addANDRule(rule);
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
			return true;
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
		if(this.leftHandSide!=null)
			return this.leftHandSide.tradeoff(intention,preference);
		return intention;
}

}