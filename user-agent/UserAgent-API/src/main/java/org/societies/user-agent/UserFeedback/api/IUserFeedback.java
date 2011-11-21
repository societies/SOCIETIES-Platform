package org.societies.user-agent.UserFeedback.api;

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
