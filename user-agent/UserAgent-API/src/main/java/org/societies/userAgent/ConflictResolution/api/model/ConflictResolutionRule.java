package org.societies.userAgent.ConflictResolution.api.model;

import org.societies.userAgent.mock.api.model.IAction;

public interface ConflictResolutionRule {
/*data type of conflict resolution rule
 * not yet understand the structure of such type*/
	public boolean match(IAction intention,
				IAction preference);
	public IAction tradeoff(IAction intention,
			IAction preference);
}
