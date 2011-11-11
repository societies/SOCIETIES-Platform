package org.societies.personalisation.preference.api.UserPreferenceLearning;

import java.util.List;



/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 15:08:35
 */
public interface IC45Consumer {

	/**
	 * All classes requesting C45 learning must implement this call-back method to
	 * receive the output from the C45 learning algorithm
	 * 
	 * @param c45Output    - the output from the C45 learning algorithm
	 */
	public void handleC45Output(List<IC45Output> c45Output);

}