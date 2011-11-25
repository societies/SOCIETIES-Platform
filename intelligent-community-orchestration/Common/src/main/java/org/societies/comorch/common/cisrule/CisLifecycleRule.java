
/**
 * This class represents a CIS lifecycle rule, which applies for the management
 * of a CIS's birth, life, and death.
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

package org.societies.comorch.common.cisrule;

public class CisLifecycleRule {
	
	//The rule embodied by the CisLifecycleRule object that this class represents.
	//The 'keys' in the HashMap may be the following (still in progress):
	//  "CIS deletion criteria" - value = array of conditions for a CIS to be deleted,
	//                                    where each element of the array is a sufficient
	//                                    condition for deletion.
	//  "CIS configuration criteria" - value = array of conditions for a CIS to be configured,
	//                                         and details on the resultant configuration for 
	//                                         each array element
	//  
	private HashMap ruleDetails;

    /*
     * Constructor for CisLifecycleRule.
     * 
	 * Description: The constructor creates a new CisLifecycleRule with the details
	 *              passed to it.
	 * Parameters: 
	 * 				1) ruleToCreate - The details on the rule that are needed
	 *                                in order to create it. 
	 */
	
	public CisLifecycleRule(String ruleType, HashMap ruleToCreate);
	
	/*
	 * Description: The modifyRule method alters the rule that the CisLifecycleRule 
	 *              object represents, using the details in the input HashMap.
	 * Parameters: 
	 * 				1) changesToRule - The changes that are to be made to the CIS lifecycle rule.
	 * Returns:
	 * 				* True if the method was able to modify the CIS lifecycle rule.
	 *				* False if the method was unable to modify the CIS lifecycle rule.
	 */
	
    public boolean modifyRule(HashMap changesToRule);
	
    /*
	 * Description: The modifyRule method alters the rule that the CisLifecycleRule 
	 *              object represents, using the information of the input
	 *              CISRule.
	 * Parameters: 
	 * 				1) CisRule - The new rule that is replacing the existing one
	 * Returns:
	 * 				* True if the method was able to modify the CIS lifecycle rule.
	 *				* False if the method was unable to modify the CIS lifecycle rule.
	 */
    
	public boolean modifyRule(CisRule replacingRule);
	
	/*
	 * Description: The deleteRule method causes the CisLifecycleRule object
	 *              that the method is invokved on to delete itself.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				* True if the method was able to delete the CIS lifecycle rule.
	 *				* False if the method was unable to delete the CIS lifecycle rule.
	 */
	
	public boolean deleteRule();

}
