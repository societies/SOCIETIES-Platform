package org.societies.api.internal.useragent.conflict;

import org.societies.api.internal.personalisation.model.IOutcome;

public interface ConflictResolutionRule {
	public boolean match(IOutcome intent, IOutcome prefernce);
	public IOutcome tradeoff(IOutcome intention, IOutcome preference);
}
