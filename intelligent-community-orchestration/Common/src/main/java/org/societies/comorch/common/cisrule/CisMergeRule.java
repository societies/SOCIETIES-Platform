
/**
 * This class represents a CIS merge rule, which applies for merging two or more CISs into one.
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

package org.societies.comorch.common.cisrule;

public class CisMergeRule {
	
	//The rule embodied by the CisMergeRule object that this interface represents.
	//The 'keys' in the HashMap may be the following (still in progress):
	//  "CIS merging criteria" - value = array of conditions for a CIS to be merged with another

	private HashMap ruleDetails;

    /*
     * Constructor for CisMergeRule.
     * 
	 * Description: The constructor creates a new CisMergeRule with the details
	 *              passed to it.
	 * Parameters: 
	 * 				1) ruleToCreate - The details on the rule that are needed
	 *                                in order to create it. 
	 */
	
	public ICisRule(HashMap ruleToCreate);
	
	/*
	 * Description: The modifyRule method alters the rule that the CisMergeRule 
	 *              object represents, using the details in the input HashMap.
	 * Parameters: 
	 * 				1) changesToRule - The changes that are to be made to the CIS merge rule.
	 * Returns:
	 * 				* True if the method was able to modify the CIS merge rule.
	 *				* False if the method was unable to modify the CIS merge rule.
	 */
	
    public boolean modifyRule(HashMap changesToRule);
	
    /*
	 * Description: The modifyRule method alters the rule that the CisMergeRule 
	 *              object represents, using the information of the input
	 *              CISRule.
	 * Parameters: 
	 * 				1) CISRule - The new rule that is replacing the existing one
	 * Returns:
	 * 				* True if the method was able to modify the CIS merge rule.
	 *				* False if the method was unable to modify the CIS merge rule.
	 */
    
	public boolean modifyRule(CisRule replacingRule);
	
	/*
	 * Description: The deleteRule method causes the CisMergeRule object
	 *              that the method is invokved on to delete itself.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				* True if the method was able to delete the CIS merge rule.
	 *				* False if the method was unable to delete the CIS merge rule.
	 */
	
	public boolean deleteRule();

}
