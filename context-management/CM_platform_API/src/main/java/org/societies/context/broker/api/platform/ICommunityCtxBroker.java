

/**
 * @author mcrotty
 * @version 1.0
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxBroker {

	/**
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveAdministratingCSS(ContextEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveBonds(ContextEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (subcommunities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveChildCommunities(ContextEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * Retrievies a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveCommunityMembers(ContextEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveParentCommunities(ContextEntityIdentifier community, ICommunityCtxBrokerCallback callback);

}