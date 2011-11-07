package org.societies.monitoring.api;

public interface IUserActionMonitor
{
	public void monitor(String userId, IAction action);
}