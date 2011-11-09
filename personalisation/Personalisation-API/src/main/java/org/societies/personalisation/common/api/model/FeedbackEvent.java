package org.societies.personalisation.common.api;

import java.io.Serializable;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
public class FeedbackEvent implements IFeedbackEvent, Serializable {

	private String actionName;
	private FeedbackTypes errorType;
	private boolean result;
	//REPLACE WITH EntityIdentifier 
	private Object user;
	private IAction action;

	public FeedbackEvent(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 
	 * @param userDPI
	 * @param action
	 * @param result
	 * @param errorType
	 */
	public FeedbackEvent(String userDPI, IAction action, boolean result, FeedbackTypes errorType){
		user = userDPI;
		this.action = action;
		this.result = result;
		this.errorType = errorType;

	}

	public IAction getAction(){
		return this.action;
	}

	public FeedbackTypes getErrorType(){
		return this.errorType;
	}

	public boolean getResult(){
		return this.result;
	}

	/*
	 * //REPLACE WITH EntityIdentifier 
	 * @see org.societies.personalisation.common.api.IFeedbackEvent#getUser()
	 */
	public Object getUserIdentity(){
		return this.user;
	}

}