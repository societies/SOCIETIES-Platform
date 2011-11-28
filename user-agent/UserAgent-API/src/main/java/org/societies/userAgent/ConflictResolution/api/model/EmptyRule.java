package org.societies.userAgent.ConflictResolution.api.model;

import org.societies.userAgent.mock.api.model.IAction;

public class EmptyRule implements ConflictResolutionRule{

	@Override
	public boolean match(IAction intention, IAction preference) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public IAction tradeoff(IAction intention, IAction preference) {
		// TODO Auto-generated method stub
		return intention;
	}

}
