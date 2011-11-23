package org.societies.userAgent.UserFeedback.api;

import org.societies.userAgent.UserFeedback.api.model.IProposal;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */
public interface IUserFeedback
{
	public boolean getExplicitFB(IProposal proposal);
	
	public boolean getImplicitFB(IProposal proposal);
	
	public boolean notify(IProposal proposal);
}
