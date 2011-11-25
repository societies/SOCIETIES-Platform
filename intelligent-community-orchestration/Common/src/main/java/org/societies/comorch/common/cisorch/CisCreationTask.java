package org.societies.comorch.common.cisorch;

/**
 * This class represents a CIS creation task
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CisCreationTask extends CisOrchestrationTask {
	
	private Cis cisToCreate;
	
	/*
     * Constructor for CISCreationTask.
     * 
	 * Description: The constructor creates a new CISCreationTask with the details
	 *              passed to it.
	 */
	
	public CisCreationTask() {}
	
	/*
	 * Description: The createCIS method creates a CIS based on the 
	 *              information passed.
	 * Parameters: 
	 * 				cisToCreate - The CIS that will be created.
	 * Returns:
	 * 				True if successful in creating the CIS, false otherwise.
	 */
	
	public boolean createCis(Cis cisToCreate) {}
	
}