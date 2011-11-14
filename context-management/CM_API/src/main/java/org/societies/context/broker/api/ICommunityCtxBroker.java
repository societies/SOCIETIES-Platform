package org.societies.context.broker.api;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxEntityIdentifier;

public interface ICommunityCtxBroker {
	
	/**
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveAdministratingCSS(EntityIdentifier requester, CtxEntityIdentifier communityEntId, ICommunityCtxBrokerCallback callback);

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveBonds(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (subcommunities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveChildCommunities(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * Retrievies a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveCommunityMembers(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback);

	/**
	 * 
	 * @param requester
	 * @param community
	 * @param callback
	 */
	public void retrieveParentCommunities(EntityIdentifier requester, CtxEntityIdentifier community, ICommunityCtxBrokerCallback callback);
}