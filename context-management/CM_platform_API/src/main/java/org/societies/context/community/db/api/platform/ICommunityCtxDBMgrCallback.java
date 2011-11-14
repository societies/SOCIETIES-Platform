import WP5.Context.Informational.pobail.ContextBond;

/**
 * @author nlia
 * @version 1.0
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxDBMgrCallback {

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param bond
	 * @param community
	 */
	public void bondsRetrieved(ContextBond bond, ContextEntityIdentifier community);

	/**
	 * 
	 * @param community
	 */
	public void childCommunitiesRetrieved(List<ContextEntityIdentifier> community);

	/**
	 * 
	 * @param community
	 */
	public void communityMembersRetrieved(List<ContextEntityIdentifier> community);

	/**
	 * 
	 * @param community
	 */
	public void parentCommunitiesRetrieved(List<ContextEntityIdentifier> community);

}