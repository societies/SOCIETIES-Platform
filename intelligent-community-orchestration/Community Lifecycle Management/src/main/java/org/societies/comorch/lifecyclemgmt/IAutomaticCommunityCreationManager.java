/**
 * This is the interface for the Automatic Community Creation Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public interface IAutomaticCommunityCreationManager {
	
	/*
     * Constructor for IAutomaticCommunityCreationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityCreationManager
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				?
	 */
	
	public IAutomaticCommunityCreationManager();
	
	public void getIDsOfInteractingCSSs();
	
	public void identifyCISsToCreate();
	
	public void isSituationSuggestiveOfTemporaryCISCreation();
}