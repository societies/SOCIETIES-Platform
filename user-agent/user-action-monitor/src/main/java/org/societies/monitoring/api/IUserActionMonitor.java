package org.societies.monitoring.api;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	public void monitor(String userId, IAction action);
}