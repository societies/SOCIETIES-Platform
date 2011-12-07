
/**
 * This class represents a CIS rule, which applies for a specific, existing CIS
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

package org.societies.comorch.api;

public interface ICisRule {
    
	//States the type of the rule, which is one of the following three:
	//  Lifecycle:  Relates to the creation, configuration,
	//              and deletion of the CIS that the rule applies for
	//  Membership: Relates to criteria for CIS membership and user 
	//              recommendation for joining
	//  Merge:      Specifies if and/or when a CIS should be merged with other CISs.
	private String ruleType;
	
	//The rule embodied by the CISRule object that this interface represents.
	//The 'keys' in the HashMap may be the following (still in progress):
	//  "CIS deletion criteria" - value = array of conditions for a CIS to be deleted,
	//                                    where each element of the array is a sufficient
	//                                    condition for deletion.
	//  "CIS configuration criteria" - value = array of conditions for a CIS to be configured,
	//                                         and details on the resultant configuration for 
	//                                         each array element
	//  "CIS merging criteria" - value = array of conditions for a CIS to be merged with another
	//  "CIS membership criteria" - value = the membership criteria for a CIS
	//  
	private HashMap ruleDetails;

    /*
     * Constructor for ICISRule.
     * 
	 * Description: The constructor creates a new CISRule with the details
	 *              passed to it.
	 * Parameters: 
	 * 				1) ruleType - The kind of rule being created, which
	 *                            is one of: Lifecycle, Membership, Merge.
	 * 				2) ruleToCreate - The details on the rule that are needed
	 *                                in order to create it. 
	 */
	
	public ICisRule(String ruleType, HashMap ruleToCreate);
	
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
	 * Description: The modifyRule method alters the rule that the CISRule 
	 *              object represents, using the information of the input
	 *              CISRule.
	 * Parameters: 
	 * 				1) CISRule - The new rule that is replacing the existing one
	 * Returns:
	 * 				* True if the method was able to modify the CIS rule.
	 *				* False if the method was unable to modify the CIS rule.
	 */
    
	public boolean modifyRule(CisRule replacingRule);
	
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
