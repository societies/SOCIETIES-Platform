package org.societies.personalisation.preference.api.UserPreferenceLearning;

import java.util.Date;

/**
 * 
 * @author S.Gallacher@hw.ac.uk
 *
 */

public interface IC45Learning
{
	/**
	 * This method starts the C4.5 learning process on context history from the date
	 * specified to present for all actions. (If Date is null, all available history
	 * is retrieved.) It returns output to a call-back method implemented by the
	 * requestor.
	 * 
	 * @param requestor    - an instance of the IC45Consumer to which the output
	 * should be returned
	 * @param date    - defines the start date of history to use as input.
	 */
	public void runC45Learning(IC45Consumer requestor, Date date);
	
	/**
	 * This method starts the C4.5 learning process on context history from the date
	 * specified to present for the specified parameterName of an IAction. (If Date is
	 * null, all available history is retrieved.) It returns output to a call-back
	 * method implemented by the requestor.
	 * 
	 * @param requestor    - an instance of the IC45Consumer to which the output
	 * should be returned
	 * @param date    - defines the start date of history to use as input.
	 * @param serviceId    - the ID of the service related to the actions upon which
	 * learning should run
	 * @param parameterName    - specifies the parameterName (of an IAction) to focus
	 * C4.5 learning on.
	 */
	public void runC45Learning(IC45Consumer requestor, Date date, ServiceResourceIdentifier serviceId, String parameterName);
}