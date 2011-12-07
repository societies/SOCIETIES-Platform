
/**
 * This class represents a CIS rule, which applies for a specific, existing CIS
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

package org.societies.api.internal.orchestration;

import java.lang.String;
import java.util.HashMap;

public interface ICisRule {
	
	/*
	 * Description: The modifyRule method alters the rule that the CISRule 
	 *              object represents, using the details in the input HashMap.
	 * Parameters: 
	 * 				1) changesToRule - The changes that are to be made to the CIS rule.
	 * Returns:
	 * 				* True if the method was able to modify the CIS rule.
	 *				* False if the method was unable to modify the CIS rule.
	 */
	
    public boolean modifyRule(HashMap changesToRule);
	
	/*
	 * Description: The deleteRule method causes the CISRule object
	 *              that the method is invokved on to delete itself.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				* True if the method was able to delete the CIS rule.
	 *				* False if the method was unable to delete the CIS rule.
	 */
	
	public boolean deleteRule();
	
	/*
	 * Description: The getRule method returns this rule.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				The rule represented by the object implementing this interface.
	 */
	
	public ICisRule getRule();

}