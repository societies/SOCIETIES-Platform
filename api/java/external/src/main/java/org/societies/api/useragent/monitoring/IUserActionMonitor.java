package org.societies.api.useragent.monitoring;

//import org.societies.personalisation.common.api.model.IAction;
import org.societies.api.personalisation.model.IAction;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserActionMonitor
{
	public void monitor(String userId, IAction action);
}