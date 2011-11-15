package org.societies.personalisation.CAUI.api.CAUIPrediction;

public interface ICAUIPrediction {

	/**
	 * 
	 * @param bool
	 */
	public void enablePrediction(Boolean bool);

	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IAction getCurrentIntentAction(EntityIdentifier requestor, EntityIdentifier ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

	public UserIntentAction getPrediction();

	/**
	 * 
	 * @param ctxAttribute
	 */
	public UserIntentAction getPrediction(ContextAttribute ctxAttribute);

	public List<List<String>> getPredictionHistory();

	/**
	 * 
	 * @param FeedbackEvent
	 */
	public void sendFeedback(FeedbackEvent FeedbackEvent);

	/**
	 * 
	 * @param ctxModelObj
	 */
	public void updateReceived(ContextModelObject ctxModelObj);
}
