package org.societies.comorch.api;

import java.util.List;

public interface IUserNotification {
	
	/* Description: this method is used to display any sort of recommentation
	 * Parameters: 
	 * 				1) A list of CISs gather from recommendations
	 * Return:
	 * 				* True if successfully showed
	 * Notes:
	 */
	public boolean showRecommendedCISes (List<Object> CISs);
	

	/* Description: this method is used to remove all recommendations
	 * Parameters:
	 * 				1) A list of CISs to remove
	 * Return:
	 * 			* True if it was able to remove everything
	 * Notes: 
	 */
	public boolean removeRecommendedCISes (List<Object> CISs);
	
}