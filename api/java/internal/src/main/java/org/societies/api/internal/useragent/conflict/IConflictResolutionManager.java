package org.societies.api.internal.useragent.conflict;

import org.societies.api.internal.personalisation.model.IOutcome;

public interface IConflictResolutionManager {
	public IOutcome resolveConflict( IOutcome intentaction, 
			 IOutcome preferaction);
}
