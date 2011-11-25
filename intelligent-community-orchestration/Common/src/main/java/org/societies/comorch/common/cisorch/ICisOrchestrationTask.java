package org.societies.comorch.common.cisorch;

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
	 * Parameters: 
	 * 				?
	 */
	
	public CisOrchestrationTask(String orchestrationType, Cis cisToOrchestrate);
	
	/*
	 * Description: The orchestrateCIS method carries out orchestration
	 *              on the CIS by creating the relevant lower-level task.
	 * Parameters: 
	 * 				?
	 * Returns:
	 * 				?
	 */
	
	//public CisOrchestrationTask(String orchestrationType, Cis cisToOrchestrate, ArrayList<Cis> otherCissInvolved) {}
	
	public void orchestrateCis(Cis cisToOrchestrate);
	
	public void orchestrateCis(Cis cisToOrchestrate, ArrayList<Cis> otherCissInvolved);
	
}