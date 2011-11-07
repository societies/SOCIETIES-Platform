package org.societies.feedback.api;

public interface IUserFeedback
{
	public boolean getExplicitFB(IProposal proposal);
	
	public boolean getImplicitFB(IProposal proposal);
	
	public boolean notify(IProposal proposal);
}
