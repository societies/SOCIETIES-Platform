/**
 * This is the interface for the Automatic Community Deletion Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public interface IAutomaticCommunityDeletionManager {

	/*
     * Constructor for IAutomaticCommunityDeletionManager
     * 
	 * Description: The constructor creates the AutomaticCommunityDeletionManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public IAutomaticCommunityDeletionManager(Css linkedCss);
	
	/*
     * Constructor for IAutomaticCommunityDeletionManager
     * 
	 * Description: The constructor creates the AutomaticCommunityDeletionManager
	 *              component abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				linkedDomain - the domain on behalf of which this object is to operate.
	 */
	
	public IAutomaticCommunityDeletionManager(Domain linkedDomain);
	
	public void determineCissToDelete();

}