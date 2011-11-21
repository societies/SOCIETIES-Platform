package org.societies.user-agent.UserActionMonitor.api;

import org.societies.personalisation.common.api.model.IAction;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	public void monitor(String userId, IAction action);
}