package org.societies.comorch.common.cisrule;

/**
 * This class represents a CIS configuration task
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CisMembershipRule {
	
	ArrayList<Cis> otherCissInvolved;
	
	/*
     * Constructor for CISConfigurationTask.
     * 
	 * Description: The constructor creates a new CISConfigurationTask with the details
	 *              passed to it.
	 * Parameters: 
	 * 				?
	 */
	
	public CisConfigurationTask() {}
	
	/*
	 * Description: The configureCIS method alters configures the given CIS
	 *              in accordance with the configuration details provided
	 * Parameters: 
	 * 				?
	 * Returns:
	 * 				?
	 */
	
	public void configureCis(Cis cisToConfigure) {}
	
	public void configureCis(Cis cisToConfigure, ArrayList<cis> otherCissInvolved) {}
	
}