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
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public IAutomaticCommunityConfigurationManager(Css linkedCss);
	
	/*
     * Constructor for IAutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				linkedDomain - the domain on behalf of which this object is to operate.
	 */
	
	public IAutomaticCommunityConfigurationManager(Domain linkedDomain);
	
	/*
     * Constructor for IAutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CIS.
	 * Parameters: 
	 * 				linkedCis - the Cis that this object will be used to check for configuration on.
	 */
	
	public IAutomaticCommunityConfigurationManager(Cis linkedCis);
	
	public void determineCissToConfigure(CisList ciss);
}