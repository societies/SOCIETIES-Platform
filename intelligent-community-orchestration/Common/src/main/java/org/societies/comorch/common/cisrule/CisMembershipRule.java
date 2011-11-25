
/**
 * This class represents a CIS membership rule, which applies for what
 * members can join a CIS
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

package org.societies.comorch.common.cisrule;

public class CisMembershipRule {
	
	//The rule embodied by the CisMembershipRule object that this interface represents.
	//The 'keys' in the HashMap may be the following (still in progress):
	//  "CIS membership criteria" - value = the membership criteria for a CIS
	//  
	private HashMap ruleDetails;

    /*
     * Constructor for CisMembershipRule.
     * 
	 * Description: The constructor creates a new CisMembershipRule with the details
	 *              passed to it.
	 * Parameters: 
	 * 				1) ruleToCreate - The details on the rule that are needed
	 *                                in order to create it. 
	 */
	
	public ICisRule(HashMap ruleToCreate);
	
	/*
	 * Description: The modifyRule method alters the rule that the CisMembershipRule 
	 *              object represents, using the details in the input HashMap.
	 * Parameters: 
	 * 				1) changesToRule - The changes that are to be made to the CIS membership rule.
	 * Returns:
	 * 				* True if the method was able to modify the CIS membership rule.
	 *				* False if the method was unable to modify the CIS membership rule.
	 */
	
    public boolean modifyRule(HashMap changesToRule);
	
    /*
	 * Description: The modifyRule method alters the rule that the CisMembershipRule 
	 *              object represents, using the information of the input
	 *              CisRule.
	 * Parameters: 
	 * 				1) CisRule - The new rule that is replacing the existing one
	 * Returns:
	 * 				* True if the method was able to modify the CIS membership rule.
	 *				* False if the method was unable to modify the CIS membership rule.
	 */
    
	public boolean modifyRule(CisRule replacingRule);
	
	/*
	 * Description: The deleteRule method causes the CIMembershipRule object
	 *              that the method is invokved on to delete itself.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				* True if the method was able to delete the CIS membership rule.
	 *				* False if the method was unable to delete the CIS membership rule.
	 */
	
	public boolean deleteRule();

}
