package org.societies.personalisation.common.api.model;



/**
 * @author Eliza
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
public interface IFeedbackEvent {


	public IAction getAction();

	public FeedbackTypes getErrorType();

	public boolean getResult();

	//REPLACE WITH EntityIdentifier 
	public Object getUserIdentity();

}