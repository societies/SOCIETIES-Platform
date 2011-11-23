/**
 * This is the interface for the Automatic Community Configuration Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public interface IAutomaticCommunityConfigurationManager {
	
	/*
     * Constructor for IAutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				?
	 */
	
	public IAutomaticCommunityConfigurationManager();
	
	public void determineCISsToConfigure(CISList ciss);
}