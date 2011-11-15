package org.societies.personalisation.CAUI.api.CAUIPrediction;


/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

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

	
	/**
	 * Predicts next action based on the last performed action
	 */
	public UserIntentAction getPrediction();

	/**
	 * Predicts next action based on the last performed action
	 * 
	 * @param ctxAttribute
	 */
	public UserIntentAction getPrediction(ContextAttribute ctxAttribute);

	/**
	 * Returns a list with the performed predictions.
	 * 
	 */
	
	public List<List<String>> getPredictionHistory();

}
