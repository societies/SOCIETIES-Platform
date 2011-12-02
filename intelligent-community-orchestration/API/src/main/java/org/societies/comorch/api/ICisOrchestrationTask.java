package org.societies.comorch.api;

/**
 * This class represents a CIS orchestration task
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public interface ICisOrchestrationTask {
	
	String orchestrationType;
	Cis cisToOrchestrate;
	
	//ArrayList<Cis> otherCissInvolved;
	
	/*
     * Constructor for CISOrchestrationTask.
     * 
	 * Description: The constructor creates a new CISOrchestrationTask with the details
	 *              passed to it.
	 */
	
	public CisOrchestrationTask();
	
	/*
	 * Description: The orchestrateCIS method carries out orchestration
	 *              on the CIS by creating the relevant lower-level task.
	 * Parameters: 
	 * 				cisToOrchestrate - The CIS that will be orchestrated.
	 * Returns:
	 * 				True if successful in orchestrating the CIS, false otherwise.
	 */
	
	public boolean orchestrateCis(Cis cisToOrchestrate);
	
    public CisOrchestrationTask(String orchestrationType, Cis cisToOrchestrate);
	
	/*
	 * Description: The orchestrateCIS method carries out orchestration
	 *              on the CIS by creating the relevant lower-level task.
	 * Parameters: 
	 * 				cisToOrchestrate - The CIS that will be orchestrated.
	 *              otherCissInvolved - Any other relevant CISs to the orchestration process.
	 * Returns:
	 * 				True if successful in orchestrating the CIS, false otherwise.
	 */
	
	public boolean orchestrateCis(Cis cisToOrchestrate, ArrayList<Cis> otherCissInvolved);
	
	/*
	 * Description: The getTask method returns the task.
	 * 
	 * Returns:
	 * 				The task represented by the object implementing this interface.
	 */
	
	public ICisOrchestrationTask getTask();
	
	//public CisOrchestrationTask(String orchestrationType, Cis cisToOrchestrate, ArrayList<Cis> otherCissInvolved) {}
	
}