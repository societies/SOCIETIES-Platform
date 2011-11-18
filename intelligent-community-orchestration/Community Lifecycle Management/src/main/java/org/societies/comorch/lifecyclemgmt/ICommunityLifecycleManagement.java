/**
 * This is the interface for the Community Lifecycle Management component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public interface ICommunityLifecycleManagement {
	
	/*
     * Constructor for ICommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				?
	 */
	
	public ICommunityLifecycleManagement();
	
	
	
	public void createCISs();
	
	public void configureCISs();
	
	public void deleteCISs();
	
	public void processPreviousLongTimeCycle();
	
	public void processPreviousShortTimeCycle();
	
	public void loop();
	
	public void stimulusForCommunityCreationDetected();
	
	public void stimulusForCommunityDeletionDetected();
}