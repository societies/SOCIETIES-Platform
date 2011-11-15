package org.societies.context.community.db.api.platform;

import org.societies.context.model.api.CtxEntityIdentifier;


/**
 * @author nlia
 * @version 1.0
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxDBMgr {

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveBonds(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (subcommunities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveChildCommunities(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

	/**
	 * Retrievies a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveCommunityMembers(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

	/**
	 * 
	 * @param community
	 * @param callback
	 */
	public void retrieveParentCommunities(CtxEntityIdentifier community, ICommunityCtxDBMgrCallback callback);

}