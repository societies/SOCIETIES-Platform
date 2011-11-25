package org.societies.comorch.common.cisorch;

/**
 * This class represents a CIS configuration task
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CisConfigurationTask extends CisOrchestrationTask {
	
	private Cis cisToConfigure;
	private ArrayList<Cis> otherCissInvolved;
	
	/*
     * Constructor for CISConfigurationTask.
     * 
	 * Description: The constructor creates a new CISConfigurationTask with the details
	 *              passed to it.
	 */
	
	public CisConfigurationTask() {}
	
	/*
	 * Description: The configureCIS method configures the given CIS
	 *              in accordance with the configuration details provided
	 * Parameters: 
	 * 				cisToConfigure - The CIS that will be configured.
	 * Returns:
	 * 				True if successful in configuring the CIS, false otherwise.
	 */
	
	public void configureCis(Cis cisToConfigure) {}
	
	/*
	 * Description: The configureCIS method configures the given CIS
	 *              in accordance with the configuration details provided
	 * Parameters: 
	 * 				cisToConfigure - The CIS that will be configured.
	 *              otherCissInvolved - Details on other CISs relevant to the
	 *              configuration process; for example, the configuration may
	 *              involve the basic CIS being replaced with two new CISs,
	 *              in a 'splitting' action.
	 * Returns:
	 * 				True if successful in configuring the CIS, false otherwise.
	 */
	
	public boolean configureCis(Cis cisToConfigure, ArrayList<cis> otherCissInvolved) {}
	
}