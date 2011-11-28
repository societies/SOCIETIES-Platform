package org.societies.userAgent.ConflictResolution.api.model;

import java.util.List;

import org.societies.userAgent.ConflictResolution.api.IConflictResolutionManager;
import org.societies.userAgent.mock.api.model.IAction;

public class AbstractConflictResolutionManager implements IConflictResolutionManager{
	private List<ConflictResolutionRule> rules;
	private ConflictResolutionRule united;
	public List<ConflictResolutionRule> getRules() {
		return rules;
	}
	public void setRules(List<ConflictResolutionRule> rules) {
		this.rules = rules;
		this.united=EnsembleConflictResolutionRule.fold(this.rules);
	}

	public void addRule(ConflictResolutionRule rule) {
		rules.add(rule);
		this.united=EnsembleConflictResolutionRule.fold(this.rules);
	}

	public void detectRule(ConflictResolutionRule rule) {
		rules.remove(rule);
		this.united=EnsembleConflictResolutionRule.fold(this.rules);
	}
	@Override
	public IAction resolveConflict(final IAction intentaction, 
				final IAction preferaction) {
		// TODO Auto-generated method stub
		if(united.match(intentaction, preferaction)){
			IAction result=united.tradeoff(intentaction, preferaction);
			return result;
		}
		/*nothing matches*/
		return null;
	}
}
