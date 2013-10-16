package org.societies.useragent.conflict;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.conflict.ConflictResolutionRule;

public class ConfidenceTradeoffRule implements ConflictResolutionRule {

	@Override
	public boolean match(IOutcome intent, IOutcome prefernce) {
		// TODO Auto-generated method stub
		if(intent==null||prefernce==null)
			return false;
		if (intent.getConfidenceLevel() != prefernce.getConfidenceLevel())
			return true;

		return false;
	}

	@Override
	public IOutcome tradeoff(IOutcome intention, IOutcome preference) {
		// TODO Auto-generated method stub
		if (intention.getConfidenceLevel() >= preference.getConfidenceLevel())
			return intention;
		return preference;
	}

}
