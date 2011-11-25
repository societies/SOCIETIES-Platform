package org.societies.comorch.common.cisorch;

/**
 * This class represents a CIS deletion task
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CisDeletionTask extends CisOrchestrationTask {
	
	private Cis cisToDelete;
	
	/*
     * Constructor for CISDeletionTask.
     * 
	 * Description: The constructor creates a new CISDeletionTask with the details
	 *              passed to it.
	 */
	
	public CisDeletionTask() {}
	
	/*
	 * Description: The deleteCIS method deletes the given CIS.
	 *          
	 * Parameters: 
	 * 				cisToDelete - The CIS that will be deleted.
	 * Returns:
	 * 				True if successful in deleting the CIS; false otherwise.
	 */
	
	public boolean deleteCis(Cis cisToDelete) {}
	
}