package org.societies.comorch.api;

import java.util.List;

public interface IUserNotification {
	
	/* Description: this method is used to display any sort of recommentation
	 * @return true if successfully showed
	 */
	public boolean showRecommendedCISes (List<Object> CISes);
	

	/* Description: this method is used to remove all recommendations
	 * @return true if it was able to remove everything
	 */
	public boolean removeRecommendedCISes (List<Object> CISes);
	
}