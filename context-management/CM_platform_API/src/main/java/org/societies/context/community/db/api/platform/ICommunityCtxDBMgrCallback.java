package org.societies.context.community.db.api.platform;

import java.util.List;

import org.societies.context.model.api.CtxEntityIdentifier;

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
	public void bondsRetrieved(ContextBond bond, CtxEntityIdentifier community);

	/**
	 * 
	 * @param community
	 */
	public void childCommunitiesRetrieved(List<CtxEntityIdentifier> community);

	/**
	 * 
	 * @param community
	 */
	public void communityMembersRetrieved(List<CtxEntityIdentifier> community);

	/**
	 * 
	 * @param community
	 */
	public void parentCommunitiesRetrieved(List<CtxEntityIdentifier> community);

}