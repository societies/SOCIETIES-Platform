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
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public IAutomaticCommunityCreationManager(Css linkedCss);
	
	/*
     * Constructor for IAutomaticCommunityCreationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityCreationManager
	 *              component abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				linkedDomain - the domain on behalf of which this object is to operate.
	 */
	
	public IAutomaticCommunityCreationManager(Domain linkedDomain);
	
	public void getIDsOfInteractingCsss();
	
	public void identifyCissToCreate();
	
	public void isSituationSuggestiveOfTemporaryCISCreation();
}